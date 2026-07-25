package com.company.hrsettlement.settlement.infrastructure.inspection;

import com.company.hrsettlement.settlement.domain.port.LabourInspectionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptateur secondaire : trace la demande d'audit.
 * <p>
 * Implementation de reference, substituable par un envoi de courriel ou un appel au
 * systeme de l'inspection du travail sans aucun impact sur le moteur.
 */
@Component
public class LoggingLabourInspectionAdapter implements LabourInspectionPort {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingLabourInspectionAdapter.class);

	@Override
	public void notifyAudit(String employeeId) {
		LOGGER.warn("Audit de l'inspection du travail declenche pour l'employe {}", employeeId);
	}
}
