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
package se.inera.intyg.rehabstod.auth.authorities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.common.integration.hsa.services.HsaPersonService;
import se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion;
import se.inera.intyg.rehabstod.auth.authorities.bootstrap.AuthoritiesConfigurationLoader;
import se.riv.infrastructure.directory.v1.PaTitleType;
import se.riv.infrastructure.directory.v1.PersonInformationType;

//CHECKSTYLE:OFF MagicNumber
@RunWith(MockitoJUnitRunner.class)
public class AuthoritiesResolverTest {

    private String configurationLocation = "AuthoritiesConfigurationLoaderTest/authorities-test.yaml";

    @Mock
    private HsaPersonService hsaPersonService;

    @Spy
    private AuthoritiesConfigurationLoader configurationLoader = new AuthoritiesConfigurationLoader(configurationLocation);

    @InjectMocks
    private AuthoritiesResolver authoritiesResolver = new AuthoritiesResolver();

    @Before
    public void setup() throws Exception {
        configurationLoader.afterPropertiesSet();
    }

    @Test
    public void lookupUserRoleWhenTitleIsDoctor() throws Exception {
        // given
        List<String> titles = Collections.singletonList("Läkare");
        // when
        Role role = authoritiesResolver.lookupUserRoleByLegitimeradeYrkesgrupper(titles);
        // then
        assertTrue(role.getName().equalsIgnoreCase(AuthoritiesConstants.ROLE_LAKARE));
    }

    @Test
    public void lookupUserRoleWhenMultipleTitlesAndOneIsDoctor() {
        // given
        List<String> titles = Arrays.asList("Läkare", "Barnmorska", "Sjuksköterska");
        // when
        Role role = authoritiesResolver.lookupUserRoleByLegitimeradeYrkesgrupper(titles);
        // then
        assertTrue(role.getName().equalsIgnoreCase(AuthoritiesConstants.ROLE_LAKARE));
    }

    @Test
    public void lookupUserRoleWhenMultipleTitlesAndNoDoctor() {
        // given
        List<String> titles = Arrays.asList("Barnmorska", "Sjuksköterska");
        // when
        Role userRole = authoritiesResolver.lookupUserRoleByLegitimeradeYrkesgrupper(titles);
        // then
        assertNull(userRole);
    }

    @Test
    public void lookupUserRoleWhenTitleCodeIs204010() {
        // given
        List<String> befattningsKoder = Collections.singletonList("204010");
        // when
        Role role = authoritiesResolver.lookupUserRoleByBefattningskod(befattningsKoder);
        // then
        assertTrue(role.getName().equalsIgnoreCase(AuthoritiesConstants.ROLE_LAKARE));
    }

    @Test
    public void lookupUserRoleWhenTitleCodeIsNot204010() {
        // given
        List<String> befattningsKoder = Arrays.asList("203090", "204090", "", null);
        // when
        Role role = authoritiesResolver.lookupUserRoleByBefattningskod(befattningsKoder);
        // then
        assertNull(role);
    }

    @Test
    public void lookupUserRoleByTitleCodeAndGroupPrescriptionCode() {
        // given
        List<String> befattningsKoder = Arrays.asList("204010", "203090", "204090");
        List<String> gruppforskrivarKoder = Arrays.asList("9300005", "9100009");

        Role[][] roleMatrix = new Role[3][2];

        // when
        for (int i = 0; i < befattningsKoder.size(); i++) {
            for (int j = 0; j < gruppforskrivarKoder.size(); j++) {
                Role role = authoritiesResolver.lookupUserRoleByBefattningskodAndGruppforskrivarkod(befattningsKoder.get(i),
                        gruppforskrivarKoder.get(j));
                roleMatrix[i][j] = role;
                // System.err.println("[" + i + "," + j + "] " + (role == null ? "null" : role.getName()));
            }
        }

        // then

        /*
         * Expected matrix:
         * [0,0] null
         * [0,1] null
         * [1,0] LAKARE
         * [1,1] null
         * [2,0] null
         * [2,1] LAKARE
         */

        for (int i = 0; i < befattningsKoder.size(); i++) {
            for (int j = 0; j < gruppforskrivarKoder.size(); j++) {
                if ((i == 0) && ((j == 0) || (j == 1))) {
                    assertNull(roleMatrix[i][j]);
                } else if ((i == 2) && (j == 0)) {
                    assertNull(roleMatrix[i][j]);
                } else if ((i == 1) && (j == 1)) {
                    assertNull(roleMatrix[i][j]);
                } else {
                    assertTrue(roleMatrix[i][j].getName().equalsIgnoreCase(AuthoritiesConstants.ROLE_LAKARE));
                }
            }
        }
    }

