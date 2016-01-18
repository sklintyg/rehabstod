package se.inera.privatlakarportal.hsa.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import se.inera.privatlakarportal.hsa.stub.BootstrapBean;
import se.inera.privatlakarportal.hsa.stub.HsaServiceStub;
import se.inera.privatlakarportal.hsa.stub.HsaWebServiceStub;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan({"se.inera.privatlakarportal.common.config"})
@Profile({"dev", "hsa-stub"})
public class HsaStubConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    BootstrapBean bootstrap() {
        return new BootstrapBean();
    }

    @Bean
    HsaServiceStub hsaServiceStub() {
        return new HsaServiceStub();
    }

    @Bean
    HsaWebServiceStub hsaWebServiceStub() {
        return new HsaWebServiceStub();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public EndpointImpl hsaWsResponder() {
        Bus bus = (Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID);
        Object implementor = hsaWebServiceStub();
        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
        endpoint.publish("/hsa");
        return endpoint;
    }
}
