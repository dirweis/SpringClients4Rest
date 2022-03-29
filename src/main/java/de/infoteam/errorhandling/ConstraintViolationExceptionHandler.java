package de.infoteam.errorhandling;

import java.util.List;
import java.util.stream.StreamSupport;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Path.Node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.infoteam.model.Error;
import de.infoteam.model.Error.InvalidParam;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught
 * {@link ConstraintViolationException}. Ensures the response code {@code 400} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets/findByStatus",
    "title": "Constraint violations",
    "instance": "urn:ERROR:61bb8581-9d92-4447-b49f-44ea526f18b8",
    "invalid_params": [
        {
            "name": "count",
            "reason": "11 is not a multiple of 10"
        }
    ]
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2021-10-25
 * @version 1.2
 *
 */
@Order(4)
@RestControllerAdvice
class ConstraintViolationExceptionHandler {

	@Autowired
	private ErrorService errorService;

	/**
	 * Catches the defined {@link Exception}s and creates an {@link Error} response body.
	 * 
	 * @param ex the {@link Exception} to catch, never {@code null}
	 * 
	 * @return the created {@link Error} object as response body, never {@code null}
	 * 
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	private ResponseEntity<Error> handleException(final ConstraintViolationException ex) {
		final List<InvalidParam> invalidParams = ex.getConstraintViolations().stream()
				.map(constraintViolation -> InvalidParam.builder()
						.name(extractParamNameFromPath(constraintViolation.getPropertyPath())).reason("Wert \""
								+ constraintViolation.getInvalidValue() + "\": " + constraintViolation.getMessage())
						.build())
				.toList();

		final String title = semanticError(ex) ? "Request body validation failed" : "Constraint violations";

		final Error error = errorService.finalizeRfc7807Error(title + " from .NET service call", null, invalidParams);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.body(error);
	}

	/**
	 * Checks for an existing {@link Node} named {@code semantic} or {@code body} in the {@link Exception}'s
	 * {@link ConstraintViolation}s.
	 * 
	 * @param ex the given exception for extracting the {@link ConstraintViolation}s
	 * 
	 * @return {@code true} if found
	 */
	private static boolean semanticError(final ConstraintViolationException ex) {
		return ex.getConstraintViolations().stream()
				.anyMatch((final ConstraintViolation<?> cv) -> StreamSupport
						.stream(cv.getPropertyPath().spliterator(), false)
						.anyMatch((final Node node) -> "body".equals(node.getName())));
	}

	/**
	 * Extracts the parameter name from the last node in the property path.
	 * 
	 * @param propertyPath the given property {@link Path}, must not be {@code null}
	 * 
	 * @return the parameter name, never {@code null}
	 */
	private static String extractParamNameFromPath(final Path propertyPath) {
		return StreamSupport.stream(propertyPath.spliterator(), false).map(Node::getName)
				.reduce((first, second) -> second).orElseGet(propertyPath::toString);
	}
}
