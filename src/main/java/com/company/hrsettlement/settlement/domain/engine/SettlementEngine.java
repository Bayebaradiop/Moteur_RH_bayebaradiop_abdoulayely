package com.company.hrsettlement.settlement.domain.engine;

import com.company.hrsettlement.settlement.domain.calculator.SettlementCalculators;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Money;
import com.company.hrsettlement.settlement.domain.model.Settlement;
import com.company.hrsettlement.settlement.domain.model.TaxBase;
import com.company.hrsettlement.settlement.domain.port.LabourInspectionPort;
import com.company.hrsettlement.settlement.domain.port.TaxAdministrationPort;
import com.company.hrsettlement.settlement.domain.predicate.AuditPredicates;
import com.company.hrsettlement.settlement.domain.validator.DepartureValidator;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * Moteur de calcul du solde de tout compte.
 * <p>
 * Il n'applique lui-meme aucune formule : il valide, enchaine les calculateurs,
 * interroge l'administration fiscale puis assemble le resultat. Sa seule
 * responsabilite est l'orchestration, ce qui lui permet de rester stable quand une
 * regle de calcul evolue.
 */
public class SettlementEngine {

	private final DepartureValidator departureValidator;
	private final SettlementCalculators calculators;
	private final TaxAdministrationPort taxAdministration;
	private final LabourInspectionPort labourInspection;

	public SettlementEngine(DepartureValidator departureValidator,
	                        SettlementCalculators calculators,
	                        TaxAdministrationPort taxAdministration,
	                        LabourInspectionPort labourInspection) {
		this.departureValidator = Objects.requireNonNull(departureValidator);
		this.calculators = Objects.requireNonNull(calculators);
		this.taxAdministration = Objects.requireNonNull(taxAdministration);
		this.labourInspection = Objects.requireNonNull(labourInspection);
	}

	public Settlement settle(EmployeeDeparture departure) {
		departureValidator.validate(departure);

		BigDecimal leaveCompensation = calculators.leave().compute(departure);
		BigDecimal seniorityBonus = calculators.seniorityBonus().compute(departure);
		BigDecimal noticePenalty = calculators.noticePenalty().compute(departure);
		BigDecimal grossAmount = calculators.gross()
				.compute(leaveCompensation, seniorityBonus, noticePenalty);

		BigDecimal taxAmount = requestTax(grossAmount, seniorityBonus);
		BigDecimal netAmount = Money.scaled(grossAmount.subtract(taxAmount));

		return new Settlement(
				departure.employeeId(),
				leaveCompensation,
				seniorityBonus,
				noticePenalty,
				grossAmount,
				taxAmount,
				netAmount,
				triggerAuditIfRequired(departure.employeeId(), netAmount));
	}

	/** L'impot n'est jamais calcule ici : le moteur prepare l'assiette et delegue. */
	private BigDecimal requestTax(BigDecimal grossAmount, BigDecimal seniorityBonus) {
		TaxBase taxBase = calculators.taxBase().compute(grossAmount, seniorityBonus);

		return Money.scaled(taxAdministration.computeTax(taxBase));
	}

	/**
	 * Notifie immediatement l'inspection du travail au-dela du seuil de net.
	 * <p>
	 * L'employe n'est concerne que si le seuil est depasse : l'Optional exprime cette
	 * absence eventuelle et {@code ifPresent} confie l'action au port sans branche
	 * conditionnelle supplementaire.
	 */
	private boolean triggerAuditIfRequired(String employeeId, BigDecimal netAmount) {
		Optional<String> employeeToAudit = Optional.of(employeeId)
				.filter(auditedEmployee -> AuditPredicates.REQUIRES_AUDIT.test(netAmount));

		employeeToAudit.ifPresent(labourInspection::notifyAudit);

		return employeeToAudit.isPresent();
	}
}
