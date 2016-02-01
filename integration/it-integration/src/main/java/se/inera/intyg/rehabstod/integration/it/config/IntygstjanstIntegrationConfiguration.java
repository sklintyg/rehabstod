package se.inera.intyg.rehabstod.integration.it.config;

import org.apache.cxf.Bus;

import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import se.inera.intyg.rehabstod.integration.it.stub.SjukfallIntygStub;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan({
        "se.inera.intyg.rehabstod.integration.it.client",
        "se.inera.intyg.rehabstod.integration.it.service",
        "se.inera.intyg.rehabstod.integration.it.stub"})
@ImportResource("classpath:it-services.xml")
public class IntygstjanstIntegrationConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SjukfallIntygStub sjukfallIntygStub;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public EndpointImpl termsWsResponder() {
        Bus bus = (Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID);
        Object implementor = sjukfallIntygStub;
        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
        endpoint.publish("/get-active-sickleaves-for-careunit/v1.0");
        return endpoint;
    }
}
