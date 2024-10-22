/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import se.inera.intyg.infra.integration.hsatk.model.HsaSystemRole;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation.PaTitle;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Mottagning;
import se.inera.intyg.infra.integration.hsatk.model.legacy.UserAuthorizationInfo;
import se.inera.intyg.infra.integration.hsatk.model.legacy.UserCredentials;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaOrganizationsService;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaPersonService;
import se.inera.intyg.infra.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.infra.security.authorities.bootstrap.SecurityConfigurationLoader;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.infra.security.common.model.UserOrigin;
import se.inera.intyg.infra.security.common.model.UserOriginType;
import se.inera.intyg.infra.security.common.service.AuthenticationLogger;
import se.inera.intyg.infra.security.exception.HsaServiceException;
import se.inera.intyg.infra.security.exception.MissingMedarbetaruppdragException;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.rehabstod.auth.exceptions.MissingUnitWithRehabSystemRoleException;
import se.inera.intyg.rehabstod.auth.util.SystemRolesParser;
import se.inera.intyg.rehabstod.persistence.model.AnvandarPreference;
import se.inera.intyg.rehabstod.persistence.repository.AnvandarPreferenceRepository;

/**
 * Created by marced on 29/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RehabstodUserDetailsServiceTest {

    protected static final String AUTHORITIES_CONFIGURATION_FILE = "classpath:AuthoritiesConfigurationLoaderTest/authorities-test.yaml";
    protected static final String FEATURES_CONFIGURATION_FILE = "classpath:AuthoritiesConfigurationLoaderTest/features-test.yaml";
    protected static final Integer DEFAULT_MAX_ALIASES_FOR_COLLECTIONS = 300;

    protected static final SecurityConfigurationLoader CONFIGURATION_LOADER = new SecurityConfigurationLoader(
        AUTHORITIES_CONFIGURATION_FILE, FEATURES_CONFIGURATION_FILE, DEFAULT_MAX_ALIASES_FOR_COLLECTIONS);
    protected static final CommonAuthoritiesResolver AUTHORITIES_RESOLVER = new CommonAuthoritiesResolver();
    protected static final AuthoritiesValidator AUTHORITIES_VALIDATOR = new AuthoritiesValidator();
    private static final String PERSONAL_HSAID = "TST5565594230-106J";

    private static final String VARDGIVARE_HSAID = "IFV1239877878-0001";
    private static final String ENHET_HSAID_1 = "IFV1239877878-103H";
    private static final String ENHET_HSAID_2 = "IFV1239877878-103P";
    private static final String MOTTAGNING_HSAID_1 = "IFV1239877878-103M";
    private static final String MOTTAGNING_HSAID_2 = "IFV1239877878-103N";
    private static final String VARDGIVARE_HSAID2 = "IFV2222";
    private static final String ENHET_HSAID_21 = "IFV_222222111";
    private static final String ENHET_HSAID_22 = "IFV_22";
    private static final String ENHET_HSAID_23 = "IFV_23";

    private static final String TITLE_HEAD_DOCTOR = "Överläkare";

    @InjectMocks
    private RehabstodUserDetailsService userDetailsService = new RehabstodUserDetailsService();

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @Mock
    private HsaPersonService hsaPersonService;

    @Mock
    private UserOrigin userOrigin;

    @Mock
    private AuthenticationLogger monitoringLogService;

    @Mock
    private AnvandarPreferenceRepository anvandarPreferenceRepository;

    @Mock
    private RehabstodUnitChangeService rehabstodUnitChangeService;

    @BeforeClass
    public static void setupAuthoritiesConfiguration() throws Exception {

        // Load configuration
        CONFIGURATION_LOADER.afterPropertiesSet();

        // Setup resolver class
        AUTHORITIES_RESOLVER.setConfigurationLoader(CONFIGURATION_LOADER);
    }

    @Before
    public void setup() {
        // Setup a servlet request
        final var request = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ReflectionTestUtils.setField(userDetailsService, "userOrigin", Optional.of(userOrigin));

        when(hsaPersonService.getHsaPersonInfo(anyString())).thenReturn(Collections.emptyList());
        when(userOrigin.resolveOrigin(request)).thenReturn(UserOriginType.NORMAL.name());
        userDetailsService.setCommonAuthoritiesResolver(AUTHORITIES_RESOLVER);

        AnvandarPreference anvandarPreference = new AnvandarPreference(PERSONAL_HSAID, "user_pdl_consent_given", "true");
        when(anvandarPreferenceRepository.findByHsaIdAndKey(PERSONAL_HSAID, "user_pdl_consent_given")).thenReturn(anvandarPreference);
    }

    @Test
    public void assertLoadsOkWhenHasMatchingSystemRole() throws Exception {
        // given
        UserCredentials userCredz = new UserCredentials();
        userCredz.getHsaSystemRole().addAll(buildSystemRoles());
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(userCredz, buildVardgivareList(), buildMiuPerCareUnitMap()));

        setupCallToGetHsaPersonInfoNonDoctor();

        // then
        IntygUser rehabstodUser = userDetailsService.buildUserPrincipal(PERSONAL_HSAID, "fake");

        AUTHORITIES_VALIDATOR.given(rehabstodUser).roles(AuthoritiesConstants.ROLE_KOORDINATOR).orThrow();
        assertEquals(3, rehabstodUser.getTotaltAntalVardenheter());
        assertNotNull("A default vardenhet should have been selected", rehabstodUser.getValdVardenhet());
        assertEquals(ENHET_HSAID_2 + " should have been selected as valdVardgivare", ENHET_HSAID_2,
            rehabstodUser.getValdVardenhet().getId());
    }

    @Test
    public void assertSelectsDefaultVardenhetWhenOnlyOneExists() throws Exception {
        // given
        UserCredentials userCredz = new UserCredentials();
        userCredz.getHsaSystemRole().addAll(buildSystemRoles());

        // Just return one enhet
        Vardgivare vardgivare = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        vardgivare.getVardenheter().add(new Vardenhet(ENHET_HSAID_2, "VårdEnhet2A"));
        List<Vardgivare> vardgivarList = new ArrayList<>();
        vardgivarList.add(vardgivare);

        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(userCredz, vardgivarList, buildMiuPerCareUnitMap()));

        setupCallToGetHsaPersonInfoNonDoctor();

        // then
        IntygUser rehabstodUser = userDetailsService.buildUserPrincipal(PERSONAL_HSAID, "fake");

        AUTHORITIES_VALIDATOR.given(rehabstodUser).roles(AuthoritiesConstants.ROLE_KOORDINATOR).orThrow();
        assertEquals(1, rehabstodUser.getTotaltAntalVardenheter());
        assertEquals(ENHET_HSAID_2 + " should have been selected as valdVardgivare", ENHET_HSAID_2,
            rehabstodUser.getValdVardenhet().getId());
    }

    @Test
    public void assertSelectsDefaultVardenhetWhenOnlyOneExistsEvenIfMottagningarExists() throws Exception {
        // given
        UserCredentials userCredz = new UserCredentials();
        userCredz.getHsaSystemRole().addAll(buildSystemRoles());

        // Just return one enhet
        Vardgivare vardgivare = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        final Vardenhet enhet = new Vardenhet(ENHET_HSAID_2, "VårdEnhet2A");
        final Mottagning mottagning = new Mottagning(MOTTAGNING_HSAID_1, "Mottagning2A1");
        enhet.getMottagningar().add(mottagning);

        vardgivare.getVardenheter().add(enhet);
        List<Vardgivare> vardgivarList = new ArrayList<>();
        vardgivarList.add(vardgivare);

        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(userCredz, vardgivarList, buildMiuPerCareUnitMap()));

        setupCallToGetHsaPersonInfoNonDoctor();

        // then
        IntygUser rehabstodUser = userDetailsService.buildUserPrincipal(PERSONAL_HSAID, "fake");

        AUTHORITIES_VALIDATOR.given(rehabstodUser).roles(AuthoritiesConstants.ROLE_KOORDINATOR).orThrow();
        assertEquals(2, rehabstodUser.getTotaltAntalVardenheter());
        assertEquals(ENHET_HSAID_2 + " should have been selected as valdVardgivare", ENHET_HSAID_2,
            rehabstodUser.getValdVardenhet().getId());
    }

    @Test(expected = MissingUnitWithRehabSystemRoleException.class)
    public void assertThrowsExceptionWhenNoMatchingSystemRole() throws Exception {
        // given
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoNonDoctor();

        // then
        userDetailsService.buildUserPrincipal(PERSONAL_HSAID, "fake");
    }

    @Test(expected = MissingUnitWithRehabSystemRoleException.class)
    public void assertNoRoleWhenUserHasTitleLakareButNoSystemRoles() throws Exception {
        // given
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoNonDoctor("Läkare");

        // then
        userDetailsService.buildUserPrincipal(PERSONAL_HSAID, "fake");
    }

    @Test(expected = HsaServiceException.class)
    public void testHsaServiceExceptionIsThrownWhenHsaGetPersonThrowsUncheckedException() throws Exception {
        // given
        when(hsaPersonService.getHsaPersonInfo(anyString())).thenThrow(new RuntimeException("some-exception"));
        setupCallToAuthorizedEnheterForHosPerson();

        // then
        userDetailsService.buildUserPrincipal(PERSONAL_HSAID, "fake");
    }

    @Test(expected = HsaServiceException.class)
    public void testHsaServiceExceptionIsThrownWhenHsaThrowsException() throws Exception {
        // given
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenThrow(new RuntimeException("some-hsa-exception"));

        // then
        userDetailsService.buildUserPrincipal(PERSONAL_HSAID, "fake");

    }

    @Test(expected = MissingMedarbetaruppdragException.class)
    public void testMissingMedarbetaruppdragExceptionIsThrownWhenEmployeeHasNoVardgivare() throws Exception {
        // given
        setupCallToGetHsaPersonInfo();
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(new UserCredentials(), new ArrayList<>(), new HashMap<>()));

        // then
        userDetailsService.buildUserPrincipal(PERSONAL_HSAID, "fake");
    }

    @Test(expected = MissingMedarbetaruppdragException.class)
    public void testMissingMedarbetaruppdragExceptionIsThrownWhenEmployeeHasNoMIU() throws Exception {
        // given
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(new UserCredentials(), new ArrayList<>(), new HashMap<>()));
        setupCallToGetHsaPersonInfo();

        // then
        userDetailsService.buildUserPrincipal(PERSONAL_HSAID, "fake");
    }

    // INTYG-2629
    @Test
    public void testUserWithTitleLakareBecomesVardadmin() throws Exception {
        UserCredentials userCredz = new UserCredentials();
        userCredz.getHsaSystemRole().addAll(buildSystemRoles());
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(userCredz, buildVardgivareList(), buildMiuPerCareUnitMap()));
        setupCallToGetHsaPersonInfoNonDoctor("Läkare");

        IntygUser rehabstodUser = userDetailsService.buildUserPrincipal(PERSONAL_HSAID, "fake");

        AUTHORITIES_VALIDATOR.given(rehabstodUser).roles(AuthoritiesConstants.ROLE_KOORDINATOR).orThrow();

    }

    @Test
    public void testLakareWithNoSystemRolesKeepsAllUnits() throws Exception {
        UserCredentials userCredz = new UserCredentials();
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(userCredz, buildVardgivareList(), buildMiuPerCareUnitMap()));
        setupCallToGetHsaPersonInfo("Läkare");

        IntygUser rehabstodUser = userDetailsService.buildUserPrincipal(PERSONAL_HSAID, "fake");

        AUTHORITIES_VALIDATOR.given(rehabstodUser).roles(AuthoritiesConstants.ROLE_LAKARE).orThrow();
        assertEquals(4, rehabstodUser.getTotaltAntalVardenheter());
        assertNull("No default vardenenhet should have been selected since user have more than 1 available unit",
            rehabstodUser.getValdVardenhet());
    }

    @Test
    public void testRemoveEnheterMissingRehabKoordinatorRole() {
        // Arrange
        Vardgivare vardgivare1 = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        Vardenhet enhet1 = new Vardenhet(ENHET_HSAID_1, "Skall bort");
        vardgivare1.getVardenheter().add(enhet1);
        Vardenhet enhet2 = new Vardenhet(ENHET_HSAID_2, "Skall vara kvar");
        vardgivare1.getVardenheter().add(enhet2);

        Vardgivare vardgivare2 = new Vardgivare(VARDGIVARE_HSAID2, "IFV Annat");
        Vardenhet enhet21 = new Vardenhet(ENHET_HSAID_21, "VårdEnhet22A");
        Vardenhet enhet22 = new Vardenhet(ENHET_HSAID_22, "Skall bort med");
        Vardenhet enhet23 = new Vardenhet(ENHET_HSAID_23, "Vårdenhet 23");
        vardgivare2.getVardenheter().addAll(Arrays.asList(enhet21, enhet22, enhet23));

        List<HsaSystemRole> systemRoles = buildSystemRoles();

        List<Vardgivare> original = Arrays.asList(vardgivare1, vardgivare2);

        // Test
        userDetailsService.removeEnheterMissingRehabKoordinatorRole(original,
            systemRoles.stream().map(HsaSystemRole::getRole).collect(Collectors.toList()),
            "userHsaId");

        // Verify
        assertTrue(original.contains(vardgivare1));
        assertFalse(vardgivare1.getVardenheter().contains(enhet1));
        assertTrue(vardgivare1.getVardenheter().contains(enhet2));

        assertTrue(original.contains(vardgivare2));
        assertTrue(vardgivare2.getVardenheter().contains(enhet21));
        assertFalse(vardgivare2.getVardenheter().contains(enhet22));
        assertTrue(vardgivare2.getVardenheter().contains(enhet23));

    }

    private List<HsaSystemRole> buildSystemRoles() {
        return Stream.of(SystemRolesParser.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_2,
                SystemRolesParser.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_21,
                SystemRolesParser.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_23)
            .map(s -> {
                HsaSystemRole hsaSystemRole = new HsaSystemRole();
                hsaSystemRole.setRole(s);
                return hsaSystemRole;
            }).collect(Collectors.toList());
    }

    @Test
    public void testRemoveEnheterMissingRehabKoordinatorRoleRemovedEmptyVardgivare() {
        // Arrange
        Vardgivare vardgivare1 = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting - skall bort");
        Vardenhet enhet1 = new Vardenhet(ENHET_HSAID_1, "Skall bort");
        vardgivare1.getVardenheter().add(enhet1);
        Vardenhet enhet2 = new Vardenhet(ENHET_HSAID_2, "Skall bort den med");
        vardgivare1.getVardenheter().add(enhet2);

        Vardgivare vardgivare2 = new Vardgivare(VARDGIVARE_HSAID2, "IFV Annat");
        Vardenhet enhet21 = new Vardenhet(ENHET_HSAID_21, "VårdEnhet22A");
        Vardenhet enhet22 = new Vardenhet(ENHET_HSAID_22, "Skall bort med");

        vardgivare2.getVardenheter().addAll(Arrays.asList(enhet21, enhet22));

        List<String> systemRoles = Arrays.asList(
            SystemRolesParser.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_21,
            SystemRolesParser.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_23);

        List<Vardgivare> original = new ArrayList<>(Arrays.asList(vardgivare1, vardgivare2));

        // Test
        userDetailsService.removeEnheterMissingRehabKoordinatorRole(original, systemRoles, "userHsaId");

        // Verify
        assertFalse(original.contains(vardgivare1));
        assertTrue(original.contains(vardgivare2));
        assertTrue(vardgivare2.getVardenheter().contains(enhet21));
        assertFalse(vardgivare2.getVardenheter().contains(enhet22));

    }

    @Test(expected = MissingUnitWithRehabSystemRoleException.class)
    public void testRemoveEnheterMissingRehabKoordinatorRoleRemoveAllThrowsException() {
        // Arrange
        Vardgivare vardgivare1 = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        Vardenhet enhet1 = new Vardenhet(ENHET_HSAID_1, "Skall bort");
        vardgivare1.getVardenheter().add(enhet1);

        List<String> systemRoles = List.of(SystemRolesParser.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_2);

        List<Vardgivare> original = new ArrayList<>(List.of(vardgivare1));

        // Act
        userDetailsService.removeEnheterMissingRehabKoordinatorRole(original, systemRoles, "userHsaId");

    }

    private void setupCallToAuthorizedEnheterForHosPerson() {
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(new UserCredentials(), buildVardgivareList(), buildMiuPerCareUnitMap()));
    }

    private List<Vardgivare> buildVardgivareList() {
        Vardgivare vardgivare = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        vardgivare.getVardenheter().add(new Vardenhet(ENHET_HSAID_1, "VårdEnhet2A"));

        final Vardenhet enhet2 = new Vardenhet(ENHET_HSAID_2, "Vårdcentralen");
        enhet2.setMottagningar(Arrays.asList(
            new Mottagning(MOTTAGNING_HSAID_1, "onkologi-mottagningen"),
            new Mottagning(MOTTAGNING_HSAID_2, "protes-mottagningen")));

        vardgivare.getVardenheter().add(enhet2);

        return new ArrayList<>(List.of(vardgivare));
    }

    private Map<String, String> buildMiuPerCareUnitMap() {
        Map<String, String> mius = new HashMap<>();
        mius.put(ENHET_HSAID_1, "Läkare på VårdEnhet2A");
        mius.put(ENHET_HSAID_2, "Stafettläkare på Vårdcentralen");
        return mius;
    }

    private void setupCallToGetHsaPersonInfo() {
        setupCallToGetHsaPersonInfo(TITLE_HEAD_DOCTOR);
    }

    private void setupCallToGetHsaPersonInfo(String title) {
        List<String> specs = Arrays.asList("Kirurgi", "Öron-, näs- och halssjukdomar", "Reumatologi");
        List<String> legitimeradeYrkesgrupper = Arrays.asList("Läkare", "Psykoterapeut");
        List<String> befattningar = Collections.emptyList();

        List<PersonInformation> userTypes = Collections
            .singletonList(buildPersonInformationType(PERSONAL_HSAID, title, specs, legitimeradeYrkesgrupper, befattningar));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private void setupCallToGetHsaPersonInfoNonDoctor() {
        setupCallToGetHsaPersonInfoNonDoctor("");
    }

    private void setupCallToGetHsaPersonInfoNonDoctor(String title) {
        List<String> specs = new ArrayList<>();
        List<String> legitimeradeYrkesgrupper = List.of("Vårdadministratör");
        List<String> befattningar = Collections.emptyList();

        List<PersonInformation> userTypes = Collections
            .singletonList(buildPersonInformationType(PERSONAL_HSAID, title, specs, legitimeradeYrkesgrupper, befattningar));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private PersonInformation buildPersonInformationType(String hsaId, String title, List<String> specialities,
        List<String> legitimeradeYrkesgrupper,
        List<String> befattningar) {

        PersonInformation type = new PersonInformation();
        type.setPersonHsaId(hsaId);
        type.setGivenName("Danne");
        type.setMiddleAndSurName("Doktorsson");

        if (title != null) {
            type.setTitle(title);
        }

        if (legitimeradeYrkesgrupper != null && legitimeradeYrkesgrupper.size() > 0) {
            for (String t : legitimeradeYrkesgrupper) {
                type.getHealthCareProfessionalLicence().add(t);
            }
        }

        if (befattningar != null) {
            for (String befattningsKod : befattningar) {
                PaTitle paTitleType = new PaTitle();
                paTitleType.setPaTitleCode(befattningsKod);
                type.getPaTitle().add(paTitleType);
            }
        }

        if ((specialities != null) && (specialities.size() > 0)) {
            type.getSpecialityName().addAll(specialities);
        }
        return type;
    }
}
