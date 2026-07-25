package com.company.hrsettlement.service;

import com.company.hrsettlement.domain.AssietteFiscale;
import com.company.hrsettlement.domain.DepartEmploye;
import com.company.hrsettlement.domain.Solde;
import com.company.hrsettlement.exception.ExceptionDatesInvalides;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.company.hrsettlement.domain.ConstructeurDepart.unDepart;
import static com.company.hrsettlement.domain.MotifDepart.DEMISSION;
import static com.company.hrsettlement.domain.MotifDepart.RETRAITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Moteur de solde de tout compte")
class MoteurSoldeTest {

	@Mock
	private AdministrationFiscale administrationFiscale;

	@Mock
	private InspectionTravail inspectionTravail;

	@Mock
	private ArchiveurSolde archiveurSolde;

	@Captor
	private ArgumentCaptor<AssietteFiscale> captureurAssiette;

	@Captor
	private ArgumentCaptor<Solde> captureurSolde;

	private MoteurSolde moteurSolde;

	@BeforeEach
	void initialiser() {
		moteurSolde = new MoteurSolde(
				new ValidateurDepart(),
				new CalculateurConges(),
				new CalculateurPrimeAnciennete(),
				new CalculateurPenalitePreavis(),
				administrationFiscale,
				inspectionTravail,
				archiveurSolde);
	}

