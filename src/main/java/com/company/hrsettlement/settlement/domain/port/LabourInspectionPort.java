package com.company.hrsettlement.settlement.domain.port;

/**
 * Port secondaire : notification de l'inspection du travail.
 * <p>
 * Interface volontairement reduite a une seule action (ISP) : l'audit est un effet
 * de bord, pas une source d'information pour le calcul.
 */
public interface LabourInspectionPort {

	/**
	 * Declenche un audit pour l'employe concerne.
	 *
	 * @param employeeId identifiant de l'employe a auditer
	 */
	void notifyAudit(String employeeId);
}
