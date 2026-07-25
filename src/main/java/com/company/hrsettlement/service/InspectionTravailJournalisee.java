package com.company.hrsettlement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Trace la demande d'audit dans les journaux.
 * <p>
 * Implementation de reference, substituable par un envoi de courriel ou un appel
 * au systeme de l'inspection du travail.
 */
@Service
public class InspectionTravailJournalisee implements InspectionTravail {

	private static final Logger JOURNAL = LoggerFactory.getLogger(InspectionTravailJournalisee.class);

	@Override
	public void notifierAudit(String matriculeEmploye) {
		JOURNAL.warn("Audit de l'inspection du travail declenche pour l'employe {}", matriculeEmploye);
	}
}
