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
package se.inera.intyg.rehabstod.auth.pdl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

import se.inera.intyg.common.logmessages.ActivityType;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by marced on 22/02/16.
 */
public class PDLActivityEntryStoreImplTest {
    // CHECKSTYLE:OFF MagicNumber

    private static final InternalSjukfall SJUKFALL_1 = createSjukFallForPatient("111");
    private static final InternalSjukfall SJUKFALL_2 = createSjukFallForPatient("222");
    private static final InternalSjukfall SJUKFALL_3 = createSjukFallForPatient("333");
    private static final InternalSjukfall SJUKFALL_4 = createSjukFallForPatient("444");
    private static final InternalSjukfall SJUKFALL_5 = createSjukFallForPatient("555");

    private static final String VARDENHET_1 = "H111111";
    private static final String VARDENHET_2 = "H222222";

    final List<InternalSjukfall> sjukfallList1 = Arrays.asList(SJUKFALL_1, SJUKFALL_2, SJUKFALL_3, SJUKFALL_4);

    // list2 has some patients as list 1 but adds patient 5
    final List<InternalSjukfall> sjukfallList2 = Arrays.asList(SJUKFALL_2, SJUKFALL_3, SJUKFALL_5);

    @Test
    public void testGetActivitiesNotInStoreEmptyOrNullList() throws Exception {
        PDLActivityStoreImpl store = new PDLActivityStoreImpl();
        assertTrue(store.getActivitiesNotInStore(VARDENHET_1, null, ActivityType.READ).isEmpty());
        assertTrue(store.getActivitiesNotInStore(VARDENHET_1, new ArrayList<>(), ActivityType.READ).isEmpty());
    }

    @Test
    public void testGetActivitiesNotInStoreSimple() throws Exception {
        PDLActivityStoreImpl store = new PDLActivityStoreImpl();

        // Asking for stored activities when none is stored should return all
        assertEquals(sjukfallList1, store.getActivitiesNotInStore(VARDENHET_1, sjukfallList1, ActivityType.READ));
    }

    @Test
    public void testGetActivitiesNotInStoreDifferentEventTypes() throws Exception {
        PDLActivityStoreImpl store = new PDLActivityStoreImpl();

        // Asking for stored activities when none is stored should return all
        assertEquals(sjukfallList1, store.getActivitiesNotInStore(VARDENHET_1, sjukfallList1, ActivityType.READ));

        // After storing these...
        store.addActivitiesToStore(VARDENHET_1, sjukfallList1, ActivityType.READ);
        // ..and asking again - we should get none in return
        assertTrue(store.getActivitiesNotInStore(VARDENHET_1, sjukfallList1, ActivityType.READ).isEmpty());

        // ..but if we ask for the same list sjukfall - but with another actionType - we should get them back
        assertEquals(sjukfallList1, store.getActivitiesNotInStore(VARDENHET_1, sjukfallList1, ActivityType.PRINT));
    }

    @Test
    public void testActivitiesNotInStoreComplexScenario() throws Exception {

        PDLActivityStoreImpl store = new PDLActivityStoreImpl();

        // Asking for stored activities when none is stored should return all
        assertEquals(sjukfallList1, store.getActivitiesNotInStore(VARDENHET_1, sjukfallList1, ActivityType.READ));
        // Add them
        store.addActivitiesToStore(VARDENHET_1, sjukfallList1, ActivityType.READ);

        // subsequent addition of same eventtype should only add and return new sjukfall not previously logged
        assertEquals(Arrays.asList(SJUKFALL_5), store.getActivitiesNotInStore(VARDENHET_1, sjukfallList2, ActivityType.READ));

        // asking for already stored sjukfall(but other eventtype) should return all again
        assertEquals(sjukfallList1, store.getActivitiesNotInStore(VARDENHET_1, sjukfallList1, ActivityType.PRINT));
        store.addActivitiesToStore(VARDENHET_1, sjukfallList1, ActivityType.PRINT);
        // and subsequent request none...
        assertTrue(store.getActivitiesNotInStore(VARDENHET_1, new ArrayList<>(), ActivityType.PRINT).isEmpty());

        // ..unless we change vardenehet, in which case we should get all again
        assertEquals(sjukfallList1, store.getActivitiesNotInStore(VARDENHET_2, sjukfallList1, ActivityType.PRINT));
    }

    private static InternalSjukfall createSjukFallForPatient(String patientId) {
        Sjukfall sjukfall = new Sjukfall();

        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setAlder(50);
        patient.setNamn("patient " + patientId);
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
