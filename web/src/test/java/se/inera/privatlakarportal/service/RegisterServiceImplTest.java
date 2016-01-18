package se.inera.privatlakarportal.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.mail.MessagingException;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.core.io.ClassPathResource;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import se.inera.ifv.hsawsresponder.v3.GetHospPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.HsaTitlesType;
import se.inera.ifv.hsawsresponder.v3.SpecialityNamesType;
import se.inera.privatlakarportal.auth.PrivatlakarUser;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.privatlakarportal.common.integration.json.CustomObjectMapper;
import se.inera.privatlakarportal.common.model.Registration;
import se.inera.privatlakarportal.common.model.RegistrationStatus;
import se.inera.privatlakarportal.common.service.DateHelperService;
import se.inera.privatlakarportal.common.service.MailService;
import se.inera.privatlakarportal.common.service.stub.MailStubStore;
import se.inera.privatlakarportal.hsa.services.HospPersonService;
import se.inera.privatlakarportal.hsa.services.HospUpdateService;
import se.inera.privatlakarportal.hsa.services.exception.HospUpdateFailedToContactHsaException;
import se.inera.privatlakarportal.persistence.model.Befattning;
import se.inera.privatlakarportal.persistence.model.LegitimeradYrkesgrupp;
import se.inera.privatlakarportal.persistence.model.Medgivande;
import se.inera.privatlakarportal.persistence.model.MedgivandeText;
import se.inera.privatlakarportal.persistence.model.Privatlakare;
import se.inera.privatlakarportal.persistence.model.PrivatlakareId;
import se.inera.privatlakarportal.persistence.model.Specialitet;
import se.inera.privatlakarportal.persistence.model.Vardform;
import se.inera.privatlakarportal.persistence.model.Verksamhetstyp;
import se.inera.privatlakarportal.persistence.repository.MedgivandeTextRepository;
import se.inera.privatlakarportal.persistence.repository.PrivatlakareIdRepository;
import se.inera.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.privatlakarportal.service.exception.PrivatlakarportalServiceExceptionMatcher;
import se.inera.privatlakarportal.service.model.HospInformation;
import se.inera.privatlakarportal.service.model.RegistrationWithHospInformation;
import se.inera.privatlakarportal.service.model.SaveRegistrationResponseStatus;
import se.inera.privatlakarportal.service.monitoring.MonitoringLogService;

@RunWith(MockitoJUnitRunner.class)
public class RegisterServiceImplTest {

    private final String PERSON_ID = "191212121212";

    @Mock
    private PrivatlakareRepository privatlakareRepository;

    @Mock
    private PrivatlakareIdRepository privatlakareidRepository;

    @Mock
    private MedgivandeTextRepository medgivandeTextRepository;

    @Mock
    private HospPersonService hospPersonService;

    @Mock
    private HospUpdateService hospUpdateService;

    @Mock
    private UserService userService;

    @Mock
    private MailService mailService;

    @Mock
    private DateHelperService dateHelperService;

    @Mock
    private MonitoringLogService monitoringLogService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule 
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private RegisterService registerService = new RegisterServiceImpl();

    private MailStubStore mailStore = new MailStubStore();

    private Privatlakare readPrivatlakare(String path) throws IOException {
        Privatlakare verifyPrivatlakare = new CustomObjectMapper().readValue(new ClassPathResource(
                path).getFile(), Privatlakare.class);
        for (Befattning befattning : verifyPrivatlakare.getBefattningar()) {
            befattning.setPrivatlakare(verifyPrivatlakare);
        }
        for (Verksamhetstyp verksamhetstyp : verifyPrivatlakare.getVerksamhetstyper()) {
            verksamhetstyp.setPrivatlakare(verifyPrivatlakare);
        }
        for (Vardform vardform : verifyPrivatlakare.getVardformer()) {
            vardform.setPrivatlakare(verifyPrivatlakare);
        }
        for (Medgivande medgivande : verifyPrivatlakare.getMedgivande()) {
            medgivande.setPrivatlakare(verifyPrivatlakare);
        }
        return verifyPrivatlakare;
    }


    private Registration createValidRegistration() {
        Registration registration = new Registration();

        registration.setVerksamhetensNamn("Test verksamhet");
        registration.setArbetsplatskod("0000000");
        registration.setAgarForm("Privat");
        registration.setAdress("Test adress");
        registration.setPostnummer("12345");
        registration.setPostort("Test postort");
        registration.setTelefonnummer("123456789");
        registration.setEpost("test@example.com");
        registration.setBefattning("201010");
        registration.setVerksamhetstyp("10");
        registration.setVardform("01");
        registration.setLan("Test län");
        registration.setKommun("Test kommun");

        return registration;
    }

