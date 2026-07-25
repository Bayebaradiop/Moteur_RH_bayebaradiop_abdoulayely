package com.company.hrsettlement.settlement.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representation relationnelle d'un solde archive.
 * <p>
 * L'entite vit dans l'infrastructure, jamais dans le domaine : les contraintes de
 * JPA (constructeur sans argument, mutabilite, identifiant technique) ne doivent
 * pas contaminer le modele metier, qui reste un record immuable.
 */
@Entity
@Table(name = "settlement_history")
public class SettlementHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "employee_id", nullable = false)
	private String employeeId;

	@Column(name = "leave_compensation", nullable = false, precision = 19, scale = 2)
	private BigDecimal leaveCompensation;

	@Column(name = "seniority_bonus", nullable = false, precision = 19, scale = 2)
	private BigDecimal seniorityBonus;

	@Column(name = "notice_penalty", nullable = false, precision = 19, scale = 2)
	private BigDecimal noticePenalty;

	@Column(name = "gross_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal grossAmount;

	@Column(name = "tax_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal taxAmount;

	@Column(name = "net_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal netAmount;

	@Column(name = "audit_triggered", nullable = false)
	private boolean auditTriggered;

	@Column(name = "recorded_at", nullable = false)
	private LocalDateTime recordedAt;

	/** Requis par JPA. */
	protected SettlementHistoryEntity() {
	}

	SettlementHistoryEntity(String employeeId,
	                        BigDecimal leaveCompensation,
	                        BigDecimal seniorityBonus,
	                        BigDecimal noticePenalty,
	                        BigDecimal grossAmount,
	                        BigDecimal taxAmount,
	                        BigDecimal netAmount,
	                        boolean auditTriggered,
	                        LocalDateTime recordedAt) {
		this.employeeId = employeeId;
		this.leaveCompensation = leaveCompensation;
		this.seniorityBonus = seniorityBonus;
		this.noticePenalty = noticePenalty;
		this.grossAmount = grossAmount;
		this.taxAmount = taxAmount;
		this.netAmount = netAmount;
		this.auditTriggered = auditTriggered;
		this.recordedAt = recordedAt;
	}

	public Long getId() {
		return id;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public BigDecimal getLeaveCompensation() {
		return leaveCompensation;
	}

	public BigDecimal getSeniorityBonus() {
		return seniorityBonus;
	}

	public BigDecimal getNoticePenalty() {
		return noticePenalty;
	}

	public BigDecimal getGrossAmount() {
		return grossAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public boolean isAuditTriggered() {
		return auditTriggered;
	}

	public LocalDateTime getRecordedAt() {
		return recordedAt;
	}
}
