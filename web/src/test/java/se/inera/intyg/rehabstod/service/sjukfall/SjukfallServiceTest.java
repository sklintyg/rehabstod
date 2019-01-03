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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.infra.sjukfall.dto.Patient;
import se.inera.intyg.infra.sjukfall.dto.Vardgivare;
import se.inera.intyg.infra.sjukfall.services.SjukfallEngineServiceImpl;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationServiceImpl;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.service.SamtyckestjanstIntegrationService;
import se.inera.intyg.rehabstod.integration.sparrtjanst.service.SparrtjanstIntegrationServiceImpl;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.hsa.EmployeeNameService;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjfSamtyckeFinnsMetaData;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallPatientResponse;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.IntygstjanstMapper;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.SjukfallEngineMapper;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.sjukfall.pu.SjukfallPuService;
import se.inera.intyg.rehabstod.service.sjukfall.srs.RiskPredictionService;
import se.inera.intyg.rehabstod.service.sjukfall.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.service.user.UserPreferencesService;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Arbetsformaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Enhet;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.HosPersonal;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Magnus Ekstrand on 2016-02-24.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallServiceTest {
    // CHECKSTYLE:OFF MagicNumber
    private static final int MAX_DAGAR_SEDAN_AVSLUT = 0;

    private final String vgId = "vg1";
    private final String vgId2 = "vg2";
    private final String vgId3 = "vg3";
    private final String enhetsId = "IFV1239877878-1042";
    private final String enhetsId2 = "enhet2";
    private final String mottagningsId = "Mottagning-1";
    private final String lakareId1 = "IFV1239877878-1049";
    private final String lakareNamn1 = "Jan Nilsson";
    private final String lakareId2 = "IFV1239877878-104B";
    private final String lakareNamn2 = "Åsa Andersson";
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

    @InjectMocks
    private SjukfallServiceImpl testee = new SjukfallServiceImpl();

    @Before
    public void init() {
        when(integrationService.getIntygsDataForCareUnit(anyString(), anyInt())).thenReturn(new ArrayList<>());
        when(integrationService.getIntygsDataForCareUnitAndPatient(anyString(), anyString(), anyInt())).thenReturn(new ArrayList<>());
        when(integrationService.getAllIntygsDataForPatient(anyString())).thenReturn(createIntygsData());

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
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNoUrvalSet() {
        testee.getByUnit(enhetsId, null, "", null, parameters);
    }

    @Test
    public void testWhenUrvalIsAll() {
        List<SjukfallEnhet> internalSjukfallList = testee.getByUnit(enhetsId, null, "", Urval.ALL, parameters).getSjukfallList();

        verify(integrationService).getIntygsDataForCareUnit(enhetsId, MAX_DAGAR_SEDAN_AVSLUT);

        assertEquals(15, internalSjukfallList.size());
    }

    @Test
    public void testWhenUrvalIsAllForUnderenhet() {
        List<SjukfallEnhet> internalSjukfallList = testee.getByUnit(enhetsId, mottagningsId, "", Urval.ALL, parameters).getSjukfallList();

        verify(integrationService).getIntygsDataForCareUnit(enhetsId, MAX_DAGAR_SEDAN_AVSLUT);

        assertEquals(7, internalSjukfallList.size());
    }

    @Test
    public void testWhenUrvalIsIssuedByMe() {
        List<SjukfallEnhet> internalSjukfallList = testee.getByUnit(enhetsId, null, lakareId1, Urval.ISSUED_BY_ME, parameters).getSjukfallList();

        verify(integrationService).getIntygsDataForCareUnit(enhetsId, MAX_DAGAR_SEDAN_AVSLUT);

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
        testee.getSummary(enhetsId, null, lakareId1, Urval.ALL, parameters);

        verify(integrationService).getIntygsDataForCareUnit(enhetsId, MAX_DAGAR_SEDAN_AVSLUT);
        verify(statisticsCalculator).getSjukfallSummary(anyListOf(SjukfallEnhet.class));
    }

    @Test
    public void testGetSjukfallSummaryWhenSelectedVardenhetIsMottagning() {
        testee.getSummary(enhetsId, mottagningsId, lakareId1, Urval.ALL, parameters);

        verify(integrationService).getIntygsDataForCareUnit(enhetsId, MAX_DAGAR_SEDAN_AVSLUT);
        verify(statisticsCalculator).getSjukfallSummary(anyListOf(SjukfallEnhet.class));
    }

    @Test
    public void testGetByPatient_utanSamtycke() {
        List<String> vgHsaId = new ArrayList<>();

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId, enhetsId, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId);

        assertEquals(1, patientResponse.getSjukfallList().size());
        assertEquals(3, patientResponse.getSjukfallList().get(0).getIntyg().size());

        assertEquals(2, patientResponse.getSjfMetaData().getKraverSamtycke().size());
        assertEquals(vgId2, patientResponse.getSjfMetaData().getKraverSamtycke().iterator().next().getVardgivareId());
        assertFalse(patientResponse.getSjfMetaData().isSamtyckeFinns());

        assertEquals(1, patientResponse.getSjfMetaData().getAndraVardgivareMedSparr().size());
        assertEquals(1, patientResponse.getSjfMetaData().getVardenheterInomVGMedSparr().size());
    }

    @Test
    public void testGetByPatient_medSamtycke() {
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId, enhetsId)).thenReturn(true);

        List<String> vgHsaId = new ArrayList<>();
        vgHsaId.add(vgId2);

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId, enhetsId, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId);

        assertEquals(1, patientResponse.getSjukfallList().size());
        assertEquals(4, patientResponse.getSjukfallList().get(0).getIntyg().size());

        assertEquals(2, patientResponse.getSjfMetaData().getKraverSamtycke().size());

        Iterator<SjfSamtyckeFinnsMetaData> kraverSamtycke = patientResponse.getSjfMetaData().getKraverSamtycke().iterator();
        SjfSamtyckeFinnsMetaData vg2metaData = kraverSamtycke.next();
        SjfSamtyckeFinnsMetaData vg3metaData = kraverSamtycke.next();

        assertEquals(vgId2, vg2metaData.getVardgivareId());
        assertEquals(vgId2 + "-VGNAME", vg2metaData.getVardgivareNamn());
        assertTrue(vg2metaData.isIncludedInSjukfall());
        assertEquals(vgId3, vg3metaData.getVardgivareId());
        assertEquals(vgId3 + "-VGNAME", vg3metaData.getVardgivareNamn());
        assertFalse(vg3metaData.isIncludedInSjukfall());
        assertTrue(patientResponse.getSjfMetaData().isSamtyckeFinns());
    }

    @Test
    /*
     * Testet ska visa att intyg inom samma vårdgivare och samma enhet, fast utanför glappet,
     * ska resultera i två sjukfall med ett intyg vardera i sig.
     */
    public void testGetByPatient_inomVardgivareOchInomEnhet() {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId, enhetsId, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId, enhetsId, lakareId1, patientId1, false,
                    activeDate.minusDays(17), activeDate.minusDays(8), activeDate.minusDays(17).atStartOfDay()));
        }};

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId, enhetsId)).thenReturn(true);

        List<String> vgHsaId = new ArrayList<>();
        vgHsaId.add(vgId);

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId, enhetsId, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId);

        assertEquals(2, patientResponse.getSjukfallList().size());
        assertEquals(1, patientResponse.getSjukfallList().get(0).getIntyg().size());
        assertEquals(1, patientResponse.getSjukfallList().get(1).getIntyg().size());
    }

    @Test
    /*
     * Testet ska visa att intyg inom samma vårdgivare och samma enhet fast där intyget är skrivet på
     * en underenhet ska resultera i ett sjukfall med ett intyg vardera i sig.
     */
    public void testGetByPatient_whenInomVardgivareOchInomEnhet_andMottagning() {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId, enhetsId, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId, mottagningsId, lakareId1, patientId1, false,
                    activeDate.minusDays(20), activeDate.minusDays(15), activeDate.minusDays(20).atStartOfDay()));

        }};

        when(hsaOrganizationsService.getVardenhet(anyString()))
                .thenAnswer(i -> createVardenhet((String) i.getArguments()[0],i.getArguments()[0] + "-VENAME",
                        createMottagning(mottagningsId, mottagningsId + "-UENAME", (String) i.getArguments()[0])));

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId, enhetsId)).thenReturn(true);

        List<String> vgHsaId = new ArrayList<>();
        vgHsaId.add(vgId);

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId, enhetsId, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId);

        assertEquals(2, patientResponse.getSjukfallList().size());
        assertEquals(1, patientResponse.getSjukfallList().get(0).getIntyg().size());
        assertEquals(1, patientResponse.getSjukfallList().get(1).getIntyg().size());
    }

    @Test
    /*
     * Testet ska visa att intyg inom samma vårdgivare och där användaren är inloggad på en mottagning,
     * så ska intyg på enheten komma med.
     */
    public void testGetByPatient_whenInomVardgivareOchInomMottagning_andParent() throws HsaServiceCallException {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId, mottagningsId, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId, enhetsId, lakareId1, patientId1, false,
                    activeDate.minusDays(20), activeDate.minusDays(15), activeDate.minusDays(20).atStartOfDay()));
        }};
        //Koppla mottagningen till enheten
        when(hsaOrganizationsService.getVardenhet(eq(mottagningsId)))
                .thenReturn(createVardenhet(enhetsId, "parentunit", createMottagning(mottagningsId, "mottagning", enhetsId)));

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId, mottagningsId)).thenReturn(true);

        List<String> vgHsaId = new ArrayList<>();
        vgHsaId.add(vgId);

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId, mottagningsId, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId);

        assertEquals(2, patientResponse.getSjukfallList().size());
        assertEquals(1, patientResponse.getSjukfallList().get(0).getIntyg().size());
        assertEquals(1, patientResponse.getSjukfallList().get(1).getIntyg().size());
    }

    @Test(expected = SjukfallServiceException.class)
    /*
     * Testet ska visa att om inget intyg är utfärdat på den enhet användarens är inloggad på, så skall fel kastas (see INTYG-7686)
     */
    public void testGetByPatient_whenNoIntygInCurrentLogedInUnitThrowsException() throws HsaServiceCallException {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId, mottagningsId, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId, enhetsId, lakareId1, patientId1, false,
                    activeDate.minusDays(20), activeDate.minusDays(15), activeDate.minusDays(20).atStartOfDay()));
        }};
        //Koppla mottagningen till enheten
        when(hsaOrganizationsService.getVardenhet(eq(mottagningsId)))
                .thenReturn(createVardenhet(enhetsId, "parentunit", createMottagning(mottagningsId, "mottagning", enhetsId)));

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);

        List<String> vgHsaId = new ArrayList<>();
        vgHsaId.add(vgId);

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId, "enhetX", lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId);
    }

    @Test
    /*
     * Testet ska visa att intyg inom samma vårdgivare fast på olika enheter och utanför glappet,
     * ska resultera i ett sjukfall (det aktiva) med ett intyg i sig.
     */
    public void testGetByPatient_inomVardgivareOchUtanforEnhet() {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId, enhetsId, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId, enhetsId2, lakareId1, patientId1, false,
                    activeDate.minusDays(17), activeDate.minusDays(8), activeDate.minusDays(17).atStartOfDay()));
        }};

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId, enhetsId)).thenReturn(true);

        List<String> vgHsaId = new ArrayList<>();
        vgHsaId.add(vgId);

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId, enhetsId, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId);

        assertEquals(1, patientResponse.getSjukfallList().size());
        assertEquals(1, patientResponse.getSjukfallList().get(0).getIntyg().size());
    }

    @Test
    /*
     * Testet ska visa att intyg inom samma vårdgivare fast på olika enheter men innanför glappet,
     * ska resultera i ett sjukfall (det aktiva) med två intyg i sig.
     */
    public void testGetByPatient_inomVardgivareOchUtanforEnhetMenInomGlappet() {
        List<IntygsData> data = new ArrayList<IntygsData>() {{
            add(createIntygsData(vgId, enhetsId, lakareId1, patientId1, false,
                    activeDate.minusDays(1), activeDate.plusDays(9), activeDate.minusDays(1).atStartOfDay()));
            add(createIntygsData(vgId, enhetsId2, lakareId1, patientId1, false,
                    activeDate.minusDays(10), activeDate.minusDays(2), activeDate.minusDays(10).atStartOfDay()));
        }};

        when(integrationService.getAllIntygsDataForPatient(eq(patientId1))).thenReturn(data);
        when(samtyckestjanstIntegrationService.checkForConsent(patientId1, lakareId1, vgId, enhetsId)).thenReturn(true);

        List<String> vgHsaId = new ArrayList<>();
        vgHsaId.add(vgId);

        SjukfallPatientResponse patientResponse = testee.getByPatient(vgId, enhetsId, lakareId1, patientId1,
                Urval.ALL, parameters, vgHsaId);

        assertEquals(1, patientResponse.getSjukfallList().size());
        assertEquals(2, patientResponse.getSjukfallList().get(0).getIntyg().size());
    }

    // - - - Private scope - - -

    private List<se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet> createSjukfallEnhetList() {
        List<se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet> sjukfallList = new ArrayList<>();

        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId, patientId1));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId, patientId1));

        return sjukfallList;
    }

    private List<IntygsData> createIntygsData() {
        List<IntygsData> intygsData = new ArrayList<>();

        intygWithSparr = new ArrayList<>();

        intygsData.add(createIntygsData(vgId, enhetsId, lakareId1, patientId1));
        intygsData.add(createIntygsData(vgId, enhetsId, lakareId1, patientId1));
        intygsData.add(createIntygsData(vgId, enhetsId2, lakareId1, patientId1));
        intygsData.add(createIntygsData(vgId, enhetsId2, lakareId1, patientId1, true));
        intygsData.add(createIntygsData(vgId2, enhetsId, lakareId1, patientId1));
        intygsData.add(createIntygsData(vgId3, enhetsId, lakareId1, patientId1));
        intygsData.add(createIntygsData(vgId3, enhetsId, lakareId1, patientId1, true));

        return intygsData;
    }

    private se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet createSjukfallEnhet(String lakareId, String lakareNamn,
                                                                                String enhetsId, String patiendId) {

        Vardgivare vardgivare = new Vardgivare(vgId, "vg");

        se.inera.intyg.infra.sjukfall.dto.Vardenhet vardenhet =
            new se.inera.intyg.infra.sjukfall.dto.Vardenhet(enhetsId, "enhet-" + enhetsId);

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
        HsaId hsaId = new HsaId();
        hsaId.setExtension(lakareId);
        hosPersonal.setFullstandigtNamn("lakare");
        hosPersonal.setPersonalId(hsaId);

        se.riv.clinicalprocess.healthcond.rehabilitation.v1.Vardgivare vardgivare =
                new se.riv.clinicalprocess.healthcond.rehabilitation.v1.Vardgivare();
        HsaId vardgivarHsaId = new HsaId();
        vardgivarHsaId.setExtension(vgId);
        vardgivare.setVardgivarId(vardgivarHsaId);

        Enhet enhet = new Enhet();
        HsaId enhetId = new HsaId();
        enhetId.setExtension(enhetsId);
        enhet.setEnhetsId(enhetId);
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
        public SjukfallEnhet map(se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet from, int maxDagarSedanAvslut, LocalDate today) {
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

            return super.map(from, maxDagarSedanAvslut, today);
        }

        @Override
        public SjukfallPatient map(se.inera.intyg.infra.sjukfall.dto.SjukfallPatient from, String vgId, String enhetId) {
            return super.map(from, vgId, enhetId);
        }

        @Override
        public PatientData map(se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg from) {
            return super.map(from);
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
