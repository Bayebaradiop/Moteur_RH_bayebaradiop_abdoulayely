package com.company.hrsettlement.settlement.infrastructure.config;

import com.company.hrsettlement.settlement.domain.calculator.CalculateurBrut;
import com.company.hrsettlement.settlement.domain.calculator.CalculateurConges;
import com.company.hrsettlement.settlement.domain.calculator.CalculateurPenalitePreavis;
import com.company.hrsettlement.settlement.domain.calculator.CalculateurPrimeAnciennete;
import com.company.hrsettlement.settlement.domain.calculator.CalculateursSolde;
import com.company.hrsettlement.settlement.domain.calculator.CalculateurAssietteFiscale;
import com.company.hrsettlement.settlement.domain.engine.MoteurSolde;
import com.company.hrsettlement.settlement.domain.port.PortInspectionTravail;
import com.company.hrsettlement.settlement.domain.port.PortAdministrationFiscale;
import com.company.hrsettlement.settlement.domain.validator.RegleDepart;
import com.company.hrsettlement.settlement.domain.validator.ValidateurDepart;
import com.company.hrsettlement.settlement.domain.validator.rule.RegleCoherenceDates;
import com.company.hrsettlement.settlement.domain.validator.rule.RegleJoursCongesNonNegatifs;
import com.company.hrsettlement.settlement.domain.validator.rule.RegleSalairePositif;
import com.company.hrsettlement.settlement.domain.validator.rule.RegleAncienneteRetraite;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Cablage du domaine.
 * <p>
 * Le domaine ne porte aucune annotation Spring : c'est ici, dans l'infrastructure,
 * qu'il est instancie. Le choix du conteneur d'injection reste ainsi une decision
 * technique, revocable sans toucher au metier.
 * <p>
 * Les regles de validation sont declarees individuellement : Spring les rassemble
 * dans la liste injectee au validateur. Ajouter une regle consiste donc a declarer
 * un bean de plus, sans modifier une seule classe existante.
 */
@Configuration
public class ConfigurationDomaine {

	@Bean
	public RegleDepart regleCoherenceDates() {
		return new RegleCoherenceDates();
	}

	@Bean
	public RegleDepart regleSalairePositif() {
		return new RegleSalairePositif();
	}

	@Bean
	public RegleDepart regleJoursCongesNonNegatifs() {
		return new RegleJoursCongesNonNegatifs();
	}

	@Bean
	public RegleDepart regleAncienneteRetraite() {
		return new RegleAncienneteRetraite();
	}

	@Bean
	public ValidateurDepart validateurDepart(List<RegleDepart> reglesDepart) {
		return new ValidateurDepart(reglesDepart);
	}

	@Bean
	public CalculateursSolde calculateursSolde() {
		return new CalculateursSolde(
				new CalculateurConges(),
				new CalculateurPrimeAnciennete(),
				new CalculateurPenalitePreavis(),
				new CalculateurBrut(),
				new CalculateurAssietteFiscale());
	}

	@Bean
	public MoteurSolde moteurSolde(ValidateurDepart validateurDepart,
	                                         CalculateursSolde calculateursSolde,
	                                         PortAdministrationFiscale portAdministrationFiscale,
	                                         PortInspectionTravail portInspectionTravail) {
		return new MoteurSolde(
				validateurDepart, calculateursSolde, portAdministrationFiscale, portInspectionTravail);
	}
}
