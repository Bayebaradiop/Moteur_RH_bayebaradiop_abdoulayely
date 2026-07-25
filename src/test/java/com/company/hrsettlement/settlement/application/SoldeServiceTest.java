package com.company.hrsettlement.settlement.application;

import com.company.hrsettlement.settlement.domain.engine.MoteurSolde;
import com.company.hrsettlement.settlement.domain.model.DepartEmploye;
import com.company.hrsettlement.settlement.domain.model.Solde;
import com.company.hrsettlement.settlement.domain.port.PortHistoriqueSoldes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.company.hrsettlement.settlement.domain.ConstructeurDepart.unDepart;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service applicatif de solde de tout compte")
class SoldeServiceTest {

	@Mock
	private MoteurSolde moteurSolde;

	@Mock
	private PortHistoriqueSoldes portHistoriqueSoldes;

	private CalculSoldeCasUsage calculSoldeCasUsage;

	@BeforeEach
	void initialiser() {
		calculSoldeCasUsage = new SoldeService(moteurSolde, portHistoriqueSoldes);
	}

	@Test
	@DisplayName("delegue integralement le calcul au moteur metier")
	void doitDeleguerLeCalculAuMoteur() {
		// Le service ne porte aucune regle : toute intelligence reste dans le domaine
		DepartEmploye depart = unDepart().construire();
		Solde soldeAttendu = unSolde();
		when(moteurSolde.calculerSolde(depart)).thenReturn(soldeAttendu);

		Solde solde = calculSoldeCasUsage.calculer(depart);

		assertThat(solde).isSameAs(soldeAttendu);
		verify(moteurSolde).calculerSolde(depart);
	}

	@Test
	@DisplayName("archive le solde calcule pour en conserver la trace")
	void doitArchiverLeSoldeCalcule() {
		DepartEmploye depart = unDepart().construire();
		Solde soldeAttendu = unSolde();
		when(moteurSolde.calculerSolde(depart)).thenReturn(soldeAttendu);

		calculSoldeCasUsage.calculer(depart);

		verify(portHistoriqueSoldes).archiver(soldeAttendu);
	}

	@Test
	@DisplayName("n'archive rien lorsque le calcul est rejete")
	void neDoitRienArchiverSiLeCalculEchoue() {
		DepartEmploye depart = unDepart().construire();
		when(moteurSolde.calculerSolde(depart)).thenThrow(new IllegalStateException("moteur indisponible"));

		assertThatThrownBy(() -> calculSoldeCasUsage.calculer(depart))
				.isInstanceOf(IllegalStateException.class);

		verifyNoInteractions(portHistoriqueSoldes);
	}

	private Solde unSolde() {
		return new Solde(
				"EMP-001",
				new BigDecimal("500000.00"),
				new BigDecimal("300000.00"),
				new BigDecimal("0.00"),
				new BigDecimal("800000.00"),
				new BigDecimal("80000.00"),
				new BigDecimal("720000.00"),
				false);
	}
}
