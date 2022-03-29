package de.infoteam.errorhandling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import de.infoteam.model.Error;
import feign.RetryableException;

/**
 * Error handling for the different client solutions in case of a not successful connection to the .NET service.
 * 
 * @author Dirk Weissmann
 * @since 2022-03-03
 * @version 1.0
 * @see ExceptionHandler
 *
 */
@RestControllerAdvice
class ClientNoConnectionHandler {

	@Autowired
	private ErrorService service;

	/**
	 * Just a first approach for no connection using the {@link WebClient} solution: Catch the
	 * {@link WebClientRequestException} in an {@link ExceptionHandler}.
	 * 
	 * @param ex the {@link WebClientRequestException}, never {@code null}
	 * 
	 * @return the response in case of an error with the status {@link HttpStatus#INTERNAL_SERVER_ERROR}, never
	 *         {@code null}
	 */
	@ExceptionHandler(WebClientRequestException.class)
	ResponseEntity<Error> handleWebClient(final WebClientRequestException ex) {
		return service.provideError(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Just a first approach for no connection using the {@link FeignClient} solution: Catch the
	 * {@link RetryableException} in an {@link ExceptionHandler}.
	 * 
	 * @param ex the {@link RetryableException}, never {@code null}
	 * 
	 * @return the response in case of an error with the status {@link HttpStatus#INTERNAL_SERVER_ERROR}, never
	 *         {@code null}
	 */
	@ExceptionHandler(RetryableException.class)
	ResponseEntity<Error> handleFeignClient(final RetryableException ex) {
		return service.provideError(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Just a first approach for no connection using the {@link RestTemplate} solution: Catch the
	 * {@link ResourceAccessException} in an {@link ExceptionHandler}.
	 * 
	 * @param ex the {@link ResourceAccessException}, never {@code null}
	 * 
	 * @return the response in case of an error with the status {@link HttpStatus#INTERNAL_SERVER_ERROR}, never
	 *         {@code null}
	 */
	@ExceptionHandler(ResourceAccessException.class)
	ResponseEntity<Error> handleRestTemplate(final ResourceAccessException ex) {
		return service.provideError(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
