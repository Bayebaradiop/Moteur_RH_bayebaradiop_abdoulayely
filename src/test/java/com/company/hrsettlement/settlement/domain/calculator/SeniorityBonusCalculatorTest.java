package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.company.hrsettlement.settlement.domain.EmployeeDepartureTestBuilder.aDeparture;
import static com.company.hrsettlement.settlement.domain.model.DepartureReason.RETIREMENT;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Prime d'anciennete")
class SeniorityBonusCalculatorTest {

	private SeniorityBonusCalculator seniorityBonusCalculator;

	@BeforeEach
	void setUp() {
		seniorityBonusCalculator = new SeniorityBonusCalculator();
	}

	@Test
	@DisplayName("verse 10 % du salaire par annee pour les premieres annees d'anciennete")
	void shouldPayTenPercentPerYearForTheFirstYears() {
		// 3 annees d'anciennete a 10 % : 1 000 000 x 0,30
		EmployeeDeparture departure = aDeparture()
				.because(RETIREMENT)
				.hiredOn(LocalDate.of(2023, 1, 1))
				.leavingOn(LocalDate.of(2026, 1, 1))
				.withMonthlySalary("1000000")
				.build();

		BigDecimal bonus = seniorityBonusCalculator.compute(departure);

		assertThat(bonus).isEqualByComparingTo("300000");
	}

	@Test
	@DisplayName("applique encore le taux de base a exactement cinq annees d'anciennete")
	void shouldStillApplyBaseRateAtExactlyFiveYears() {
		// Valeur limite : 5 annees pleines restent integralement a 10 %
		EmployeeDeparture departure = aDeparture()
				.because(RETIREMENT)
				.hiredOn(LocalDate.of(2021, 1, 1))
				.leavingOn(LocalDate.of(2026, 1, 1))
				.withMonthlySalary("1000000")
				.build();

		BigDecimal bonus = seniorityBonusCalculator.compute(departure);

		assertThat(bonus).isEqualByComparingTo("500000");
	}
}
