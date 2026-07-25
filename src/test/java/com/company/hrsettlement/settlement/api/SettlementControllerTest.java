package com.company.hrsettlement.settlement.api;

import com.company.hrsettlement.settlement.api.mapper.SettlementMapper;
import com.company.hrsettlement.settlement.application.SettlementUseCase;
import com.company.hrsettlement.settlement.domain.exception.InvalidDatesException;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Settlement;
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

@WebMvcTest(SettlementController.class)
@Import(SettlementMapper.class)
@DisplayName("API de calcul du solde de tout compte")
class SettlementControllerTest {

	private static final String SETTLEMENTS_PATH = "/api/v1/settlements";

	private static final String VALID_REQUEST = """
			{
			  "employeeId": "EMP-001",
			  "hireDate": "2016-01-01",
			  "departureDate": "2026-01-01",
			  "departureReason": "RETIREMENT",
			  "baseSalary": 1050000,
			  "remainingLeaveDays": 10,
			  "noticeRespected": true
			}
			""";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SettlementUseCase settlementUseCase;

	@Test
	@DisplayName("retourne 200 et le detail du solde calcule")
	void shouldReturnCalculatedSettlement() throws Exception {
		when(settlementUseCase.calculate(any(EmployeeDeparture.class))).thenReturn(new Settlement(
				"EMP-001",
				new BigDecimal("500000.00"),
				new BigDecimal("1312500.00"),
				new BigDecimal("0.00"),
				new BigDecimal("1812500.00"),
				new BigDecimal("200000.00"),
				new BigDecimal("1612500.00"),
				false));

		mockMvc.perform(post(SETTLEMENTS_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_REQUEST))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.employeeId").value("EMP-001"))
				.andExpect(jsonPath("$.leaveCompensation").value(500000.00))
				.andExpect(jsonPath("$.seniorityBonus").value(1312500.00))
				.andExpect(jsonPath("$.grossAmount").value(1812500.00))
				.andExpect(jsonPath("$.taxAmount").value(200000.00))
				.andExpect(jsonPath("$.netAmount").value(1612500.00))
				.andExpect(jsonPath("$.auditTriggered").value(false));
	}

	@Test
	@DisplayName("retourne 400 lorsque le contrat d'entree n'est pas respecte")
	void shouldReturnBadRequestWhenPayloadViolatesContract() throws Exception {
		String salaryIsNegative = VALID_REQUEST.replace("1050000", "-5");

		mockMvc.perform(post(SETTLEMENTS_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(salaryIsNegative))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.path").value(SETTLEMENTS_PATH))
				.andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("salaire")));
	}

	@Test
	@DisplayName("retourne 400 et un message metier lorsqu'une regle du domaine est violee")
	void shouldReturnBadRequestWhenBusinessRuleIsViolated() throws Exception {
		when(settlementUseCase.calculate(any(EmployeeDeparture.class)))
				.thenThrow(new InvalidDatesException("La date de depart doit etre posterieure a la date d'embauche"));

		mockMvc.perform(post(SETTLEMENTS_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_REQUEST))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message")
						.value("La date de depart doit etre posterieure a la date d'embauche"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	@DisplayName("retourne 400 lorsque le motif de depart est inconnu")
	void shouldReturnBadRequestWhenDepartureReasonIsUnknown() throws Exception {
		String unknownReason = VALID_REQUEST.replace("RETIREMENT", "VACANCES");

		mockMvc.perform(post(SETTLEMENTS_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(unknownReason))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}
}
