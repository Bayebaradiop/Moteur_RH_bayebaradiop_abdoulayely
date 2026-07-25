package com.company.hrsettlement.settlement.domain.validator;

import com.company.hrsettlement.settlement.domain.model.DepartEmploye;

import java.util.List;
import java.util.Objects;

/**
 * Applique l'ensemble des regles metier a un depart.
 * <p>
 * Le validateur ne connait aucune regle en particulier : il orchestre une liste
 * injectee. Enrichir la validation consiste donc a fournir une regle de plus, sans
 * toucher a cette classe (principe ouvert/ferme).
 */
public class ValidateurDepart {

	private final List<RegleDepart> rules;

	public ValidateurDepart(List<RegleDepart> rules) {
		this.rules = List.copyOf(Objects.requireNonNull(rules, "rules est obligatoire"));
	}

	/**
	 * Verifie le depart et interrompt le traitement des la premiere regle violee.
	 *
	 * @param depart depart a controler
	 */
	public void valider(DepartEmploye depart) {
		rules.forEach(rule -> rule.verifier(depart));
	}
}
