package se.inera.intyg.rehabstod.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by eriklupander on 2017-03-01.
 */
@Configuration
@ComponentScan({"se.inera.intyg.privatlakarportal.common"})
@ImportResource("classpath:pu-stub-context.xml")
@Profile({"dev", "pu-stub", "wc-pu-stub"})
public class PuStubConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}

