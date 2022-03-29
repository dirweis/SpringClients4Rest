package de.infoteam.errorhandling;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.infoteam.model.Error;
import de.infoteam.model.Error.InvalidParam;
import de.infoteam.model.WeatherForecast;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * A little {@link Service} bean for processing business logic that creates an {@link Error} response.
 * 
 * @author Dirk Weissmann
 * @since 2022-03-03
 * @version 0.3
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7807">RFC7807</a>
 *
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ErrorService {

	private final HttpServletRequest request;
	private final Validator validator;

	/**
	 * Provides a {@link ResponseEntity} with an {@link Error} body as response in case of errors.
	 * 
	 * @param ex     the specific {@link Exception} object, must not be {@code null}
	 * @param status the {@link HttpStatus} value for the response, must not be {@code null}
	 * 
	 * @return the response of the Spring service
	 */
	final ResponseEntity<Error> provideError(final Exception ex, final HttpStatus status) {
		final Error responseBody = Error.builder().title(ex.getLocalizedMessage())
				.instance(URI.create("urn:ERROR:" + UUID.randomUUID())).type(URI.create(request.getRequestURI()))
				.build();

		return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(responseBody);
	}

	/**
	 * Prepares the {@link Error} object with commonly used values.
	 * 
	 * @param title         the error's title, must not be {@code null}
	 * @param detail        the error's detailed description, may be {@code null}
	 * @param invalidParams special field for parameter violations, may be {@code null}
	 * 
	 * @return the final {@link Error}, never {@code null}
	 */
	final Error finalizeRfc7807Error(final String title, final String detail,
			@Valid final List<InvalidParam> invalidParams) {
		final UUID errorId = UUID.randomUUID();

		log.warn("Problems in request. ID: {}", errorId);

		return Error.builder().type(URI.create(request.getRequestURI())).title(title)
				.instance(URI.create("urn:ERROR:" + errorId)).detail(detail).invalidParams(invalidParams).build();
	}

	/**
	 * A helper method which is a shame: Validation in Feign is done with 2 annotations - and that's about it! This here
	 * is programmed like in old days - WTF.
	 * 
	 * @param wtfItems the {@link WeatherForecast} items to be validated
	 */
	public void validateDotNetResponse(final WeatherForecast[] wtfItems) {
		final Set<ConstraintViolation<WeatherForecast>> returnSet = new HashSet<>();

		Arrays.stream(wtfItems).forEach((final WeatherForecast wtf) -> returnSet.addAll(validator.validate(wtf)));

		if (!returnSet.isEmpty()) {
			throw new ConstraintViolationException(returnSet);
		}
	}
}
