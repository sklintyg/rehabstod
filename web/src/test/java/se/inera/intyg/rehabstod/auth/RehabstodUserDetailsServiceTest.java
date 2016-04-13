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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;

import se.inera.intyg.common.integration.hsa.model.Mottagning;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.common.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.common.integration.hsa.services.HsaPersonService;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesResolver;
import se.inera.intyg.rehabstod.auth.authorities.bootstrap.AuthoritiesConfigurationLoader;
import se.inera.intyg.rehabstod.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.rehabstod.auth.exceptions.GenericAuthenticationException;
import se.inera.intyg.rehabstod.auth.exceptions.HsaServiceException;
import se.inera.intyg.rehabstod.auth.exceptions.MissingMedarbetaruppdragException;
import se.riv.infrastructure.directory.v1.PaTitleType;
import se.riv.infrastructure.directory.v1.PersonInformationType;

/**
 * Created by marced on 29/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RehabstodUserDetailsServiceTest {

    protected static final String CONFIGURATION_LOCATION = "AuthoritiesConfigurationLoaderTest/authorities-test.yaml";

    protected static final AuthoritiesConfigurationLoader CONFIGURATION_LOADER = new AuthoritiesConfigurationLoader(CONFIGURATION_LOCATION);
    protected static final AuthoritiesResolver AUTHORITIES_RESOLVER = new AuthoritiesResolver();
    protected static final AuthoritiesValidator AUTHORITIES_VALIDATOR = new AuthoritiesValidator();
    private static final String PERSONAL_HSAID = "TST5565594230-106J";

    private static final String VARDGIVARE_HSAID = "IFV1239877878-0001";
    private static final String ENHET_HSAID_1 = "IFV1239877878-103H";
    private static final String ENHET_HSAID_2 = "IFV1239877878-103P";
    private static final String MOTTAGNING_HSAID_1 = "IFV1239877878-103M";
    private static final String MOTTAGNING_HSAID_2 = "IFV1239877878-103N";

    @InjectMocks
    private RehabstodUserDetailsService userDetailsService = new RehabstodUserDetailsService();

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @Mock
    private HsaPersonService hsaPersonService;

    private Vardgivare vardgivare;

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

        when(hsaPersonService.getHsaPersonInfo(anyString())).thenReturn(Collections.emptyList());
        AUTHORITIES_RESOLVER.setHsaPersonService(hsaPersonService);
        userDetailsService.setAuthoritiesResolver(AUTHORITIES_RESOLVER);
    }

    @Test
    public void assertRoleAndWhenUserHasTitleLakare() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-with-title-lakare.xml");
        setupCallToAuthorizedEnheterForHosPerson();

        // then
        RehabstodUser rehabstodUser = (RehabstodUser) userDetailsService.loadUserBySAML(samlCredential);

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
        userDetailsService.getAssertion((Assertion) null);
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
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID)).thenReturn(new ArrayList<>());
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-with-title-lakare.xml");

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = MissingMedarbetaruppdragException.class)
    public void testMissingMedarbetaruppdragExceptionIsThrownWhenEmployeeHasNoMIUOnSAMLTicket() throws Exception {
        // given
        setupCallToAuthorizedEnheterForHosPerson();
        SAMLCredential samlCredential = createSamlCredential("saml-assertion-with-other-mui.xml");

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test
    public void testExtractBefattningar() throws Exception {

        // Arrange
        PersonInformationType pt = new PersonInformationType();
        pt.setTitle("title1");
        final PaTitleType paTitleType1 = new PaTitleType();
        paTitleType1.setPaTitleName("paTitle1");
        pt.getPaTitle().add(paTitleType1);

        final PaTitleType paTitleType2 = new PaTitleType();
        paTitleType2.setPaTitleName("paTitle2");
        pt.getPaTitle().add(paTitleType2);

        List<PersonInformationType> hsaPersonInfo = new ArrayList<>();
        hsaPersonInfo.add(pt);

        // Test
        final List<String> befattningar = userDetailsService.extractBefattningar(hsaPersonInfo);

        // Verify
        assertEquals(2, befattningar.size());
        assertTrue(befattningar.contains("paTitle1"));
        assertTrue(befattningar.contains("paTitle2"));

    }

    @Test
    public void testExtractTitel() throws Exception {

        // Arrange
        PersonInformationType pt = new PersonInformationType();
        pt.setTitle("xpitTitle");

        PersonInformationType pt2 = new PersonInformationType();
        pt2.getHealthCareProfessionalLicence().add("hcpl1");
        pt2.getHealthCareProfessionalLicence().add("hcpl2");

        List<PersonInformationType> hsaPersonInfo = new ArrayList<>();
        hsaPersonInfo.add(pt);
        hsaPersonInfo.add(pt2);

        // Test
        final String titleString = userDetailsService.extractTitel(hsaPersonInfo);

        // Verify
        assertEquals("hcpl1, hcpl2, xpitTitle", titleString);

    }

    @Test
    public void testExtractLegitimeradeYrkesgrupper() throws Exception {

        // Arrange
        PersonInformationType pt = new PersonInformationType();
        pt.setTitle("title1");
        final PaTitleType paTitleType1 = new PaTitleType();
        paTitleType1.setPaTitleName("paTitle1");
        pt.getPaTitle().add(paTitleType1);

        final PaTitleType paTitleType2 = new PaTitleType();
        paTitleType2.setPaTitleName("paTitle2");
        pt.getPaTitle().add(paTitleType2);

        List<PersonInformationType> hsaPersonInfo = new ArrayList<>();
        hsaPersonInfo.add(pt);

        // Test
        final List<String> legitimeradeYrkesgrupper = userDetailsService.extractLegitimeradeYrkesgrupper(hsaPersonInfo);

        // Verify
        // Verify
        assertEquals(2, legitimeradeYrkesgrupper.size());
        assertTrue(legitimeradeYrkesgrupper.contains("paTitle1"));
        assertTrue(legitimeradeYrkesgrupper.contains("paTitle2"));

    }

    @Test
    public void testSetFirstVardenhetOnFirstVardgivareAsDefault() throws Exception {

        // Arrange
        Vardgivare vardgivare = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        Vardenhet enhet1 = new Vardenhet(ENHET_HSAID_1, "VårdEnhet2A");
        vardgivare.getVardenheter().add(enhet1);
        Vardenhet enhet2 = new Vardenhet(ENHET_HSAID_2, "Vårdcentralen");
        vardgivare.getVardenheter().add(enhet2);

        RehabstodUser user = new RehabstodUser("1", "Dr. Doctor");
        user.setVardgivare(Arrays.asList(vardgivare));

        // Test
        final boolean success = userDetailsService.setFirstVardenhetOnFirstVardgivareAsDefault(user);

        // Verify
        assertTrue(success);
        assertEquals(vardgivare, user.getValdVardgivare());
        assertEquals(enhet1, user.getValdVardenhet());

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
        vardgivare = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        vardgivare.getVardenheter().add(new Vardenhet(ENHET_HSAID_1, "VårdEnhet2A"));

        final Vardenhet enhet2 = new Vardenhet(ENHET_HSAID_2, "Vårdcentralen");
        enhet2.setMottagningar(Arrays.asList(
                new Mottagning(MOTTAGNING_HSAID_1, "onkologi-mottagningen"),
                new Mottagning(MOTTAGNING_HSAID_2, "protes-mottagningen")));

        vardgivare.getVardenheter().add(enhet2);

        List<Vardgivare> vardgivareList = Collections.singletonList(vardgivare);
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID)).thenReturn(vardgivareList);
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
