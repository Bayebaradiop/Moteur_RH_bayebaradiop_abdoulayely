package com.company.hrsettlement.settlement.api.exception;

import com.company.hrsettlement.settlement.api.dto.ErrorResponse;
import com.company.hrsettlement.settlement.domain.exception.BusinessException;
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
 * C'est la frontiere entre deux vocabulaires : le domaine leve une exception
 * metier, l'API repond un code HTTP. Aucune exception brute ne remonte au client,
 * et aucun detail technique ne fuit dans le corps de la reponse.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private static final String UNREADABLE_PAYLOAD_MESSAGE =
			"La requete est illisible ou contient une valeur non autorisee";

	private static final String UNEXPECTED_ERROR_MESSAGE =
			"Une erreur interne est survenue";

	/** Violation d'une regle du domaine : la demande est recevable mais invalide. */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception,
	                                                            HttpServletRequest request) {
		return badRequest(exception.getMessage(), request);
	}

	/** Violation du contrat d'entree detectee par la Bean Validation. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception,
	                                                              HttpServletRequest request) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(" ; "));

		return badRequest(message, request);
	}

	/** Corps JSON malforme ou valeur d'enumeration inconnue. */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleUnreadablePayload(HttpMessageNotReadableException exception,
	                                                            HttpServletRequest request) {
		LOGGER.warn("Requete illisible sur {} : {}", request.getRequestURI(), exception.getMessage());

		return badRequest(UNREADABLE_PAYLOAD_MESSAGE, request);
	}

	/** Filet de securite : rien d'inattendu ne doit atteindre le client tel quel. */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception,
	                                                              HttpServletRequest request) {
		LOGGER.error("Erreur inattendue sur {}", request.getRequestURI(), exception);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(toErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR_MESSAGE, request));
	}

	private ResponseEntity<ErrorResponse> badRequest(String message, HttpServletRequest request) {
		return ResponseEntity.badRequest()
				.body(toErrorResponse(HttpStatus.BAD_REQUEST, message, request));
	}

	private ErrorResponse toErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
		return new ErrorResponse(
				LocalDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI());
	}
}
