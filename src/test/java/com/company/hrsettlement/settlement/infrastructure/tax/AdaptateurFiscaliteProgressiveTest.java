package com.company.hrsettlement.settlement.infrastructure.tax;

import com.company.hrsettlement.settlement.domain.model.AssietteFiscale;
import com.company.hrsettlement.settlement.domain.port.PortAdministrationFiscale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Bareme fiscal progressif")
class AdaptateurFiscaliteProgressiveTest {

	private PortAdministrationFiscale administrationFiscale;

	@BeforeEach
	void initialiser() {
		administrationFiscale = new AdaptateurFiscaliteProgressive();
	}

	@Test
	@DisplayName("n'impose pas un revenu contenu dans la tranche exoneree")
	void neDoitPasImposerLaTrancheExoneree() {
		BigDecimal impot = administrationFiscale.calculerImpot(new AssietteFiscale(new BigDecimal("500000"), BigDecimal.ZERO));

		assertThat(impot).isEqualByComparingTo("0");
	}

	@Test
	@DisplayName("applique successivement le taux de chaque tranche traversee")
	void doitAppliquerLeTauxDeChaqueTrancheTraversee() {
		// 630 000 a 0 % + 870 000 a 20 % + 500 000 a 30 %
		BigDecimal impot = administrationFiscale.calculerImpot(new AssietteFiscale(new BigDecimal("2000000"), BigDecimal.ZERO));

		assertThat(impot).isEqualByComparingTo("324000.00");
	}

	@Test
	@DisplayName("applique le taux marginal le plus eleve a la fraction superieure")
	void doitAppliquerLeTauxMarginalLePlusEleve() {
		BigDecimal impot = administrationFiscale.calculerImpot(new AssietteFiscale(new BigDecimal("10000000"), BigDecimal.ZERO));

		assertThat(impot).isEqualByComparingTo("3124000.00");
	}

	@Test
	@DisplayName("n'impose jamais la part exoneree transmise par le moteur")
	void neDoitJamaisImposerLaPartExoneree() {
		BigDecimal impot = administrationFiscale.calculerImpot(new AssietteFiscale(BigDecimal.ZERO, new BigDecimal("5000000")));

		assertThat(impot).isEqualByComparingTo("0");
	}
}
