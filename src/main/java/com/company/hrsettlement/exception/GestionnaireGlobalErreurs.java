package com.company.hrsettlement.exception;

import com.company.hrsettlement.dto.response.ErreurReponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Traduction unique des erreurs en reponses HTTP.
 * <p>
 * Le metier leve une exception explicite, l'API repond un code HTTP. Aucune
 * exception brute ne remonte au client, aucun detail technique ne fuit.
 */
@RestControllerAdvice
public class GestionnaireGlobalErreurs {

	private static final Logger JOURNAL = LoggerFactory.getLogger(GestionnaireGlobalErreurs.class);

	private static final String MESSAGE_REQUETE_ILLISIBLE =
			"La requete est illisible ou contient une valeur non autorisee";

	private static final String MESSAGE_ERREUR_INATTENDUE =
			"Une erreur interne est survenue";

	/** Violation d'une regle metier : la demande est recevable mais invalide. */
	@ExceptionHandler(ExceptionMetier.class)
	public ResponseEntity<ErreurReponseDto> gererExceptionMetier(ExceptionMetier exception,
	                                                            HttpServletRequest requete) {
		return requeteInvalide(exception.getMessage(), requete);
	}

	/** Violation du contrat d'entree detectee par la Bean Validation. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErreurReponseDto> gererErreurValidation(MethodArgumentNotValidException exception,
	                                                              HttpServletRequest requete) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(" ; "));

		return requeteInvalide(message, requete);
	}

	/** Corps JSON malforme ou valeur d'enumeration inconnue. */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErreurReponseDto> gererRequeteIllisible(HttpMessageNotReadableException exception,
	                                                             HttpServletRequest requete) {
		JOURNAL.warn("Requete illisible sur {} : {}", requete.getRequestURI(), exception.getMessage());

		return requeteInvalide(MESSAGE_REQUETE_ILLISIBLE, requete);
	}

	/** Filet de securite : rien d'inattendu ne doit atteindre le client tel quel. */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErreurReponseDto> gererErreurInattendue(Exception exception,
	                                                             HttpServletRequest requete) {
		JOURNAL.error("Erreur inattendue sur {}", requete.getRequestURI(), exception);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(versErreurReponse(HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE_ERREUR_INATTENDUE, requete));
	}

	private ResponseEntity<ErreurReponseDto> requeteInvalide(String message, HttpServletRequest requete) {
		return ResponseEntity.badRequest()
				.body(versErreurReponse(HttpStatus.BAD_REQUEST, message, requete));
	}

	private ErreurReponseDto versErreurReponse(HttpStatus statut, String message, HttpServletRequest requete) {
		return new ErreurReponseDto(
				LocalDateTime.now(),
				statut.value(),
				statut.getReasonPhrase(),
				message,
				requete.getRequestURI());
	}
}
