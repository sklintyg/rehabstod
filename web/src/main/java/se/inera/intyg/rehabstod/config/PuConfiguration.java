package se.inera.intyg.rehabstod.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by eriklupander on 2017-03-01.
 */
@Configuration
@ImportResource("classpath:pu-services-config.xml")
@Import(PuStubConfiguration.class)
@ComponentScan("se.inera.intyg.infra.integration.pu.services")
public class PuConfiguration {

    public PuConfiguration() { //NOSONAR
    }
}
