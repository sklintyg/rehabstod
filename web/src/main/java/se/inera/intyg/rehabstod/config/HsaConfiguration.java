package se.inera.intyg.rehabstod.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by eriklupander on 2016-01-18.
 */
@Configuration
@ComponentScan({"se.inera.intyg.rehabstod.integration.hsa.services"})
@Import(HsaStubConfiguration.class)
@ImportResource("classpath:hsa-services-config.xml")
public class HsaConfiguration {

}
