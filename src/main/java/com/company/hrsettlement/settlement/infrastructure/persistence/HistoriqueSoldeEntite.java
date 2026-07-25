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
@Table(name = "historique_solde")
public class HistoriqueSoldeEntite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "matricule_employe", nullable = false)
	private String matriculeEmploye;

	@Column(name = "indemnite_conges", nullable = false, precision = 19, scale = 2)
	private BigDecimal indemniteConges;

	@Column(name = "prime_anciennete", nullable = false, precision = 19, scale = 2)
	private BigDecimal primeAnciennete;

	@Column(name = "penalite_preavis", nullable = false, precision = 19, scale = 2)
	private BigDecimal penalitePreavis;

	@Column(name = "montant_brut", nullable = false, precision = 19, scale = 2)
	private BigDecimal montantBrut;

	@Column(name = "montant_impot", nullable = false, precision = 19, scale = 2)
	private BigDecimal montantImpot;

	@Column(name = "montant_net", nullable = false, precision = 19, scale = 2)
	private BigDecimal montantNet;

	@Column(name = "audit_declenche", nullable = false)
	private boolean auditDeclenche;

	@Column(name = "enregistre_le", nullable = false)
	private LocalDateTime enregistreLe;

	/** Requis par JPA. */
	protected HistoriqueSoldeEntite() {
	}

	HistoriqueSoldeEntite(String matriculeEmploye,
	                        BigDecimal indemniteConges,
	                        BigDecimal primeAnciennete,
	                        BigDecimal penalitePreavis,
	                        BigDecimal montantBrut,
	                        BigDecimal montantImpot,
	                        BigDecimal montantNet,
	                        boolean auditDeclenche,
	                        LocalDateTime enregistreLe) {
		this.matriculeEmploye = matriculeEmploye;
		this.indemniteConges = indemniteConges;
		this.primeAnciennete = primeAnciennete;
		this.penalitePreavis = penalitePreavis;
		this.montantBrut = montantBrut;
		this.montantImpot = montantImpot;
		this.montantNet = montantNet;
		this.auditDeclenche = auditDeclenche;
		this.enregistreLe = enregistreLe;
	}

	public Long getId() {
		return id;
	}

	public String getMatriculeEmploye() {
		return matriculeEmploye;
	}

	public BigDecimal getIndemniteConges() {
		return indemniteConges;
	}

	public BigDecimal getPrimeAnciennete() {
		return primeAnciennete;
	}

	public BigDecimal getPenalitePreavis() {
		return penalitePreavis;
	}

	public BigDecimal getMontantBrut() {
		return montantBrut;
	}

	public BigDecimal getMontantImpot() {
		return montantImpot;
	}

	public BigDecimal getMontantNet() {
		return montantNet;
	}

	public boolean isAuditDeclenche() {
		return auditDeclenche;
	}

	public LocalDateTime getEnregistreLe() {
		return enregistreLe;
	}
}
