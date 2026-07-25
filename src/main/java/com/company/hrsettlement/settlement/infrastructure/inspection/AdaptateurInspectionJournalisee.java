package com.company.hrsettlement.settlement.infrastructure.inspection;

import com.company.hrsettlement.settlement.domain.port.PortInspectionTravail;
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
public class AdaptateurInspectionJournalisee implements PortInspectionTravail {

	private static final Logger JOURNAL = LoggerFactory.getLogger(AdaptateurInspectionJournalisee.class);

	@Override
	public void notifierAudit(String matriculeEmploye) {
		JOURNAL.warn("Audit de l'inspection du travail declenche pour l'employe {}", matriculeEmploye);
	}
}
