package com.company.hrsettlement.settlement.infrastructure.persistence;

import com.company.hrsettlement.settlement.domain.model.Solde;
import com.company.hrsettlement.settlement.domain.port.PortHistoriqueSoldes;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Adaptateur secondaire : archive les soldes dans PostgreSQL.
 * <p>
 * La date d'archivage est lue ici, dans l'infrastructure : le domaine, lui, reste
 * sans horloge et donc parfaitement deterministe.
 */
@Component
public class AdaptateurHistoriqueSoldeJpa implements PortHistoriqueSoldes {

	private final HistoriqueSoldeDepotJpa historiqueSoldeDepotJpa;
	private final Clock horloge;

	public AdaptateurHistoriqueSoldeJpa(HistoriqueSoldeDepotJpa historiqueSoldeDepotJpa, Clock horloge) {
		this.historiqueSoldeDepotJpa = Objects.requireNonNull(historiqueSoldeDepotJpa);
		this.horloge = Objects.requireNonNull(horloge);
	}

	@Override
	public void archiver(Solde solde) {
		historiqueSoldeDepotJpa.save(versEntite(solde));
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
