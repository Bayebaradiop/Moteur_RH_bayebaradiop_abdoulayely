package com.company.hrsettlement.service;

/**
 * Notification de l'inspection du travail.
 * <p>
 * Reduite a une seule action : l'audit est un effet de bord, pas une source
 * d'information pour le calcul.
 */
public interface InspectionTravail {

	/**
	 * Declenche un audit pour l'employe concerne.
	 *
	 * @param matriculeEmploye identifiant de l'employe a auditer
	 */
	void notifierAudit(String matriculeEmploye);
}
