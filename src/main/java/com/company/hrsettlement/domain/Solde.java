package com.company.hrsettlement.domain;

import java.math.BigDecimal;

/**
 * Resultat du calcul : le solde de tout compte d'un employe.
 * <p>
 * Le detail de chaque composante est conserve afin que la RH puisse justifier le
 * montant verse ligne par ligne.
 *
 * @param matriculeEmploye identifiant de l'employe concerne
 * @param indemniteConges  indemnite de conges non pris
 * @param primeAnciennete  prime d'anciennete
 * @param penalitePreavis  penalite de preavis, exprimee en valeur positive
 * @param montantBrut      montant brut, potentiellement negatif
 * @param montantImpot     impot calcule par l'administration fiscale
 * @param montantNet       montant net du solde de tout compte
 * @param auditDeclenche   vrai si l'inspection du travail a ete notifiee
 */
public record Solde(
		String matriculeEmploye,
		BigDecimal indemniteConges,
		BigDecimal primeAnciennete,
		BigDecimal penalitePreavis,
		BigDecimal montantBrut,
		BigDecimal montantImpot,
		BigDecimal montantNet,
		boolean auditDeclenche) {
}