    @Test
    public void lookupUserRoleByTitleCodeAndGroupPrescriptionCodeNoMatchReturnsNull() {
        // Act
        Role role = authoritiesResolver.lookupUserRoleByBefattningskodAndGruppforskrivarkod(new ArrayList<String>(), new ArrayList<String>());

        // Assert
        assertNull(role);

    }

    @Test
    public void lookupUserRoleByTitleCodeAndGroupPrescriptionCodeCombination() {
        // Arrange
        List<String> befattningsKoder = Arrays.asList("204010", "203090", "204090");
        List<String> gruppforskrivarKoder = Arrays.asList("9300005", "9100009");

        // Act
        Role role = authoritiesResolver.lookupUserRoleByBefattningskodAndGruppforskrivarkod(befattningsKoder, gruppforskrivarKoder);

        // Assert
        assertEquals(AuthoritiesConstants.ROLE_LAKARE, role.getName());

    }

    @Test
    public void testExtractLegitimeradeYrkesgrupper() throws Exception {

        // Arrange
        final PaTitleType paTitleType1 = new PaTitleType();
        paTitleType1.setPaTitleName("paTitle1");

        final PaTitleType paTitleType2 = new PaTitleType();
        paTitleType2.setPaTitleName("paTitle2");

        PersonInformationType pt = new PersonInformationType();
        pt.setTitle("title1");

        pt.getPaTitle().addAll(Arrays.asList(paTitleType1, paTitleType2));

        List<PersonInformationType> hsaPersonInfo = new ArrayList<>();
        hsaPersonInfo.add(pt);

        // Test
        final List<String> legitimeradeYrkesgrupper = authoritiesResolver.extractLegitimeradeYrkesgrupper(hsaPersonInfo);

        // Verify
        assertEquals(2, legitimeradeYrkesgrupper.size());
        assertTrue(legitimeradeYrkesgrupper.contains("paTitle1"));
        assertTrue(legitimeradeYrkesgrupper.contains("paTitle2"));

    }

    @Test
    public void testResolveRehabkoordinatorRole() throws Exception {
        // Arrange
        SakerhetstjanstAssertion sa = Mockito.mock(SakerhetstjanstAssertion.class);

        // Act
        Role role = authoritiesResolver.lookupUserRole(sa, new ArrayList<>());

        // Verify
        assertEquals(AuthoritiesConstants.ROLE_KOORDINATOR, role.getName());
    }

    @Test
    public void testGetIntygsTyper() throws Exception {
        // Arrange

        // Act
        final List<String> intygstyper = authoritiesResolver.getIntygstyper();

        // Verify
        // (We don't have any in rehabstod)
        assertEquals(0, intygstyper.size());
    }

    @Test
    public void testGetPrivileges() throws Exception {
        // Arrange

        // Act
        final List<Privilege> privileges = authoritiesResolver.getPrivileges();

        // Verify
        assertEquals(1, privileges.size());
        assertEquals("VISA_SJUKFALL", privileges.get(0).getName());
    }

    @Test
    public void testGetRequestOrigins() throws Exception {
        // Arrange

        // Act
        final List<RequestOrigin> requestOrigins = authoritiesResolver.getRequestOrigins();

        // Verify
        assertEquals(1, requestOrigins.size());
        assertEquals("NORMAL", requestOrigins.get(0).getName());
    }

    @Test
    public void testGetTitles() throws Exception {
        // Arrange

        // Act
        final List<Title> titles = authoritiesResolver.getTitles();

        // Verify
        assertEquals(2, titles.size());

    }

}
