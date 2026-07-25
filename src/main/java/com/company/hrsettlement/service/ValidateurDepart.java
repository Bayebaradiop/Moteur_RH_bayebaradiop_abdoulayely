package com.company.hrsettlement.service;

import com.company.hrsettlement.domain.DepartEmploye;
import com.company.hrsettlement.domain.MotifDepart;
import com.company.hrsettlement.exception.ExceptionDatesInvalides;
import com.company.hrsettlement.exception.ExceptionDepartInvalide;
import com.company.hrsettlement.exception.ExceptionSalaireInvalide;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Validation metier du depart, appliquee avant tout calcul.
 * <p>
 * Distincte de la Bean Validation des DTO : celle-ci protege le contrat d'API,
 * celle-la protege les regles RH, y compris si l'appel ne vient pas de HTTP.
 */
@Component
public class ValidateurDepart {

	/** Anciennete minimale exigee pour un depart a la retraite. */
	private static final int ANNEES_ANCIENNETE_MINIMALE_RETRAITE = 1;

	/**
	 * Verifie le depart et s'arrete a la premiere regle violee.
	 *
	 * @param depart depart a controler
	 */
	public void valider(DepartEmploye depart) {
		verifierCoherenceDates(depart);
		verifierSalaire(depart);
		verifierJoursConges(depart);
		verifierAncienneteRetraite(depart);
	}

	/** Sans cette regle, une anciennete negative produirait une prime negative. */
	private void verifierCoherenceDates(DepartEmploye depart) {
		if (!depart.dateDepart().isAfter(depart.dateEmbauche())) {
			throw new ExceptionDatesInvalides(
					"La date de depart doit etre posterieure a la date d'embauche");
		}
	}

	/** Toutes les indemnites derivent du salaire de base. */
	private void verifierSalaire(DepartEmploye depart) {
		if (depart.salaireBase().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ExceptionSalaireInvalide("Le salaire de base doit etre strictement positif");
		}
	}

	/** Un solde negatif transformerait une indemnite en retenue silencieuse. */
	private void verifierJoursConges(DepartEmploye depart) {
		if (depart.joursCongesRestants() < 0) {
			throw new ExceptionDepartInvalide(
					"Le nombre de jours de conges restants ne peut pas etre negatif");
		}
	}

	/** Regle dependante du motif : une retraite suppose une anciennete minimale. */
	private void verifierAncienneteRetraite(DepartEmploye depart) {
		if (depart.motifDepart() != MotifDepart.RETRAITE) {
			return;
		}

		if (depart.anneesTravaillees() < ANNEES_ANCIENNETE_MINIMALE_RETRAITE) {
			throw new ExceptionDepartInvalide(
					"Un depart a la retraite exige au moins une annee d'anciennete");
		}
	}
}
