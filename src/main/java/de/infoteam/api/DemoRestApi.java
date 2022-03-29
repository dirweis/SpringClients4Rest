package de.infoteam.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import de.infoteam.model.WeatherForecast;

/**
 * End points for client demo purposes. 3 client variants are implemented:
 * <dl>
 * <dt>Web Client</dt>
 * <dd>The state-of-the-art client solution in Spring, supporting asynchronous calls, see also
 * <a href="https://www.baeldung.com/spring-5-webclient">The Baeldung Page</a></dd>
 * <dt>Open Feign Client</dt>
 * <dd>The most handsome implementation for a client as long as asynchronous calls are not needed</dd>
 * <dt>Rest Template</dt>
 * <dd>An easy-to-use solution, the <i>classic</i> client in Java, independent from the Spring framework</dd>
 * </dl>
 * 
 * @author Dirk Weissmann
 * @since 2022-03-02
 * @version 1.0
 * @see <a href="https://stackoverflow.com/questions/67191617/springboot-feignclient-vs-webclient">Web Client vs. Feign
 *      Client</a>
 *
 */
@RequestMapping("/demoservice/client/v1/forecasts")
interface DemoRestApi {

	/**
	 * The end point that works with a {@link WebClient} for calling the .NET service.
	 * 
	 * @return the {@link ResponseEntity} with a {@code JSON} array of {@link WeatherForecast} items as body and code
	 *         {@code 200} in case of success, never {@code null}
	 */
	@GetMapping(path = "/use-web-client", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<WeatherForecast[]> getWeatherForecastViaWebClient();

	/**
	 * The end point that works with a {@link FeignClient} for calling the .NET service.
	 * 
	 * @return the {@link ResponseEntity} with a {@link List} of {@link WeatherForecast} items as body and code
	 *         {@code 200} in case of success, never {@code null}
	 */
	@GetMapping(path = "/use-feign-client", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<WeatherForecast>> getWeatherForecastViaFeignClient();

	/**
	 * The end point that works with a {@link RestTemplate} for calling the .NET service.
	 * 
	 * @return the {@link ResponseEntity} with a {@code JSON} array of {@link WeatherForecast} items as body and code
	 *         {@code 200} in case of success, never {@code null}
	 */
	@GetMapping(path = "/use-rest-template", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<WeatherForecast[]> getWeatherForecastViaRestTemplate();
}
