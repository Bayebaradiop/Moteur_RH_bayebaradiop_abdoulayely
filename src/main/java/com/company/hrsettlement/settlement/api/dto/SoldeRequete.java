package com.company.hrsettlement.settlement.api.dto;

import com.company.hrsettlement.settlement.domain.model.MotifDepart;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Contrat d'entree de l'API.
 * <p>
 * Les types enveloppes ({@code Integer}, {@code Boolean}) sont volontaires : un
 * type primitif prendrait silencieusement sa valeur par defaut si le client omet
 * le champ, alors qu'ici {@code @NotNull} le detecte.
 * <p>
 * Cette validation est <em>technique</em> : elle protege le contrat d'API. La
 * coherence metier reste verifiee par le domaine, meme si l'appel ne vient pas de
 * HTTP.
 */
@Schema(description = "Informations de depart d'un employe")
public record SoldeRequete(

		@Schema(description = "Identifiant de l'employe", example = "EMP-001")
		@NotBlank(message = "L'identifiant de l'employe est obligatoire")
		String matriculeEmploye,

		@Schema(description = "Date d'embauche", example = "2010-01-01")
		@NotNull(message = "La date d'embauche est obligatoire")
		@Past(message = "La date d'embauche doit etre passee")
		LocalDate dateEmbauche,

		@Schema(description = "Date de depart", example = "2026-01-01")
		@NotNull(message = "La date de depart est obligatoire")
		LocalDate dateDepart,

		@Schema(description = "Motif du depart", example = "RETRAITE")
		@NotNull(message = "Le motif de depart est obligatoire")
		MotifDepart motifDepart,

		@Schema(description = "Salaire mensuel de base en XOF", example = "1000000")
		@NotNull(message = "Le salaire de base est obligatoire")
		@Positive(message = "Le salaire de base doit etre strictement positif")
		BigDecimal salaireBase,

		@Schema(description = "Jours de conges non pris", example = "10")
		@NotNull(message = "Le nombre de jours de conges restants est obligatoire")
		@PositiveOrZero(message = "Le nombre de jours de conges restants ne peut pas etre negatif")
		Integer joursCongesRestants,

		@Schema(description = "Preavis respecte", example = "true")
		@NotNull(message = "L'information de preavis est obligatoire")
		Boolean preavisRespecte) {
}
