package com.company.hrsettlement.service;

import com.company.hrsettlement.domain.DepartEmploye;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.company.hrsettlement.domain.ConstructeurDepart.unDepart;
import static com.company.hrsettlement.domain.MotifDepart.DEMISSION;
import static com.company.hrsettlement.domain.MotifDepart.FAUTE_GRAVE;
import static com.company.hrsettlement.domain.MotifDepart.LICENCIEMENT_ECONOMIQUE;
import static com.company.hrsettlement.domain.MotifDepart.RETRAITE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Prime d'anciennete")
class CalculateurPrimeAncienneteTest {

	private CalculateurPrimeAnciennete calculateurPrimeAnciennete;

	@BeforeEach
	void initialiser() {
		calculateurPrimeAnciennete = new CalculateurPrimeAnciennete();
	}

	@Test
	@DisplayName("verse 10 % du salaire par annee pour les premieres annees")
	void doitVerserDixPourCentParAnneePourLesPremieresAnnees() {
		DepartEmploye depart = departDe(RETRAITE, LocalDate.of(2023, 1, 1));

		BigDecimal prime = calculateurPrimeAnciennete.calculer(depart);

		assertThat(prime).isEqualByComparingTo("300000");
	}

	@Test
	@DisplayName("applique encore le taux de base a exactement cinq annees")
	void doitAppliquerLeTauxDeBaseAExactementCinqAnnees() {
		// Valeur limite : 5 annees pleines restent integralement a 10 %
		DepartEmploye depart = departDe(RETRAITE, LocalDate.of(2021, 1, 1));

		BigDecimal prime = calculateurPrimeAnciennete.calculer(depart);

		assertThat(prime).isEqualByComparingTo("500000");
	}

	@Test
	@DisplayName("majore la premiere annee suivant le seuil de cinq ans")
	void doitMajorerDesLaSixiemeAnnee() {
		// Valeur limite : 6 annees = 5 x 10 % + 1 x 15 %
		DepartEmploye depart = departDe(RETRAITE, LocalDate.of(2020, 1, 1));

		BigDecimal prime = calculateurPrimeAnciennete.calculer(depart);

		assertThat(prime).isEqualByComparingTo("650000");
	}

	@Test
	@DisplayName("majore a 15 % chaque annee au-dela de la cinquieme")
	void doitVerserQuinzePourCentAuDelaDeLaCinquiemeAnnee() {
		// 10 annees : 5 x 10 % + 5 x 15 % = 125 % du salaire
		DepartEmploye depart = departDe(RETRAITE, LocalDate.of(2016, 1, 1));

		BigDecimal prime = calculateurPrimeAnciennete.calculer(depart);

		assertThat(prime).isEqualByComparingTo("1250000");
	}

	@Test
	@DisplayName("ouvre le droit a la prime en cas de licenciement economique")
	void doitVerserLaPrimeEnCasDeLicenciementEconomique() {
		DepartEmploye depart = departDe(LICENCIEMENT_ECONOMIQUE, LocalDate.of(2023, 1, 1));

		BigDecimal prime = calculateurPrimeAnciennete.calculer(depart);

		assertThat(prime).isEqualByComparingTo("300000");
	}

	@Test
	@DisplayName("refuse la prime en cas de demission, quelle que soit l'anciennete")
	void neDoitPasVerserLaPrimeEnCasDeDemission() {
		DepartEmploye depart = departDe(DEMISSION, LocalDate.of(2006, 1, 1));

		BigDecimal prime = calculateurPrimeAnciennete.calculer(depart);

		assertThat(prime).isEqualByComparingTo("0");
	}

	@Test
	@DisplayName("refuse la prime en cas de faute grave, quelle que soit l'anciennete")
	void neDoitPasVerserLaPrimeEnCasDeFauteGrave() {
		DepartEmploye depart = departDe(FAUTE_GRAVE, LocalDate.of(2006, 1, 1));

		BigDecimal prime = calculateurPrimeAnciennete.calculer(depart);

		assertThat(prime).isEqualByComparingTo("0");
	}

	private DepartEmploye departDe(com.company.hrsettlement.domain.MotifDepart motif, LocalDate dateEmbauche) {
		return unDepart()
				.pourMotif(motif)
				.embaucheLe(dateEmbauche)
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("1000000")
				.construire();
	}
}
