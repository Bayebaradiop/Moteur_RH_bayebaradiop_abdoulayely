package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.company.hrsettlement.settlement.domain.ConstructeurDepart.unDepart;
import static com.company.hrsettlement.settlement.domain.model.MotifDepart.LICENCIEMENT_ECONOMIQUE;
import static com.company.hrsettlement.settlement.domain.model.MotifDepart.DEMISSION;
import static com.company.hrsettlement.settlement.domain.model.MotifDepart.RETRAITE;
import static com.company.hrsettlement.settlement.domain.model.MotifDepart.FAUTE_GRAVE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Prime d'anciennete")
class CalculateurPrimeAncienneteTest {

	private CalculateurPrimeAnciennete calculateurPrimeAnciennete;

	@BeforeEach
	void initialiser() {
		calculateurPrimeAnciennete = new CalculateurPrimeAnciennete();
	}

	@Test
	@DisplayName("verse 10 % du salaire par annee pour les premieres annees d'anciennete")
	void doitVerserDixPourCentParAnneePourLesPremieresAnnees() {
		// 3 annees d'anciennete a 10 % : 1 000 000 x 0,30
		DepartEmploye depart = unDepart()
				.pourMotif(RETRAITE)
				.embaucheLe(LocalDate.of(2023, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("1000000")
				.construire();

		BigDecimal bonus = calculateurPrimeAnciennete.calculer(depart);

		assertThat(bonus).isEqualByComparingTo("300000");
	}

	@Test
	@DisplayName("applique encore le taux de base a exactement cinq annees d'anciennete")
	void doitAppliquerLeTauxDeBaseAExactementCinqAnnees() {
		// Valeur limite : 5 annees pleines restent integralement a 10 %
		DepartEmploye depart = unDepart()
				.pourMotif(RETRAITE)
				.embaucheLe(LocalDate.of(2021, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("1000000")
				.construire();

		BigDecimal bonus = calculateurPrimeAnciennete.calculer(depart);

		assertThat(bonus).isEqualByComparingTo("500000");
	}

	@Test
	@DisplayName("majore a 15 % chaque annee au-dela de la cinquieme")
	void doitVerserQuinzePourCentAuDelaDeLaCinquiemeAnnee() {
		// 10 annees : 5 x 10 % + 5 x 15 % = 125 % du salaire
		DepartEmploye depart = unDepart()
				.pourMotif(RETRAITE)
				.embaucheLe(LocalDate.of(2016, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("1000000")
				.construire();

		BigDecimal bonus = calculateurPrimeAnciennete.calculer(depart);

		assertThat(bonus).isEqualByComparingTo("1250000");
	}

	@Test
	@DisplayName("majore la premiere annee suivant le seuil de cinq ans")
	void doitMajorerDesLaSixiemeAnnee() {
		// Valeur limite : 6 annees = 5 x 10 % + 1 x 15 %
		DepartEmploye depart = unDepart()
				.pourMotif(RETRAITE)
				.embaucheLe(LocalDate.of(2020, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("1000000")
				.construire();

		BigDecimal bonus = calculateurPrimeAnciennete.calculer(depart);

		assertThat(bonus).isEqualByComparingTo("650000");
	}

	@Test
	@DisplayName("ouvre le droit a la prime en cas de licenciement economique")
	void doitVerserLaPrimeEnCasDeLicenciementEconomique() {
		DepartEmploye depart = unDepart()
				.pourMotif(LICENCIEMENT_ECONOMIQUE)
				.embaucheLe(LocalDate.of(2023, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("1000000")
				.construire();

		BigDecimal bonus = calculateurPrimeAnciennete.calculer(depart);

		assertThat(bonus).isEqualByComparingTo("300000");
	}

	@Test
	@DisplayName("refuse la prime en cas de demission, quelle que soit l'anciennete")
	void neDoitPasVerserLaPrimeEnCasDeDemission() {
		DepartEmploye depart = unDepart()
				.pourMotif(DEMISSION)
				.embaucheLe(LocalDate.of(2006, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("1000000")
				.construire();

		BigDecimal bonus = calculateurPrimeAnciennete.calculer(depart);

		assertThat(bonus).isEqualByComparingTo("0");
	}

	@Test
	@DisplayName("refuse la prime en cas de faute grave, quelle que soit l'anciennete")
	void neDoitPasVerserLaPrimeEnCasDeFauteGrave() {
		DepartEmploye depart = unDepart()
				.pourMotif(FAUTE_GRAVE)
				.embaucheLe(LocalDate.of(2006, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("1000000")
				.construire();

		BigDecimal bonus = calculateurPrimeAnciennete.calculer(depart);

		assertThat(bonus).isEqualByComparingTo("0");
	}
}
