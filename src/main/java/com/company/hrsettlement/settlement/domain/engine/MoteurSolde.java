package com.company.hrsettlement.settlement.domain.engine;

import com.company.hrsettlement.settlement.domain.calculator.CalculateursSolde;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.model.Monnaie;
import com.company.hrsettlement.settlement.domain.model.Solde;
import com.company.hrsettlement.settlement.domain.model.AssietteFiscale;
import com.company.hrsettlement.settlement.domain.port.PortInspectionTravail;
import com.company.hrsettlement.settlement.domain.port.PortAdministrationFiscale;
import com.company.hrsettlement.settlement.domain.predicate.PredicatsAudit;
import com.company.hrsettlement.settlement.domain.validator.ValidateurDepart;

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
public class MoteurSolde {

	private final ValidateurDepart validateurDepart;
	private final CalculateursSolde calculators;
	private final PortAdministrationFiscale administrationFiscale;
	private final PortInspectionTravail inspectionTravail;

	public MoteurSolde(ValidateurDepart validateurDepart,
	                        CalculateursSolde calculators,
	                        PortAdministrationFiscale administrationFiscale,
	                        PortInspectionTravail inspectionTravail) {
		this.validateurDepart = Objects.requireNonNull(validateurDepart);
		this.calculators = Objects.requireNonNull(calculators);
		this.administrationFiscale = Objects.requireNonNull(administrationFiscale);
		this.inspectionTravail = Objects.requireNonNull(inspectionTravail);
	}

	public Solde calculerSolde(DepartEmploye depart) {
		validateurDepart.valider(depart);

		BigDecimal indemniteConges = calculators.leave().calculer(depart);
		BigDecimal primeAnciennete = calculators.primeAnciennete().calculer(depart);
		BigDecimal penalitePreavis = calculators.penalitePreavis().calculer(depart);
		BigDecimal montantBrut = calculators.gross()
				.calculer(indemniteConges, primeAnciennete, penalitePreavis);

		BigDecimal montantImpot = demanderImpot(montantBrut, primeAnciennete);
		BigDecimal montantNet = Monnaie.arrondir(montantBrut.subtract(montantImpot));

		return new Solde(
				depart.matriculeEmploye(),
				indemniteConges,
				primeAnciennete,
				penalitePreavis,
				montantBrut,
				montantImpot,
				montantNet,
				declencherAuditSiRequis(depart.matriculeEmploye(), montantNet));
	}

	/** L'impot n'est jamais calcule ici : le moteur prepare l'assiette et delegue. */
	private BigDecimal demanderImpot(BigDecimal montantBrut, BigDecimal primeAnciennete) {
		AssietteFiscale taxBase = calculators.taxBase().calculer(montantBrut, primeAnciennete);

		return Monnaie.arrondir(administrationFiscale.calculerImpot(taxBase));
	}

	/**
	 * Notifie immediatement l'inspection du travail au-dela du seuil de net.
	 * <p>
	 * L'employe n'est concerne que si le seuil est depasse : l'Optional exprime cette
	 * absence eventuelle et {@code ifPresent} confie l'action au port sans branche
	 * conditionnelle supplementaire.
	 */
	private boolean declencherAuditSiRequis(String matriculeEmploye, BigDecimal montantNet) {
		Optional<String> employeAAuditer = Optional.of(matriculeEmploye)
				.filter(auditedEmployee -> PredicatsAudit.AUDIT_REQUIS.test(montantNet));

		employeAAuditer.ifPresent(inspectionTravail::notifierAudit);

		return employeAAuditer.isPresent();
	}
}
