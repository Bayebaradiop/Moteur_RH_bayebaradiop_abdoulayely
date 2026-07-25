package com.company.hrsettlement.settlement.api.mapper;

import com.company.hrsettlement.settlement.api.dto.SoldeRequete;
import com.company.hrsettlement.settlement.api.dto.SoldeReponse;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.model.Solde;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.company.hrsettlement.settlement.domain.model.MotifDepart.LICENCIEMENT_ECONOMIQUE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Mapping entre le contrat d'API et le domaine")
class SoldeConvertisseurTest {

	private SoldeConvertisseur soldeConvertisseur;

	@BeforeEach
	void initialiser() {
		soldeConvertisseur = new SoldeConvertisseur();
	}

	@Test
	@DisplayName("convertit la requete en depart d'employe")
	void doitConvertirLaRequeteVersLeDomaine() {
		SoldeRequete requete = new SoldeRequete(
				"EMP-042",
				LocalDate.of(2015, 3, 1),
				LocalDate.of(2026, 3, 1),
				LICENCIEMENT_ECONOMIQUE,
				new BigDecimal("900000"),
				12,
				false);

		DepartEmploye depart = soldeConvertisseur.versDomaine(requete);

		assertThat(depart.matriculeEmploye()).isEqualTo("EMP-042");
		assertThat(depart.dateEmbauche()).isEqualTo(LocalDate.of(2015, 3, 1));
		assertThat(depart.dateDepart()).isEqualTo(LocalDate.of(2026, 3, 1));
		assertThat(depart.motifDepart()).isEqualTo(LICENCIEMENT_ECONOMIQUE);
		assertThat(depart.salaireBase()).isEqualByComparingTo("900000");
		assertThat(depart.joursCongesRestants()).isEqualTo(12);
		assertThat(depart.preavisRespecte()).isFalse();
	}

	@Test
	@DisplayName("convertit le solde calcule en reponse d'API")
	void doitConvertirLeSoldeVersLaReponse() {
		Solde solde = new Solde(
				"EMP-042",
				new BigDecimal("500000.00"),
				new BigDecimal("300000.00"),
				new BigDecimal("0.00"),
				new BigDecimal("800000.00"),
				new BigDecimal("80000.00"),
				new BigDecimal("720000.00"),
				true);

		SoldeReponse reponse = soldeConvertisseur.versReponse(solde);

		assertThat(reponse.matriculeEmploye()).isEqualTo("EMP-042");
		assertThat(reponse.indemniteConges()).isEqualByComparingTo("500000.00");
		assertThat(reponse.primeAnciennete()).isEqualByComparingTo("300000.00");
		assertThat(reponse.penalitePreavis()).isEqualByComparingTo("0.00");
		assertThat(reponse.montantBrut()).isEqualByComparingTo("800000.00");
		assertThat(reponse.montantImpot()).isEqualByComparingTo("80000.00");
		assertThat(reponse.montantNet()).isEqualByComparingTo("720000.00");
		assertThat(reponse.auditDeclenche()).isTrue();
	}
}
