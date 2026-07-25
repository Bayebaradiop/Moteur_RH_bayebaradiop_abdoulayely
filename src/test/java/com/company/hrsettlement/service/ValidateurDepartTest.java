package com.company.hrsettlement.service;

import com.company.hrsettlement.domain.DepartEmploye;
import com.company.hrsettlement.exception.ExceptionDatesInvalides;
import com.company.hrsettlement.exception.ExceptionDepartInvalide;
import com.company.hrsettlement.exception.ExceptionSalaireInvalide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.company.hrsettlement.domain.ConstructeurDepart.unDepart;
import static com.company.hrsettlement.domain.MotifDepart.RETRAITE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Validation metier du depart")
class ValidateurDepartTest {

	private ValidateurDepart validateurDepart;

	@BeforeEach
	void initialiser() {
		validateurDepart = new ValidateurDepart();
	}

	@Test
	@DisplayName("accepte un depart coherent")
	void doitAccepterUnDepartCoherent() {
		DepartEmploye depart = unDepart().construire();

		assertThatCode(() -> validateurDepart.valider(depart)).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("refuse une date de depart anterieure a la date d'embauche")
	void doitRefuserUnDepartAvantEmbauche() {
		DepartEmploye depart = unDepart()
				.embaucheLe(LocalDate.of(2026, 1, 1))
				.departLe(LocalDate.of(2025, 12, 31))
				.construire();

		assertThatThrownBy(() -> validateurDepart.valider(depart))
				.isInstanceOf(ExceptionDatesInvalides.class)
				.hasMessageContaining("posterieure");
	}

	@Test
	@DisplayName("refuse une date de depart egale a la date d'embauche")
	void doitRefuserUnDepartLeJourDeEmbauche() {
		// Valeur limite : le meme jour ne constitue pas une relation de travail
		DepartEmploye depart = unDepart()
				.embaucheLe(LocalDate.of(2026, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.construire();

		assertThatThrownBy(() -> validateurDepart.valider(depart))
				.isInstanceOf(ExceptionDatesInvalides.class);
	}

	@Test
	@DisplayName("refuse un salaire de base nul")
	void doitRefuserUnSalaireNul() {
		DepartEmploye depart = unDepart().avecSalaireMensuel("0").construire();

		assertThatThrownBy(() -> validateurDepart.valider(depart))
				.isInstanceOf(ExceptionSalaireInvalide.class);
	}

	@Test
	@DisplayName("refuse un nombre de jours de conges negatif")
	void doitRefuserDesJoursDeCongesNegatifs() {
		DepartEmploye depart = unDepart().avecJoursCongesRestants(-1).construire();

		assertThatThrownBy(() -> validateurDepart.valider(depart))
				.isInstanceOf(ExceptionDepartInvalide.class);
	}

	@Test
	@DisplayName("refuse un depart a la retraite sans anciennete d'au moins un an")
	void doitRefuserUneRetraiteSansAncienneteMinimale() {
		DepartEmploye depart = unDepart()
				.pourMotif(RETRAITE)
				.embaucheLe(LocalDate.of(2025, 6, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.construire();

		assertThatThrownBy(() -> validateurDepart.valider(depart))
				.isInstanceOf(ExceptionDepartInvalide.class)
				.hasMessageContaining("retraite");
	}
}
