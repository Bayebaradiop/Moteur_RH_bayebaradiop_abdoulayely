package com.company.hrsettlement.settlement.domain.port;

/**
 * Port secondaire : notification de l'inspection du travail.
 * <p>
 * Interface volontairement reduite a une seule action (ISP) : l'audit est un effet
 * de bord, pas une source d'information pour le calcul.
 */
public interface PortInspectionTravail {

	/**
	 * Declenche un audit pour l'employe concerne.
	 *
	 * @param matriculeEmploye identifiant de l'employe a auditer
	 */
	void notifierAudit(String matriculeEmploye);
}
