/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.infra.sjukfall.services.SjukfallEngineService;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationServiceImpl;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.hsa.EmployeeNameService;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
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
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Magnus Ekstrand on 2016-02-24.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallServiceTest {
    // CHECKSTYLE:OFF MagicNumber

    private static final int MAX_DAGAR_SEDAN_AVSLUT = 0;
    private final String enhetsId = "IFV1239877878-1042";
    private final String mottagningsId = "Mottagning-1";
    private final String lakareId1 = "IFV1239877878-1049";
    private final String lakareNamn1 = "Jan Nilsson";
    private final String lakareId2 = "IFV1239877878-104B";
    private final String lakareNamn2 = "Åsa Andersson";

    private Integer intygsGlapp = 5;
    private LocalDate activeDate = LocalDate.parse("2016-02-16");

    private IntygParametrar parameters = new IntygParametrar(intygsGlapp, MAX_DAGAR_SEDAN_AVSLUT, activeDate);

    @Mock
    private IntygstjanstIntegrationServiceImpl integrationService;

    @Mock
    private StatisticsCalculator statisticsCalculator;

    @Mock
    private MonitoringLogService monitoringLogService;

    @Mock
    private SjukfallEngineService sjukfallEngine;

    @Spy
    private SjukfallEngineMapperTest sjukfallEngineMapper;

    @Mock
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

    @InjectMocks
    private SjukfallServiceImpl testee = new SjukfallServiceImpl();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws IOException {
        when(intygstjanstMapper.map(any(IntygsData.class))).thenReturn(new se.inera.intyg.infra.sjukfall.dto.IntygData());
        when(integrationService.getIntygsDataForCareUnit(anyString(), anyInt())).thenReturn(new ArrayList<IntygsData>());
        when(integrationService.getIntygsDataForCareUnitAndPatient(anyString(), anyString(), anyInt())).thenReturn(new ArrayList<IntygsData>());
        when(sjukfallEngine.beraknaSjukfallForEnhet(anyListOf(se.inera.intyg.infra.sjukfall.dto.IntygData.class),
            any(se.inera.intyg.infra.sjukfall.dto.IntygParametrar.class))).thenReturn(createSjukfallEnhetList());
        when(sjukfallEngine.beraknaSjukfallForPatient(anyListOf(se.inera.intyg.infra.sjukfall.dto.IntygData.class),
            any(se.inera.intyg.infra.sjukfall.dto.IntygParametrar.class))).thenReturn(createSjukfallPatientList());
        when(statisticsCalculator.getSjukfallSummary(anyListOf(SjukfallEnhet.class))).thenReturn(
            new SjukfallSummary(0, Collections.emptyList(), new ArrayList<>(), new ArrayList<>()));
        when(employeeNameService.getEmployeeHsaName(anyString())).thenReturn("Tolvan Tolvansson");
        when(userPreferencesService.getPreferenceValue(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG)).thenReturn("5");

        doNothing().when(sjukfallEmployeeNameResolver).enrichWithHsaEmployeeNames(anyListOf(SjukfallEnhet.class));
        doNothing().when(sjukfallEmployeeNameResolver).updateDuplicateDoctorNamesWithHsaId(anyListOf(SjukfallEnhet.class));
    }

    @Test
    public void testWhenNoUrvalSet() {
        thrown.expect(IllegalArgumentException.class);
        testee.getByUnit(enhetsId, null, "", null, parameters);
    }

    @Test
    public void testWhenUrvalIsAll() {
        List<SjukfallEnhet> internalSjukfallList = testee.getByUnit(enhetsId, null, "", Urval.ALL, parameters).getSjukfallList();

        verify(integrationService).getIntygsDataForCareUnit(enhetsId, MAX_DAGAR_SEDAN_AVSLUT);

        assertTrue("Expected 15 but was " + internalSjukfallList.size(), internalSjukfallList.size() == 15);
    }

    @Test
    public void testWhenUrvalIsAllForUnderenhet() {
        List<SjukfallEnhet> internalSjukfallList = testee.getByUnit(enhetsId, mottagningsId, "", Urval.ALL, parameters).getSjukfallList();

        verify(integrationService).getIntygsDataForCareUnit(enhetsId, MAX_DAGAR_SEDAN_AVSLUT);

        assertTrue("Expected 7 but was " + internalSjukfallList.size(), internalSjukfallList.size() == 7);
    }

    @Test
    public void testWhenUrvalIsIssuedByMe() {
        List<SjukfallEnhet> internalSjukfallList = testee.getByUnit(enhetsId, null, lakareId1, Urval.ISSUED_BY_ME, parameters).getSjukfallList();

        verify(integrationService).getIntygsDataForCareUnit(enhetsId, MAX_DAGAR_SEDAN_AVSLUT);

        assertTrue("Expected 8 but was " + internalSjukfallList.size(), internalSjukfallList.size() == 8);
        for (SjukfallEnhet internalSjukfall : internalSjukfallList) {
            String hsaId = internalSjukfall.getLakare().getHsaId();
            String namn = internalSjukfall.getLakare().getNamn();
            assertTrue(lakareId1 == hsaId);
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

    // - - - Private scope - - -

    private List<se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet> createSjukfallEnhetList() {
        List<se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet> sjukfallList = new ArrayList<>();

        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId));
        sjukfallList.add(createSjukfallEnhet(lakareId1, lakareNamn1, enhetsId));
        sjukfallList.add(createSjukfallEnhet(lakareId2, lakareNamn2, mottagningsId));

        return sjukfallList;
    }

    private List<se.inera.intyg.infra.sjukfall.dto.SjukfallPatient> createSjukfallPatientList() {
        return new ArrayList<>();
    }

    private se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet createSjukfallEnhet(String lakareId, String lakareNamn, String enhetsId) {

        se.inera.intyg.infra.sjukfall.dto.Vardenhet vardenhet =
            new se.inera.intyg.infra.sjukfall.dto.Vardenhet(enhetsId, "enhet-" + enhetsId);

        se.inera.intyg.infra.sjukfall.dto.Lakare lakare =
            new se.inera.intyg.infra.sjukfall.dto.Lakare(lakareId, lakareNamn);

        se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet sjukfall
            = new se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet();

        sjukfall.setVardenhet(vardenhet);
        sjukfall.setLakare(lakare);
        sjukfall.setSlut(LocalDate.now().plusDays(5L));
        return sjukfall;
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
        public SjukfallPatient map(se.inera.intyg.infra.sjukfall.dto.SjukfallPatient from) {
            return super.map(from);
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
}
