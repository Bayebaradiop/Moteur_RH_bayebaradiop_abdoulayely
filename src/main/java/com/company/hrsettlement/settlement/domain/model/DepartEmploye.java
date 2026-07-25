package com.company.hrsettlement.settlement.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Informations de depart d'un employe : donnee d'entree du moteur de calcul.
 * <p>
 * Record immuable : une fois construit, l'objet ne peut plus etre altere par un
 * calculateur. Les dates arrivent de l'exterieur, le domaine ne lit jamais
 * l'horloge : chaque calcul est donc reproductible.
 *
 * @param matriculeEmploye         identifiant de l'employe
 * @param dateEmbauche           date d'embauche
 * @param dateDepart      date de depart
 * @param motifDepart    motif du depart
 * @param salaireBase         salaire mensuel de base, en XOF
 * @param joursCongesRestants nombre de jours de conges non pris
 * @param preavisRespecte    indique si le preavis a ete respecte
 */
public record DepartEmploye(
		String matriculeEmploye,
		LocalDate dateEmbauche,
		LocalDate dateDepart,
		MotifDepart motifDepart,
		BigDecimal salaireBase,
		int joursCongesRestants,
		boolean preavisRespecte) {

	/**
	 * Garantit l'absence de valeur nulle : les regles metier de coherence
	 * (dates, montants) sont, elles, portees par le validateur du domaine.
	 */
	public DepartEmploye {
		Objects.requireNonNull(matriculeEmploye, "matriculeEmploye est obligatoire");
		Objects.requireNonNull(dateEmbauche, "dateEmbauche est obligatoire");
		Objects.requireNonNull(dateDepart, "dateDepart est obligatoire");
		Objects.requireNonNull(motifDepart, "motifDepart est obligatoire");
		Objects.requireNonNull(salaireBase, "salaireBase est obligatoire");
	}
}
