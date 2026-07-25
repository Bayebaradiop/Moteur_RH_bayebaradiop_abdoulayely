package com.company.hrsettlement.settlement.api;

import com.company.hrsettlement.settlement.api.dto.ErreurReponse;
import com.company.hrsettlement.settlement.api.dto.SoldeRequete;
import com.company.hrsettlement.settlement.api.dto.SoldeReponse;
import com.company.hrsettlement.settlement.api.mapper.SoldeConvertisseur;
import com.company.hrsettlement.settlement.application.CalculSoldeCasUsage;
import com.company.hrsettlement.settlement.domain.model.Solde;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * Adaptateur primaire : expose le cas d'usage en HTTP.
 * <p>
 * Le controleur recoit, delegue, repond. Il ne contient aucune regle de calcul et
 * n'ecrit aucune conversion : le mapping est confie au mapper, le metier au cas
 * d'usage.
 */
@RestController
@RequestMapping(path = "/api/v1/settlements", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Soldes", description = "Calcul du solde de tout compte")
public class SoldeControleur {

	private final CalculSoldeCasUsage calculSoldeCasUsage;
	private final SoldeConvertisseur soldeConvertisseur;

	public SoldeControleur(CalculSoldeCasUsage calculSoldeCasUsage, SoldeConvertisseur soldeConvertisseur) {
		this.calculSoldeCasUsage = Objects.requireNonNull(calculSoldeCasUsage);
		this.soldeConvertisseur = Objects.requireNonNull(soldeConvertisseur);
	}

	@Operation(
			summary = "Calcule le solde de tout compte d'un employe",
			description = "Le montant net peut etre negatif lorsque la penalite de preavis depasse les indemnites dues")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Solde de tout compte calcule"),
			@ApiResponse(responseCode = "400", description = "Contrat non respecte ou regle metier violee",
					content = @Content(schema = @Schema(implementation = ErreurReponse.class))),
			@ApiResponse(responseCode = "500", description = "Erreur interne inattendue",
					content = @Content(schema = @Schema(implementation = ErreurReponse.class)))
	})
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public SoldeReponse calculerSolde(@Valid @RequestBody SoldeRequete requete) {
		Solde solde = calculSoldeCasUsage.calculer(soldeConvertisseur.versDomaine(requete));

		return soldeConvertisseur.versReponse(solde);
	}
}
