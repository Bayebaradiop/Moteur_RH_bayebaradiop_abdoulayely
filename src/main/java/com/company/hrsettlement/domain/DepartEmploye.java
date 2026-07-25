package com.company.hrsettlement.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Informations de depart d'un employe : donnee d'entree du calcul.
 * <p>
 * Record immuable : aucun service ne peut alterer la demande en cours de route.
 * Les dates viennent de l'appelant, le calcul ne lit jamais l'horloge : chaque
 * resultat est donc reproductible.
 *
 * @param matriculeEmploye    identifiant de l'employe
 * @param dateEmbauche        date d'embauche
 * @param dateDepart          date de depart
 * @param motifDepart         motif du depart
 * @param salaireBase         salaire mensuel de base, en XOF
 * @param joursCongesRestants nombre de jours de conges non pris
 * @param preavisRespecte     indique si le preavis a ete respecte
 */
public record DepartEmploye(
		String matriculeEmploye,
		LocalDate dateEmbauche,
		LocalDate dateDepart,
		MotifDepart motifDepart,
		BigDecimal salaireBase,
		int joursCongesRestants,
		boolean preavisRespecte) {

	public DepartEmploye {
		Objects.requireNonNull(matriculeEmploye, "matriculeEmploye est obligatoire");
		Objects.requireNonNull(dateEmbauche, "dateEmbauche est obligatoire");
		Objects.requireNonNull(dateDepart, "dateDepart est obligatoire");
		Objects.requireNonNull(motifDepart, "motifDepart est obligatoire");
		Objects.requireNonNull(salaireBase, "salaireBase est obligatoire");
	}

	/** Nombre d'annees pleines separant l'embauche du depart. */
	public int anneesTravaillees() {
		return (int) ChronoUnit.YEARS.between(dateEmbauche, dateDepart);
	}

	/** Seule une demission sans preavis expose a la retenue d'un mois de salaire. */
	public boolean estDemissionSansPreavis() {
		return motifDepart == MotifDepart.DEMISSION && !preavisRespecte;
	}

	/**
	 * Seuls la retraite et le licenciement economique ouvrent droit a la prime.
	 * <p>
	 * Le switch est exhaustif et sans branche par defaut : ajouter un motif
	 * provoquera une erreur de compilation ici, forcant une decision explicite.
	 */
	public boolean eligiblePrimeAnciennete() {
		return switch (motifDepart) {
			case RETRAITE, LICENCIEMENT_ECONOMIQUE -> true;
			case DEMISSION, FAUTE_GRAVE -> false;
		};
	}
}
