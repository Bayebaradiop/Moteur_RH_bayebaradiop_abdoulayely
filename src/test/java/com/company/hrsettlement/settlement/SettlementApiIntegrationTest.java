package com.company.hrsettlement.settlement;

import com.company.hrsettlement.settlement.infrastructure.persistence.SettlementHistoryJpaRepository;
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
 * Test bout en bout : controleur, service, moteur et adaptateurs reels sont
 * assembles par Spring. Il verifie le cablage complet, la ou les tests unitaires
 * verifient les regles.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Parcours complet de calcul du solde de tout compte")
class SettlementApiIntegrationTest {

	private static final String SETTLEMENTS_PATH = "/api/v1/settlements";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SettlementHistoryJpaRepository settlementHistoryJpaRepository;

	@Test
	@DisplayName("calcule un solde de demission en appliquant le bareme fiscal reel")
	void shouldCalculateResignationSettlementThroughTheWholeChain() throws Exception {
		// Conges : 5 x (3 000 000 / 21) = 714 285,70 ; impot : 84 285,70 a 20 %
		String request = """
				{
				  "employeeId": "EMP-001",
				  "hireDate": "2020-01-01",
				  "departureDate": "2026-01-01",
				  "departureReason": "RESIGNATION",
				  "baseSalary": 3000000,
				  "remainingLeaveDays": 5,
				  "noticeRespected": true
				}
				""";

		mockMvc.perform(post(SETTLEMENTS_PATH).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leaveCompensation").value(714285.70))
				.andExpect(jsonPath("$.seniorityBonus").value(0.00))
				.andExpect(jsonPath("$.noticePenalty").value(0.00))
				.andExpect(jsonPath("$.grossAmount").value(714285.70))
				.andExpect(jsonPath("$.taxAmount").value(16857.14))
				.andExpect(jsonPath("$.netAmount").value(697428.56))
				.andExpect(jsonPath("$.auditTriggered").value(false));
	}

	@Test
	@DisplayName("archive en base le solde calcule")
	void shouldPersistCalculatedSettlement() throws Exception {
		String request = """
				{
				  "employeeId": "EMP-900",
				  "hireDate": "2020-01-01",
				  "departureDate": "2026-01-01",
				  "departureReason": "RESIGNATION",
				  "baseSalary": 3000000,
				  "remainingLeaveDays": 5,
				  "noticeRespected": true
				}
				""";

		mockMvc.perform(post(SETTLEMENTS_PATH).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk());

		assertThat(settlementHistoryJpaRepository.findByEmployeeId("EMP-900"))
				.singleElement()
				.satisfies(archived -> {
					assertThat(archived.getNetAmount()).isEqualByComparingTo("697428.56");
					assertThat(archived.isAuditTriggered()).isFalse();
					assertThat(archived.getRecordedAt()).isNotNull();
				});
	}

	@Test
	@DisplayName("retient un mois de salaire et produit un net negatif sans preavis")
	void shouldProduceNegativeNetWhenNoticeIsNotRespected() throws Exception {
		String request = """
				{
				  "employeeId": "EMP-002",
				  "hireDate": "2022-01-01",
				  "departureDate": "2026-01-01",
				  "departureReason": "RESIGNATION",
				  "baseSalary": 500000,
				  "remainingLeaveDays": 2,
				  "noticeRespected": false
				}
				""";

		mockMvc.perform(post(SETTLEMENTS_PATH).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.noticePenalty").value(500000.00))
				.andExpect(jsonPath("$.grossAmount").value(-452380.96))
				.andExpect(jsonPath("$.taxAmount").value(0.00))
				.andExpect(jsonPath("$.netAmount").value(-452380.96));
	}

	@Test
	@DisplayName("declenche l'audit de l'inspection du travail sur un net eleve")
	void shouldTriggerAuditOnHighNetAmount() throws Exception {
		String request = """
				{
				  "employeeId": "EMP-003",
				  "hireDate": "2016-01-01",
				  "departureDate": "2026-01-01",
				  "departureReason": "RETIREMENT",
				  "baseSalary": 100000000,
				  "remainingLeaveDays": 0,
				  "noticeRespected": true
				}
				""";

		mockMvc.perform(post(SETTLEMENTS_PATH).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.seniorityBonus").value(125000000.00))
				.andExpect(jsonPath("$.taxAmount").value(47124000.00))
				.andExpect(jsonPath("$.netAmount").value(77876000.00))
				.andExpect(jsonPath("$.auditTriggered").value(true));
	}

	@Test
	@DisplayName("refuse un depart anterieur a l'embauche avec une erreur uniforme")
	void shouldRejectDepartureBeforeHireDate() throws Exception {
		String request = """
				{
				  "employeeId": "EMP-004",
				  "hireDate": "2025-01-01",
				  "departureDate": "2024-01-01",
				  "departureReason": "RESIGNATION",
				  "baseSalary": 500000,
				  "remainingLeaveDays": 0,
				  "noticeRespected": true
				}
				""";

		mockMvc.perform(post(SETTLEMENTS_PATH).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.path").value(SETTLEMENTS_PATH))
				.andExpect(jsonPath("$.message").value("La date de depart doit etre posterieure a la date d'embauche"));
	}
}
