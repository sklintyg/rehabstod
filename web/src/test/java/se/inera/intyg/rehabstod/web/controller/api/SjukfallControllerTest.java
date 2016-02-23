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
package se.inera.intyg.rehabstod.web.controller.api;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.logmessages.ActivityType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStore;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallControllerTest {

    private static final String VARDENHETS_ID = "123";
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    RehabstodUser rehabUserMock;

    @Mock
    UserService userService;

    @Mock
    PDLActivityStore pdlStoreMock;

    @Mock
    LogService logserviceMock;

    @Mock
    private SjukfallService sjukfallService;

    @InjectMocks
    private SjukfallController sjukfallController = new SjukfallController();

    @Before
    public void before() {
        when(userService.getUser()).thenReturn(rehabUserMock);
        when(rehabUserMock.getPdlActivityStore()).thenReturn(pdlStoreMock);
        when(rehabUserMock.getValdVardenhet()).thenReturn(new Vardenhet(VARDENHETS_ID, "enhet"));
    }

    @Test
    public void testGetSjukfall() {
        List<InternalSjukfall> result = new ArrayList<>();
        result.add(createSjukFallForPatient("111"));
        result.add(createSjukFallForPatient("222"));

        List<InternalSjukfall> toLog = new ArrayList<>();
        result.add(createSjukFallForPatient("333"));

        // Given
        GetSjukfallRequest request = new GetSjukfallRequest();

        // When
        when(sjukfallService.getSjukfall(eq(VARDENHETS_ID), anyString(), any(Urval.class), any(GetSjukfallRequest.class))).thenReturn(result);
        when(pdlStoreMock.getActivitiesNotInStore(eq(VARDENHETS_ID), eq(result), eq(ActivityType.READ))).thenReturn(toLog);

        // Then
        List<Sjukfall> response = sjukfallController.getSjukfallForCareUnit(request);

        verify(sjukfallService).getSjukfall(eq(VARDENHETS_ID), anyString(), any(Urval.class), any(GetSjukfallRequest.class));

        verify(pdlStoreMock).getActivitiesNotInStore(eq(VARDENHETS_ID), eq(result), eq(ActivityType.READ));
        verify(logserviceMock).logSjukfallData(eq(toLog), eq(ActivityType.READ));
        verify(pdlStoreMock).addActivitiesToStore(eq(VARDENHETS_ID), eq(toLog), eq(ActivityType.READ));

    }

    @Test
    public void testGetSjukfallAsPDF() {
        List<InternalSjukfall> result = new ArrayList<>();
        result.add(createSjukFallForPatient("111"));
        result.add(createSjukFallForPatient("222"));

        List<InternalSjukfall> toLog = new ArrayList<>();
        result.add(createSjukFallForPatient("333"));

        // Given
        GetSjukfallRequest request = new GetSjukfallRequest();

        // When
        when(sjukfallService.getSjukfall(eq(VARDENHETS_ID), anyString(), any(Urval.class), any(GetSjukfallRequest.class))).thenReturn(result);
        when(pdlStoreMock.getActivitiesNotInStore(eq(VARDENHETS_ID), eq(result), eq(ActivityType.PRINT))).thenReturn(toLog);

        // Then
        List<Sjukfall> response = sjukfallController.getSjukfallForCareUnitAsPDF(request);

        verify(sjukfallService).getSjukfall(eq(VARDENHETS_ID), anyString(), any(Urval.class), any(GetSjukfallRequest.class));

        verify(pdlStoreMock).getActivitiesNotInStore(eq(VARDENHETS_ID), eq(result), eq(ActivityType.PRINT));
        verify(logserviceMock).logSjukfallData(eq(toLog), eq(ActivityType.PRINT));
        verify(pdlStoreMock).addActivitiesToStore(eq(VARDENHETS_ID), eq(toLog), eq(ActivityType.PRINT));

    }

    private static InternalSjukfall createSjukFallForPatient(String personNummer) {
        // CHECKSTYLE:OFF MagicNumber
        Sjukfall sjukfall = new Sjukfall();
        Patient patient = new Patient();
        patient.setId(personNummer);
        patient.setNamn("patient " + personNummer);
        patient.setAlder(50);
        sjukfall.setPatient(patient);

        // Not really interested in these properties, but the sjukfall equals /hashcode will fail without them
        final Diagnos diagnos = new Diagnos();
        diagnos.setKapitel("M00-M99");
        diagnos.setKod("M16");
        diagnos.setIntygsVarde("M16");

        sjukfall.setDiagnos(diagnos);
        sjukfall.setStart(new LocalDate());
        sjukfall.setSlut(new LocalDate());
        sjukfall.setDagar(1);
        sjukfall.setIntyg(1);
        sjukfall.setGrader(new ArrayList<>());
        sjukfall.setAktivGrad(50);
        sjukfall.setLakare("Hr Doktor");
        InternalSjukfall is = new InternalSjukfall();
        is.setSjukfall(sjukfall);
        return is;
    }

}