	@Test
	@DisplayName("detaille chaque composante puis deduit l'impot du brut")
	void doitDetaillerChaqueComposanteEtDeduireLImpot() {
		// 10 annees d'anciennete : conges 10 x 50 000, prime 1 050 000 x 1,25
		DepartEmploye depart = unDepart()
				.avecMatricule("EMP-100")
				.pourMotif(RETRAITE)
				.embaucheLe(LocalDate.of(2016, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("1050000")
				.avecJoursCongesRestants(10)
				.construire();
		when(administrationFiscale.calculerImpot(any(AssietteFiscale.class))).thenReturn(new BigDecimal("200000"));

		Solde solde = moteurSolde.calculerSolde(depart);

		assertThat(solde.matriculeEmploye()).isEqualTo("EMP-100");
		assertThat(solde.indemniteConges()).isEqualByComparingTo("500000");
		assertThat(solde.primeAnciennete()).isEqualByComparingTo("1312500");
		assertThat(solde.penalitePreavis()).isEqualByComparingTo("0");
		assertThat(solde.montantBrut()).isEqualByComparingTo("1812500");
		assertThat(solde.montantImpot()).isEqualByComparingTo("200000");
		assertThat(solde.montantNet()).isEqualByComparingTo("1612500");
		assertThat(solde.auditDeclenche()).isFalse();
	}

	@Test
	@DisplayName("produit un net negatif lorsque la penalite depasse les indemnites")
	void doitProduireUnNetNegatifQuandLaPenaliteDepasse() {
		DepartEmploye depart = unDepart()
				.pourMotif(DEMISSION)
				.avecPreavisRespecte(false)
				.avecSalaireMensuel("500000")
				.avecJoursCongesRestants(2)
				.construire();
		when(administrationFiscale.calculerImpot(any(AssietteFiscale.class))).thenReturn(BigDecimal.ZERO);

		Solde solde = moteurSolde.calculerSolde(depart);

		assertThat(solde.penalitePreavis()).isEqualByComparingTo("500000");
		assertThat(solde.montantBrut()).isEqualByComparingTo("-452380.96");
		assertThat(solde.montantNet()).isEqualByComparingTo("-452380.96");
	}

	@Test
	@DisplayName("transmet a l'administration fiscale la part imposable et la part exoneree")
	void doitTransmettreLAssietteALAdministrationFiscale() {
		// Prime de 12 500 000 : exoneree a hauteur de 5 000 000, le surplus est imposable
		DepartEmploye depart = unDepart()
				.pourMotif(RETRAITE)
				.embaucheLe(LocalDate.of(2016, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("10000000")
				.construire();
		when(administrationFiscale.calculerImpot(any(AssietteFiscale.class))).thenReturn(BigDecimal.ZERO);

		moteurSolde.calculerSolde(depart);

		verify(administrationFiscale).calculerImpot(captureurAssiette.capture());
		assertThat(captureurAssiette.getValue().montantExonere()).isEqualByComparingTo("5000000");
		assertThat(captureurAssiette.getValue().montantImposable()).isEqualByComparingTo("7500000");
	}

	@Test
	@DisplayName("n'expose aucune assiette imposable negative lorsque le brut est negatif")
	void neDoitPasTransmettreUneAssietteNegative() {
		DepartEmploye depart = unDepart()
				.pourMotif(DEMISSION)
				.avecPreavisRespecte(false)
				.avecSalaireMensuel("500000")
				.construire();
		when(administrationFiscale.calculerImpot(any(AssietteFiscale.class))).thenReturn(BigDecimal.ZERO);

		moteurSolde.calculerSolde(depart);

		verify(administrationFiscale).calculerImpot(captureurAssiette.capture());
		assertThat(captureurAssiette.getValue().montantImposable()).isEqualByComparingTo("0");
	}

	@Test
	@DisplayName("notifie l'inspection du travail lorsque le net depasse trente millions")
	void doitNotifierLInspectionAuDelaDuSeuil() {
		DepartEmploye depart = unDepart()
				.avecMatricule("EMP-777")
				.pourMotif(RETRAITE)
				.embaucheLe(LocalDate.of(2016, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("30000000")
				.construire();
		when(administrationFiscale.calculerImpot(any(AssietteFiscale.class))).thenReturn(BigDecimal.ZERO);

		Solde solde = moteurSolde.calculerSolde(depart);

		verify(inspectionTravail).notifierAudit("EMP-777");
		assertThat(solde.auditDeclenche()).isTrue();
	}

	@Test
	@DisplayName("ne declenche aucun audit lorsque le net atteint exactement le seuil")
	void neDoitPasNotifierAuSeuilExact() {
		// Valeur limite : 24 000 000 x 1,25 = 30 000 000 exactement
		DepartEmploye depart = unDepart()
				.pourMotif(RETRAITE)
				.embaucheLe(LocalDate.of(2016, 1, 1))
				.departLe(LocalDate.of(2026, 1, 1))
				.avecSalaireMensuel("24000000")
				.construire();
		when(administrationFiscale.calculerImpot(any(AssietteFiscale.class))).thenReturn(BigDecimal.ZERO);

		Solde solde = moteurSolde.calculerSolde(depart);

		verifyNoInteractions(inspectionTravail);
		assertThat(solde.montantNet()).isEqualByComparingTo("30000000");
		assertThat(solde.auditDeclenche()).isFalse();
	}

	@Test
	@DisplayName("archive le solde calcule")
	void doitArchiverLeSoldeCalcule() {
		DepartEmploye depart = unDepart().avecJoursCongesRestants(10).construire();
		when(administrationFiscale.calculerImpot(any(AssietteFiscale.class))).thenReturn(BigDecimal.ZERO);

		Solde solde = moteurSolde.calculerSolde(depart);

		verify(archiveurSolde).archiver(captureurSolde.capture());
		assertThat(captureurSolde.getValue()).isEqualTo(solde);
	}

	@Test
	@DisplayName("rejette un depart incoherent sans solliciter le moindre systeme externe")
	void doitRefuserUnDepartInvalideAvantToutAppelExterne() {
		DepartEmploye depart = unDepart()
				.embaucheLe(LocalDate.of(2026, 1, 1))
				.departLe(LocalDate.of(2025, 1, 1))
				.construire();

		assertThatThrownBy(() -> moteurSolde.calculerSolde(depart))
				.isInstanceOf(ExceptionDatesInvalides.class);

		verifyNoInteractions(administrationFiscale, inspectionTravail, archiveurSolde);
	}
}
