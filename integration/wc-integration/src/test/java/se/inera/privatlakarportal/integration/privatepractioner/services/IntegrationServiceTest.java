package se.inera.privatlakarportal.integration.privatepractioner.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import se.inera.privatlakarportal.common.integration.json.CustomObjectMapper;
import se.inera.privatlakarportal.common.service.DateHelperService;
import se.inera.privatlakarportal.hsa.services.HospUpdateService;
import se.inera.privatlakarportal.persistence.model.*;
import se.inera.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerresponder.v1.GetPrivatePractitionerResponseType;
import se.riv.infrastructure.directory.privatepractitioner.v1.HoSPersonType;
import se.riv.infrastructure.directory.privatepractitioner.v1.ResultCodeEnum;
import se.riv.infrastructure.directory.privatepractitioner.validateprivatepractitionerresponder.v1.ValidatePrivatePractitionerResponseType;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by pebe on 2015-08-18.
 */
@RunWith(MockitoJUnitRunner.class)
public class IntegrationServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationServiceTest.class);

    @Mock
    private PrivatlakareRepository privatlakareRepository;

    @Mock
    private HospUpdateService hospUpdateService;

    @Mock
    private DateHelperService dateHelperService;

    @InjectMocks
    private IntegrationServiceImpl integrationService;

    private static final String EJ_GODKAND_HSA_ID = "nonExistingId";
    private static final String EJ_GODKAND_PERSON_ID = "nonExistingId";
    private static final String EJ_LAKARE_HSA_ID = "ejLakareHsaId";
    private static final String EJ_LAKARE_PERSON_ID = "ejLakarePersonId";
    private static final String GODKAND_HSA_ID = "existingHsaId";
    private static final String GODKAND_PERSON_ID = "existingPersonId";
    private static final String FINNS_EJ_HSA_ID = "nonExistingHsaId";
    private static final String FINNS_EJ_PERSON_ID = "nonExistingPersonId";

    private HoSPersonType verifyHosPerson;

    @Before
    public void setup() throws IOException {
        ObjectMapper objectMapper = new CustomObjectMapper();

        Resource res = new ClassPathResource("IntegrationServiceTest/test_Privatlakare.json");
        Privatlakare privatlakare = objectMapper.readValue(res.getInputStream(), Privatlakare.class);

        Privatlakare privatlakare_ej_godkand = objectMapper.readValue(res.getInputStream(), Privatlakare.class);
        privatlakare_ej_godkand.setGodkandAnvandare(false);

        Privatlakare privatlakare_ej_lakare = objectMapper.readValue(res.getInputStream(), Privatlakare.class);
        Set<LegitimeradYrkesgrupp> legitimeradYrkesgrupper = new HashSet<>();
        legitimeradYrkesgrupper.add(new LegitimeradYrkesgrupp(privatlakare, "Dietist", "DT"));
        privatlakare_ej_lakare.setLegitimeradeYrkesgrupper(legitimeradYrkesgrupper);

        res = new ClassPathResource("IntegrationServiceTest/test_HosPerson.json");
        verifyHosPerson = objectMapper.readValue(res.getInputStream(), HoSPersonType.class);

        when(privatlakareRepository.findByHsaId(GODKAND_HSA_ID)).thenReturn(privatlakare);
        when(privatlakareRepository.findByPersonId(GODKAND_PERSON_ID)).thenReturn(privatlakare);
        when(privatlakareRepository.findByHsaId(FINNS_EJ_HSA_ID)).thenReturn(null);
        when(privatlakareRepository.findByPersonId(FINNS_EJ_PERSON_ID)).thenReturn(null);
        when(privatlakareRepository.findByHsaId(EJ_GODKAND_HSA_ID)).thenReturn(privatlakare_ej_godkand);
        when(privatlakareRepository.findByPersonId(EJ_GODKAND_PERSON_ID)).thenReturn(privatlakare_ej_godkand);
        when(privatlakareRepository.findByHsaId(EJ_LAKARE_HSA_ID)).thenReturn(privatlakare_ej_lakare);
        when(privatlakareRepository.findByPersonId(EJ_LAKARE_PERSON_ID)).thenReturn(privatlakare_ej_lakare);

        when(dateHelperService.now()).thenReturn(LocalDateTime.parse("2015-09-09"));
    }

    @Test
    public void testGetPrivatePractitionerByHsaId() {
        GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByHsaId(GODKAND_HSA_ID);
        assertEquals(ResultCodeEnum.OK, response.getResultCode());
        assertNotNull(response.getHoSPerson());
        ReflectionAssert.assertReflectionEquals(verifyHosPerson, response.getHoSPerson(), ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void testGetPrivatePractitionerByPersonId() {
        GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByPersonId(GODKAND_PERSON_ID);
        assertEquals(ResultCodeEnum.OK, response.getResultCode());
        assertNotNull(response.getHoSPerson());
        ReflectionAssert.assertReflectionEquals(verifyHosPerson, response.getHoSPerson(), ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void testGetPrivatePractitionerByHsaIdNonExisting() {
        GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByHsaId(FINNS_EJ_HSA_ID);
        assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
        assertNull(response.getHoSPerson());
    }

    @Test
    public void testGetPrivatePractitionerByPersonIdNonExisting() {
        GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByPersonId(FINNS_EJ_PERSON_ID);
        assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
        assertNull(response.getHoSPerson());
    }

    @Test
    public void testValidatePrivatePractitionerByHsaId() {
        ValidatePrivatePractitionerResponseType response = integrationService.validatePrivatePractitionerByHsaId(GODKAND_HSA_ID);
        assertEquals(ResultCodeEnum.OK, response.getResultCode());

        // Startdates should NOT be updated to current time
        Privatlakare privatlakare = privatlakareRepository.findByHsaId(GODKAND_HSA_ID);
        assertEquals(verifyHosPerson.getEnhet().getStartdatum(), privatlakare.getEnhetStartdatum());
        assertEquals(verifyHosPerson.getEnhet().getVardgivare().getStartdatum(), privatlakare.getVardgivareStartdatum());
        // HospUpdateService should be called to verify that privatlakare still has lakarbehorighet
        verify(hospUpdateService).checkForUpdatedHospInformation(privatlakare);
    }

    @Test
    public void testValidatePrivatePractitionerByPersonId() {
        ValidatePrivatePractitionerResponseType response = integrationService.validatePrivatePractitionerByPersonId(GODKAND_PERSON_ID);
        assertEquals(ResultCodeEnum.OK, response.getResultCode());

        // Startdates should NOT be updated to current time
        Privatlakare privatlakare = privatlakareRepository.findByHsaId(GODKAND_HSA_ID);
        assertEquals(verifyHosPerson.getEnhet().getStartdatum(), privatlakare.getEnhetStartdatum());
        assertEquals(verifyHosPerson.getEnhet().getVardgivare().getStartdatum(), privatlakare.getVardgivareStartdatum());
        // HospUpdateService should be called to verify that privatlakare still has lakarbehorighet
        verify(hospUpdateService).checkForUpdatedHospInformation(privatlakare);
    }

    @Test
    public void testValidatePrivatePractitionerByHsaIdFirstLogin() {
        Privatlakare privatlakare = privatlakareRepository.findByHsaId(GODKAND_HSA_ID);
        privatlakare.setEnhetStartdatum(null);
        privatlakare.setVardgivareStartdatum(null);

        ValidatePrivatePractitionerResponseType response = integrationService.validatePrivatePractitionerByHsaId(GODKAND_HSA_ID);
        assertEquals(ResultCodeEnum.OK, response.getResultCode());

        // Startdates should be updated to current time
        assertNotNull(privatlakare.getEnhetStartdatum());
        assertNotNull(privatlakare.getVardgivareStartdatum());
    }

    @Test
    public void testValidatePrivatePractitionerByPersonIdFirstLogin() {
        Privatlakare privatlakare = privatlakareRepository.findByHsaId(GODKAND_HSA_ID);
        privatlakare.setEnhetStartdatum(null);
        privatlakare.setVardgivareStartdatum(null);

        ValidatePrivatePractitionerResponseType response = integrationService.validatePrivatePractitionerByPersonId(GODKAND_PERSON_ID);
        assertEquals(ResultCodeEnum.OK, response.getResultCode());

        // Startdates should be updated to current time
        assertNotNull(privatlakare.getEnhetStartdatum());
        assertNotNull(privatlakare.getVardgivareStartdatum());
    }

    @Test
    public void testValidatePrivatePractitionerByHsaIdEjGodkand() {
        ValidatePrivatePractitionerResponseType response = integrationService.validatePrivatePractitionerByHsaId(EJ_GODKAND_HSA_ID);
        assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    }

    @Test
    public void testValidatePrivatePractitionerByPersonIdEjGodkänd() {
        ValidatePrivatePractitionerResponseType response = integrationService.validatePrivatePractitionerByPersonId(EJ_GODKAND_PERSON_ID);
        assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    }

    @Test
    public void testValidatePrivatePractitionerByHsaIdEjLakare() {
        ValidatePrivatePractitionerResponseType response = integrationService.validatePrivatePractitionerByHsaId(EJ_LAKARE_HSA_ID);
        assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    }

    @Test
    public void testValidatePrivatePractitionerByPersonIdEjLakare() {
        ValidatePrivatePractitionerResponseType response = integrationService.validatePrivatePractitionerByPersonId(EJ_LAKARE_PERSON_ID);
        assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    }

    @Test
    public void testValidatePrivatePractitionerByHsaIdNonExisting() {
        ValidatePrivatePractitionerResponseType response = integrationService.validatePrivatePractitionerByHsaId(FINNS_EJ_HSA_ID);
        assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    }

    @Test
    public void testValidatePrivatePractitionerByPersonIdNonExisting() {
        ValidatePrivatePractitionerResponseType response = integrationService.validatePrivatePractitionerByHsaId(FINNS_EJ_PERSON_ID);
        assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    }

}
