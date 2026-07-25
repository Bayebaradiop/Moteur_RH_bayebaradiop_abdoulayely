package com.company.hrsettlement.mapper;

import com.company.hrsettlement.domain.DepartEmploye;
import com.company.hrsettlement.domain.Solde;
import com.company.hrsettlement.dto.request.SoldeRequeteDto;
import com.company.hrsettlement.dto.response.SoldeReponseDto;
import org.springframework.stereotype.Component;

/**
 * Traduit le contrat d'API en objets metier, et inversement.
 * <p>
 * Le mapping est isole ici : ni le controleur ni le service n'ecrivent de
 * conversion. Il est ainsi testable seul.
 */
@Component
public class SoldeConvertisseur {

	public DepartEmploye versDomaine(SoldeRequeteDto requete) {
		return new DepartEmploye(
				requete.matriculeEmploye(),
				requete.dateEmbauche(),
				requete.dateDepart(),
				requete.motifDepart(),
				requete.salaireBase(),
				requete.joursCongesRestants(),
				requete.preavisRespecte());
	}

	public SoldeReponseDto versReponse(Solde solde) {
		return new SoldeReponseDto(
				solde.matriculeEmploye(),
				solde.indemniteConges(),
				solde.primeAnciennete(),
				solde.penalitePreavis(),
				solde.montantBrut(),
				solde.montantImpot(),
				solde.montantNet(),
				solde.auditDeclenche());
	}
}
