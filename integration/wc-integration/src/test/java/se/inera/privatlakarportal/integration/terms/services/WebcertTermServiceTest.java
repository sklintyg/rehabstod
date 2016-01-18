package se.inera.privatlakarportal.integration.terms.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.privatlakarportal.integration.terms.services.dto.Terms;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionertermsresponder.v1.GetPrivatePractitionerTermsResponseType;
import se.riv.infrastructure.directory.privatepractitioner.terms.v1.AvtalType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by pebe on 2015-08-25.
 */
@RunWith(MockitoJUnitRunner.class)
public class WebcertTermServiceTest {

    @Mock
    private TermsWebServiceCalls client;

    @InjectMocks
    private WebcertTermsServiceImpl webcertTermsService;

    @Test
    public void getTerms() {
        AvtalType avtalType = new AvtalType();
        avtalType.setAvtalText("TestText");
        avtalType.setAvtalVersion(42);
        GetPrivatePractitionerTermsResponseType getPrivatePractitionerTermsResponseType = new GetPrivatePractitionerTermsResponseType();
        getPrivatePractitionerTermsResponseType.setAvtal(avtalType);
        when(client.getPrivatePractitionerTerms()).thenReturn(getPrivatePractitionerTermsResponseType);

        Terms terms = webcertTermsService.getTerms();
        assertEquals("TestText", terms.getText());
        assertEquals(42, terms.getVersion());
    }

    @Test(expected = PrivatlakarportalServiceException.class)
    public void getTermsNull() {
        GetPrivatePractitionerTermsResponseType getPrivatePractitionerTermsResponseType = new GetPrivatePractitionerTermsResponseType();
        when(client.getPrivatePractitionerTerms()).thenReturn(getPrivatePractitionerTermsResponseType);

        Terms terms = webcertTermsService.getTerms();
        assertEquals("TestText", terms.getText());
        assertEquals(42, terms.getVersion());
    }
}
