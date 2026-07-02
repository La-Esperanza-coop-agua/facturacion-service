package cl.esperanza.facturacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient sociosWebClient(@Value("${socios.service.url:http://localhost:8082/api/v1/socios}") String sociosServiceUrl) {
        return WebClient.builder().baseUrl(sociosServiceUrl).build();
    }

    @Bean
    public WebClient consumoWebClient(@Value("${consumo.service.url:http://localhost:8085/api/v1/lectura}") String lecturaServiceUrl){
        return WebClient.builder().baseUrl(lecturaServiceUrl).build();
    }
}
