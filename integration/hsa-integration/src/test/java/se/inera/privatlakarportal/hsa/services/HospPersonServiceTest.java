package se.inera.privatlakarportal.hsa.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.asm.Handle;
import se.inera.ifv.hsawsresponder.v3.*;
import se.inera.ifv.privatlakarportal.spi.authorization.impl.HSAWebServiceCalls;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HospPersonServiceTest {

    private static final String VALID_PERSON_ID = "1912121212";
    private static final String INVALID_PERSON_ID = "0000000000";
    private static final String CERTIFIER_ID = "CERTIFIER_0001";

    @Mock
    HSAWebServiceCalls hsaWebServiceCalls;

    @InjectMocks
    HospPersonServiceImpl hospPersonService;

    @Before
    public void setupExpectations() {

        GetHospPersonType validParams = new GetHospPersonType();
        validParams.setPersonalIdentityNumber(VALID_PERSON_ID);

        GetHospPersonType invalidParams = new GetHospPersonType();
        invalidParams.setPersonalIdentityNumber(INVALID_PERSON_ID);

        GetHospPersonResponseType response = new GetHospPersonResponseType();
        when(hsaWebServiceCalls.callGetHospPerson(validParams)).thenReturn(response);

        when(hsaWebServiceCalls.callGetHospPerson(invalidParams)).thenReturn(null);
    }

    @Test
    public void testGetHsaPersonInfoWithValidPerson() {

        GetHospPersonResponseType res = hospPersonService.getHospPerson(VALID_PERSON_ID);

        assertNotNull(res);
    }

    @Test
    public void testGetHsaPersonInfoWithInvalidPerson() {

        GetHospPersonResponseType res = hospPersonService.getHospPerson(INVALID_PERSON_ID);

        assertNull(res);
    }


    @Test
    public void testAddToCertifier() {
        HandleCertifierResponseType response = new HandleCertifierResponseType();
        response.setResult("OK");
        when(hsaWebServiceCalls.callHandleCertifier(any(HandleCertifierType.class))).thenReturn(response);

        hospPersonService.addToCertifier(VALID_PERSON_ID, CERTIFIER_ID);

        HandleCertifierType parameters = new HandleCertifierType();
        parameters.setAddToCertifiers(true);
        parameters.setCertifierId(CERTIFIER_ID);
        parameters.setPersonalIdentityNumber(VALID_PERSON_ID);
        verify(hsaWebServiceCalls).callHandleCertifier(parameters);
    }

    @Test
    public void testRemoveFromCertifier() {

        HandleCertifierResponseType response = new HandleCertifierResponseType();
        response.setResult("OK");
        when(hsaWebServiceCalls.callHandleCertifier(any(HandleCertifierType.class))).thenReturn(response);

        hospPersonService.removeFromCertifier(VALID_PERSON_ID, CERTIFIER_ID, "Test");

        HandleCertifierType parameters = new HandleCertifierType();
        parameters.setAddToCertifiers(false);
        parameters.setCertifierId(CERTIFIER_ID);
        parameters.setPersonalIdentityNumber(VALID_PERSON_ID);
        parameters.setReason("Test");
        verify(hsaWebServiceCalls).callHandleCertifier(parameters);
    }

}
