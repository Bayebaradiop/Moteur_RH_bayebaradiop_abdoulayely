package com.company.hrsettlement.settlement.application;

import com.company.hrsettlement.settlement.domain.engine.SettlementEngine;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Settlement;
import com.company.hrsettlement.settlement.domain.port.SettlementHistoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.company.hrsettlement.settlement.domain.EmployeeDepartureTestBuilder.aDeparture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service applicatif de solde de tout compte")
class SettlementServiceTest {

	@Mock
	private SettlementEngine settlementEngine;

	@Mock
	private SettlementHistoryPort settlementHistoryPort;

	private SettlementUseCase settlementUseCase;

	@BeforeEach
	void setUp() {
		settlementUseCase = new SettlementService(settlementEngine, settlementHistoryPort);
	}

	@Test
	@DisplayName("delegue integralement le calcul au moteur metier")
	void shouldDelegateCalculationToTheEngine() {
		// Le service ne porte aucune regle : toute intelligence reste dans le domaine
		EmployeeDeparture departure = aDeparture().build();
		Settlement expectedSettlement = aSettlement();
		when(settlementEngine.settle(departure)).thenReturn(expectedSettlement);

		Settlement settlement = settlementUseCase.calculate(departure);

		assertThat(settlement).isSameAs(expectedSettlement);
		verify(settlementEngine).settle(departure);
	}

	@Test
	@DisplayName("archive le solde calcule pour en conserver la trace")
	void shouldRecordCalculatedSettlement() {
		EmployeeDeparture departure = aDeparture().build();
		Settlement expectedSettlement = aSettlement();
		when(settlementEngine.settle(departure)).thenReturn(expectedSettlement);

		settlementUseCase.calculate(departure);

		verify(settlementHistoryPort).record(expectedSettlement);
	}

	@Test
	@DisplayName("n'archive rien lorsque le calcul est rejete")
	void shouldNotRecordAnythingWhenCalculationFails() {
		EmployeeDeparture departure = aDeparture().build();
		when(settlementEngine.settle(departure)).thenThrow(new IllegalStateException("moteur indisponible"));

		assertThatThrownBy(() -> settlementUseCase.calculate(departure))
				.isInstanceOf(IllegalStateException.class);

		verifyNoInteractions(settlementHistoryPort);
	}

	private Settlement aSettlement() {
		return new Settlement(
				"EMP-001",
				new BigDecimal("500000.00"),
				new BigDecimal("300000.00"),
				new BigDecimal("0.00"),
				new BigDecimal("800000.00"),
				new BigDecimal("80000.00"),
				new BigDecimal("720000.00"),
				false);
	}
}
