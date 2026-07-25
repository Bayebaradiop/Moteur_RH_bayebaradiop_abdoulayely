package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.Monnaie;

import java.math.BigDecimal;

/**
 * Consolide le montant brut du solde de tout compte.
 * <p>
 * Regle : indemnite de conges + prime d'anciennete - penalite de preavis. Le
 * resultat peut etre negatif : l'employe est alors redevable envers l'employeur.
 */
public class CalculateurBrut {

	public BigDecimal calculer(BigDecimal indemniteConges, BigDecimal primeAnciennete, BigDecimal penalitePreavis) {
		return Monnaie.arrondir(indemniteConges
				.add(primeAnciennete)
				.subtract(penalitePreavis));
	}
}
