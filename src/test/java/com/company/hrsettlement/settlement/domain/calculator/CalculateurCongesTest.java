package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.company.hrsettlement.settlement.domain.ConstructeurDepart.unDepart;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Indemnite de conges non pris")
class CalculateurCongesTest {

	private CalculateurConges calculateurConges;

	@BeforeEach
	void initialiser() {
		calculateurConges = new CalculateurConges();
	}

	@Test
	@DisplayName("multiplie les jours restants par la valeur d'une journee de travail")
	void doitIndemniserLesJoursRestantsAuSalaireJournalier() {
		// 1 050 000 / 21 = 50 000 par jour, pour 10 jours restants
		DepartEmploye depart = unDepart()
				.avecSalaireMensuel("1050000")
				.avecJoursCongesRestants(10)
				.construire();

		BigDecimal compensation = calculateurConges.calculer(depart);

		assertThat(compensation).isEqualByComparingTo("500000");
	}

	@Test
	@DisplayName("ne verse aucune indemnite lorsque tous les conges ont ete pris")
	void doitRetournerZeroSansJourDeCongeRestant() {
		DepartEmploye depart = unDepart()
				.avecSalaireMensuel("1050000")
				.avecJoursCongesRestants(0)
				.construire();

		BigDecimal compensation = calculateurConges.calculer(depart);

		assertThat(compensation).isEqualByComparingTo("0");
	}

	@Test
	@DisplayName("arrondit la valeur journaliere au centime superieur le plus proche")
	void doitArrondirLeSalaireJournalierADeuxDecimales() {
		// 1 000 000 / 21 = 47 619,047... arrondi a 47 619,05 puis multiplie par 3
		DepartEmploye depart = unDepart()
				.avecSalaireMensuel("1000000")
				.avecJoursCongesRestants(3)
				.construire();

		BigDecimal compensation = calculateurConges.calculer(depart);

		assertThat(compensation).isEqualByComparingTo("142857.15");
	}
}
