package com.company.hrsettlement.settlement.domain.port;

import com.company.hrsettlement.settlement.domain.model.Solde;

/**
 * Port secondaire : conservation des soldes calcules.
 * <p>
 * Le domaine exprime un besoin d'archivage sans rien connaitre du support :
 * PostgreSQL aujourd'hui, un entrepot d'evenements demain. L'interface reste
 * volontairement minimale (ISP).
 */
public interface PortHistoriqueSoldes {

	/**
	 * Conserve la trace d'un solde de tout compte calcule.
	 *
	 * @param solde solde a archiver
	 */
	void archiver(Solde solde);
}
