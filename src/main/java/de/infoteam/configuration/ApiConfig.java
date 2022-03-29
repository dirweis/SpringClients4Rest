package de.infoteam.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import feign.Client;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import reactor.netty.http.client.HttpClient;

/**
 * A configuration bean providing a {@link WebClient}, a {@link RestTemplate} and a {@link FeignClient} {@link Bean}s
 * for further use.
 * <p>
 * The preparation that is done here consists of 2 steps:
 * <ul>
 * <li>Secure the clients with a {@code TLS} security, based on the certificate the {@code .NET} service uses</li>
 * <li>Prepare the clients with the base URL to the secured service ({@code HTTPS})</li>
 * </ul>
 * 
 * @author Dirk Weissmann
 * @since 2022-03-03
 * @version 2.0
 * 
 * @see <a href="https://www.computerweekly.com/de/definition/Transport-Layer-Security-TLS">TLS (German)</a>
 *
 */
@Configuration
@NoArgsConstructor
@Log4j2
public class ApiConfig {

	@Value("${doch.net.security.baseUrl}")
	private String securedUrl;

	@Value("${doch.net.security.client_pw}")
	private char[] clientPw;

	@Value("${doch.net.security.server_pw}")
	private char[] serverPw;

	/**
	 * Provides the {@link WebClient} {@link Bean} for further use with a configuration containing the secured
	 * ({@code HTTPS}) URL to the {@code .NET} service as well as the TLS support.
	 * 
	 * @param builder the object based on the {@code Builder} pattern for creating a {@link WebClient} {@link Bean}
	 * 
	 * @return the {@link WebClient} bean, never {@code null}
	 */
	@Bean
	@SneakyThrows
	WebClient webClient(final WebClient.Builder builder) {
		final SslContext sslContext = SslContextBuilder.forClient().keyManager(createKeyManagerFactory())
				.trustManager(createTrustManagerFactory()).build();
		final HttpClient client = HttpClient.create().secure(spec -> spec.sslContext(sslContext));
		final ClientHttpConnector connector = new ReactorClientHttpConnector(client);

		return WebClient.builder().baseUrl(securedUrl).clientConnector(connector).build();
	}

	/**
	 * Provides a {@link FeignClient} that is trusted for calling the secured .NET service.
	 * 
	 * @return a TLS-ready {@link FeignClient} for using the {@code X509} certificate secured .NET service
	 */
	@Bean
	@SneakyThrows
	public Client feignClient() {
		return new Client.Default(createSSLContext().getSocketFactory(), null);
	}

	/**
	 * Provides the {@link RestTemplate} {@link Bean} for further use with a configuration containing the URL and the
	 * key-/truststore to the secured .NET service.
	 * 
	 * @param builder the object based on the {@code Builder} pattern for creating a {@link RestTemplate} {@link Bean}
	 * 
	 * @return the {@link RestTemplate} bean, never {@code null}
	 */
	@Bean
	@SneakyThrows
	RestTemplate restTemplate(final RestTemplateBuilder builder) {
		final HostnameVerifier verifier = (final String ownHostname, final SSLSession sslSession) -> {
			try {
				return new URL(securedUrl).openConnection() != null;
			} catch (IOException e) {
				log.debug(e);
				return false;
			}
		};

		final SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(createSSLContext(), verifier);
		final CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
		final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

		return builder.requestFactory(() -> requestFactory).rootUri(securedUrl).build();
	}

	/**
	 * Creates the {@link KeyManagerFactory} for the client's {@code keystore} on the given keystore file.
	 * 
	 * @return the {@link KeyManagerFactory}, never {@code null}; in case of an error an {@link Exception} is thrown
	 * 
	 * @see <a href="https://www.tabnine.com/code/java/classes/javax.net.ssl.KeyManagerFactory">Code examples</a>
	 */
	@SneakyThrows
	private KeyManagerFactory createKeyManagerFactory() {
		try (final FileInputStream keyStoreFileInputStream = new FileInputStream(
				ResourceUtils.getFile("classpath:client.keystore"))) {
			final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

			keyStore.load(keyStoreFileInputStream, clientPw);

			final KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());

			keyManagerFactory.init(keyStore, clientPw);

			return keyManagerFactory;
		}
	}

	/**
	 * Creates the {@link TrustManagerFactory} for the client's {@code truststore} on the given keystore file.
	 * 
	 * @return the {@link TrustManagerFactory}, never {@code null}; in case of an error an {@link Exception} is thrown
	 * 
	 * @see <a href="https://www.programcreek.com/java-api-examples/?api=javax.net.ssl.TrustManagerFactory">Code
	 *      examples</a>
	 */
	@SneakyThrows
	private TrustManagerFactory createTrustManagerFactory() {
		try (final FileInputStream trustStoreFileInputStream = new FileInputStream(
				ResourceUtils.getFile("classpath:server.truststore"))) {
			final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

			trustStore.load(trustStoreFileInputStream, serverPw);

			final TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());

			trustManagerFactory.init(trustStore);

			return trustManagerFactory;
		}
	}

	/**
	 * Provides the {@link SSLContext} that is used for the {@link FeignClient}'s and the {@link RestTemplate}'s
	 * {@code TLS} support.
	 * 
	 * @return the loaded {@link SSLContext}, never {@code null}
	 */
	@SneakyThrows
	private SSLContext createSSLContext() {
		return new SSLContextBuilder().loadTrustMaterial(ResourceUtils.getFile("classpath:server.truststore"), serverPw)
				.loadKeyMaterial(ResourceUtils.getFile("classpath:client.keystore"), clientPw, clientPw).build();
	}
}
