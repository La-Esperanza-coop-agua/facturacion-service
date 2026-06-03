package cl.esperanza.facturacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient sociosWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8082/api/v1/socios").build();
    }

    @Bean
    public WebClient consumoWebClient(WebClient.Builder builder){
        return builder.baseUrl("http://localhost:8085/api/v1/lectura").build();
    }
}
