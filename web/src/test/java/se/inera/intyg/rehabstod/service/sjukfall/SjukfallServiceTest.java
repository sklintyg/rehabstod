/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.sjukfall;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.infra.sjukfall.dto.Lakare;
import se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet;
import se.inera.intyg.infra.sjukfall.dto.SjukfallPatient;
import se.inera.intyg.infra.sjukfall.dto.Vardenhet;
import se.inera.intyg.infra.sjukfall.services.SjukfallEngineService;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationServiceImpl;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.sjukfall.pu.SjukfallPuService;
import se.inera.intyg.rehabstod.service.sjukfall.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhetRS;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Vardgivare;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Magnus Ekstrand on 2016-02-24.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallServiceTest {
    // CHECKSTYLE:OFF MagicNumber

    private final String enhetsId = "IFV1239877878-1042";
    private final String mottagningsId = "Mottagning-1";
    private final String lakareId1 = "IFV1239877878-1049";
    private final String lakareNamn1 = "Jan Nilsson";
    private final String lakareId2 = "IFV1239877878-104B";
    private final String lakareNamn2 = "Åsa Andersson";

    private Integer intygsGlapp = 5;
    private LocalDate activeDate = LocalDate.parse("2016-02-16");

    @Mock
    private IntygstjanstIntegrationServiceImpl integrationService;

    @Mock
    private StatisticsCalculator statisticsCalculator;

    @Mock
    private MonitoringLogService monitoringLogService;

    @Mock
    private SjukfallEngineService sjukfallEngine;

    @Mock
    private SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver;

    @Mock
    private SjukfallPuService sjukfallPuService;

    @InjectMocks
    private SjukfallServiceImplTest testee = new SjukfallServiceImplTest();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws IOException {
        when(integrationService.getIntygsDataForCareUnit(anyString())).thenReturn(new ArrayList<IntygsData>());
        when(integrationService.getIntygsDataForPatient(anyString(), anyString())).thenReturn(new ArrayList<IntygsData>());
        when(sjukfallEngine.beraknaSjukfallForEnhet(anyListOf(IntygData.class), any(IntygParametrar.class))).thenReturn(createSjukfallEnhetList());
        when(sjukfallEngine.beraknaSjukfallForPatient(anyListOf(IntygData.class), any(IntygParametrar.class))).thenReturn(createSjukfallPatientList());
        when(statisticsCalculator.getSjukfallSummary(anyListOf(SjukfallEnhetRS.class))).thenReturn(new SjukfallSummary(0, Collections.emptyList(), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    public void testWhenNoUrvalSet() {
        thrown.expect(IllegalArgumentException.class);
        testee.getSjukfall(enhetsId, null, "", null, getSjukfallRequest(intygsGlapp, activeDate));
    }

    @Test
    public void testWhenUrvalIsAll() {
        List<SjukfallEnhetRS> internalSjukfallList = testee.getSjukfall(enhetsId, null, "", Urval.ALL, getSjukfallRequest(intygsGlapp, activeDate));

        verify(integrationService).getIntygsDataForCareUnit(enhetsId);

        assertTrue("Expected 15 but was " + internalSjukfallList.size(), internalSjukfallList.size() == 15);
    }

    @Test
    public void testWhenUrvalIsAllForUnderenhet() {
        List<SjukfallEnhetRS> internalSjukfallList = testee.getSjukfall(enhetsId, mottagningsId, "", Urval.ALL, getSjukfallRequest(intygsGlapp, activeDate));

        verify(integrationService).getIntygsDataForCareUnit(enhetsId);

        assertTrue("Expected 7 but was " + internalSjukfallList.size(), internalSjukfallList.size() == 7);
    }

    @Test
    public void testWhenUrvalIsIssuedByMe() {
        List<SjukfallEnhetRS> internalSjukfallList = testee.getSjukfall(enhetsId, null, lakareId1, Urval.ISSUED_BY_ME,
                getSjukfallRequest(intygsGlapp, activeDate));

        verify(integrationService).getIntygsDataForCareUnit(enhetsId);

        assertTrue("Expected 8 but was " + internalSjukfallList.size(), internalSjukfallList.size() == 8);
        for (SjukfallEnhetRS internalSjukfall : internalSjukfallList) {
            String hsaId = internalSjukfall.getLakare().getHsaId();
            String namn = internalSjukfall.getLakare().getNamn();
            assertTrue(lakareId1 == hsaId);
            assertEquals(lakareNamn1, namn);
        }
    }

    @Test
    public void testGetSjukfallSummary() {
        testee.getSummary(enhetsId, null, lakareId1, Urval.ALL, getSjukfallRequest(intygsGlapp, activeDate));

        verify(integrationService).getIntygsDataForCareUnit(enhetsId);
        verify(statisticsCalculator).getSjukfallSummary(anyListOf(SjukfallEnhetRS.class));
    }

    @Test
    public void testGetSjukfallSummaryWhenSelectedVardenhetIsMottagning() {
        testee.getSummary(enhetsId, mottagningsId, lakareId1, Urval.ALL, getSjukfallRequest(intygsGlapp, activeDate));

        verify(integrationService).getIntygsDataForCareUnit(enhetsId);
        verify(statisticsCalculator).getSjukfallSummary(anyListOf(SjukfallEnhetRS.class));
    }

    // - - - Private scope - - -

    private GetSjukfallRequest getSjukfallRequest(int maxIntygsGlapp, LocalDate aktivtDatum) {
        GetSjukfallRequest request = new GetSjukfallRequest();
        request.setMaxIntygsGlapp(maxIntygsGlapp);
        request.setAktivtDatum(aktivtDatum);
        return request;
    }

    private List<SjukfallEnhet> createSjukfallEnhetList() {
        List<SjukfallEnhet> sjukfallList = new ArrayList<>();

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

    private List<SjukfallPatient> createSjukfallPatientList() {
        return new ArrayList<>();
    }

    private SjukfallEnhet createSjukfallEnhet(String lakareId, String lakareNamn, String enhetsId) {
        SjukfallEnhet sjukfall = new SjukfallEnhet();

        Vardenhet vardenhet = new Vardenhet(enhetsId, "enhet-" + enhetsId);
        sjukfall.setVardenhet(vardenhet);

        Lakare lakare = new Lakare(lakareId, lakareNamn);
        sjukfall.setLakare(lakare);

        return sjukfall;
    }

    private Diagnos createDiagnos() {
        Diagnos diagnos = new Diagnos("M123   Palindrom reumatism", "M123", "Palindrom reumatism");
        diagnos.setBeskrivning("En beskrivning");
        diagnos.setKapitel("M00-M99Ett diagnoskapitel");

        return diagnos;
    }


    private class SjukfallServiceImplTest extends SjukfallServiceImpl {

        private final String vardgivareId = "IFV1239877878-0000";
        private final String vardgivareNamn = "Vårdgivare-1";
        private final String patientId = "19121212-1212";
        private final String patinetNamn = "Tolvan Tolvansson";

        @Override
        IntygData map(IntygsData from) {
            HsaId hsaId = new HsaId();
            hsaId.setExtension(vardgivareId);

            Vardgivare vardgivare = new Vardgivare();
            vardgivare.setVardgivarId(hsaId);
            vardgivare.setVardgivarnamn(vardgivareNamn);

            // Update IntygsData/Enhet with a Vardgivare, otherwise tests will fail
            from.getSkapadAv().getEnhet().setVardgivare(vardgivare);

            return super.map(from);
        }

        @Override
        SjukfallEnhetRS map(SjukfallEnhet from) {
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

            return super.map(from);
        }

        @Override
        Diagnos map(se.inera.intyg.infra.sjukfall.dto.DiagnosKod from) {
            Diagnos to = new Diagnos(from.getOriginalCode(), from.getCleanedCode(), from.getName());
            to.setBeskrivning("en beskrivning");
            to.setKapitel("ett kapitel");
            return to;
        }
    }
}
