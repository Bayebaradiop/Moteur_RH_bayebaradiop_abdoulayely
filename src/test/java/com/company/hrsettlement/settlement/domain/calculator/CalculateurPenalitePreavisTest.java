package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.company.hrsettlement.settlement.domain.ConstructeurDepart.unDepart;
import static com.company.hrsettlement.settlement.domain.model.MotifDepart.DEMISSION;
import static com.company.hrsettlement.settlement.domain.model.MotifDepart.RETRAITE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Penalite de preavis")
class CalculateurPenalitePreavisTest {

	private CalculateurPenalitePreavis calculateurPenalitePreavis;

	@BeforeEach
	void initialiser() {
		calculateurPenalitePreavis = new CalculateurPenalitePreavis();
	}

	@Test
	@DisplayName("retient un mois de salaire lorsqu'une demission ne respecte pas le preavis")
	void doitRetenirUnMoisDeSalaireSiPreavisNonRespecte() {
		DepartEmploye depart = unDepart()
				.pourMotif(DEMISSION)
				.avecPreavisRespecte(false)
				.avecSalaireMensuel("750000")
				.construire();

		BigDecimal penalty = calculateurPenalitePreavis.calculer(depart);

		assertThat(penalty).isEqualByComparingTo("750000");
	}

	@Test
	@DisplayName("n'applique aucune penalite lorsque le preavis est respecte")
	void neDoitPasPenaliserSiPreavisRespecte() {
		DepartEmploye depart = unDepart()
				.pourMotif(DEMISSION)
				.avecPreavisRespecte(true)
				.avecSalaireMensuel("750000")
				.construire();

		BigDecimal penalty = calculateurPenalitePreavis.calculer(depart);

		assertThat(penalty).isEqualByComparingTo("0");
	}

	@Test
	@DisplayName("n'applique aucune penalite en dehors d'une demission")
	void neDoitPasPenaliserHorsDemission() {
		DepartEmploye depart = unDepart()
				.pourMotif(RETRAITE)
				.avecPreavisRespecte(false)
				.avecSalaireMensuel("750000")
				.construire();

		BigDecimal penalty = calculateurPenalitePreavis.calculer(depart);

		assertThat(penalty).isEqualByComparingTo("0");
	}
}
