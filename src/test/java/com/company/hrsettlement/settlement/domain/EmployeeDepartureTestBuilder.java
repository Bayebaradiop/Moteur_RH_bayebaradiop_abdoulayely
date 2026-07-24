package com.company.hrsettlement.settlement.domain;

import com.company.hrsettlement.settlement.domain.model.DepartureReason;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Constructeur de donnees de test.
 * <p>
 * Chaque test ne declare que les champs qui portent le comportement teste : les
 * autres gardent une valeur par defaut neutre. Cela evite la duplication et rend
 * l'intention du test immediatement lisible.
 */
public final class EmployeeDepartureTestBuilder {

	private String employeeId = "EMP-001";
	private LocalDate hireDate = LocalDate.of(2020, 1, 1);
	private LocalDate departureDate = LocalDate.of(2026, 1, 1);
	private DepartureReason departureReason = DepartureReason.RESIGNATION;
	private BigDecimal baseSalary = new BigDecimal("1050000");
	private int remainingLeaveDays = 0;
	private boolean noticeRespected = true;

	private EmployeeDepartureTestBuilder() {
	}

	public static EmployeeDepartureTestBuilder aDeparture() {
		return new EmployeeDepartureTestBuilder();
	}

	public EmployeeDepartureTestBuilder withEmployeeId(String employeeId) {
		this.employeeId = employeeId;
		return this;
	}

	public EmployeeDepartureTestBuilder hiredOn(LocalDate hireDate) {
		this.hireDate = hireDate;
		return this;
	}

	public EmployeeDepartureTestBuilder leavingOn(LocalDate departureDate) {
		this.departureDate = departureDate;
		return this;
	}

	public EmployeeDepartureTestBuilder because(DepartureReason departureReason) {
		this.departureReason = departureReason;
		return this;
	}

	public EmployeeDepartureTestBuilder withMonthlySalary(String baseSalary) {
		this.baseSalary = new BigDecimal(baseSalary);
		return this;
	}

	public EmployeeDepartureTestBuilder withRemainingLeaveDays(int remainingLeaveDays) {
		this.remainingLeaveDays = remainingLeaveDays;
		return this;
	}

	public EmployeeDepartureTestBuilder withNoticeRespected(boolean noticeRespected) {
		this.noticeRespected = noticeRespected;
		return this;
	}

	public EmployeeDeparture build() {
		return new EmployeeDeparture(
				employeeId,
				hireDate,
				departureDate,
				departureReason,
				baseSalary,
				remainingLeaveDays,
				noticeRespected);
	}
}
