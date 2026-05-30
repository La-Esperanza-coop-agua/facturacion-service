package cl.esperanza.facturacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient sociosWebClient() {
        // En lugar de pedirle a Spring el Builder por parámetro,
        // lo instanciamos explícitamente usando WebClient.builder()
        return WebClient.builder()
                .baseUrl("http://localhost:8082/api/v1/socios")
                .build();
    }
}