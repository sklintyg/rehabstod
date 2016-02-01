package se.inera.intyg.rehabstod.integration.it.stub;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan({"se.inera.intyg.rehabstod.integration.it.stub"})
@Profile({"dev", "rhs-it-stub"})
public class IntygstjanstIntegrationStubConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SjukfallIntygStub sjukfallIntygStub;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public EndpointImpl intygstjanstResponder() {
        Bus bus = (Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID);
        Object implementor = sjukfallIntygStub;
        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
        endpoint.publish("/get-active-sickleaves-for-careunit/v1.0");
        return endpoint;
    }
}
