package com.company.hrsettlement.settlement.domain.calculator;

import java.util.Objects;

/**
 * Regroupe les calculateurs mobilises par le moteur.
 * <p>
 * Ce parametre-objet garde le constructeur du moteur lisible tout en preservant
 * l'injection de chaque calculateur : le moteur reste ouvert au remplacement d'une
 * regle sans en connaitre l'implementation.
 *
 * @param leave          indemnite de conges non pris
 * @param primeAnciennete prime d'anciennete
 * @param penalitePreavis  penalite de preavis
 * @param gross          consolidation du brut
 * @param taxBase        repartition de l'assiette fiscale
 */
public record CalculateursSolde(
		CalculateurConges leave,
		CalculateurPrimeAnciennete primeAnciennete,
		CalculateurPenalitePreavis penalitePreavis,
		CalculateurBrut gross,
		CalculateurAssietteFiscale taxBase) {

	public CalculateursSolde {
		Objects.requireNonNull(leave, "leave est obligatoire");
		Objects.requireNonNull(primeAnciennete, "primeAnciennete est obligatoire");
		Objects.requireNonNull(penalitePreavis, "penalitePreavis est obligatoire");
		Objects.requireNonNull(gross, "gross est obligatoire");
		Objects.requireNonNull(taxBase, "taxBase est obligatoire");
	}
}
