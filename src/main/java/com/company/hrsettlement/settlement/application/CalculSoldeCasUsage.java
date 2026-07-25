package com.company.hrsettlement.settlement.application;

import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.model.Solde;

/**
 * Port primaire : ce que le systeme sait faire, exprime en vocabulaire metier.
 * <p>
 * L'adaptateur REST ne depend que de ce contrat, jamais du moteur : il peut donc
 * etre teste isolement et l'implementation reste substituable.
 */
public interface CalculSoldeCasUsage {

	/**
	 * Calcule le solde de tout compte correspondant a un depart.
	 *
	 * @param depart informations de depart de l'employe
	 * @return le detail du solde de tout compte
	 */
	Solde calculer(DepartEmploye depart);
}
