package com.company.hrsettlement.settlement.domain.calculator;

import java.util.Objects;

/**
 * Regroupe les calculateurs mobilises par le moteur.
 * <p>
 * Ce parametre-objet garde le constructeur du moteur lisible tout en preservant
 * l'injection de chaque calculateur : le moteur reste ouvert au remplacement d'une
 * regle sans en connaitre l'implementation.
 *
 * @param leave          indemnite de conges non pris
 * @param seniorityBonus prime d'anciennete
 * @param noticePenalty  penalite de preavis
 * @param gross          consolidation du brut
 * @param taxBase        repartition de l'assiette fiscale
 */
public record SettlementCalculators(
		LeaveCalculator leave,
		SeniorityBonusCalculator seniorityBonus,
		NoticePenaltyCalculator noticePenalty,
		GrossCalculator gross,
		TaxBaseCalculator taxBase) {

	public SettlementCalculators {
		Objects.requireNonNull(leave, "leave est obligatoire");
		Objects.requireNonNull(seniorityBonus, "seniorityBonus est obligatoire");
		Objects.requireNonNull(noticePenalty, "noticePenalty est obligatoire");
		Objects.requireNonNull(gross, "gross est obligatoire");
		Objects.requireNonNull(taxBase, "taxBase est obligatoire");
	}
}
