package se.inera.privatlakarportal.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.support.ServletContextAttributeExporter;

import se.inera.privatlakarportal.service.monitoring.HealthCheckService;
import se.inera.privatlakarportal.service.monitoring.PingForConfigurationResponderImpl;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerterms.v1.rivtabp21.GetPrivatePractitionerTermsResponderInterface;

/**
 * Created by pebe on 2015-09-07.
 */
@Configuration
@ComponentScan("se.inera.privatlakarportal.service, se.inera.privatlakarportal.common.service")
@EnableScheduling
public class ServiceConfig {

    @Autowired
    HealthCheckService healtCheckService;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${terms.ws.services.url}")
    private String termsUrl;

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

    @Bean
    public GetPrivatePractitionerTermsResponderInterface termsWebServiceClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceClass(GetPrivatePractitionerTermsResponderInterface.class);
        proxyFactoryBean.setAddress(termsUrl);
        return (GetPrivatePractitionerTermsResponderInterface) proxyFactoryBean.create();
    }
}
