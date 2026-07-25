package com.company.hrsettlement.settlement.domain;

import com.company.hrsettlement.settlement.domain.model.MotifDepart;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Constructeur de donnees de test.
 * <p>
 * Chaque test ne declare que les champs qui portent le comportement teste : les
 * autres gardent une valeur par defaut neutre. Cela evite la duplication et rend
 * l'intention du test immediatement lisible.
 */
public final class ConstructeurDepart {

	private String matriculeEmploye = "EMP-001";
	private LocalDate dateEmbauche = LocalDate.of(2020, 1, 1);
	private LocalDate dateDepart = LocalDate.of(2026, 1, 1);
	private MotifDepart motifDepart = MotifDepart.DEMISSION;
	private BigDecimal salaireBase = new BigDecimal("1050000");
	private int joursCongesRestants = 0;
	private boolean preavisRespecte = true;

	private ConstructeurDepart() {
	}

	public static ConstructeurDepart unDepart() {
		return new ConstructeurDepart();
	}

	public ConstructeurDepart avecMatricule(String matriculeEmploye) {
		this.matriculeEmploye = matriculeEmploye;
		return this;
	}

	public ConstructeurDepart embaucheLe(LocalDate dateEmbauche) {
		this.dateEmbauche = dateEmbauche;
		return this;
	}

	public ConstructeurDepart departLe(LocalDate dateDepart) {
		this.dateDepart = dateDepart;
		return this;
	}

	public ConstructeurDepart pourMotif(MotifDepart motifDepart) {
		this.motifDepart = motifDepart;
		return this;
	}

	public ConstructeurDepart avecSalaireMensuel(String salaireBase) {
		this.salaireBase = new BigDecimal(salaireBase);
		return this;
	}

	public ConstructeurDepart avecJoursCongesRestants(int joursCongesRestants) {
		this.joursCongesRestants = joursCongesRestants;
		return this;
	}

	public ConstructeurDepart avecPreavisRespecte(boolean preavisRespecte) {
		this.preavisRespecte = preavisRespecte;
		return this;
	}

	public DepartEmploye construire() {
		return new DepartEmploye(
				matriculeEmploye,
				dateEmbauche,
				dateDepart,
				motifDepart,
				salaireBase,
				joursCongesRestants,
				preavisRespecte);
	}
}
