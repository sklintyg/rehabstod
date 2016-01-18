package se.inera.privatlakarportal.integration.terms.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import se.inera.privatlakarportal.integration.terms.stub.TermsWebServiceStub;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Profile({"dev", "wc-stub"})
public class TermsStubConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    TermsWebServiceStub termsWebServiceStub() {
        return new TermsWebServiceStub();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public EndpointImpl termsWsResponder() {
        Bus bus = (Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID);
        Object implementor = termsWebServiceStub();
        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
        endpoint.publish("/terms");
        return endpoint;
    }
}
