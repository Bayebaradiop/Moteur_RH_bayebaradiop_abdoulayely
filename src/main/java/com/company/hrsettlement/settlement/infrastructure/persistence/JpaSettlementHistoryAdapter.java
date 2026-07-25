package com.company.hrsettlement.settlement.infrastructure.persistence;

import com.company.hrsettlement.settlement.domain.model.Settlement;
import com.company.hrsettlement.settlement.domain.port.SettlementHistoryPort;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Adaptateur secondaire : archive les soldes dans PostgreSQL.
 * <p>
 * La date d'archivage est lue ici, dans l'infrastructure : le domaine, lui, reste
 * sans horloge et donc parfaitement deterministe.
 */
@Component
public class JpaSettlementHistoryAdapter implements SettlementHistoryPort {

	private final SettlementHistoryJpaRepository settlementHistoryJpaRepository;
	private final Clock clock;

	public JpaSettlementHistoryAdapter(SettlementHistoryJpaRepository settlementHistoryJpaRepository, Clock clock) {
		this.settlementHistoryJpaRepository = Objects.requireNonNull(settlementHistoryJpaRepository);
		this.clock = Objects.requireNonNull(clock);
	}

	@Override
	public void record(Settlement settlement) {
		settlementHistoryJpaRepository.save(toEntity(settlement));
	}

	private SettlementHistoryEntity toEntity(Settlement settlement) {
		return new SettlementHistoryEntity(
				settlement.employeeId(),
				settlement.leaveCompensation(),
				settlement.seniorityBonus(),
				settlement.noticePenalty(),
				settlement.grossAmount(),
				settlement.taxAmount(),
				settlement.netAmount(),
				settlement.auditTriggered(),
				LocalDateTime.now(clock));
	}
}
