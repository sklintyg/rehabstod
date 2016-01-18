package se.inera.privatlakarportal.pu.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import se.inera.privatlakarportal.pu.stub.LookupResidentForFullProfileWsStub;
import se.inera.privatlakarportal.pu.stub.PUBootstrapBean;
import se.inera.privatlakarportal.pu.stub.ResidentStore;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Profile({"dev", "pu-stub"})
public class PUStubConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    PUBootstrapBean puBootstrap() {
        return new PUBootstrapBean();
    }

    @Bean
    ResidentStore residentStore() {
        return new ResidentStore();
    }

    @Bean
    LookupResidentForFullProfileWsStub lookupResidentForFullProfileWsStub() {
        return new LookupResidentForFullProfileWsStub();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public EndpointImpl puWsResponder() {
        Bus bus = (Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID);
        Object implementor = lookupResidentForFullProfileWsStub();
        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
        endpoint.publish("/pu");
        return endpoint;
    }
}
