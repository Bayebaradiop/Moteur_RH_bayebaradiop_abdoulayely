package com.company.hrsettlement.service;

import com.company.hrsettlement.domain.AssietteFiscale;
import com.company.hrsettlement.domain.DepartEmploye;
import com.company.hrsettlement.domain.Monnaie;
import com.company.hrsettlement.domain.Solde;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Moteur de calcul du solde de tout compte.
 * <p>
 * Il orchestre : il valide, enchaine les calculateurs, interroge l'administration
 * fiscale, declenche l'audit puis archive le resultat. Les formules elles-memes
 * appartiennent aux calculateurs : le moteur reste stable quand une regle evolue.
 */
@Service
public class MoteurSolde {

	/** Plafond d'exoneration de la prime d'anciennete, en XOF. */
	private static final BigDecimal PLAFOND_EXONERATION_PRIME = new BigDecimal("5000000");

	/** Seuil de net a partir duquel un audit devient obligatoire, en XOF. */
	private static final BigDecimal SEUIL_AUDIT = new BigDecimal("30000000");

	/** Le seuil doit etre depasse : un net exactement egal ne declenche pas d'audit. */
	private static final Predicate<BigDecimal> AUDIT_REQUIS =
			montantNet -> montantNet.compareTo(SEUIL_AUDIT) > 0;

	private final ValidateurDepart validateurDepart;
	private final CalculateurConges calculateurConges;
	private final CalculateurPrimeAnciennete calculateurPrimeAnciennete;
	private final CalculateurPenalitePreavis calculateurPenalitePreavis;
	private final AdministrationFiscale administrationFiscale;
	private final InspectionTravail inspectionTravail;
	private final ArchiveurSolde archiveurSolde;

	public MoteurSolde(ValidateurDepart validateurDepart,
	                   CalculateurConges calculateurConges,
	                   CalculateurPrimeAnciennete calculateurPrimeAnciennete,
	                   CalculateurPenalitePreavis calculateurPenalitePreavis,
	                   AdministrationFiscale administrationFiscale,
	                   InspectionTravail inspectionTravail,
	                   ArchiveurSolde archiveurSolde) {
		this.validateurDepart = Objects.requireNonNull(validateurDepart);
		this.calculateurConges = Objects.requireNonNull(calculateurConges);
		this.calculateurPrimeAnciennete = Objects.requireNonNull(calculateurPrimeAnciennete);
		this.calculateurPenalitePreavis = Objects.requireNonNull(calculateurPenalitePreavis);
		this.administrationFiscale = Objects.requireNonNull(administrationFiscale);
		this.inspectionTravail = Objects.requireNonNull(inspectionTravail);
		this.archiveurSolde = Objects.requireNonNull(archiveurSolde);
	}

	/**
	 * Calcule le solde de tout compte, declenche l'audit si necessaire et archive
	 * le resultat.
	 *
	 * @param depart informations de depart de l'employe
	 * @return le detail du solde de tout compte
	 */
	@Transactional
	public Solde calculerSolde(DepartEmploye depart) {
		validateurDepart.valider(depart);

		BigDecimal indemniteConges = calculateurConges.calculer(depart);
		BigDecimal primeAnciennete = calculateurPrimeAnciennete.calculer(depart);
		BigDecimal penalitePreavis = calculateurPenalitePreavis.calculer(depart);
		BigDecimal montantBrut = calculerBrut(indemniteConges, primeAnciennete, penalitePreavis);

		BigDecimal montantImpot = demanderImpot(montantBrut, primeAnciennete);
		BigDecimal montantNet = Monnaie.arrondir(montantBrut.subtract(montantImpot));

		Solde solde = new Solde(
				depart.matriculeEmploye(),
				indemniteConges,
				primeAnciennete,
				penalitePreavis,
				montantBrut,
				montantImpot,
				montantNet,
				declencherAuditSiRequis(depart.matriculeEmploye(), montantNet));

		archiveurSolde.archiver(solde);

		return solde;
	}

	/** Le brut peut etre negatif : l'employe est alors redevable envers l'employeur. */
	private BigDecimal calculerBrut(BigDecimal indemniteConges,
	                                BigDecimal primeAnciennete,
	                                BigDecimal penalitePreavis) {
		return Monnaie.arrondir(indemniteConges
				.add(primeAnciennete)
				.subtract(penalitePreavis));
	}

	/** L'impot n'est jamais calcule ici : le moteur prepare l'assiette et delegue. */
	private BigDecimal demanderImpot(BigDecimal montantBrut, BigDecimal primeAnciennete) {
		BigDecimal montantExonere = primeAnciennete.min(PLAFOND_EXONERATION_PRIME);
		BigDecimal montantImposable = montantBrut.subtract(montantExonere).max(BigDecimal.ZERO);

		AssietteFiscale assiette = new AssietteFiscale(
				Monnaie.arrondir(montantImposable), Monnaie.arrondir(montantExonere));

		return Monnaie.arrondir(administrationFiscale.calculerImpot(assiette));
	}

	/**
	 * Notifie immediatement l'inspection du travail au-dela du seuil de net.
	 * <p>
	 * L'employe n'est concerne que si le seuil est depasse : l'Optional exprime
	 * cette absence eventuelle et {@code ifPresent} confie l'action au service
	 * d'inspection sans condition supplementaire.
	 */
	private boolean declencherAuditSiRequis(String matriculeEmploye, BigDecimal montantNet) {
		Optional<String> employeAAuditer = Optional.of(matriculeEmploye)
				.filter(matricule -> AUDIT_REQUIS.test(montantNet));

		employeAAuditer.ifPresent(inspectionTravail::notifierAudit);

		return employeAAuditer.isPresent();
	}
}
