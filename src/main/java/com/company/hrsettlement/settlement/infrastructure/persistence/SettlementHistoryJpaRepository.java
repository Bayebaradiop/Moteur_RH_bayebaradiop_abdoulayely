package com.company.hrsettlement.settlement.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acces aux soldes archives. Detail technique confine a l'infrastructure : le
 * domaine ne connait que {@code SettlementHistoryPort}.
 */
public interface SettlementHistoryJpaRepository extends JpaRepository<SettlementHistoryEntity, Long> {

	List<SettlementHistoryEntity> findByEmployeeId(String employeeId);
}
