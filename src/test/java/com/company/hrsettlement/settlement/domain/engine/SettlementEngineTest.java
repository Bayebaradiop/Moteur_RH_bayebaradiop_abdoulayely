package com.company.hrsettlement.settlement.domain.engine;

import com.company.hrsettlement.settlement.domain.calculator.GrossCalculator;
import com.company.hrsettlement.settlement.domain.calculator.LeaveCalculator;
import com.company.hrsettlement.settlement.domain.calculator.NoticePenaltyCalculator;
import com.company.hrsettlement.settlement.domain.calculator.SeniorityBonusCalculator;
import com.company.hrsettlement.settlement.domain.calculator.SettlementCalculators;
import com.company.hrsettlement.settlement.domain.calculator.TaxBaseCalculator;
import com.company.hrsettlement.settlement.domain.exception.InvalidDatesException;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Settlement;
import com.company.hrsettlement.settlement.domain.model.TaxBase;
import com.company.hrsettlement.settlement.domain.port.LabourInspectionPort;
import com.company.hrsettlement.settlement.domain.port.TaxAdministrationPort;
import com.company.hrsettlement.settlement.domain.validator.DepartureValidator;
import com.company.hrsettlement.settlement.domain.validator.rule.DatesConsistencyRule;
import com.company.hrsettlement.settlement.domain.validator.rule.NonNegativeLeaveDaysRule;
import com.company.hrsettlement.settlement.domain.validator.rule.PositiveSalaryRule;
import com.company.hrsettlement.settlement.domain.validator.rule.RetirementSeniorityRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.company.hrsettlement.settlement.domain.EmployeeDepartureTestBuilder.aDeparture;
import static com.company.hrsettlement.settlement.domain.model.DepartureReason.RETIREMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Moteur de solde de tout compte")
class SettlementEngineTest {

	@Mock
	private TaxAdministrationPort taxAdministration;

	@Mock
	private LabourInspectionPort labourInspection;

	@Captor
	private ArgumentCaptor<TaxBase> taxBaseCaptor;

	private SettlementEngine settlementEngine;

	@BeforeEach
	void setUp() {
		DepartureValidator departureValidator = new DepartureValidator(List.of(
				new DatesConsistencyRule(),
				new PositiveSalaryRule(),
				new NonNegativeLeaveDaysRule(),
				new RetirementSeniorityRule()));

		SettlementCalculators calculators = new SettlementCalculators(
				new LeaveCalculator(),
				new SeniorityBonusCalculator(),
				new NoticePenaltyCalculator(),
				new GrossCalculator(),
				new TaxBaseCalculator());

		settlementEngine = new SettlementEngine(
				departureValidator, calculators, taxAdministration, labourInspection);
	}

	@Test
	@DisplayName("detaille chaque composante puis deduit l'impot du brut")
	void shouldDetailEachComponentAndDeductTaxFromGross() {
		// 10 annees d'anciennete : conges 10 x 50 000, prime 1 050 000 x 1,25
		EmployeeDeparture departure = aDeparture()
				.withEmployeeId("EMP-100")
				.because(RETIREMENT)
				.hiredOn(LocalDate.of(2016, 1, 1))
				.leavingOn(LocalDate.of(2026, 1, 1))
				.withMonthlySalary("1050000")
				.withRemainingLeaveDays(10)
				.build();
		when(taxAdministration.computeTax(any(TaxBase.class))).thenReturn(new BigDecimal("200000"));

		Settlement settlement = settlementEngine.settle(departure);

		assertThat(settlement.employeeId()).isEqualTo("EMP-100");
		assertThat(settlement.leaveCompensation()).isEqualByComparingTo("500000");
		assertThat(settlement.seniorityBonus()).isEqualByComparingTo("1312500");
		assertThat(settlement.noticePenalty()).isEqualByComparingTo("0");
		assertThat(settlement.grossAmount()).isEqualByComparingTo("1812500");
		assertThat(settlement.taxAmount()).isEqualByComparingTo("200000");
		assertThat(settlement.netAmount()).isEqualByComparingTo("1612500");
		assertThat(settlement.auditTriggered()).isFalse();
	}

	@Test
	@DisplayName("transmet a l'administration fiscale la part imposable et la part exoneree")
	void shouldSubmitTaxableAndExemptAmountsToTaxAdministration() {
		// Prime de 12 500 000 : exoneree a hauteur de 5 000 000, le surplus est imposable
		EmployeeDeparture departure = aDeparture()
				.because(RETIREMENT)
				.hiredOn(LocalDate.of(2016, 1, 1))
				.leavingOn(LocalDate.of(2026, 1, 1))
				.withMonthlySalary("10000000")
				.build();
		when(taxAdministration.computeTax(any(TaxBase.class))).thenReturn(BigDecimal.ZERO);

		settlementEngine.settle(departure);

		verify(taxAdministration).computeTax(taxBaseCaptor.capture());
		assertThat(taxBaseCaptor.getValue().exemptAmount()).isEqualByComparingTo("5000000");
		assertThat(taxBaseCaptor.getValue().taxableAmount()).isEqualByComparingTo("7500000");
	}

	@Test
	@DisplayName("notifie l'inspection du travail lorsque le net depasse trente millions")
	void shouldNotifyLabourInspectionWhenNetExceedsThreshold() {
		EmployeeDeparture departure = aDeparture()
				.withEmployeeId("EMP-777")
				.because(RETIREMENT)
				.hiredOn(LocalDate.of(2016, 1, 1))
				.leavingOn(LocalDate.of(2026, 1, 1))
				.withMonthlySalary("30000000")
				.build();
		when(taxAdministration.computeTax(any(TaxBase.class))).thenReturn(BigDecimal.ZERO);

		Settlement settlement = settlementEngine.settle(departure);

		verify(labourInspection).notifyAudit("EMP-777");
		assertThat(settlement.auditTriggered()).isTrue();
	}

	@Test
	@DisplayName("ne declenche aucun audit lorsque le net atteint exactement le seuil")
	void shouldNotNotifyLabourInspectionAtExactThreshold() {
		// Valeur limite : 24 000 000 x 1,25 = 30 000 000 exactement
		EmployeeDeparture departure = aDeparture()
				.because(RETIREMENT)
				.hiredOn(LocalDate.of(2016, 1, 1))
				.leavingOn(LocalDate.of(2026, 1, 1))
				.withMonthlySalary("24000000")
				.build();
		when(taxAdministration.computeTax(any(TaxBase.class))).thenReturn(BigDecimal.ZERO);

		Settlement settlement = settlementEngine.settle(departure);

		verifyNoInteractions(labourInspection);
		assertThat(settlement.netAmount()).isEqualByComparingTo("30000000");
		assertThat(settlement.auditTriggered()).isFalse();
	}

	@Test
	@DisplayName("rejette un depart incoherent sans solliciter le moindre systeme externe")
	void shouldRejectInvalidDepartureBeforeCallingAnyPort() {
		EmployeeDeparture departure = aDeparture()
				.hiredOn(LocalDate.of(2026, 1, 1))
				.leavingOn(LocalDate.of(2025, 1, 1))
				.build();

		assertThatThrownBy(() -> settlementEngine.settle(departure))
				.isInstanceOf(InvalidDatesException.class);

		verifyNoInteractions(taxAdministration, labourInspection);
	}
}
