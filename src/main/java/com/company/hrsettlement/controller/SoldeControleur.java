package com.company.hrsettlement.controller;

import com.company.hrsettlement.domain.Solde;
import com.company.hrsettlement.dto.request.SoldeRequeteDto;
import com.company.hrsettlement.dto.response.ErreurReponseDto;
import com.company.hrsettlement.dto.response.SoldeReponseDto;
import com.company.hrsettlement.mapper.SoldeConvertisseur;
import com.company.hrsettlement.service.MoteurSolde;
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
 * Expose le calcul du solde de tout compte en HTTP.
 * <p>
 * Le controleur recoit, delegue, repond. Il ne contient aucune regle de calcul et
 * n'ecrit aucune conversion : le mapping revient au convertisseur, le metier au
 * moteur.
 */
@RestController
@RequestMapping(path = "/api/v1/settlements", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Soldes", description = "Calcul du solde de tout compte")
public class SoldeControleur {

	private final MoteurSolde moteurSolde;
	private final SoldeConvertisseur soldeConvertisseur;

	public SoldeControleur(MoteurSolde moteurSolde, SoldeConvertisseur soldeConvertisseur) {
		this.moteurSolde = Objects.requireNonNull(moteurSolde);
		this.soldeConvertisseur = Objects.requireNonNull(soldeConvertisseur);
	}

	@Operation(
			summary = "Calcule le solde de tout compte d'un employe",
			description = "Le montant net peut etre negatif lorsque la penalite de preavis depasse les indemnites dues")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Solde de tout compte calcule"),
			@ApiResponse(responseCode = "400", description = "Contrat non respecte ou regle metier violee",
					content = @Content(schema = @Schema(implementation = ErreurReponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Erreur interne inattendue",
					content = @Content(schema = @Schema(implementation = ErreurReponseDto.class)))
	})
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public SoldeReponseDto calculerSolde(@Valid @RequestBody SoldeRequeteDto requete) {
		Solde solde = moteurSolde.calculerSolde(soldeConvertisseur.versDomaine(requete));

		return soldeConvertisseur.versReponse(solde);
	}
}
