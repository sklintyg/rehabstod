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
package se.inera.intyg.rehabstod.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.authorities.Role;
import se.inera.intyg.rehabstod.service.Urval;

import com.google.common.collect.ImmutableMap;

/**
 * Created by marced on 01/03/16.
 */
public class RehabstodUserTest {

    private static final String ENHET_1 = "ENHET_1";

    @Test
    public void testChangeSelectedUrvalToNull() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson");
        assertTrue(user.changeSelectedUrval(null));

    }

    @Test
    public void testChangeSelectedUrvalWithoutRole() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson");
        assertFalse(user.changeSelectedUrval(Urval.ALL));
        assertFalse(user.changeSelectedUrval(Urval.ALL));
    }

    @Test
    public void testChangeSelectedUrvalLakare() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson");
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_LAKARE, new Role()));
        assertFalse(user.changeSelectedUrval(Urval.ALL));
        assertTrue(user.changeSelectedUrval(Urval.ISSUED_BY_ME));
    }

    @Test
    public void testChangeSelectedUrvalRehabKoordinator() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson");
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_KOORDINATOR, new Role()));
        assertTrue(user.changeSelectedUrval(Urval.ALL));
        assertFalse(user.changeSelectedUrval(Urval.ISSUED_BY_ME));
    }

    @Test
    public void testGetDefaultUrvalRehabKoordinator() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson");
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_KOORDINATOR, new Role()));
        assertEquals(Urval.ALL, user.getDefaultUrval());
    }

    @Test
    public void testGetDefaultUrvalLakare() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson");
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_LAKARE, new Role()));
        assertEquals(Urval.ISSUED_BY_ME, user.getDefaultUrval());
    }

    @Test
    public void testChangeValdVardenhet() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson");

        user.setVardgivare(buildVardgivare("VG1"));

        assertEquals(1, user.getVardgivare().size());
        assertNull(user.getValdVardgivare());
        assertNull(user.getValdVardenhet());
        user.setValdVardgivare(user.getVardgivare().get(0));

        assertTrue(user.changeValdVardenhet(ENHET_1));
        assertFalse(user.changeValdVardenhet("dummyid"));
    }

    @Test
    public void testGetTotaltAntalVardenheter() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson");
        List<Vardgivare> vgList = new ArrayList<>();
        vgList.addAll(buildVardgivare("VG1"));
        vgList.addAll(buildVardgivare("VG2"));
        user.setVardgivare(vgList);

        assertEquals(4, user.getTotaltAntalVardenheter());

    }

    private List<Vardgivare> buildVardgivare(String vardgivarId) {
        List<Vardgivare> list = new ArrayList<>();
        Vardgivare vg = new Vardgivare(vardgivarId, "Vårdgivare 1");
        Vardenhet ve1 = new Vardenhet(ENHET_1, "En enhet");
        Vardenhet ve2 = new Vardenhet("ENHET_2", "En annan enhet");
        vg.setVardenheter(Arrays.asList(ve1, ve2));
        list.add(vg);
        return list;
    }

}
