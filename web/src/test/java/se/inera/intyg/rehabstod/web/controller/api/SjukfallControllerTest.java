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

import com.itextpdf.text.DocumentException;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.logmessages.ActivityType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityEntry;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStore;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.pdf.PdfExportService;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PDLActivityStore.class)
public class SjukfallControllerTest {

    private static final String VARDENHETS_ID = "123";
    private static final String MOTTAGNINGS_ID = "654";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    RehabstodUser rehabUserMock;

    @Mock
    UserService userService;

    @Mock
    LogService logserviceMock;

    @Mock
    PdfExportService pdfExportService;

    @Mock
    private SjukfallService sjukfallService;

    @InjectMocks
    private SjukfallController sjukfallController = new SjukfallController();

    @Before
    public void before() {
        when(userService.getUser()).thenReturn(rehabUserMock);
        //when(rehabUserMock.getPdlActivityStore()).thenReturn(pdlStoreMock);
        when(rehabUserMock.getValdVardenhet()).thenReturn(new Vardenhet(VARDENHETS_ID, "enhet"));
        when(rehabUserMock.getUrval()).thenReturn(Urval.ALL);
    }

    @Test
    public void testGetSjukfall() {
        List<InternalSjukfall> result = new ArrayList<>();
        result.add(createSjukFallForPatient("111"));
        result.add(createSjukFallForPatient("222"));

        List<InternalSjukfall> toLog = new ArrayList<>();
        toLog.add(createSjukFallForPatient("333"));

        Map<String, List<PDLActivityEntry>> storedActivities = new HashMap<>();

        // Given
        GetSjukfallRequest request = new GetSjukfallRequest();

        // When
        mockStatic(PDLActivityStore.class);
        when(PDLActivityStore.getActivitiesNotInStore(eq(VARDENHETS_ID), eq(result), eq(ActivityType.READ), eq(storedActivities))).thenReturn(toLog);
        when(sjukfallService.getSjukfall(eq(VARDENHETS_ID), isNull(String.class), anyString(), any(Urval.class), any(GetSjukfallRequest.class))).thenReturn(result);

        // Then
        List<Sjukfall> response = sjukfallController.getSjukfallForCareUnit(request);

        // Verify
        verifyStatic();
        PDLActivityStore.getActivitiesNotInStore(eq(VARDENHETS_ID), eq(result), eq(ActivityType.READ), eq(storedActivities));

        verify(sjukfallService).getSjukfall(eq(VARDENHETS_ID), isNull(String.class), anyString(), any(Urval.class), any(GetSjukfallRequest.class));
        verify(logserviceMock).logSjukfallData(eq(toLog), eq(ActivityType.READ));
    }

    @Test
    public void testGetSjukfallAsPDF() throws DocumentException, IOException {

        InternalSjukfall a = createSjukFallForPatient("111");
        InternalSjukfall b = createSjukFallForPatient("222");
        InternalSjukfall c = createSjukFallForPatient("333");

        List<InternalSjukfall> allSjukFall = Arrays.asList(a, b, c);
        List<InternalSjukfall> finalList = Arrays.asList(a, b);
        List<InternalSjukfall> toLog = Arrays.asList(c);

        Map<String, List<PDLActivityEntry>> storedActivities = new HashMap<>();

        // Given
        PrintSjukfallRequest request = new PrintSjukfallRequest();
        request.setPersonnummer(Arrays.asList("111", "222"));

        // When
        mockStatic(PDLActivityStore.class);
        doNothing().when(PDLActivityStore.class); //This is the preferred way to mock static void methods
        PDLActivityStore.addActivitiesToStore(eq(VARDENHETS_ID), eq(toLog), eq(ActivityType.PRINT), eq(storedActivities));
        when(PDLActivityStore.getActivitiesNotInStore(eq(VARDENHETS_ID), eq(finalList), eq(ActivityType.PRINT), eq(storedActivities))).thenReturn(toLog);

        when(sjukfallService.getSjukfall(eq(VARDENHETS_ID), isNull(String.class), anyString(), any(Urval.class), any(GetSjukfallRequest.class))).thenReturn(allSjukFall);
        when(pdfExportService.export(eq(finalList), eq(request), eq(rehabUserMock), eq(allSjukFall.size()))).thenReturn(new byte[0]);

        // Then
        ResponseEntity response = sjukfallController.getSjukfallForCareUnitAsPdf(request);

        // Verify
        verifyStatic();
        PDLActivityStore.addActivitiesToStore(eq(VARDENHETS_ID), eq(toLog), eq(ActivityType.PRINT), eq(storedActivities));
        verifyStatic();
        PDLActivityStore.getActivitiesNotInStore(eq(VARDENHETS_ID), eq(finalList), eq(ActivityType.PRINT), eq(storedActivities));

        verify(sjukfallService).getSjukfall(eq(VARDENHETS_ID), isNull(String.class), anyString(), any(Urval.class), any(GetSjukfallRequest.class));
        verify(logserviceMock).logSjukfallData(eq(toLog), eq(ActivityType.PRINT));

        assertTrue(response.getStatusCode().equals(HttpStatus.OK));
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

        Lakare lakare = new Lakare();
        lakare.setHsaId("123456-7890");
        lakare.setNamn("Hr Doktor");
        sjukfall.setLakare(lakare);

        InternalSjukfall is = new InternalSjukfall();
        is.setSjukfall(sjukfall);

        return is;
    }

}
