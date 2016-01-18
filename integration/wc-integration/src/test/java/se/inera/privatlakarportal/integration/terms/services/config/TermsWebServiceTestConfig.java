package se.inera.privatlakarportal.integration.terms.services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import se.inera.privatlakarportal.integration.terms.services.TermsWebServiceCalls;
import se.inera.privatlakarportal.integration.terms.stub.TermsWebServiceStub;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerterms.v1.rivtabp21.GetPrivatePractitionerTermsResponderInterface;

@Configuration
public class TermsWebServiceTestConfig {

    @Bean
    public GetPrivatePractitionerTermsResponderInterface termsWebServiceClient() {
        return new TermsWebServiceStub();
    }

    @Bean 
    public TermsWebServiceCalls testTermsWSCalls() {
        return new TermsWebServiceCalls();
    }
}
