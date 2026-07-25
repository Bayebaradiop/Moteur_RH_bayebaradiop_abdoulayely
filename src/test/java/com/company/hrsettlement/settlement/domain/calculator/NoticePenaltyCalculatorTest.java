package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.company.hrsettlement.settlement.domain.EmployeeDepartureTestBuilder.aDeparture;
import static com.company.hrsettlement.settlement.domain.model.DepartureReason.RESIGNATION;
import static com.company.hrsettlement.settlement.domain.model.DepartureReason.RETIREMENT;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Penalite de preavis")
class NoticePenaltyCalculatorTest {

	private NoticePenaltyCalculator noticePenaltyCalculator;

	@BeforeEach
	void setUp() {
		noticePenaltyCalculator = new NoticePenaltyCalculator();
	}

	@Test
	@DisplayName("retient un mois de salaire lorsqu'une demission ne respecte pas le preavis")
	void shouldWithholdOneMonthSalaryWhenNoticeIsNotRespectedOnResignation() {
		EmployeeDeparture departure = aDeparture()
				.because(RESIGNATION)
				.withNoticeRespected(false)
				.withMonthlySalary("750000")
				.build();

		BigDecimal penalty = noticePenaltyCalculator.compute(departure);

		assertThat(penalty).isEqualByComparingTo("750000");
	}

	@Test
	@DisplayName("n'applique aucune penalite lorsque le preavis est respecte")
	void shouldNotPenaliseWhenNoticeIsRespected() {
		EmployeeDeparture departure = aDeparture()
				.because(RESIGNATION)
				.withNoticeRespected(true)
				.withMonthlySalary("750000")
				.build();

		BigDecimal penalty = noticePenaltyCalculator.compute(departure);

		assertThat(penalty).isEqualByComparingTo("0");
	}

	@Test
	@DisplayName("n'applique aucune penalite en dehors d'une demission")
	void shouldNotPenaliseWhenDepartureIsNotAResignation() {
		EmployeeDeparture departure = aDeparture()
				.because(RETIREMENT)
				.withNoticeRespected(false)
				.withMonthlySalary("750000")
				.build();

		BigDecimal penalty = noticePenaltyCalculator.compute(departure);

		assertThat(penalty).isEqualByComparingTo("0");
	}
}
