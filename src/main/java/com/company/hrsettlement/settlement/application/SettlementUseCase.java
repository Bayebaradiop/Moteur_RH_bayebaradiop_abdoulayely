package com.company.hrsettlement.settlement.application;

import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Settlement;

/**
 * Port primaire : ce que le systeme sait faire, exprime en vocabulaire metier.
 * <p>
 * L'adaptateur REST ne depend que de ce contrat, jamais du moteur : il peut donc
 * etre teste isolement et l'implementation reste substituable.
 */
public interface SettlementUseCase {

	/**
	 * Calcule le solde de tout compte correspondant a un depart.
	 *
	 * @param departure informations de depart de l'employe
	 * @return le detail du solde de tout compte
	 */
	Settlement calculate(EmployeeDeparture departure);
}
