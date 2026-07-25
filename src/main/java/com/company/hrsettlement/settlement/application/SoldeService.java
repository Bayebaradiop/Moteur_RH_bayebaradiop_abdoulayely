package com.company.hrsettlement.settlement.application;

import com.company.hrsettlement.settlement.domain.engine.MoteurSolde;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.model.Solde;
import com.company.hrsettlement.settlement.domain.port.PortHistoriqueSoldes;
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
public class SoldeService implements CalculSoldeCasUsage {

	private final MoteurSolde moteurSolde;
	private final PortHistoriqueSoldes portHistoriqueSoldes;

	public SoldeService(MoteurSolde moteurSolde, PortHistoriqueSoldes portHistoriqueSoldes) {
		this.moteurSolde = Objects.requireNonNull(moteurSolde);
		this.portHistoriqueSoldes = Objects.requireNonNull(portHistoriqueSoldes);
	}

	@Override
	@Transactional
	public Solde calculer(DepartEmploye depart) {
		Solde solde = moteurSolde.calculerSolde(depart);

		portHistoriqueSoldes.archiver(solde);

		return solde;
	}
}
