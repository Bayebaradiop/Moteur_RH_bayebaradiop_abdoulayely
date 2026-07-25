package com.company.hrsettlement.settlement.domain.calculator;

import com.company.hrsettlement.settlement.domain.model.AssietteFiscale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Assiette fiscale")
class CalculateurAssietteFiscaleTest {

	private CalculateurAssietteFiscale calculateurAssietteFiscale;

	@BeforeEach
	void initialiser() {
		calculateurAssietteFiscale = new CalculateurAssietteFiscale();
	}

	@Test
	@DisplayName("exonere integralement une prime inferieure au plafond")
	void doitExonererToutePrimeSousLePlafond() {
		AssietteFiscale taxBase = calculateurAssietteFiscale.calculer(
				new BigDecimal("6000000"),
				new BigDecimal("2000000"));

		assertThat(taxBase.montantExonere()).isEqualByComparingTo("2000000");
		assertThat(taxBase.montantImposable()).isEqualByComparingTo("4000000");
	}

	@Test
	@DisplayName("plafonne l'exoneration a cinq millions et impose le surplus")
	void doitPlafonnerLExoneration() {
		AssietteFiscale taxBase = calculateurAssietteFiscale.calculer(
				new BigDecimal("12000000"),
				new BigDecimal("8000000"));

		assertThat(taxBase.montantExonere()).isEqualByComparingTo("5000000");
		assertThat(taxBase.montantImposable()).isEqualByComparingTo("7000000");
	}

	@Test
	@DisplayName("exonere encore la totalite d'une prime egale au plafond")
	void doitExonererUnePrimeEgaleAuPlafond() {
		// Valeur limite : 5 000 000 exactement reste integralement exonere
		AssietteFiscale taxBase = calculateurAssietteFiscale.calculer(
				new BigDecimal("9000000"),
				new BigDecimal("5000000"));

		assertThat(taxBase.montantExonere()).isEqualByComparingTo("5000000");
		assertThat(taxBase.montantImposable()).isEqualByComparingTo("4000000");
	}

	@Test
	@DisplayName("n'expose aucune assiette imposable negative lorsque le brut est negatif")
	void neDoitPasProduireUneAssietteNegative() {
		AssietteFiscale taxBase = calculateurAssietteFiscale.calculer(
				new BigDecimal("-700000"),
				BigDecimal.ZERO);

		assertThat(taxBase.montantExonere()).isEqualByComparingTo("0");
		assertThat(taxBase.montantImposable()).isEqualByComparingTo("0");
	}
}
