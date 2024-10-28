package se.inera.intyg.rehabstod.integration.wc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WcRestClientConfig {

    @Bean
    public RestClient wcRestClient() {
        return RestClient.create();
    }
}
