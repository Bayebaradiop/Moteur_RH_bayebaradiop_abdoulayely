package com.company.hrsettlement.settlement.domain.port;

import com.company.hrsettlement.settlement.domain.model.AssietteFiscale;

import java.math.BigDecimal;

/**
 * Port secondaire : calcul de l'impot par l'administration fiscale.
 * <p>
 * Le bareme fiscal evolue independamment des regles RH : il n'a donc pas sa place
 * dans le moteur. Le domaine pose la question, un adaptateur y repond.
 */
public interface PortAdministrationFiscale {

	/**
	 * @param taxBase repartition entre part imposable et part exoneree
	 * @return le montant de l'impot du
	 */
	BigDecimal calculerImpot(AssietteFiscale taxBase);
}
