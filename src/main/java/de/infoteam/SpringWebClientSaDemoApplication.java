package de.infoteam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * The main class of the service. Does not much but running the service indefinitely (until stopped explicitly).
 * 
 * @author Dirk Weissmann
 * @since 2022-03-03
 * @version 1.0
 *
 */
@SpringBootApplication(exclude = { MultipartAutoConfiguration.class, JmxAutoConfiguration.class })
@EnableFeignClients
public class SpringWebClientSaDemoApplication {

	/**
	 * Runs the service.
	 * 
	 * @param args usually empty
	 */
	public static void main(final String[] args) {
		SpringApplication.run(SpringWebClientSaDemoApplication.class, args);
	}
}
