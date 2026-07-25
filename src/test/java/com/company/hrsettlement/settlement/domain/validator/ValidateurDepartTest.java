package com.company.hrsettlement.settlement.domain.validator;

import com.company.hrsettlement.settlement.domain.exception.ExceptionDatesInvalides;
import com.company.hrsettlement.settlement.domain.exception.ExceptionDepartInvalide;
import com.company.hrsettlement.settlement.domain.exception.ExceptionSalaireInvalide;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.validator.rule.RegleCoherenceDates;
import com.company.hrsettlement.settlement.domain.validator.rule.RegleJoursCongesNonNegatifs;
import com.company.hrsettlement.settlement.domain.validator.rule.RegleSalairePositif;
import com.company.hrsettlement.settlement.domain.validator.rule.RegleAncienneteRetraite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static com.company.hrsettlement.settlement.domain.ConstructeurDepart.unDepart;
import static com.company.hrsettlement.settlement.domain.model.MotifDepart.RETRAITE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Validation metier du depart")
class ValidateurDepartTest {

	@Mock
	private RegleDepart regleSupplementaire;

	private ValidateurDepart validateurDepart;

	@BeforeEach
	void initialiser() {
		validateurDepart = new ValidateurDepart(List.of(
				new RegleCoherenceDates(),
				new RegleSalairePositif(),
				new RegleJoursCongesNonNegatifs(),
				new RegleAncienneteRetraite()));
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

	@Test
	@DisplayName("applique toute regle ajoutee sans modification du validateur")
	void doitAppliquerChaqueRegleEnregistree() {
		// Ouvert a l'extension : une nouvelle regle est simplement ajoutee a la liste
		ValidateurDepart extendedValidator = new ValidateurDepart(List.of(regleSupplementaire));
		DepartEmploye depart = unDepart().construire();

		extendedValidator.valider(depart);

		verify(regleSupplementaire).verifier(depart);
	}
}
