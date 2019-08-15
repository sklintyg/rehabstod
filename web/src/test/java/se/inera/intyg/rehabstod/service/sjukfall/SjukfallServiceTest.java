/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.sjukfall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.infra.sjukfall.dto.Patient;
import se.inera.intyg.infra.sjukfall.dto.Vardgivare;
import se.inera.intyg.infra.sjukfall.services.SjukfallEngineServiceImpl;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationServiceImpl;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.service.SamtyckestjanstIntegrationService;
import se.inera.intyg.rehabstod.integration.sparrtjanst.service.SparrtjanstIntegrationServiceImpl;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.hsa.EmployeeNameService;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjfMetaData;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjfMetaDataItemType;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallPatientResponse;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.komplettering.KompletteringInfoDecorator;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.IntygstjanstMapper;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.SjukfallEngineMapper;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.sjukfall.pu.SjukfallPuService;
import se.inera.intyg.rehabstod.service.sjukfall.srs.RiskPredictionService;
import se.inera.intyg.rehabstod.service.sjukfall.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.service.user.UserPreferencesService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Arbetsformaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Enhet;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.HosPersonal;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

/**
 * Created by Magnus Ekstrand on 2016-02-24.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallServiceTest {
    // CHECKSTYLE:OFF MagicNumber
    private static final int MAX_DAGAR_SEDAN_AVSLUT = 0;
    private static final String LAKARE_ID = "HSA-1223311";
    private static final String LAKARE_NAMN = "Jan Itor";
    private static final String VARDGIVARE_ID = "HSA-VG-123-77";
    private static final String VARDENHETS_ID = "HSA-VG-123-77_VE1";

    private final String vgId1 = "vg1";
    private final String vgId2 = "vg2";
    private final String vgId3 = "vg3";
    private final String enhetsId1_1 = "IFV1239877878-1042";
    private final String enhetsId1_2 = "IFV1239877878-1043";
    private final String enhetsId1_3 = "IFV1239877878-1044";
    private final String enhetsId2 = "enhet2";
    private final String enhetsId3 = "enhet3";
    private final String mottagningsId = "Mottagning-1";
    private final String lakareId1 = "IFV1239877878-1049";
    private final String lakareNamn1 = "Jan Nilsson";
    private final String lakareId2 = "IFV1239877878-104B";
    private final String lakareNamn2 = "Åsa Andersson";
    private final String lakareId3 = "TSTNMT2321000156-103F";
    private final String lakareNamn3 = "Leonie Koehl";
    private final String patientId1 = "19121212-1212";

    private int intygsIdCounter = 1;

    private Integer intygsGlapp = 5;
    private LocalDate activeDate = LocalDate.parse("2016-02-16");

    private List<String> intygWithSparr = new ArrayList<>();

    private IntygParametrar parameters = new IntygParametrar(intygsGlapp, MAX_DAGAR_SEDAN_AVSLUT, activeDate);

    @Mock
    private IntygstjanstIntegrationServiceImpl integrationService;

    @Mock
    private StatisticsCalculator statisticsCalculator;

    @Mock
    private MonitoringLogService monitoringLogService;

    @Spy
    private SjukfallEngineServiceImpl sjukfallEngine;

    @Spy
    private SjukfallEngineMapperTest sjukfallEngineMapper;

    @Spy
    private IntygstjanstMapper intygstjanstMapper;

    @Mock
    private SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver;

    @Mock
    private EmployeeNameService  employeeNameService;

    @Mock
    private SjukfallPuService sjukfallPuService;

    @Mock
    private RiskPredictionService riskPredictionService;

    @Mock
    private UserPreferencesService userPreferencesService;

    @Spy
    private SparrtjanstIntegrationServiceImplTest sparrtjanstIntegrationService;

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @Mock
    private SamtyckestjanstIntegrationService samtyckestjanstIntegrationService;

    @Mock
    private KompletteringInfoDecorator kompletteringInfoDecorator;

    @Mock
    private UserService userService;

    @Mock
    private LogService logService;

    @InjectMocks
    private SjukfallServiceImpl testee = new SjukfallServiceImpl();

    @Before
    public void init() {
        when(integrationService.getIntygsDataForCareUnit(anyString(), anyInt())).thenReturn(new ArrayList<>());
        when(integrationService.getIntygsDataForCareUnitAndPatient(anyString(), anyString(), anyInt())).thenReturn(new ArrayList<>());
        when(integrationService.getAllIntygsDataForPatient(anyString())).thenReturn(createIntygsData());
        when(sjukfallPuService.filterSekretessForPatientHistory(anyListOf(IntygData.class), anyString(), anyString())).thenAnswer(returnsFirstArg());

        doReturn(createSjukfallEnhetList()).when(sjukfallEngine).beraknaSjukfallForEnhet(anyListOf(se.inera.intyg.infra.sjukfall.dto.IntygData.class),
                any(se.inera.intyg.infra.sjukfall.dto.IntygParametrar.class));

        when(statisticsCalculator.getSjukfallSummary(anyListOf(SjukfallEnhet.class))).thenReturn(
            new SjukfallSummary(0, Collections.emptyList(), new ArrayList<>(), new ArrayList<>()));

        when(employeeNameService.getEmployeeHsaName(anyString())).thenReturn("Tolvan Tolvansson");

        when(userPreferencesService.getPreferenceValue(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG)).thenReturn("5");

        when(hsaOrganizationsService.getVardgivareInfo(anyString()))
                .thenAnswer(i -> createVardgivare((String) i.getArguments()[0], i.getArguments()[0] + "-VGNAME"));

        when(hsaOrganizationsService.getVardenhet(anyString()))
                .thenAnswer(i -> createVardenhet((String) i.getArguments()[0],i.getArguments()[0] + "-VENAME"));

        doNothing().when(sjukfallEmployeeNameResolver).enrichWithHsaEmployeeNames(anyListOf(SjukfallEnhet.class));
        doNothing().when(sjukfallEmployeeNameResolver).updateDuplicateDoctorNamesWithHsaId(anyListOf(SjukfallEnhet.class));

        when(userService.getUser()).thenReturn(buildUser());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNoUrvalSet() {
        testee.getByUnit(enhetsId1_1, null, "", null, parameters);
    }

    @Test
    public void testWhenUrvalIsAll() {
        List<SjukfallEnhet> internalSjukfallList = testee.getByUnit(enhetsId1_1, null, "", Urval.ALL, parameters).getSjukfallList();

        verify(integrationService).getIntygsDataForCareUnit(enhetsId1_1, MAX_DAGAR_SEDAN_AVSLUT);

        assertEquals(15, internalSjukfallList.size());
    }

    @Test
    public void testWhenUrvalIsAllForUnderenhet() {
        List<SjukfallEnhet> internalSjukfallList = testee.getByUnit(enhetsId1_1, mottagningsId, "", Urval.ALL, parameters).getSjukfallList();

        verify(integrationService).getIntygsDataForCareUnit(enhetsId1_1, MAX_DAGAR_SEDAN_AVSLUT);

        assertEquals(7, internalSjukfallList.size());
    }

    @Test
    public void testWhenUrvalIsIssuedByMe() {
        List<SjukfallEnhet> internalSjukfallList = testee.getByUnit(enhetsId1_1, null, lakareId1, Urval.ISSUED_BY_ME, parameters).getSjukfallList();

        verify(integrationService).getIntygsDataForCareUnit(enhetsId1_1, MAX_DAGAR_SEDAN_AVSLUT);

        assertEquals(8, internalSjukfallList.size());
        for (SjukfallEnhet internalSjukfall : internalSjukfallList) {
            String hsaId = internalSjukfall.getLakare().getHsaId();
            String namn = internalSjukfall.getLakare().getNamn();
            assertEquals(lakareId1, hsaId);
            assertEquals(lakareNamn1, namn);
        }
    }

    @Test
    public void testGetSjukfallSummary() {
        testee.getSummary(enhetsId1_1, null, lakareId1, Urval.ALL, parameters);

        verify(integrationService).getIntygsDataForCareUnit(enhetsId1_1, MAX_DAGAR_SEDAN_AVSLUT);
        verify(statisticsCalculator).getSjukfallSummary(anyListOf(SjukfallEnhet.class));
    }

    @Test
    public void testGetSjukfallSummaryWhenSelectedVardenhetIsMottagning() {
        testee.getSummary(enhetsId1_1, mottagningsId, lakareId1, Urval.ALL, parameters);

        verify(integrationService).getIntygsDataForCareUnit(enhetsId1_1, MAX_DAGAR_SEDAN_AVSLUT);
        verify(statisticsCalculator).getSjukfallSummary(anyListOf(SjukfallEnhet.class));
    }

    /*
     * Testet ska visa att endast intyg inom samma vårdgivare och samma enhet
     * ska resultera i ett sjukfall med två intyg i sig.
     */
    @Test
    public void testGetByPatient_utanSamtycke() {
        List<String> vgHsaId = new ArrayList<>();
        List<String> veHsaId = new ArrayList<>();

        SjukfallPatientResponse patientResponse =
                testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1, Urval.ALL, parameters, vgHsaId, veHsaId);

        assertEquals(1, patientResponse.getSjukfallList().size());
        assertEquals(2, patientResponse.getSjukfallList().get(0).getIntyg().size());

        SjfMetaData metaData = patientResponse.getSjfMetaData();

        assertSjfMetaData(false, metaData);

        assertSjfMetaDataItem(vgId2, SjfMetaDataItemType.VARDGIVARE, false, metaData);
        assertSjfMetaDataItem(vgId3, SjfMetaDataItemType.VARDGIVARE, false, metaData);
        assertSjfMetaDataItem(enhetsId1_2, SjfMetaDataItemType.VARDENHET, false, metaData);
    }

    /*
     * Testet ska visa att intyg inom samma vårdgivare och samma enhet plus registrerad
     * vårdgivare ska resultera i ett sjukfall med tre intyg i sig när samtycke finns.
     */
    @Test
    public void testGetByPatient_medSamtyckeForVardgivare() {
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId1, enhetsId1_1)).thenReturn(true);

        List<String> vgHsaId = new ArrayList<>();
        List<String> veHsaId = new ArrayList<>();

        //Registrera vardgivare vars intyg ska inkluderas i beräkning av aktivt sjukfall
        vgHsaId.add(vgId2);

        SjukfallPatientResponse patientResponse =
                testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1, Urval.ALL, parameters, vgHsaId, veHsaId);


        assertEquals(1, patientResponse.getSjukfallList().size());
        assertEquals(3, patientResponse.getSjukfallList().get(0).getIntyg().size());

        SjfMetaData metaData = patientResponse.getSjfMetaData();

        assertSjfMetaData(true, metaData);

        assertSjfMetaDataItem(vgId2, SjfMetaDataItemType.VARDGIVARE, true, metaData);
        assertSjfMetaDataItem(vgId3, SjfMetaDataItemType.VARDGIVARE, false, metaData);
        assertSjfMetaDataItem(enhetsId1_2, SjfMetaDataItemType.VARDENHET, false, metaData);

        Personnummer expectedPersonnummer = Personnummer.createPersonnummer(patientId1).get();

        verify(logService).logConsentActivity(eq(expectedPersonnummer), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_SAMTYCKE));
    }

    /*
     * Testet ska visa att samtyckesläsning INTE PDL loggas om inget samtycke fanns
     */
    @Test
    public void testGetByPatientNoConsentPDLLoggingIfNoConsent() {
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId1, enhetsId1_1)).thenReturn(false);

        testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1, Urval.ALL, parameters, new ArrayList<>(), new ArrayList<>());

        verifyZeroInteractions(logService);
    }

    /*
     * Testet ska visa att samtyckesläsning PDL loggas om samtycke fanns
     */
    @Test
    public void testGetByPatientConsentPDLLoggingIfConsentExists() {
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId1, enhetsId1_1)).thenReturn(true);

        testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1, Urval.ALL, parameters, new ArrayList<>(), new ArrayList<>());

        Personnummer expectedPersonnummer = Personnummer.createPersonnummer(patientId1).get();
        verify(logService).logConsentActivity(eq(expectedPersonnummer), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_SAMTYCKE));
    }

    /*
     * Testet ska visa att samtyckesläsning PDL loggas endast en gång om anropad flera gånger per user
     */
    @Test
    public void testGetByPatientConsentPDLLoggingOnlyOnceIfActivityAlreadyLogged() {
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId1, enhetsId1_1)).thenReturn(true);

        testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1, Urval.ALL, parameters, new ArrayList<>(), new ArrayList<>());
        testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1, Urval.ALL, parameters, new ArrayList<>(), new ArrayList<>());

        Personnummer expectedPersonnummer = Personnummer.createPersonnummer(patientId1).get();
        verify(logService, times(1))
                .logConsentActivity(eq(expectedPersonnummer), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_SAMTYCKE));
    }


    /*
     * Testet ska visa att intyg inom samma vårdgivare och samma enhet inklusive
     * registrerade andra vårdenheter inom vårdgivaren ska resultera i ett sjukfall
     * med 4 intyg i sig.
     *
     * Testet visar också att intyg med spärr på annan vårdenhet VE inte kommer med medan
     * intyg utan spärr på samma vårdenhet VE kommer med.
     */
    @Test
    public void testGetByPatient_medSamtyckeForVardenhet() {

        List<String> vgHsaId = new ArrayList<>();
        List<String> veHsaId = new ArrayList<>();

        //Registrera vardenheter vars intyg ska inkluderas i beräkning av aktivt sjukfall
        veHsaId.add(enhetsId1_2);
        veHsaId.add(enhetsId1_3);

        SjukfallPatientResponse patientResponse =
                testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1, Urval.ALL, parameters, vgHsaId, veHsaId);

        assertEquals(1, patientResponse.getSjukfallList().size());
        assertEquals(4, patientResponse.getSjukfallList().get(0).getIntyg().size());

        SjfMetaData metaData = patientResponse.getSjfMetaData();

        assertSjfMetaData(false, metaData);

        assertSjfMetaDataItem(vgId2, SjfMetaDataItemType.VARDGIVARE, false, metaData);
        assertSjfMetaDataItem(vgId3, SjfMetaDataItemType.VARDGIVARE, false, metaData);
        assertSjfMetaDataItem(enhetsId1_2, SjfMetaDataItemType.VARDENHET, true, metaData);
    }

    /*
     * Testet ska visa att intyg inom samma vårdgivare och samma enhet, fast utanför glappet,
     * ska resultera i två sjukfall med ett intyg vardera i sig.
     */
    @Test
    public void testGetByPatient_inomVardgivareOchInomEnhet() {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId1, enhetsId1_1, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId1, enhetsId1_1, lakareId1, patientId1, false,
                    activeDate.minusDays(17), activeDate.minusDays(8), activeDate.minusDays(17).atStartOfDay()));
        }};

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);

        List<String> vgHsaId = new ArrayList<>();
        List<String> veHsaId = new ArrayList<>();

        //vgHsaId.add(vgId1);

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId, veHsaId);

        assertEquals(2, patientResponse.getSjukfallList().size());
        assertEquals(1, patientResponse.getSjukfallList().get(0).getIntyg().size());
        assertEquals(1, patientResponse.getSjukfallList().get(1).getIntyg().size());
    }

    /*
     * Testet ska visa att intyg inom samma vårdgivare och samma enhet fast där intyget är skrivet på
     * en underenhet ska resultera i ett sjukfall med ett intyg vardera i sig.
     */
    @Test
    public void testGetByPatient_whenInomVardgivareOchInomEnhet_andMottagning() {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId1, enhetsId1_1, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId1, mottagningsId, lakareId1, patientId1, false,
                    activeDate.minusDays(20), activeDate.minusDays(15), activeDate.minusDays(20).atStartOfDay()));
        }};

        when(hsaOrganizationsService.getVardenhet(anyString()))
                .thenAnswer(i -> createVardenhet((String) i.getArguments()[0],i.getArguments()[0] + "-VENAME",
                        createMottagning(mottagningsId, mottagningsId + "-UENAME", (String) i.getArguments()[0])));

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);

        List<String> vgHsaId = new ArrayList<>();
        List<String> veHsaId = new ArrayList<>();

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId, veHsaId);

        assertEquals(2, patientResponse.getSjukfallList().size());
        assertEquals(1, patientResponse.getSjukfallList().get(0).getIntyg().size());
        assertEquals(1, patientResponse.getSjukfallList().get(1).getIntyg().size());
    }

    /*
     * Testet ska visa att intyg inom samma vårdgivare och där användaren är inloggad på en mottagning,
     * så ska intyg på enheten komma med.
     */
    @Test
    public void testGetByPatient_whenInomVardgivareOchInomMottagning_andParent() {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId1, mottagningsId, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId1, enhetsId1_1, lakareId1, patientId1, false,
                    activeDate.minusDays(20), activeDate.minusDays(15), activeDate.minusDays(20).atStartOfDay()));
        }};

        //Koppla mottagningen till enheten
        when(hsaOrganizationsService.getVardenhet(eq(mottagningsId)))
                .thenReturn(createVardenhet(enhetsId1_1, "parentunit", createMottagning(mottagningsId, "mottagning", enhetsId1_1)));

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);

        List<String> vgHsaId = new ArrayList<>();
        List<String> veHsaId = new ArrayList<>();

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId1, mottagningsId, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId, veHsaId);

        assertEquals(2, patientResponse.getSjukfallList().size());
        assertEquals(1, patientResponse.getSjukfallList().get(0).getIntyg().size());
        assertEquals(1, patientResponse.getSjukfallList().get(1).getIntyg().size());
    }

    /*
     * Testet ska visa att om inget intyg är utfärdat på den enhet användaren är inloggad på,
     * så skall fel kastas (see INTYG-7686)
     */
    @Test(expected = SjukfallServiceException.class)
    public void testGetByPatient_whenNoIntygAtCurrentLoggedInUnitThrowsException() {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId1, mottagningsId, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId1, enhetsId1_1, lakareId1, patientId1, false,
                    activeDate.minusDays(20), activeDate.minusDays(15), activeDate.minusDays(20).atStartOfDay()));
        }};
        //Koppla mottagningen till enheten
        when(hsaOrganizationsService.getVardenhet(eq(mottagningsId)))
                .thenReturn(createVardenhet(enhetsId1_1, "parentunit", createMottagning(mottagningsId, "mottagning", enhetsId1_1)));

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);

        List<String> vgHsaId = new ArrayList<>();
        List<String> veHsaId = new ArrayList<>();

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId1, "enhetX", lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId, veHsaId);
    }

    /*
     * Testet ska visa att intyg inom samma vårdgivare fast på olika enheter och utanför glappet,
     * ska resultera i ett sjukfall (det aktiva) med ett intyg i sig.
     */
    @Test
    public void testGetByPatient_inomVardgivareOchAnnanEnhetOchUtanforGlappet() {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId1, enhetsId1_1, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId1, enhetsId1_2, lakareId1, patientId1, false,
                    activeDate.minusDays(17), activeDate.minusDays(8), activeDate.minusDays(17).atStartOfDay()));
        }};

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);

        List<String> vgHsaId = new ArrayList<>();
        List<String> veHsaId = new ArrayList<>();

        // Registrera annan vårdenhet
        veHsaId.add(enhetsId1_2);

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId, veHsaId);

        assertEquals(1, patientResponse.getSjukfallList().size());
        assertEquals(1, patientResponse.getSjukfallList().get(0).getIntyg().size());
        assertEquals(1, patientResponse.getSjfMetaData().getKraverInteSamtycke().size());

        SjfMetaData metaData = patientResponse.getSjfMetaData();

        assertFalse(metaData.isSamtyckeFinns());
        assertSjfMetaDataItem(enhetsId1_2, SjfMetaDataItemType.VARDENHET, true, metaData);

    }

    /*
     * Testet ska visa att intyg inom samma vårdgivare fast på olika enheter men innanför glappet,
     * ska resultera i ett sjukfall (det aktiva) med två intyg i sig.
     */
    @Test
    public void testGetByPatient_inomVardgivareOchUtanforEnhetMenInomGlappet() {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId1, enhetsId1_1, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId1, enhetsId1_2, lakareId1, patientId1, false,
                    activeDate.minusDays(10), activeDate.minusDays(2), activeDate.minusDays(10).atStartOfDay()));
        }};

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId1, enhetsId1_1)).thenReturn(true);

        List<String> vgHsaId = new ArrayList<>();
        List<String> veHsaId = new ArrayList<>();

        veHsaId.add(enhetsId1_2);

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId, veHsaId);

        assertEquals(1, patientResponse.getSjukfallList().size());
        assertEquals(2, patientResponse.getSjukfallList().get(0).getIntyg().size());

        SjfMetaData metaData = patientResponse.getSjfMetaData();

        assertTrue(metaData.isSamtyckeFinns());
        assertSjfMetaDataItem(enhetsId1_2, SjfMetaDataItemType.VARDENHET, true, metaData);

    }

    /*
     * Testet ska visa att alla vårdgivare som en patient har något intyg på
     * ska finnas med i SjfMetaData oavsett om intygen i sig ingår i det
     * aktiva sjukfallet eller inte.
     */
    @Test
    public void testGetByPatient_allaVardgivareSkaFinnasMedISjfMetaData() {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId1, enhetsId1_1, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId2, enhetsId2, lakareId1, patientId1, false,
                    activeDate.minusDays(10), activeDate.minusDays(2), activeDate.minusDays(10).atStartOfDay()));
            add(createIntygsData(vgId3, enhetsId3, lakareId3, patientId1, false,
                    activeDate.minusDays(30), activeDate.minusDays(22), activeDate.minusDays(30).atStartOfDay()));
        }};

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId1, enhetsId1_1)).thenReturn(true);

        List<String> vgHsaId = new ArrayList<>();
        List<String> veHsaId = new ArrayList<>();

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId1, enhetsId1_1, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId, veHsaId);

        assertEquals(1, patientResponse.getSjukfallList().size());
        assertEquals(1, patientResponse.getSjukfallList().get(0).getIntyg().size());

        // Except vgId1, we expect that both vgId2 and vgId3 are present.
        // Every vårdgivare shall be present, not just the ones that contributes
        // to the active sjukfall (INTYG-7912).
        SjfMetaData metaData = patientResponse.getSjfMetaData();

        assertTrue(metaData.isSamtyckeFinns());
        assertEquals(2, metaData.getKraverSamtycke().size());
        assertSjfMetaDataItem(vgId2, SjfMetaDataItemType.VARDGIVARE, false, metaData);
        assertSjfMetaDataItem(vgId3, SjfMetaDataItemType.VARDGIVARE, false, metaData);

        assertTrue(metaData.getKraverSamtycke().stream()
                .filter(md -> md.getItemId().equals(vgId2) && md.isBidrarTillAktivtSjukfall()).count() > 0);
        assertTrue(metaData.getKraverSamtycke().stream()
                .filter(md -> md.getItemId().equals(vgId3) && !md.isBidrarTillAktivtSjukfall()).count() > 0);
    }

    // - - - Private scope - - -

    // This method asserts test data created in method createIntygsData
    private static void assertSjfMetaData(boolean finnsSamtycke, SjfMetaData metaData) {
        assertEquals(finnsSamtycke, metaData.isSamtyckeFinns());
        assertEquals(1, metaData.getAndraVardgivareMedSparr().size());
        assertEquals(1, metaData.getVardenheterInomVGMedSparr().size());
        assertEquals(2, metaData.getKraverSamtycke().size());
        assertEquals(2, metaData.getKraverInteSamtycke().size());
    }

    private static void assertSjfMetaDataItem(String itemId, SjfMetaDataItemType itemType, boolean isIncludedInSjukfall, SjfMetaData metaData) {
        switch (itemType) {
            case VARDGIVARE:
                assertTrue(metaData.getKraverSamtycke().stream()
                        .anyMatch(md -> md.getItemId().equals(itemId) && md.isIncludedInSjukfall() == isIncludedInSjukfall));
                break;
            case VARDENHET:
                assertTrue(metaData.getKraverInteSamtycke().stream()
                        .anyMatch(md -> md.getItemId().equals(itemId) && md.isIncludedInSjukfall() == isIncludedInSjukfall));
                break;
            default:
                fail();
        }
    }


    private RehabstodUser buildUser() {
        RehabstodUserPreferences preferences = RehabstodUserPreferences.empty();
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "5");
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "0");

        RehabstodUser user = new RehabstodUser(LAKARE_ID, LAKARE_NAMN, true);
        user.setPreferences(preferences);
        user.setValdVardgivare(new se.inera.intyg.infra.integration.hsa.model.Vardgivare(VARDGIVARE_ID, "vårdgivare"));
        user.setValdVardenhet(new Vardenhet(VARDENHETS_ID, "enhet"));

        return user;
    }

    private List<se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet> createSjukfallEnhetList() {
        List<se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet> sjukfallList = new ArrayList<>();

        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId1_1, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId1_1, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId1_1, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId1_1, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId1_1, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId1_1, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId1_1, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId1_1, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));

        return sjukfallList;
    }

    private List<IntygsData> createIntygsData() {
        List<IntygsData> intygsData = new ArrayList<>();

        intygWithSparr = new ArrayList<>();

        intygsData.add(createIntygsData(vgId1, enhetsId1_1, lakareId1, patientId1));
        intygsData.add(createIntygsData(vgId1, enhetsId1_1, lakareId1, patientId1));
        intygsData.add(createIntygsData(vgId1, enhetsId1_2, lakareId1, patientId1));

        // Lägg till intyg på annan vårdenhet. Ett utan spärr och ett med spärr.
        intygsData.add(createIntygsData(vgId1, enhetsId1_3, lakareId1, patientId1));
        intygsData.add(createIntygsData(vgId1, enhetsId1_3, lakareId1, patientId1, true));

        intygsData.add(createIntygsData(vgId2, enhetsId2, lakareId1, patientId1));
        intygsData.add(createIntygsData(vgId3, enhetsId3, lakareId1, patientId1));
        intygsData.add(createIntygsData(vgId3, enhetsId3, lakareId1, patientId1, true));

        return intygsData;
    }

    private se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet createSjukfallEnhet(String lakareId, String lakareNamn,
                                                                                String enhetsId, String patiendId) {

        Vardgivare vardgivare = new Vardgivare(vgId1, "vg");

        se.inera.intyg.infra.sjukfall.dto.Vardenhet vardenhet =
            new se.inera.intyg.infra.sjukfall.dto.Vardenhet(enhetsId, "ve-" + enhetsId);

        se.inera.intyg.infra.sjukfall.dto.Lakare lakare =
            new se.inera.intyg.infra.sjukfall.dto.Lakare(lakareId, lakareNamn);

        se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet sjukfall
            = new se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet();

        Patient patient = new Patient(patiendId, "name");

        sjukfall.setVardgivare(vardgivare);
        sjukfall.setVardenhet(vardenhet);
        sjukfall.setLakare(lakare);
        sjukfall.setSlut(LocalDate.now().plusDays(5L));
        sjukfall.setPatient(patient);
        sjukfall.setStart(LocalDate.now().minusDays(1L));
        return sjukfall;
    }

    private IntygsData createIntygsData(String vgId, String enhetsId, String lakareId, String patiendId) {
        return createIntygsData(vgId, enhetsId, lakareId, patiendId, false);
    }

    private IntygsData createIntygsData(String vgId, String enhetsId, String lakareId, String patientId, boolean harSparr) {
        LocalDate startdatum = activeDate.minusDays(15);
        LocalDate slutdatum = activeDate.plusDays(15);

        return createIntygsData(vgId, enhetsId, lakareId, patientId, harSparr, startdatum, slutdatum, activeDate.atStartOfDay());
    }

    private IntygsData createIntygsData(String vgId, String enhetsId, String lakareId, String patientId, boolean harSparr,
                                        LocalDate startDatum, LocalDate slutDatum, LocalDateTime signeringsDatum) {

        se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient patient =
                new se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient();
        PersonId personId = new PersonId();
        personId.setExtension(patientId);
        patient.setFullstandigtNamn("name");
        patient.setPersonId(personId);


        HosPersonal hosPersonal = new HosPersonal();
        HsaId hospId = new HsaId();
        hospId.setExtension(lakareId);
        hosPersonal.setPersonalId(hospId);
        hosPersonal.setFullstandigtNamn("lakare");

        se.riv.clinicalprocess.healthcond.rehabilitation.v1.Vardgivare vardgivare =
                new se.riv.clinicalprocess.healthcond.rehabilitation.v1.Vardgivare();
        HsaId vardgivarHsaId = new HsaId();
        vardgivarHsaId.setExtension(vgId);
        vardgivare.setVardgivarId(vardgivarHsaId);

        Enhet enhet = new Enhet();
        HsaId veId = new HsaId();
        veId.setExtension(enhetsId);
        enhet.setEnhetsId(veId);
        enhet.setEnhetsnamn("ve-" + enhetsId);
        enhet.setVardgivare(vardgivare);

        hosPersonal.setEnhet(enhet);

        Formaga formaga = new Formaga();
        formaga.setSlutdatum(slutDatum);
        formaga.setStartdatum(startDatum);

        Arbetsformaga arbetsformaga = new Arbetsformaga();
        arbetsformaga.getFormaga().add(formaga);

        IntygsData intygsData = new IntygsData();

        intygsData.setPatient(patient);
        intygsData.setSkapadAv(hosPersonal);

        intygsData.setSigneringsTidpunkt(signeringsDatum);
        intygsData.setArbetsformaga(arbetsformaga);
        intygsData.setDiagnoskod("J20");

        String intygId = "intygsId" + intygsIdCounter++;

        intygsData.setIntygsId(intygId);

        if (harSparr) {
            intygWithSparr.add(intygId);
        }

        return intygsData;
    }

    private se.inera.intyg.infra.integration.hsa.model.Vardgivare createVardgivare(String id, String namn) {
        return new se.inera.intyg.infra.integration.hsa.model.Vardgivare(id, namn);
    }

    private se.inera.intyg.infra.integration.hsa.model.Vardenhet createVardenhet(String id, String namn) {
        return createVardenhet(id, namn, new ArrayList<>());
    }

    private se.inera.intyg.infra.integration.hsa.model.Vardenhet createVardenhet(String id, String namn, Mottagning mottagning) {
        if (mottagning == null) {
            return createVardenhet(id, namn, new ArrayList<>());
        }

        List<Mottagning> mottagningar = Arrays.asList(mottagning);
        return createVardenhet(id, namn, mottagningar);
    }

    private se.inera.intyg.infra.integration.hsa.model.Vardenhet createVardenhet(String id, String namn, List<Mottagning> mottagningar) {
        se.inera.intyg.infra.integration.hsa.model.Vardenhet vardenhet =
                new se.inera.intyg.infra.integration.hsa.model.Vardenhet(id, namn);

        if (mottagningar == null) {
            mottagningar = new ArrayList<>();
        }

        vardenhet.setMottagningar(mottagningar);
        return vardenhet;
    }

    private se.inera.intyg.infra.integration.hsa.model.Mottagning createMottagning(String id, String namn, String parentId) {
        Mottagning mottagning = new Mottagning();
        mottagning.setId(id);
        mottagning.setNamn(namn);
        mottagning.setParentHsaId(parentId);
        return mottagning;
    }

    class SjukfallEngineMapperTest extends SjukfallEngineMapper {

        private final String vardgivareId = "IFV1239877878-0000";
        private final String vardgivareNamn = "Vårdgivare-1";
        private final String patientId = "19121212-1212";
        private final String patinetNamn = "Tolvan Tolvansson";

        @Override
        public SjukfallEnhet mapToSjukfallEnhetDto(se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet from, int maxDagarSedanAvslut, LocalDate today) {
            se.inera.intyg.infra.sjukfall.dto.Vardgivare vardgivare =
                new se.inera.intyg.infra.sjukfall.dto.Vardgivare(vardgivareId, vardgivareNamn);

            se.inera.intyg.infra.sjukfall.dto.Patient patient =
                new se.inera.intyg.infra.sjukfall.dto.Patient(patientId, patinetNamn);

            se.inera.intyg.infra.sjukfall.dto.DiagnosKod diagnosKod =
                new se.inera.intyg.infra.sjukfall.dto.DiagnosKod("J22");

            // Update Sjukfall with these objects to avoid failing test
            from.setVardgivare(vardgivare);
            from.setPatient(patient);
            from.setDiagnosKod(diagnosKod);

            return super.mapToSjukfallEnhetDto(from, maxDagarSedanAvslut, today);
        }

        @Override
        public SjukfallPatient mapToSjukfallPatientDto(se.inera.intyg.infra.sjukfall.dto.SjukfallPatient from, Map<String, IntygAccessControlMetaData> intygAccessMetaData) {
            return super.mapToSjukfallPatientDto(from, intygAccessMetaData);
        }

        @Override
        public PatientData mapSjukfallIntygToPatientData(se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg from, IntygAccessControlMetaData iacm) {
            return super.mapSjukfallIntygToPatientData(from, iacm);
        }

        @Override
        public Diagnos getDiagnos(se.inera.intyg.infra.sjukfall.dto.DiagnosKod from) {
            Diagnos to = new Diagnos(from.getOriginalCode(), from.getCleanedCode(), from.getName());
            to.setBeskrivning("En beskrivning");
            to.setKapitel("Ett kapitel");
            return to;
        }

    }

    class SparrtjanstIntegrationServiceImplTest extends SparrtjanstIntegrationServiceImpl {

        @Override
        public void decorateWithBlockStatus(String currentVardgivarHsaId, String currentVardenhetHsaId, String userHsaId,
                                            String patientId, Map<String, IntygAccessControlMetaData> intygAccessMetaData,
                                            List<IntygData> intygLista) {

            intygWithSparr.forEach(intygsId -> {
                if (intygAccessMetaData.containsKey(intygsId)) {
                    intygAccessMetaData.get(intygsId).setSparr(true);
                }
            });
        }
    }
}
