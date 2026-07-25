package com.company.hrsettlement.settlement.domain.predicate;

import com.company.hrsettlement.settlement.domain.model.MotifDepart;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;

import java.util.function.Predicate;

/**
 * Regles booleennes du domaine, nommees une fois et reutilisables.
 * <p>
 * Le switch est volontairement exhaustif et sans branche par defaut : l'ajout d'un
 * nouveau motif de depart provoquera une erreur de compilation ici, forcant une
 * decision metier explicite plutot qu'un comportement implicite.
 */
public final class PredicatsDepart {

	/** Seuls la retraite et le licenciement economique ouvrent droit a la prime d'anciennete. */
	public static final Predicate<DepartEmploye> ELIGIBLE_PRIME_ANCIENNETE =
			depart -> switch (depart.motifDepart()) {
				case RETRAITE, LICENCIEMENT_ECONOMIQUE -> true;
				case DEMISSION, FAUTE_GRAVE -> false;
			};

	/** Le preavis n'est exigible que d'un employe qui demissionne. */
	public static final Predicate<DepartEmploye> EST_DEMISSION =
			depart -> depart.motifDepart() == MotifDepart.DEMISSION;

	public static final Predicate<DepartEmploye> PREAVIS_NON_RESPECTE =
			depart -> !depart.preavisRespecte();

	/** Seule une demission sans preavis expose l'employe a la retenue d'un mois de salaire. */
	public static final Predicate<DepartEmploye> PENALITE_PREAVIS_APPLICABLE =
			EST_DEMISSION.and(PREAVIS_NON_RESPECTE);

	private PredicatsDepart() {
	}
}
