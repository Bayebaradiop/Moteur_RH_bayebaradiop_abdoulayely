package com.company.hrsettlement.settlement.api;

import com.company.hrsettlement.settlement.api.mapper.SoldeConvertisseur;
import com.company.hrsettlement.settlement.application.CalculSoldeCasUsage;
import com.company.hrsettlement.settlement.domain.exception.ExceptionDatesInvalides;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.model.Solde;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SoldeControleur.class)
@Import(SoldeConvertisseur.class)
@DisplayName("API de calcul du solde de tout compte")
class SoldeControleurTest {

	private static final String CHEMIN_SOLDES = "/api/v1/settlements";

	private static final String REQUETE_VALIDE = """
			{
			  "matriculeEmploye": "EMP-001",
			  "dateEmbauche": "2016-01-01",
			  "dateDepart": "2026-01-01",
			  "motifDepart": "RETRAITE",
			  "salaireBase": 1050000,
			  "joursCongesRestants": 10,
			  "preavisRespecte": true
			}
			""";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CalculSoldeCasUsage calculSoldeCasUsage;

	@Test
	@DisplayName("retourne 200 et le detail du solde calcule")
	void doitRetournerLeSoldeCalcule() throws Exception {
		when(calculSoldeCasUsage.calculer(any(DepartEmploye.class))).thenReturn(new Solde(
				"EMP-001",
				new BigDecimal("500000.00"),
				new BigDecimal("1312500.00"),
				new BigDecimal("0.00"),
				new BigDecimal("1812500.00"),
				new BigDecimal("200000.00"),
				new BigDecimal("1612500.00"),
				false));

		mockMvc.perform(post(CHEMIN_SOLDES)
						.contentType(MediaType.APPLICATION_JSON)
						.content(REQUETE_VALIDE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.matriculeEmploye").value("EMP-001"))
				.andExpect(jsonPath("$.indemniteConges").value(500000.00))
				.andExpect(jsonPath("$.primeAnciennete").value(1312500.00))
				.andExpect(jsonPath("$.montantBrut").value(1812500.00))
				.andExpect(jsonPath("$.montantImpot").value(200000.00))
				.andExpect(jsonPath("$.montantNet").value(1612500.00))
				.andExpect(jsonPath("$.auditDeclenche").value(false));
	}

	@Test
	@DisplayName("retourne 400 lorsque le contrat d'entree n'est pas respecte")
	void doitRetournerErreurSiLeContratEstViole() throws Exception {
		String salaireNegatif = REQUETE_VALIDE.replace("1050000", "-5");

		mockMvc.perform(post(CHEMIN_SOLDES)
						.contentType(MediaType.APPLICATION_JSON)
						.content(salaireNegatif))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.statut").value(400))
				.andExpect(jsonPath("$.erreur").value("Bad Request"))
				.andExpect(jsonPath("$.chemin").value(CHEMIN_SOLDES))
				.andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("salaire")));
	}

	@Test
	@DisplayName("retourne 400 et un message metier lorsqu'une regle du domaine est violee")
	void doitRetournerErreurSiUneRegleMetierEstViolee() throws Exception {
		when(calculSoldeCasUsage.calculer(any(DepartEmploye.class)))
				.thenThrow(new ExceptionDatesInvalides("La date de depart doit etre posterieure a la date d'embauche"));

		mockMvc.perform(post(CHEMIN_SOLDES)
						.contentType(MediaType.APPLICATION_JSON)
						.content(REQUETE_VALIDE))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.statut").value(400))
				.andExpect(jsonPath("$.message")
						.value("La date de depart doit etre posterieure a la date d'embauche"))
				.andExpect(jsonPath("$.horodatage").exists());
	}

	@Test
	@DisplayName("retourne 400 lorsque le motif de depart est inconnu")
	void doitRetournerErreurSiLeMotifEstInconnu() throws Exception {
		String motifInconnu = REQUETE_VALIDE.replace("RETRAITE", "VACANCES");

		mockMvc.perform(post(CHEMIN_SOLDES)
						.contentType(MediaType.APPLICATION_JSON)
						.content(motifInconnu))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.statut").value(400));
	}
}
