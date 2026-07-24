package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.company.hrsettlement.settlement.domain.EmployeeDepartureTestBuilder.aDeparture;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Indemnite de conges non pris")
class LeaveCalculatorTest {

	private LeaveCalculator leaveCalculator;

	@BeforeEach
	void setUp() {
		leaveCalculator = new LeaveCalculator();
	}

	@Test
	@DisplayName("multiplie les jours restants par la valeur d'une journee de travail")
	void shouldCompensateRemainingLeaveDaysAtDailySalary() {
		// 1 050 000 / 21 = 50 000 par jour, pour 10 jours restants
		EmployeeDeparture departure = aDeparture()
				.withMonthlySalary("1050000")
				.withRemainingLeaveDays(10)
				.build();

		BigDecimal compensation = leaveCalculator.compute(departure);

		assertThat(compensation).isEqualByComparingTo("500000");
	}

	@Test
	@DisplayName("ne verse aucune indemnite lorsque tous les conges ont ete pris")
	void shouldReturnZeroWhenNoRemainingLeaveDay() {
		EmployeeDeparture departure = aDeparture()
				.withMonthlySalary("1050000")
				.withRemainingLeaveDays(0)
				.build();

		BigDecimal compensation = leaveCalculator.compute(departure);

		assertThat(compensation).isEqualByComparingTo("0");
	}

	@Test
	@DisplayName("arrondit la valeur journaliere au centime superieur le plus proche")
	void shouldRoundDailySalaryToTwoDecimals() {
		// 1 000 000 / 21 = 47 619,047... arrondi a 47 619,05 puis multiplie par 3
		EmployeeDeparture departure = aDeparture()
				.withMonthlySalary("1000000")
				.withRemainingLeaveDays(3)
				.build();

		BigDecimal compensation = leaveCalculator.compute(departure);

		assertThat(compensation).isEqualByComparingTo("142857.15");
	}
}
