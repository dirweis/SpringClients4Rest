package de.infoteam.feign;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import de.infoteam.configuration.ApiConfig;
import de.infoteam.model.WeatherForecast;

/**
 * The {@link FeignClient} for accessing the .NET service. All that is needed is the endpoint's definition in this
 * interface - no implementation. Compared to the other possibilities here are the advantages:
 * <ul>
 * <li>Great code reduction due to interfaces only</li>
 * <li>Full Spring support for validation of the response body</li>
 * <li>Write the interface like the one for your own service (native implementing)</li>
 * <li>Fully object-oriented. No array or conversion is needed for retrieving a {@code JSON} array as a Java
 * {@link Collection} like e.g {@link List} or {@link Set}</li>
 * </ul>
 * <p>
 * ...and - of course - the disadvantages :D
 * <ul>
 * <li>No asynchronous calls possible</li>
 * <li>Dynamic URL processing (which can occur in HATEOAS roundtrips) is ugly, see also the <a href=
 * "https://stackoverflow.com/questions/43733569/how-can-i-change-the-feign-url-during-the-runtime">Stackoverflow
 * Discussion</a></li>
 * </ul>
 * <p>
 * <em>For activation don't forget the annotation {@link EnableFeignClients} in your main class or in a {@code Feign}
 * {@link Configuration} bean.</em>
 * 
 * @author Dirk Weissmann
 * @since 2022-03-03
 * @version 1.0
 * 
 * @see <a href="https://stackoverflow.com/questions/67191617/springboot-feignclient-vs-webclient">WebClient vs.
 *      OpenFeign on Stackoverflow</a>
 *
 */
@FeignClient(url = "${doch.net.security.baseUrl}", name = "feignclient", configuration = ApiConfig.class)
@Validated
public interface FeignClientDochNet {

	/**
	 * The endpoint from the {@code .NET} service that is to be called.
	 * 
	 * @return a {@link List} of {@link WeatherForecast} items, never {@code null}
	 */
	@GetMapping("/WeatherForecast")
	List<@Valid WeatherForecast> getForecasts();
}
