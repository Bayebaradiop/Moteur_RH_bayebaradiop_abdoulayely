package com.company.hrsettlement.settlement.api.mapper;

import com.company.hrsettlement.settlement.api.dto.SettlementRequest;
import com.company.hrsettlement.settlement.api.dto.SettlementResponse;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Settlement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.company.hrsettlement.settlement.domain.model.DepartureReason.ECONOMIC_DISMISSAL;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Mapping entre le contrat d'API et le domaine")
class SettlementMapperTest {

	private SettlementMapper settlementMapper;

	@BeforeEach
	void setUp() {
		settlementMapper = new SettlementMapper();
	}

	@Test
	@DisplayName("convertit la requete en depart d'employe")
	void shouldMapRequestToDomain() {
		SettlementRequest request = new SettlementRequest(
				"EMP-042",
				LocalDate.of(2015, 3, 1),
				LocalDate.of(2026, 3, 1),
				ECONOMIC_DISMISSAL,
				new BigDecimal("900000"),
				12,
				false);

		EmployeeDeparture departure = settlementMapper.toDomain(request);

		assertThat(departure.employeeId()).isEqualTo("EMP-042");
		assertThat(departure.hireDate()).isEqualTo(LocalDate.of(2015, 3, 1));
		assertThat(departure.departureDate()).isEqualTo(LocalDate.of(2026, 3, 1));
		assertThat(departure.departureReason()).isEqualTo(ECONOMIC_DISMISSAL);
		assertThat(departure.baseSalary()).isEqualByComparingTo("900000");
		assertThat(departure.remainingLeaveDays()).isEqualTo(12);
		assertThat(departure.noticeRespected()).isFalse();
	}

	@Test
	@DisplayName("convertit le solde calcule en reponse d'API")
	void shouldMapSettlementToResponse() {
		Settlement settlement = new Settlement(
				"EMP-042",
				new BigDecimal("500000.00"),
				new BigDecimal("300000.00"),
				new BigDecimal("0.00"),
				new BigDecimal("800000.00"),
				new BigDecimal("80000.00"),
				new BigDecimal("720000.00"),
				true);

		SettlementResponse response = settlementMapper.toResponse(settlement);

		assertThat(response.employeeId()).isEqualTo("EMP-042");
		assertThat(response.leaveCompensation()).isEqualByComparingTo("500000.00");
		assertThat(response.seniorityBonus()).isEqualByComparingTo("300000.00");
		assertThat(response.noticePenalty()).isEqualByComparingTo("0.00");
		assertThat(response.grossAmount()).isEqualByComparingTo("800000.00");
		assertThat(response.taxAmount()).isEqualByComparingTo("80000.00");
		assertThat(response.netAmount()).isEqualByComparingTo("720000.00");
		assertThat(response.auditTriggered()).isTrue();
	}
}
