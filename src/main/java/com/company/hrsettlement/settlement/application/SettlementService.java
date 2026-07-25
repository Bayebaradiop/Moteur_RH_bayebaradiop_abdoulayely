package com.company.hrsettlement.settlement.application;

import com.company.hrsettlement.settlement.domain.engine.SettlementEngine;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Settlement;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Realisation du cas d'usage.
 * <p>
 * Volontairement minimaliste : le service pilote le cas d'usage (et accueillerait
 * la transaction, la tracabilite ou la securite), mais ne contient aucune regle de
 * calcul. Toute logique qui apparaitrait ici serait une regle metier echappee du
 * domaine.
 */
@Service
public class SettlementService implements SettlementUseCase {

	private final SettlementEngine settlementEngine;

	public SettlementService(SettlementEngine settlementEngine) {
		this.settlementEngine = Objects.requireNonNull(settlementEngine);
	}

	@Override
	public Settlement calculate(EmployeeDeparture departure) {
		return settlementEngine.settle(departure);
	}
}
