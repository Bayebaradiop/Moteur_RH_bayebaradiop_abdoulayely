package com.company.hrsettlement;

import com.company.hrsettlement.repository.HistoriqueSoldeDepot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test bout en bout : controleur, moteur, bareme fiscal et base de donnees reels
 * sont assembles par Spring. Il verifie le cablage complet, la ou les tests
 * unitaires verifient les regles.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Parcours complet de calcul du solde de tout compte")
class SoldeApiIntegrationTest {

	private static final String CHEMIN_SOLDES = "/api/v1/settlements";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private HistoriqueSoldeDepot historiqueSoldeDepot;

	@Test
	@DisplayName("calcule un solde de demission en appliquant le bareme fiscal reel")
	void doitCalculerUnSoldeDeDemissionDeBoutEnBout() throws Exception {
		// Conges : 5 x (3 000 000 / 21) = 714 285,70 ; impot : 84 285,70 a 20 %
		String requete = """
				{
				  "matriculeEmploye": "EMP-001",
				  "dateEmbauche": "2020-01-01",
				  "dateDepart": "2026-01-01",
				  "motifDepart": "DEMISSION",
				  "salaireBase": 3000000,
				  "joursCongesRestants": 5,
				  "preavisRespecte": true
				}
				""";

		mockMvc.perform(post(CHEMIN_SOLDES).contentType(MediaType.APPLICATION_JSON).content(requete))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.indemniteConges").value(714285.70))
				.andExpect(jsonPath("$.primeAnciennete").value(0.00))
				.andExpect(jsonPath("$.penalitePreavis").value(0.00))
				.andExpect(jsonPath("$.montantBrut").value(714285.70))
				.andExpect(jsonPath("$.montantImpot").value(16857.14))
				.andExpect(jsonPath("$.montantNet").value(697428.56))
				.andExpect(jsonPath("$.auditDeclenche").value(false));
	}

	@Test
	@DisplayName("archive en base le solde calcule")
	void doitArchiverLeSoldeEnBase() throws Exception {
		String requete = """
				{
				  "matriculeEmploye": "EMP-900",
				  "dateEmbauche": "2020-01-01",
				  "dateDepart": "2026-01-01",
				  "motifDepart": "DEMISSION",
				  "salaireBase": 3000000,
				  "joursCongesRestants": 5,
				  "preavisRespecte": true
				}
				""";

		mockMvc.perform(post(CHEMIN_SOLDES).contentType(MediaType.APPLICATION_JSON).content(requete))
				.andExpect(status().isOk());

		assertThat(historiqueSoldeDepot.findByMatriculeEmploye("EMP-900"))
				.singleElement()
				.satisfies(archive -> {
					assertThat(archive.getMontantNet()).isEqualByComparingTo("697428.56");
					assertThat(archive.isAuditDeclenche()).isFalse();
					assertThat(archive.getEnregistreLe()).isNotNull();
				});
	}

	@Test
	@DisplayName("retient un mois de salaire et produit un net negatif sans preavis")
	void doitProduireUnNetNegatifSansPreavis() throws Exception {
		String requete = """
				{
				  "matriculeEmploye": "EMP-002",
				  "dateEmbauche": "2022-01-01",
				  "dateDepart": "2026-01-01",
				  "motifDepart": "DEMISSION",
				  "salaireBase": 500000,
				  "joursCongesRestants": 2,
				  "preavisRespecte": false
				}
				""";

		mockMvc.perform(post(CHEMIN_SOLDES).contentType(MediaType.APPLICATION_JSON).content(requete))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.penalitePreavis").value(500000.00))
				.andExpect(jsonPath("$.montantBrut").value(-452380.96))
				.andExpect(jsonPath("$.montantImpot").value(0.00))
				.andExpect(jsonPath("$.montantNet").value(-452380.96));
	}

	@Test
	@DisplayName("declenche l'audit de l'inspection du travail sur un net eleve")
	void doitDeclencherLAuditSurUnNetEleve() throws Exception {
		String requete = """
				{
				  "matriculeEmploye": "EMP-003",
				  "dateEmbauche": "2016-01-01",
				  "dateDepart": "2026-01-01",
				  "motifDepart": "RETRAITE",
				  "salaireBase": 100000000,
				  "joursCongesRestants": 0,
				  "preavisRespecte": true
				}
				""";

		mockMvc.perform(post(CHEMIN_SOLDES).contentType(MediaType.APPLICATION_JSON).content(requete))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.primeAnciennete").value(125000000.00))
				.andExpect(jsonPath("$.montantImpot").value(47124000.00))
				.andExpect(jsonPath("$.montantNet").value(77876000.00))
				.andExpect(jsonPath("$.auditDeclenche").value(true));
	}

	@Test
	@DisplayName("refuse un depart anterieur a l'embauche avec une erreur uniforme")
	void doitRefuserUnDepartAvantEmbauche() throws Exception {
		String requete = """
				{
				  "matriculeEmploye": "EMP-004",
				  "dateEmbauche": "2025-01-01",
				  "dateDepart": "2024-01-01",
				  "motifDepart": "DEMISSION",
				  "salaireBase": 500000,
				  "joursCongesRestants": 0,
				  "preavisRespecte": true
				}
				""";

		mockMvc.perform(post(CHEMIN_SOLDES).contentType(MediaType.APPLICATION_JSON).content(requete))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.statut").value(400))
				.andExpect(jsonPath("$.erreur").value("Bad Request"))
				.andExpect(jsonPath("$.chemin").value(CHEMIN_SOLDES))
				.andExpect(jsonPath("$.message").value("La date de depart doit etre posterieure a la date d'embauche"));
	}
}
