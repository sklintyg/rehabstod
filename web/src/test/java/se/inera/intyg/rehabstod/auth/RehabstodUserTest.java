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
package se.inera.intyg.rehabstod.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.inera.intyg.rehabstod.auth.util.SystemRolesParser.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.service.Urval;

/**
 * @author marced on 01/03/16.
 */
public class RehabstodUserTest {

    private static final String VG_1 = "VG_1";
    private static final String VG_1_NAME = "Vårdgivare 1";
    private static final String VG_2 = "VG_2";
    private static final String VG_2_NAME = "Vårdgivare 2";

    private static final String ENHET_1 = "ENHET_1";
    private static final String ENHET_1_NAME = "En enhet";
    private static final String ENHET_2 = "ENHET_2";
    private static final String ENHET_2_NAME = "En annan enhet";


    @Test
    public void testGetUrvalLakare() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson", true);
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_LAKARE, new Role()));

        assertEquals(Urval.ISSUED_BY_ME, user.getUrval());
    }

    @Test
    public void testGetUrvalTandlakare() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson", true);
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_TANDLAKARE, new Role()));

        assertEquals(Urval.ISSUED_BY_ME, user.getUrval());
    }

    @Test
    public void testGetUrvalRehabKoordinator() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson", false);
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_KOORDINATOR, new Role()));

        assertEquals(Urval.ALL, user.getUrval());
    }

    @Test
    public void testChangeValdVardenhet() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson", true);
        user.setVardgivare(buildVardgivare(VG_1, VG_1_NAME));

        assertEquals(1, user.getVardgivare().size());
        assertNull(user.getValdVardgivare());
        assertNull(user.getValdVardenhet());
        user.setValdVardgivare(user.getVardgivare().get(0));

        assertTrue(user.changeValdVardenhet(ENHET_1));
        assertFalse(user.changeValdVardenhet("dummyid"));
        assertFalse(user.changeValdVardenhet(null));
    }

    @Test
    public void testGetTotaltAntalVardenheter() throws Exception {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson", true);
        List<Vardgivare> vgList = new ArrayList<>();
        vgList.addAll(buildVardgivare(VG_1, VG_1_NAME));
        vgList.addAll(buildVardgivare(VG_2, VG_2_NAME));
        user.setVardgivare(vgList);

        assertEquals(4, user.getTotaltAntalVardenheter());

    }

    @Test
    public void testRoleSwitchPossibleForDoctorWithMatchingSystemRole() {
        RehabstodUser user = setupRehabstodUserWithSystemRoles(true, new ArrayList<>(), HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_1);
        assertTrue(user.isRoleSwitchPossible());
    }

    @Test
    public void testRoleSwitchNotPossibleForDoctorWithoutMatchingSystemRole() {
        RehabstodUser user = setupRehabstodUserWithSystemRoles(true, new ArrayList<>(),
            HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + "some-other-unit-id");
        assertFalse(user.isRoleSwitchPossible());
    }

    @Test
    public void testRoleSwitchNotPossibleForNonDoctorWithMatchingSystemRole() {
        RehabstodUser user = setupRehabstodUserWithSystemRoles(false, new ArrayList<>(), HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_1);
        assertFalse(user.isRoleSwitchPossible());
    }

    private RehabstodUser setupRehabstodUserWithSystemRoles(boolean isLakare, ArrayList<String> systemRoles, String e) {
        RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson", isLakare);
        List<Vardgivare> vgList = new ArrayList<>();
        vgList.addAll(buildVardgivare(VG_1, VG_1_NAME));
        vgList.addAll(buildVardgivare(VG_2, VG_2_NAME));
        user.setVardgivare(vgList);
        user.setSystemRoles(systemRoles);
        user.getSystemRoles().add(e);
        return user;
    }

    @Test
    public void serializeToDisk() {
        try {
            RehabstodUser user = new RehabstodUser("HSA1111", "Per Nilsson", true);
            user.setVardgivare(buildVardgivare(VG_1, VG_1_NAME));

            // Write to disk
            FileOutputStream fos = new FileOutputStream("tempdata.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
            oos.close();
        } catch (Exception ex) {
            fail("Exception thrown during test: " + ex.toString());
        }

        try {
            FileInputStream fis = new FileInputStream("tempdata.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            RehabstodUser user = (RehabstodUser) ois.readObject();
            ois.close();

            assertEquals(user.getHsaId(), "HSA1111");
            assertEquals(user.getNamn(), "Per Nilsson");
            assertEquals(user.getVardgivare().get(0).getId(), VG_1);
            assertEquals(user.getVardgivare().get(0).getNamn(), VG_1_NAME);
            assertEquals(user.getVardgivare().get(0).getVardenheter().get(0).getId(), ENHET_1);
            assertEquals(user.getVardgivare().get(0).getVardenheter().get(0).getNamn(), ENHET_1_NAME);
            assertEquals(user.getVardgivare().get(0).getVardenheter().get(1).getId(), ENHET_2);
            assertEquals(user.getVardgivare().get(0).getVardenheter().get(1).getNamn(), ENHET_2_NAME);

            // Clean up the file
            new File("tempdata.ser").delete();
        } catch (Exception ex) {
            fail("Exception thrown during test: " + ex.toString());
        }
    }

    private List<Vardgivare> buildVardgivare(String vardgivarId, String vardgivarName) {
        List<Vardgivare> list = new ArrayList<>();
        Vardgivare vg = new Vardgivare(vardgivarId, vardgivarName);
        Vardenhet ve1 = new Vardenhet(ENHET_1, ENHET_1_NAME);
        Vardenhet ve2 = new Vardenhet(ENHET_2, ENHET_2_NAME);
        vg.setVardenheter(Arrays.asList(ve1, ve2));
        list.add(vg);
        return list;
    }

}
