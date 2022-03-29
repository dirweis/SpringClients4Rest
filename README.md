# SpringClients4Rest

Comparison for the 3 commonly used REST clients in Spring:
 - WebClient (see also the [Baeldung page](https://www.baeldung.com/spring-5-webclient))
 - FeignClient (OpenFeign, see also the [Spring Cloud page](https://cloud.spring.io/spring-cloud-openfeign/reference/html))
 - RestTemplate (see also the [Baeldung page](https://www.baeldung.com/rest-template))

The clients fire the requests on the .Net REST example project, see also the [Microsoft Tutorial](https://docs.microsoft.com/de-de/aspnet/core/tutorials/first-web-api?view=aspnetcore-6.0&tabs=visual-studio).

:warning: **Don't forget to update the files `client.keystore` and `server.truststore`**.

Else you have to deactivate the HTTPS connection in the .Net service.