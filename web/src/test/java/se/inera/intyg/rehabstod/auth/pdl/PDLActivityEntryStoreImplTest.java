/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.auth.pdl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

/**
 * Created by marced on 22/02/16.
 */
public class PDLActivityEntryStoreImplTest {
    // CHECKSTYLE:OFF MagicNumber

    private static final SjukfallEnhet SJUKFALL_1 = createSjukFallForPatient("19990824-2382");
    private static final SjukfallEnhet SJUKFALL_2 = createSjukFallForPatient("19990826-2380");
    private static final SjukfallEnhet SJUKFALL_3 = createSjukFallForPatient("19990828-2388");
    private static final SjukfallEnhet SJUKFALL_4 = createSjukFallForPatient("19990831-2391");
    private static final SjukfallEnhet SJUKFALL_5 = createSjukFallForPatient("19990903-2386");

    private static final String VARDENHET_1 = "H111111";
    private static final String VARDENHET_2 = "H222222";

    final List<SjukfallEnhet> sjukfallList1 = Arrays.asList(SJUKFALL_1, SJUKFALL_2, SJUKFALL_3, SJUKFALL_4);

    // list2 has some patients as list 1 but adds patient 5
    final List<SjukfallEnhet> sjukfallList2 = Arrays.asList(SJUKFALL_2, SJUKFALL_3, SJUKFALL_5);

    @Test
    public void testGetActivitiesNotInStoreEmptyOrNullList() throws Exception {
        assertTrue(PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, null,
            ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, new HashMap<>()).isEmpty());
        assertTrue(PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, new ArrayList<>(),
            ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, new HashMap<>()).isEmpty());
        assertTrue(PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, null,
            ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, null).isEmpty());
        assertTrue(PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, new ArrayList<>(),
            ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, null).isEmpty());
    }

    @Test
    public void testGetActivitiesNotInStoreSimple() throws Exception {
        // Asking for stored activities when none is stored should return all
        assertEquals(sjukfallList1,
            PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, sjukfallList1,
                ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, new HashMap<>()));
    }

    @Test
    public void testGetActivitiesNotInStoreDifferentEventTypes() throws Exception {
        Map<String, List<PDLActivityEntry>> storedActivities = new HashMap<>();

        // Asking for stored activities when none is stored should return all
        assertEquals(sjukfallList1,
            PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, sjukfallList1,
                ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, storedActivities));

        // After storing these...
        PDLActivityStore.addActivitiesToStore(VARDENHET_1, sjukfallList1,
            ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, storedActivities);
        // ..and asking again - we should get none in return
        assertTrue(PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, sjukfallList1,
            ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, storedActivities).isEmpty());

        // ..but if we ask for the same list sjukfall - but with another actionType - we should get them back
        assertEquals(sjukfallList1,
            PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, sjukfallList1,
                ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL, storedActivities));
    }

    @Test
    public void testActivitiesNotInStoreComplexScenario() throws Exception {
        Map<String, List<PDLActivityEntry>> storedActivities = new HashMap<>();

        // Asking for stored activities when none is stored should return all
        assertEquals(sjukfallList1,
            PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, sjukfallList1,
                ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, storedActivities));

        // ...now add them
        PDLActivityStore.addActivitiesToStore(VARDENHET_1, sjukfallList1,
            ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, storedActivities);

        // subsequent addition of same eventtype should only add and return new sjukfall not previously logged
        assertEquals(Arrays.asList(SJUKFALL_5),
            PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, sjukfallList2,
                ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, storedActivities));

        // asking for already stored sjukfall(but other eventtype) should return all again
        assertEquals(sjukfallList1,
            PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, sjukfallList1,
                ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL, storedActivities));
        PDLActivityStore.addActivitiesToStore(VARDENHET_1, sjukfallList1,
            ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL, storedActivities);

        // and subsequent request none...
        assertTrue(PDLActivityStore.getActivitiesNotInStore(VARDENHET_1, new ArrayList<>(),
            ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL, storedActivities).isEmpty());

        // ..unless we change vardenehet, in which case we should get all again
        assertEquals(sjukfallList1,
            PDLActivityStore.getActivitiesNotInStore(VARDENHET_2, sjukfallList1,
                ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL, storedActivities));
    }

    private static SjukfallEnhet createSjukFallForPatient(String patientId) {
        // CHECKSTYLE:OFF MagicNumber
        SjukfallEnhet isf = new SjukfallEnhet();

        Lakare lakare = new Lakare("123456-0987", "Hr Doktor");
        isf.setLakare(lakare);

        Patient patient = new Patient(patientId, patientId);
        patient.setAlder(50);
        isf.setPatient(patient);

        // Not really interested in these properties, but the sjukfall equals /hashcode will fail without them
        Diagnos diagnos = new Diagnos("M16", "M16", "diagnosnamn");
        diagnos.setKapitel("M00-M99");
        isf.setDiagnos(diagnos);

        isf.setStart(LocalDate.now());
        isf.setSlut(LocalDate.now());

        isf.setDagar(1);
        isf.setIntyg(1);
        isf.setGrader(new ArrayList<>());
        isf.setAktivGrad(50);

        return isf;
        // CHECKSTYLE:ON MagicNumber
    }
}
