package com.company.hrsettlement.settlement.domain.validator;

import com.company.hrsettlement.settlement.domain.exception.InvalidDatesException;
import com.company.hrsettlement.settlement.domain.exception.InvalidDepartureException;
import com.company.hrsettlement.settlement.domain.exception.InvalidSalaryException;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.validator.rule.DatesConsistencyRule;
import com.company.hrsettlement.settlement.domain.validator.rule.NonNegativeLeaveDaysRule;
import com.company.hrsettlement.settlement.domain.validator.rule.PositiveSalaryRule;
import com.company.hrsettlement.settlement.domain.validator.rule.RetirementSeniorityRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static com.company.hrsettlement.settlement.domain.EmployeeDepartureTestBuilder.aDeparture;
import static com.company.hrsettlement.settlement.domain.model.DepartureReason.RETIREMENT;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Validation metier du depart")
class DepartureValidatorTest {

	@Mock
	private DepartureRule additionalRule;

	private DepartureValidator departureValidator;

	@BeforeEach
	void setUp() {
		departureValidator = new DepartureValidator(List.of(
				new DatesConsistencyRule(),
				new PositiveSalaryRule(),
				new NonNegativeLeaveDaysRule(),
				new RetirementSeniorityRule()));
	}

	@Test
	@DisplayName("accepte un depart coherent")
	void shouldAcceptConsistentDeparture() {
		EmployeeDeparture departure = aDeparture().build();

		assertThatCode(() -> departureValidator.validate(departure)).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("refuse une date de depart anterieure a la date d'embauche")
	void shouldRejectDepartureDateBeforeHireDate() {
		EmployeeDeparture departure = aDeparture()
				.hiredOn(LocalDate.of(2026, 1, 1))
				.leavingOn(LocalDate.of(2025, 12, 31))
				.build();

		assertThatThrownBy(() -> departureValidator.validate(departure))
				.isInstanceOf(InvalidDatesException.class)
				.hasMessageContaining("posterieure");
	}

	@Test
	@DisplayName("refuse une date de depart egale a la date d'embauche")
	void shouldRejectDepartureDateEqualToHireDate() {
		// Valeur limite : le meme jour ne constitue pas une relation de travail
		EmployeeDeparture departure = aDeparture()
				.hiredOn(LocalDate.of(2026, 1, 1))
				.leavingOn(LocalDate.of(2026, 1, 1))
				.build();

		assertThatThrownBy(() -> departureValidator.validate(departure))
				.isInstanceOf(InvalidDatesException.class);
	}

	@Test
	@DisplayName("refuse un salaire de base nul")
	void shouldRejectZeroSalary() {
		EmployeeDeparture departure = aDeparture().withMonthlySalary("0").build();

		assertThatThrownBy(() -> departureValidator.validate(departure))
				.isInstanceOf(InvalidSalaryException.class);
	}

	@Test
	@DisplayName("refuse un nombre de jours de conges negatif")
	void shouldRejectNegativeRemainingLeaveDays() {
		EmployeeDeparture departure = aDeparture().withRemainingLeaveDays(-1).build();

		assertThatThrownBy(() -> departureValidator.validate(departure))
				.isInstanceOf(InvalidDepartureException.class);
	}

	@Test
	@DisplayName("refuse un depart a la retraite sans anciennete d'au moins un an")
	void shouldRejectRetirementWithoutMinimumSeniority() {
		EmployeeDeparture departure = aDeparture()
				.because(RETIREMENT)
				.hiredOn(LocalDate.of(2025, 6, 1))
				.leavingOn(LocalDate.of(2026, 1, 1))
				.build();

		assertThatThrownBy(() -> departureValidator.validate(departure))
				.isInstanceOf(InvalidDepartureException.class)
				.hasMessageContaining("retraite");
	}

	@Test
	@DisplayName("applique toute regle ajoutee sans modification du validateur")
	void shouldApplyEveryRegisteredRule() {
		// Ouvert a l'extension : une nouvelle regle est simplement ajoutee a la liste
		DepartureValidator extendedValidator = new DepartureValidator(List.of(additionalRule));
		EmployeeDeparture departure = aDeparture().build();

		extendedValidator.validate(departure);

		verify(additionalRule).check(departure);
	}
}
