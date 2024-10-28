package se.inera.intyg.rehabstod.integration.it.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ItRestClientConfig {

    @Bean
    public RestClient itRestClient() {
        return RestClient.create();
    }
}
