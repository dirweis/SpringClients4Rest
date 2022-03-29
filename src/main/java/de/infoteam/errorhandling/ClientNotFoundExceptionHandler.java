package de.infoteam.errorhandling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import de.infoteam.model.Error;
import feign.FeignException;

/**
 * Error handling for the different client solutions in case of a {@code 404} response from the .NET service.
 * 
 * @author Dirk Weissmann
 * @since 2022-03-03
 * @version 0.2
 * @see ExceptionHandler
 *
 */
@RestControllerAdvice
class ClientNotFoundExceptionHandler {

	@Autowired
	private ErrorService service;

	/**
	 * YES! Very elegant: The framework not only provides an own {@link Exception} class with
	 * {@link WebClientResponseException} but also sub classes for different problems. In this case the sub class is the
	 * {@code NotFound} class (test with a typo in the .NET service's URL).
	 * 
	 * @param ex the WebClientResponseException.NotFound exception
	 * @return an error {@link ResponseEntity}, never {@code null}
	 */
	@ExceptionHandler(WebClientResponseException.NotFound.class)
	ResponseEntity<Error> handle404FromWebClient(final WebClientResponseException.NotFound ex) {
		return service.provideError(ex, HttpStatus.NOT_FOUND);
	}

	/**
	 * RIGTH ON! Once again very elegant: The framework not only provides an own {@link Exception} class with
	 * {@link FeignException} but also sub classes for different problems. In this case the sub class is the
	 * {@code NotFound} class (test with a typo in the .NET service's URL).
	 * 
	 * @param ex the FeignException.NotFound exception
	 * @return an error {@link ResponseEntity}, never {@code null}
	 */
	@ExceptionHandler(FeignException.NotFound.class)
	ResponseEntity<Error> handle404FromFeignClient(final FeignException.NotFound ex) {
		return service.provideError(ex, HttpStatus.NOT_FOUND);
	}

	/**
	 * FINALLY! Even the {@link RestTemplate}: The framework not only provides an own {@link Exception} class with
	 * {@link HttpClientErrorException} but also sub classes for different problems. In this case the sub class is the
	 * {@code NotFound} class (test with a typo in the .NET service's URL).
	 * 
	 * @param ex the HttpClientErrorException.NotFound exception
	 * @return an error {@link ResponseEntity}, never {@code null}
	 */
	@ExceptionHandler(HttpClientErrorException.NotFound.class)
	ResponseEntity<Error> handle404FromRestTemplate(final HttpClientErrorException.NotFound ex) {
		return service.provideError(ex, HttpStatus.NOT_FOUND);
	}
}
