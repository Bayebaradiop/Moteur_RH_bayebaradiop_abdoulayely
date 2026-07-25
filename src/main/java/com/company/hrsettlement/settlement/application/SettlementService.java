package com.company.hrsettlement.settlement.application;

import com.company.hrsettlement.settlement.domain.engine.SettlementEngine;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Settlement;
import com.company.hrsettlement.settlement.domain.port.SettlementHistoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Realisation du cas d'usage.
 * <p>
 * Le service ne contient aucune regle de calcul : il pilote le cas d'usage. La
 * transaction et l'archivage sont des preoccupations applicatives, pas metier,
 * d'ou leur presence ici plutot que dans le moteur.
 * <p>
 * L'archivage suit le calcul : un depart rejete par le domaine leve avant d'avoir
 * pu laisser la moindre trace en base.
 */
@Service
public class SettlementService implements SettlementUseCase {

	private final SettlementEngine settlementEngine;
	private final SettlementHistoryPort settlementHistoryPort;

	public SettlementService(SettlementEngine settlementEngine, SettlementHistoryPort settlementHistoryPort) {
		this.settlementEngine = Objects.requireNonNull(settlementEngine);
		this.settlementHistoryPort = Objects.requireNonNull(settlementHistoryPort);
	}

	@Override
	@Transactional
	public Settlement calculate(EmployeeDeparture departure) {
		Settlement settlement = settlementEngine.settle(departure);

		settlementHistoryPort.record(settlement);

		return settlement;
	}
}
