package com.company.hrsettlement.settlement.api.mapper;

import com.company.hrsettlement.settlement.api.dto.SettlementRequest;
import com.company.hrsettlement.settlement.api.dto.SettlementResponse;
import com.company.hrsettlement.settlement.domain.model.EmployeeDeparture;
import com.company.hrsettlement.settlement.domain.model.Settlement;
import org.springframework.stereotype.Component;

/**
 * Traduit le contrat d'API en objets du domaine, et inversement.
 * <p>
 * Le mapping est isole ici : ni le controleur ni le service n'ecrivent de
 * conversion. Il est ainsi testable seul et concentre le point de rupture entre le
 * vocabulaire public et le vocabulaire metier.
 */
@Component
public class SettlementMapper {

	public EmployeeDeparture toDomain(SettlementRequest request) {
		return new EmployeeDeparture(
				request.employeeId(),
				request.hireDate(),
				request.departureDate(),
				request.departureReason(),
				request.baseSalary(),
				request.remainingLeaveDays(),
				request.noticeRespected());
	}

	public SettlementResponse toResponse(Settlement settlement) {
		return new SettlementResponse(
				settlement.employeeId(),
				settlement.leaveCompensation(),
				settlement.seniorityBonus(),
				settlement.noticePenalty(),
				settlement.grossAmount(),
				settlement.taxAmount(),
				settlement.netAmount(),
				settlement.auditTriggered());
	}
}
