package de.infoteam.api;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import de.infoteam.errorhandling.ErrorService;
import de.infoteam.feign.FeignClientDochNet;
import de.infoteam.model.WeatherForecast;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * The controller implementing from the interface {@link DemoRestApi}. While all end points including validation is done
 * in the interface, the implementation is to be found here.
 * <p>
 * <i>Security is not yet supported</i>
 * 
 * @author Dirk Weissmann
 * @since 2022-03-03
 * @version 1.1
 *
 */
@RequiredArgsConstructor
@RestController
class DemoRestApiController implements DemoRestApi {

	/* The 3 client objects for calling the .NET endpoint */
	private final WebClient webClient;
	private final FeignClientDochNet feignClient;
	private final RestTemplate restTemplate;

	private final ErrorService errorService;

	/**
	 * {@inheritDoc}
	 * <p>
	 * The basic configuration is to be found in the {@link Configuration} bean {@code ApiConfig}.
	 */
	@Override
	public ResponseEntity<WeatherForecast[]> getWeatherForecastViaWebClient() {
		final Mono<WeatherForecast[]> serviceCallResult = webClient.get().uri("/WeatherForecast").retrieve()
				.bodyToMono(WeatherForecast[].class);

		final WeatherForecast[] responseBody = serviceCallResult.block();

		errorService.validateDotNetResponse(responseBody);

		return ResponseEntity.ok(responseBody);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see FeignClientDochNet
	 */
	@Override
	public ResponseEntity<List<WeatherForecast>> getWeatherForecastViaFeignClient() {
		return ResponseEntity.ok(feignClient.getForecasts());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The basic configuration is to be found in the {@link Configuration} bean {@code ApiConfig}.
	 */
	@Override
	public ResponseEntity<WeatherForecast[]> getWeatherForecastViaRestTemplate() {
		final ResponseEntity<WeatherForecast[]> dotNetResponse = restTemplate.getForEntity("/WeatherForecast",
				WeatherForecast[].class);

		errorService.validateDotNetResponse(dotNetResponse.getBody());

		return dotNetResponse;
	}
}
