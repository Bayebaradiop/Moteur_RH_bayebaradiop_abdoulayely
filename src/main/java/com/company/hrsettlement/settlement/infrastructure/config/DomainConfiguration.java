package com.company.hrsettlement.settlement.infrastructure.config;

import com.company.hrsettlement.settlement.domain.calculator.GrossCalculator;
import com.company.hrsettlement.settlement.domain.calculator.LeaveCalculator;
import com.company.hrsettlement.settlement.domain.calculator.NoticePenaltyCalculator;
import com.company.hrsettlement.settlement.domain.calculator.SeniorityBonusCalculator;
import com.company.hrsettlement.settlement.domain.calculator.SettlementCalculators;
import com.company.hrsettlement.settlement.domain.calculator.TaxBaseCalculator;
import com.company.hrsettlement.settlement.domain.engine.SettlementEngine;
import com.company.hrsettlement.settlement.domain.port.LabourInspectionPort;
import com.company.hrsettlement.settlement.domain.port.TaxAdministrationPort;
import com.company.hrsettlement.settlement.domain.validator.DepartureRule;
import com.company.hrsettlement.settlement.domain.validator.DepartureValidator;
import com.company.hrsettlement.settlement.domain.validator.rule.DatesConsistencyRule;
import com.company.hrsettlement.settlement.domain.validator.rule.NonNegativeLeaveDaysRule;
import com.company.hrsettlement.settlement.domain.validator.rule.PositiveSalaryRule;
import com.company.hrsettlement.settlement.domain.validator.rule.RetirementSeniorityRule;
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
public class DomainConfiguration {

	@Bean
	public DepartureRule datesConsistencyRule() {
		return new DatesConsistencyRule();
	}

	@Bean
	public DepartureRule positiveSalaryRule() {
		return new PositiveSalaryRule();
	}

	@Bean
	public DepartureRule nonNegativeLeaveDaysRule() {
		return new NonNegativeLeaveDaysRule();
	}

	@Bean
	public DepartureRule retirementSeniorityRule() {
		return new RetirementSeniorityRule();
	}

	@Bean
	public DepartureValidator departureValidator(List<DepartureRule> departureRules) {
		return new DepartureValidator(departureRules);
	}

	@Bean
	public SettlementCalculators settlementCalculators() {
		return new SettlementCalculators(
				new LeaveCalculator(),
				new SeniorityBonusCalculator(),
				new NoticePenaltyCalculator(),
				new GrossCalculator(),
				new TaxBaseCalculator());
	}

	@Bean
	public SettlementEngine settlementEngine(DepartureValidator departureValidator,
	                                         SettlementCalculators settlementCalculators,
	                                         TaxAdministrationPort taxAdministrationPort,
	                                         LabourInspectionPort labourInspectionPort) {
		return new SettlementEngine(
				departureValidator, settlementCalculators, taxAdministrationPort, labourInspectionPort);
	}
}
