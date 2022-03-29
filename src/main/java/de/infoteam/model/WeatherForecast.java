package de.infoteam.model;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * The DTO for receiving and sending the data in the response bodies. For the sake of Java 16 realized as record.
 * 
 * @author Dirk Weissmann
 * @since 2022-03-03
 * @version 1.0
 * 
 * @param date         the date and time of the weather's forecast, must not be {@code null}
 * @param temperatureC the temperature in Celcius of the weather's forecast, must not be {@code null}
 * @param temperatureF the temperature in Fahrenheit of the weather's forecast, must not be {@code null}
 * @param summary      a summary text, may be {@code null}
 *
 */
public record WeatherForecast(@NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") LocalDateTime date,
		@NotNull @Min(-20) @Max(55) Integer temperatureC, @NotNull @Min(-4) @Max(131) Integer temperatureF,
		@Size(min = 3, max = 15) String summary) {
	/* Simple record, nothing to implement here */
}
