package se.inera.privatlakarportal.integration.terms.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import se.inera.privatlakarportal.integration.terms.services.config.TermsWebServiceTestConfig;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionertermsresponder.v1.GetPrivatePractitionerTermsResponseType;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "dev")
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = TermsWebServiceTestConfig.class)
public class TermsWebServiceCallsTest {
    private static final int AVTAL_VERSION = 1;
    private static final String AVTAL_VERSION_DATUM = "2015-09-30T00:00:00.000";

    @Autowired
    private TermsWebServiceCalls testTermsWS;

    @Test
    public void testTerms() {
        GetPrivatePractitionerTermsResponseType response = testTermsWS.getPrivatePractitionerTerms();
        assertEquals(AVTAL_VERSION_DATUM, response.getAvtal().getAvtalVersionDatum().toString());
        assertEquals(AVTAL_VERSION, response.getAvtal().getAvtalVersion());
    }

}
