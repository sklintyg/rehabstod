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

import org.apache.cxf.staxutils.StaxUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.NameID;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import se.inera.intyg.common.integration.hsa.model.Mottagning;
import se.inera.intyg.common.integration.hsa.model.UserAuthorizationInfo;
import se.inera.intyg.common.integration.hsa.model.UserCredentials;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.common.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.common.integration.hsa.services.HsaPersonService;
import se.inera.intyg.common.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.common.security.authorities.bootstrap.AuthoritiesConfigurationLoader;
import se.inera.intyg.common.security.common.exception.GenericAuthenticationException;
import se.inera.intyg.common.security.common.model.IntygUser;
import se.inera.intyg.common.security.common.model.UserOrigin;
import se.inera.intyg.common.security.common.model.UserOriginType;
import se.inera.intyg.common.security.common.service.AuthenticationLogger;
import se.inera.intyg.common.security.common.service.CommonFeatureService;
import se.inera.intyg.common.security.exception.HsaServiceException;
import se.inera.intyg.common.security.exception.MissingMedarbetaruppdragException;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.rehabstod.auth.exceptions.MissingUnitWithRehabSystemRoleException;
import se.riv.infrastructure.directory.v1.HsaSystemRoleType;
import se.riv.infrastructure.directory.v1.PaTitleType;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import javax.xml.transform.stream.StreamSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by marced on 29/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RehabstodUserDetailsServiceTest {

    protected static final String CONFIGURATION_LOCATION = "AuthoritiesConfigurationLoaderTest/authorities-test.yaml";

    protected static final AuthoritiesConfigurationLoader CONFIGURATION_LOADER = new AuthoritiesConfigurationLoader(CONFIGURATION_LOCATION);
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
    private CommonFeatureService commonFeatureService;

    @Mock
    private AuthenticationLogger monitoringLogService;

    @BeforeClass
    public static void setupAuthoritiesConfiguration() throws Exception {

        DefaultBootstrap.bootstrap();

        // Load configuration
        CONFIGURATION_LOADER.afterPropertiesSet();

        // Setup resolver class
        AUTHORITIES_RESOLVER.setConfigurationLoader(CONFIGURATION_LOADER);
    }

    @Before
    public void setup() {
        // Setup a servlet request
        MockHttpServletRequest request = mockHttpServletRequest("/any/path");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ReflectionTestUtils.setField(userDetailsService, "commonFeatureService", Optional.of(commonFeatureService));
        ReflectionTestUtils.setField(userDetailsService, "userOrigin", Optional.of(userOrigin));

        when(hsaPersonService.getHsaPersonInfo(anyString())).thenReturn(Collections.emptyList());
        when(userOrigin.resolveOrigin(request)).thenReturn(UserOriginType.NORMAL.name());
        when(commonFeatureService.getActiveFeatures()).thenReturn(new HashSet<>());
        userDetailsService.setCommonAuthoritiesResolver(AUTHORITIES_RESOLVER);
    }

    @Test
    public void assertLoadsOkWhenHasMatchingSystemRole() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-with-matching-rehab-systemrole.xml");
        UserCredentials userCredz = new UserCredentials();
        userCredz.getHsaSystemRole().addAll(buildSystemRoles());
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID)).thenReturn(new UserAuthorizationInfo(userCredz, buildVardgivareList()));

        setupCallToGetHsaPersonInfoNonDoctor();

        // then
        IntygUser rehabstodUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        AUTHORITIES_VALIDATOR.given(rehabstodUser).roles(AuthoritiesConstants.ROLE_KOORDINATOR).orThrow();
        assertEquals("The hsaId defined in credentials should have been selected as default vardenhet", ENHET_HSAID_1, rehabstodUser
                .getValdVardenhet().getId());
    }

    @Test(expected = MissingUnitWithRehabSystemRoleException.class)
    public void assertThrowsExceptionWhenNoMatchingSystemRole() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-without-matching-rehab-systemrole.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoNonDoctor();

        // then
        userDetailsService.loadUserBySAML(samlCredential);

    }

    @Test
    public void assertRoleAndWhenUserHasTitleLakare() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-with-title-lakare.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfo();

        // then
        IntygUser rehabstodUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        AUTHORITIES_VALIDATOR.given(rehabstodUser).roles(AuthoritiesConstants.ROLE_LAKARE).orThrow();
        assertEquals("The hsaId defined in credentials should have been selected as default vardenhet", ENHET_HSAID_1, rehabstodUser
                .getValdVardenhet().getId());
    }

    @Test(expected = GenericAuthenticationException.class)
    public void testGenericAuthenticationExceptionIsThrownWhenNoSamlCredentialsGiven() throws Exception {
        userDetailsService.loadUserBySAML(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAssertionWithNullThrowsIllegalArgumentException() throws Exception {
        userDetailsService.getAssertion(null);
    }

    @Test(expected = HsaServiceException.class)
    public void testHsaServiceExceptionIsThrownWhenHsaGetPersonThrowsUncheckedException() throws Exception {
        // given
        when(hsaPersonService.getHsaPersonInfo(anyString())).thenThrow(new RuntimeException("some-exception"));
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-with-title-lakare.xml");
        setupCallToAuthorizedEnheterForHosPerson();

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = HsaServiceException.class)
    public void testHsaServiceExceptionIsThrownWhenHsaThrowsException() throws Exception {
        // given
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID)).thenThrow(new RuntimeException("some-hsa-exception"));
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-with-title-lakare.xml");

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = MissingMedarbetaruppdragException.class)
    public void testMissingMedarbetaruppdragExceptionIsThrownWhenEmployeeHasNoVardgivare() throws Exception {
        // given
        setupCallToGetHsaPersonInfo();
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID)).thenReturn(new UserAuthorizationInfo(new UserCredentials(), new ArrayList<>()));
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-with-title-lakare.xml");

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = MissingMedarbetaruppdragException.class)
    public void testMissingMedarbetaruppdragExceptionIsThrownWhenEmployeeHasNoMIU() throws Exception {
        // given
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID)).thenReturn(new UserAuthorizationInfo(new UserCredentials(), new ArrayList<>()));
        setupCallToGetHsaPersonInfo();
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-with-other-mui.xml");

        // then
        userDetailsService.loadUserBySAML(samlCredential);
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

        List<HsaSystemRoleType> systemRoles = buildSystemRoles();

        List<Vardgivare> original = Arrays.asList(vardgivare1, vardgivare2);

        // Test
        userDetailsService.removeEnheterMissingRehabKoordinatorRole(original, systemRoles.stream().map(rt -> rt.getRole()).collect(Collectors.toList()), "userHsaId");

        // Verify
        assertTrue(original.contains(vardgivare1));
        assertFalse(vardgivare1.getVardenheter().contains(enhet1));
        assertTrue(vardgivare1.getVardenheter().contains(enhet2));

        assertTrue(original.contains(vardgivare2));
        assertTrue(vardgivare2.getVardenheter().contains(enhet21));
        assertFalse(vardgivare2.getVardenheter().contains(enhet22));
        assertTrue(vardgivare2.getVardenheter().contains(enhet23));

    }

    private List<HsaSystemRoleType> buildSystemRoles() {
        return Arrays.asList(RehabstodUserDetailsService.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_2,
                RehabstodUserDetailsService.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_21,
                RehabstodUserDetailsService.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_23)
                .stream()
                .map(s -> {
                    HsaSystemRoleType hsaSystemRole = new HsaSystemRoleType();
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
                RehabstodUserDetailsService.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_21,
                RehabstodUserDetailsService.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_23);

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

        List<String> systemRoles = Arrays.asList(RehabstodUserDetailsService.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + ENHET_HSAID_2);

        List<Vardgivare> original = new ArrayList<>(Arrays.asList(vardgivare1));

        // Act
        userDetailsService.removeEnheterMissingRehabKoordinatorRole(original, systemRoles, "userHsaId");

    }

    private SAMLCredential createSamlCredential(String filename) throws Exception {
        Document doc = StaxUtils.read(new StreamSource(new ClassPathResource(
                "RehabstodUserDetailsServiceTest/" + filename).getInputStream()));
        UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(Assertion.DEFAULT_ELEMENT_NAME);

        Assertion assertion = (Assertion) unmarshaller.unmarshall(doc.getDocumentElement());
        NameID nameId = assertion.getSubject().getNameID();
        return new SAMLCredential(nameId, assertion, "remoteId", "localId");
    }

    private void setupCallToAuthorizedEnheterForHosPerson() {
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID)).thenReturn(new UserAuthorizationInfo(new UserCredentials(), buildVardgivareList()));
    }

    private List<Vardgivare> buildVardgivareList() {
        Vardgivare vardgivare = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        vardgivare.getVardenheter().add(new Vardenhet(ENHET_HSAID_1, "VårdEnhet2A"));

        final Vardenhet enhet2 = new Vardenhet(ENHET_HSAID_2, "Vårdcentralen");
        enhet2.setMottagningar(Arrays.asList(
                new Mottagning(MOTTAGNING_HSAID_1, "onkologi-mottagningen"),
                new Mottagning(MOTTAGNING_HSAID_2, "protes-mottagningen")));

        vardgivare.getVardenheter().add(enhet2);

        return new ArrayList<>(Arrays.asList(vardgivare));
    }

    private void setupCallToGetHsaPersonInfo() {
        List<String> specs = Arrays.asList("Kirurgi", "Öron-, näs- och halssjukdomar", "Reumatologi");
        List<String> titles = Arrays.asList("Läkare", "Psykoterapeut");

        List<PersonInformationType> userTypes = Collections.singletonList(buildPersonInformationType(PERSONAL_HSAID, TITLE_HEAD_DOCTOR, specs, titles));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private void setupCallToGetHsaPersonInfoNonDoctor() {
        List<String> specs = new ArrayList<>();
        List<String> titles = Arrays.asList("Vårdadministratör");

        List<PersonInformationType> userTypes = Collections.singletonList(buildPersonInformationType(PERSONAL_HSAID, "", specs, titles));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private PersonInformationType buildPersonInformationType(String hsaId, String title, List<String> specialities, List<String> titles) {

        PersonInformationType type = new PersonInformationType();
        type.setPersonHsaId(hsaId);
        type.setGivenName("Danne");
        type.setMiddleAndSurName("Doktorsson");

        if (title != null) {
            type.setTitle(title);
        }

        if ((titles != null) && (titles.size() > 0)) {
            for (String t : titles) {
                PaTitleType paTitle = new PaTitleType();
                paTitle.setPaTitleName(t);
                type.getPaTitle().add(paTitle);
            }
        }

        if ((specialities != null) && (specialities.size() > 0)) {
            type.getSpecialityName().addAll(specialities);
        }
        return type;
    }

    private MockHttpServletRequest mockHttpServletRequest(String requestURI) {
        MockHttpServletRequest request = new MockHttpServletRequest();

        if ((requestURI != null) && (requestURI.length() > 0)) {
            request.setRequestURI(requestURI);
        }

        // SavedRequest savedRequest = new DefaultSavedRequest(request, new PortResolverImpl());
        // request.getSession().setAttribute(SPRING_SECURITY_SAVED_REQUEST_KEY, savedRequest);

        return request;
    }
}
