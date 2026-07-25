package com.company.hrsettlement.service;

import com.company.hrsettlement.domain.Solde;
import com.company.hrsettlement.repository.HistoriqueSoldeDepot;
import com.company.hrsettlement.repository.HistoriqueSoldeEntite;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Archive les soldes calcules dans PostgreSQL.
 * <p>
 * Si un employe conteste son montant plus tard, l'historique fournit le detail
 * exact et sa date de calcul.
 */
@Service
public class ArchiveurSolde {

	private final HistoriqueSoldeDepot historiqueSoldeDepot;
	private final Clock horloge;

	public ArchiveurSolde(HistoriqueSoldeDepot historiqueSoldeDepot, Clock horloge) {
		this.historiqueSoldeDepot = Objects.requireNonNull(historiqueSoldeDepot);
		this.horloge = Objects.requireNonNull(horloge);
	}

	public void archiver(Solde solde) {
		historiqueSoldeDepot.save(versEntite(solde));
	}

	private HistoriqueSoldeEntite versEntite(Solde solde) {
		return new HistoriqueSoldeEntite(
				solde.matriculeEmploye(),
				solde.indemniteConges(),
				solde.primeAnciennete(),
				solde.penalitePreavis(),
				solde.montantBrut(),
				solde.montantImpot(),
				solde.montantNet(),
				solde.auditDeclenche(),
				LocalDateTime.now(horloge));
	}
}
