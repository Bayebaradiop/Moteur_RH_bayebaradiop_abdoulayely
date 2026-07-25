package com.company.hrsettlement.settlement.domain.validator;

import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;

import java.util.List;
import java.util.Objects;

/**
 * Applique l'ensemble des regles metier a un depart.
 * <p>
 * Le validateur ne connait aucune regle en particulier : il orchestre une liste
 * injectee. Enrichir la validation consiste donc a fournir une regle de plus, sans
 * toucher a cette classe (principe ouvert/ferme).
 */
public class DepartureValidator {

	private final List<DepartureRule> rules;

	public DepartureValidator(List<DepartureRule> rules) {
		this.rules = List.copyOf(Objects.requireNonNull(rules, "rules est obligatoire"));
	}

	/**
	 * Verifie le depart et interrompt le traitement des la premiere regle violee.
	 *
	 * @param departure depart a controler
	 */
	public void validate(EmployeeDeparture departure) {
		rules.forEach(rule -> rule.check(departure));
	}
}
