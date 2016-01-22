package se.inera.intyg.rehabstod.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import se.inera.intyg.common.integration.hsa.stub.BootstrapBean;
import se.inera.intyg.common.integration.hsa.stub.HsaServiceStub;

/**
 * Created by eriklupander on 2016-01-18.
 */
@Configuration
@ComponentScan({"se.inera.intyg.rehabstod.common"})
@ImportResource("classpath:hsa-stub-context.xml")
@Profile({"dev", "hsa-stub"})
public class HsaStubConfiguration {

    @Bean
    HsaServiceStub hsaServiceStub() {
        return new HsaServiceStub();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
