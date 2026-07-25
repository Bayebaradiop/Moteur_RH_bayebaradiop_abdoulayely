package com.company.hrsettlement.settlement.application;

import com.company.hrsettlement.settlement.domain.engine.SettlementEngine;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Settlement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.company.hrsettlement.settlement.domain.EmployeeDepartureTestBuilder.aDeparture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service applicatif de solde de tout compte")
class SettlementServiceTest {

	@Mock
	private SettlementEngine settlementEngine;

	private SettlementUseCase settlementUseCase;

	@BeforeEach
	void setUp() {
		settlementUseCase = new SettlementService(settlementEngine);
	}

	@Test
	@DisplayName("delegue integralement le calcul au moteur metier")
	void shouldDelegateCalculationToTheEngine() {
		// Le service ne porte aucune regle : toute intelligence reste dans le domaine
		EmployeeDeparture departure = aDeparture().build();
		Settlement expectedSettlement = new Settlement(
				"EMP-001", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false);
		when(settlementEngine.settle(departure)).thenReturn(expectedSettlement);

		Settlement settlement = settlementUseCase.calculate(departure);

		assertThat(settlement).isSameAs(expectedSettlement);
		verify(settlementEngine).settle(departure);
	}
}