    @Before
    public void setup() {
        PrivatlakarUser privatlakarUser = new PrivatlakarUser(PERSON_ID, "Test User");
        privatlakarUser.updateNameFromPuService("Test User");
        when(userService.getUser()).thenReturn(privatlakarUser);

        MedgivandeText medgivandeText = new MedgivandeText();
        medgivandeText.setDatum(LocalDateTime.parse("2015-08-01"));
        medgivandeText.setMedgivandeText("Medgivandetext");
        medgivandeText.setVersion(1L);
        when(medgivandeTextRepository.findOne(1L)).thenReturn(medgivandeText);

        when(dateHelperService.now()).thenReturn(LocalDateTime.parse("2015-09-09"));

        registerService.injectHsaInterval(50);
    }

    @Test
    public void testHsaMailSent() throws HospUpdateFailedToContactHsaException, IOException, MessagingException {

        // Notify admin every 50 registrations
        final int SEND_HSA_MAIL_INTERVAL = 50;

        Mockito.doAnswer(new Answer<Object>() {
                    public Object answer(InvocationOnMock invocation) {
                        mailStore.addMail("ADMIN-EMAIL", "TEST");
                        return null;
                    }})
                    .when(mailService).sendHsaGenerationStatusEmail();

        // Create enough registrations to reach the threshold
        for (int i = 1; i <= SEND_HSA_MAIL_INTERVAL; i++) {
            PrivatlakareId privatlakareId = new PrivatlakareId();
            privatlakareId.setId(i);
            when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);
            when(privatlakareidRepository.findLatestGeneratedHsaId()).thenReturn(new Integer(i));

            when(hospUpdateService.updateHospInformation(any(Privatlakare.class), eq(true))).thenReturn(RegistrationStatus.AUTHORIZED);
            Registration registration = createValidRegistration();
            registerService.createRegistration(registration, 1L);
        }
        assertTrue(mailStore.getMails().containsKey("ADMIN-EMAIL"));
    }

    @Test
    public void testHsaMailThreshold() throws HospUpdateFailedToContactHsaException, IOException, MessagingException {
        // Notify admin every 50 registrations
        final int SEND_HSA_MAIL_INTERVAL = 49;

        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                mailStore.addMail("ADMIN-EMAIL", "TEST");
                return null;
            }})
            .when(mailService).sendHsaGenerationStatusEmail();

        // Create one less registration than the threshold
        for (int i = 1; i <= SEND_HSA_MAIL_INTERVAL; i++) {
            PrivatlakareId privatlakareId = new PrivatlakareId();
            privatlakareId.setId(i);
            when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);
            when(privatlakareidRepository.findLatestGeneratedHsaId()).thenReturn(new Integer(i));
            
            when(hospUpdateService.updateHospInformation(any(Privatlakare.class), eq(true))).thenReturn(RegistrationStatus.AUTHORIZED);
            Registration registration = createValidRegistration();
            registerService.createRegistration(registration, 1L);
        }
        assertFalse(mailStore.getMails().containsKey("ADMIN-EMAIL"));
    }

    @Test
    public void testInvalidCreateRegistration() {

        thrown.expect(PrivatlakarportalServiceException.class);
        thrown.expect(PrivatlakarportalServiceExceptionMatcher.hasErrorCode(PrivatlakarportalErrorCodeEnum.BAD_REQUEST));

        Registration registration = new Registration();
        registerService.createRegistration(registration, 1L);
    }

    @Test
    public void testcreatePrivatlakareAlreadyExists() {

        thrown.expect(PrivatlakarportalServiceException.class);
        thrown.expect(PrivatlakarportalServiceExceptionMatcher.hasErrorCode(PrivatlakarportalErrorCodeEnum.ALREADY_EXISTS));

        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(new Privatlakare());

        Registration registration = createValidRegistration();
        RegistrationStatus response = registerService.createRegistration(registration, 1L);
    }

    @Test
    public void testCreateRegistrationLakare() throws IOException, HospUpdateFailedToContactHsaException {

        PrivatlakareId privatlakareId = new PrivatlakareId();
        privatlakareId.setId(1);
        when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);

        when(hospUpdateService.updateHospInformation(any(Privatlakare.class), eq(true))).thenReturn(RegistrationStatus.AUTHORIZED);

        Registration registration = createValidRegistration();
        RegistrationStatus response = registerService.createRegistration(registration, 1L);

        ArgumentCaptor<Privatlakare> savedPrivatlakare = ArgumentCaptor.forClass(Privatlakare.class);
        verify(privatlakareRepository).save(savedPrivatlakare.capture());
        assertEquals(response, RegistrationStatus.AUTHORIZED);
        assertEquals(1, savedPrivatlakare.getValue().getMedgivande().size());

        Privatlakare verifyPrivatlakare = readPrivatlakare("RegisterServiceImplTest/test.json");
        ReflectionAssert.assertReflectionEquals(verifyPrivatlakare, savedPrivatlakare.getValue(), ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void testCreateRegistrationLakareUtanMedgivande() {

        thrown.expect(PrivatlakarportalServiceException.class);
        thrown.expect(PrivatlakarportalServiceExceptionMatcher.hasErrorCode(PrivatlakarportalErrorCodeEnum.BAD_REQUEST));

        PrivatlakareId privatlakareId = new PrivatlakareId();
        privatlakareId.setId(1);
        when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);

        Registration registration = createValidRegistration();
        RegistrationStatus response = registerService.createRegistration(registration, null);
    }

    @Test
    public void testCreateRegistrationLakareFelMedgivandeVersion() {

        thrown.expect(PrivatlakarportalServiceException.class);
        thrown.expect(PrivatlakarportalServiceExceptionMatcher.hasErrorCode(PrivatlakarportalErrorCodeEnum.BAD_REQUEST));

        PrivatlakareId privatlakareId = new PrivatlakareId();
        privatlakareId.setId(1);
        when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);

        Registration registration = createValidRegistration();
        RegistrationStatus response = registerService.createRegistration(registration, 2L);
    }


    @Test
    public void testCreateRegistrationEjLakare() throws HospUpdateFailedToContactHsaException {

        PrivatlakareId privatlakareId = new PrivatlakareId();
        privatlakareId.setId(1);
        when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);

        when(hospUpdateService.updateHospInformation(any(Privatlakare.class), eq(true))).thenReturn(RegistrationStatus.NOT_AUTHORIZED);

        Registration registration = createValidRegistration();
        RegistrationStatus response = registerService.createRegistration(registration, 1L);

        verify(privatlakareRepository).save(any(Privatlakare.class));
        assertEquals(response, RegistrationStatus.NOT_AUTHORIZED);
    }

    @Test
    public void testCreateRegistrationEjIHosp() throws HospUpdateFailedToContactHsaException {

        PrivatlakareId privatlakareId = new PrivatlakareId();
        privatlakareId.setId(1);
        when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);

        when(hospUpdateService.updateHospInformation(any(Privatlakare.class), eq(true))).thenReturn(RegistrationStatus.WAITING_FOR_HOSP);

        Registration registration = createValidRegistration();
        RegistrationStatus response = registerService.createRegistration(registration, 1L);

        verify(privatlakareRepository).save(any(Privatlakare.class));
        assertEquals(response, RegistrationStatus.WAITING_FOR_HOSP);
    }

    @Test
    public void testCreateRegistrationEjIPUService() {

        thrown.expect(PrivatlakarportalServiceException.class);
        thrown.expect(PrivatlakarportalServiceExceptionMatcher.hasErrorCode(PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM));

        when(userService.getUser()).thenReturn(new PrivatlakarUser(PERSON_ID, "Test User"));

        Registration registration = createValidRegistration();
        RegistrationStatus response = registerService.createRegistration(registration, 1L);

        verifyNoMoreInteractions(privatlakareRepository);
        verifyNoMoreInteractions(hospUpdateService);
    }

    @Test
    public void testSavePrivatlakare() {

        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(new Privatlakare());

        Registration registration = createValidRegistration();
        SaveRegistrationResponseStatus response = registerService.saveRegistration(registration);

        verify(privatlakareRepository).save(any(Privatlakare.class));
        assertEquals(response, SaveRegistrationResponseStatus.OK);
    }

    @Test
    public void testSavePrivatlakareNotFound() {

        thrown.expect(PrivatlakarportalServiceException.class);
        thrown.expect(PrivatlakarportalServiceExceptionMatcher.hasErrorCode(PrivatlakarportalErrorCodeEnum.NOT_FOUND));

        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);

        Registration registration = createValidRegistration();
        SaveRegistrationResponseStatus response = registerService.saveRegistration(registration);
    }

    @Test
    public void testSavePrivatlakareInvalid() {

        thrown.expect(PrivatlakarportalServiceException.class);
        thrown.expect(PrivatlakarportalServiceExceptionMatcher.hasErrorCode(PrivatlakarportalErrorCodeEnum.BAD_REQUEST));

        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(new Privatlakare());

        Registration registration = new Registration();
        SaveRegistrationResponseStatus response = registerService.saveRegistration(registration);
    }

    @Test
    public void testRemovePrivatlakare() {
        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(new Privatlakare());

        Registration registration = createValidRegistration();

        registerService.saveRegistration(registration);

        assertTrue(registerService.removePrivatlakare(PERSON_ID));
    }

    @Test
    public void testRemoveNonExistingPrivatlakare() {
        assertFalse(registerService.removePrivatlakare("195206142597"));
    }

    @Test
    public void getHospInformation() {

        GetHospPersonResponseType hospPersonResponse = new GetHospPersonResponseType();
        hospPersonResponse.setPersonalIdentityNumber(PERSON_ID);
        hospPersonResponse.setPersonalPrescriptionCode("0000000");
        HsaTitlesType hasTitles = new HsaTitlesType();
        hasTitles.getHsaTitle().add("Test title");
        hospPersonResponse.setHsaTitles(hasTitles);
        SpecialityNamesType specialityNamesType = new SpecialityNamesType();
        specialityNamesType.getSpecialityName().add("Test speciality");
        hospPersonResponse.setSpecialityNames(specialityNamesType);

        when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(hospPersonResponse);

        HospInformation hospInformation = registerService.getHospInformation();

        assertEquals(hospInformation.getPersonalPrescriptionCode(), "0000000");
        assertEquals(hospInformation.getHsaTitles().size(), 1);
        assertEquals(hospInformation.getHsaTitles().get(0), "Test title");
        assertEquals(hospInformation.getSpecialityNames().size(), 1);
        assertEquals(hospInformation.getSpecialityNames().get(0), "Test speciality");
    }

    @Test
    public void getHospInformationNotInHosp() {

        when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(null);

        HospInformation hospInformation = registerService.getHospInformation();

        assertNull(hospInformation);
    }

    @Test
    public void getRegistration() throws IOException {
        Privatlakare testPrivatlakare = readPrivatlakare("RegisterServiceImplTest/test_lakare.json");
        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(testPrivatlakare);

        RegistrationWithHospInformation registration = registerService.getRegistration();

        // assert registration information
        assertEquals(testPrivatlakare.getPostadress(), registration.getRegistration().getAdress());
        assertEquals(testPrivatlakare.getAgarform(), registration.getRegistration().getAgarForm());
        assertEquals(testPrivatlakare.getArbetsplatsKod(), registration.getRegistration().getArbetsplatskod());
        assertEquals(testPrivatlakare.getBefattningar().iterator().next().getKod(), registration.getRegistration().getBefattning());
        assertEquals(testPrivatlakare.getEpost(), registration.getRegistration().getEpost());
        assertEquals(testPrivatlakare.getKommun(), registration.getRegistration().getKommun());
        assertEquals(testPrivatlakare.getLan(), registration.getRegistration().getLan());
        assertEquals(testPrivatlakare.getPostnummer(), registration.getRegistration().getPostnummer());
        assertEquals(testPrivatlakare.getPostort(), registration.getRegistration().getPostort());
        assertEquals(testPrivatlakare.getTelefonnummer(), registration.getRegistration().getTelefonnummer());
        assertEquals(testPrivatlakare.getVardformer().iterator().next().getKod(), registration.getRegistration().getVardform());
        assertEquals(testPrivatlakare.getEnhetsNamn(), registration.getRegistration().getVerksamhetensNamn());
        assertEquals(testPrivatlakare.getVerksamhetstyper().iterator().next().getKod(), registration.getRegistration().getVerksamhetstyp());
        // assert Hosp-information
        assertEquals(testPrivatlakare.getLegitimeradeYrkesgrupper().size(), registration.getHospInformation().getHsaTitles().size());
        for (LegitimeradYrkesgrupp legitimeradYrkesgrupp : testPrivatlakare.getLegitimeradeYrkesgrupper()) {
            assert (registration.getHospInformation().getHsaTitles().contains(legitimeradYrkesgrupp.getNamn()));
        }
        assertEquals(testPrivatlakare.getForskrivarKod(), registration.getHospInformation().getPersonalPrescriptionCode());
        assertEquals(testPrivatlakare.getSpecialiteter().size(), registration.getHospInformation().getSpecialityNames().size());
        for (Specialitet specialitet : testPrivatlakare.getSpecialiteter()) {
            assert (registration.getHospInformation().getSpecialityNames().contains(specialitet.getNamn()));
        }
    }

    @Test
    public void getEmptyRegistration() {
        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);

        RegistrationWithHospInformation registration = registerService.getRegistration();
        assertNull(registration.getHospInformation());
        assertNull(registration.getRegistration());
    }

}
