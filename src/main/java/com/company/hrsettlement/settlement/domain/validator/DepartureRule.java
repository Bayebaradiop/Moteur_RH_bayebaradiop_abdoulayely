package com.company.hrsettlement.settlement.domain.validator;

import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;

/**
 * Contrat d'une regle de validation metier.
 * <p>
 * Interface a une seule methode (ISP) : chaque regle est independante, testable
 * seule et remplacable. Ajouter une regle n'oblige jamais a modifier les autres.
 */
@FunctionalInterface
public interface DepartureRule {

	/**
	 * @param departure depart a controler
	 * @throws com.company.hrsettlement.settlement.domain.exception.BusinessException si la regle est violee
	 */
	void check(EmployeeDeparture departure);
}
