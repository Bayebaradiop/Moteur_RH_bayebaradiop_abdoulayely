package com.company.hrsettlement.settlement.api.mapper;

import com.company.hrsettlement.settlement.api.dto.SoldeRequete;
import com.company.hrsettlement.settlement.api.dto.SoldeReponse;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.model.Solde;
import org.springframework.stereotype.Component;

/**
 * Traduit le contrat d'API en objets du domaine, et inversement.
 * <p>
 * Le mapping est isole ici : ni le controleur ni le service n'ecrivent de
 * conversion. Il est ainsi testable seul et concentre le point de rupture entre le
 * vocabulaire public et le vocabulaire metier.
 */
@Component
public class SoldeConvertisseur {

	public DepartEmploye versDomaine(SoldeRequete requete) {
		return new DepartEmploye(
				requete.matriculeEmploye(),
				requete.dateEmbauche(),
				requete.dateDepart(),
				requete.motifDepart(),
				requete.salaireBase(),
				requete.joursCongesRestants(),
				requete.preavisRespecte());
	}

	public SoldeReponse versReponse(Solde solde) {
		return new SoldeReponse(
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
