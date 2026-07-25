package com.company.hrsettlement.service;

import com.company.hrsettlement.domain.AssietteFiscale;

import java.math.BigDecimal;

/**
 * Calcul de l'impot par l'administration fiscale.
 * <p>
 * Interface et non classe concrete : le bareme fiscal evolue independamment des
 * regles RH, et les tests du moteur la remplacent par un mock.
 */
public interface AdministrationFiscale {

	/**
	 * @param assietteFiscale repartition entre part imposable et part exoneree
	 * @return le montant de l'impot du
	 */
	BigDecimal calculerImpot(AssietteFiscale assietteFiscale);
}
