package se.inera.intyg.rehabstod.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import se.inera.intyg.rehabstod.integration.hsa.stub.BootstrapBean;
import se.inera.intyg.rehabstod.integration.hsa.stub.GetEmployeeResponderStub;
import se.inera.intyg.rehabstod.integration.hsa.stub.HsaServiceStub;

/**
 * Created by eriklupander on 2016-01-18.
 */
@Configuration
@ComponentScan({"se.inera.intyg.rehabstod.common"})
@ImportResource("classpath:hsa-stub-context.xml")
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

//    @Bean
//    GetEmployeeResponderStub getEmployeeStub() {
//        return new GetEmployeeResponderStub();
//    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

//    @Bean
//    public EndpointImpl getEmployeeIncludingProtectedPersonResponder() {
//        Bus bus = (Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID);
//        Object implementor = getEmployeeStub();
//        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
//        endpoint.publish("/hsa");
//        return endpoint;
//    }

//    @Bean
//    public EndpointImpl hsaWsResponder() {
//        Bus bus = (Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID);
//        Object implementor = hsaWebServiceStub();
//        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
//        endpoint.publish("/hsa");
//        return endpoint;
//    }
}
