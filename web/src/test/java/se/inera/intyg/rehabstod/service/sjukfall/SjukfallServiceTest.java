/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationServiceImpl;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.SjukfallEngineImpl;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

/**
 * Created by Magnus Ekstrand on 2016-02-24.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallServiceTest {
    // CHECKSTYLE:OFF MagicNumber

    private final String enhetsId = "IFV1239877878-1042";
    private final String lakareId1 = "IFV1239877878-1049";
    private final String lakareNamn1 = "Jan Nilssom";
    private final String lakareId2 = "IFV1239877878-104B";
    private final String lakareNamn2 = "Åsa Andersson";

    private Integer intygsGlapp = 5;
    private LocalDate activeDate = LocalDate.parse("2016-02-16");

    @Mock
    private IntygstjanstIntegrationServiceImpl integrationService;

    @Mock
    private SjukfallEngineImpl engine;

    @Mock
    private StatisticsCalculator statisticsCalculator;

    @InjectMocks
    private SjukfallService testee = new SjukfallServiceImpl();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws IOException {
        when(integrationService.getIntygsDataForCareUnit(anyString())).thenReturn(new ArrayList<IntygsData>());
        when(engine.calculate(anyListOf(IntygsData.class), any(GetSjukfallRequest.class))).thenReturn(createInternalSjukfallList());
        when(statisticsCalculator.getSjukfallSummary(anyListOf(InternalSjukfall.class))).thenReturn(new SjukfallSummary(0, Collections.emptyList(), new ArrayList<>()));
    }

    @Test
    public void testWhenNoUrvalSet() {
        thrown.expect(IllegalArgumentException.class);
        testee.getSjukfall(enhetsId, "", null, getSjukfallRequest(intygsGlapp, activeDate));
    }

    @Test
    public void testWhenUrvalIsAll() {
        List<InternalSjukfall> internalSjukfallList = testee.getSjukfall(enhetsId, "", Urval.ALL, getSjukfallRequest(intygsGlapp, activeDate));

        verify(integrationService).getIntygsDataForCareUnit(enhetsId);

        assertTrue("Expected 15 but was " + internalSjukfallList.size(), internalSjukfallList.size() == 15);
    }

    @Test
    public void testWhenUrvalIsIssuedByMe() {
        List<InternalSjukfall> internalSjukfallList = testee.getSjukfall(enhetsId, lakareId1, Urval.ISSUED_BY_ME,
                getSjukfallRequest(intygsGlapp, activeDate));

        verify(integrationService).getIntygsDataForCareUnit(enhetsId);

        assertTrue("Expected 8 but was " + internalSjukfallList.size(), internalSjukfallList.size() == 8);
        for (InternalSjukfall internalSjukfall : internalSjukfallList) {
            String hsaId = internalSjukfall.getSjukfall().getLakare().getHsaId();
            String namn = internalSjukfall.getSjukfall().getLakare().getNamn();
            assertTrue(lakareId1 == hsaId);
            assertEquals(lakareNamn1, namn);
        }
    }

    @Test
    public void testGetSjukfallSummary() {
        testee.getSummary(enhetsId, lakareId1, Urval.ALL, getSjukfallRequest(intygsGlapp, activeDate));

        verify(integrationService).getIntygsDataForCareUnit(enhetsId);
        verify(statisticsCalculator).getSjukfallSummary(anyListOf(InternalSjukfall.class));

    }
    // - - - Private scope - - -

    private GetSjukfallRequest getSjukfallRequest(int maxIntygsGlapp, LocalDate aktivtDatum) {
        GetSjukfallRequest request = new GetSjukfallRequest();
        request.setMaxIntygsGlapp(maxIntygsGlapp);
        request.setAktivtDatum(aktivtDatum);
        return request;
    }

    private List<InternalSjukfall> createInternalSjukfallList() {
        List<InternalSjukfall> internalSjukfallList = new ArrayList<>();

        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2));

        return internalSjukfallList;
    }

    private InternalSjukfall createInternalSjukfall(String lakareId, String lakareNamn) {
        Lakare lakare = new Lakare();
        lakare.setHsaId(lakareId);
        lakare.setNamn(lakareNamn);

        Sjukfall sjukfall = new Sjukfall();
        sjukfall.setLakare(lakare);

        InternalSjukfall internalSjukfall = new InternalSjukfall();
        internalSjukfall.setSjukfall(sjukfall);

        return internalSjukfall;
    }

}
