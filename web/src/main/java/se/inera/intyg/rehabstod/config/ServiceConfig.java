package se.inera.intyg.rehabstod.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.support.ServletContextAttributeExporter;

import se.inera.intyg.rehabstod.service.monitoring.HealthCheckService;
import se.inera.intyg.rehabstod.service.monitoring.PingForConfigurationResponderImpl;

/**
 * Created by pebe on 2015-09-07.
 */
@Configuration
@ComponentScan("se.inera.intyg.rehabstod.service, se.inera.intyg.rehabstod.auth, se.inera.intyg.rehabstod.common.service")
@EnableScheduling
public class ServiceConfig {

    @Autowired
    HealthCheckService healtCheckService;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ServletContextAttributeExporter contextAttributes() {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("healthcheck", healtCheckService);
        final ServletContextAttributeExporter exporter = new ServletContextAttributeExporter();
        exporter.setAttributes(attributes);
        return exporter;
    }

    @Bean
    public PingForConfigurationResponderImpl pingForConfigurationResponder() {
        return new PingForConfigurationResponderImpl();
    }
    
    @Bean
    public EndpointImpl pingForConfigurationEndpoint() {
        Bus bus = (Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID);
        Object implementor = pingForConfigurationResponder();
        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
        endpoint.publish("/ping-for-configuration");
        return endpoint;
    }


}
