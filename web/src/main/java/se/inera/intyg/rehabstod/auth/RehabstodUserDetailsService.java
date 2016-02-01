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

import static se.inera.intyg.common.integration.hsa.stub.Medarbetaruppdrag.VARD_OCH_BEHANDLING;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.opensaml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.common.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.common.integration.hsa.services.HsaPersonService;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesResolver;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesResolverUtil;
import se.inera.intyg.rehabstod.auth.authorities.Role;
import se.inera.intyg.rehabstod.auth.exceptions.HsaServiceException;
import se.inera.intyg.rehabstod.auth.exceptions.MissingMedarbetaruppdragException;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author andreaskaltenbach
 */
@Service
public class RehabstodUserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(RehabstodUserDetailsService.class);

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    private HsaPersonService hsaPersonService;

    @Autowired
    private AuthoritiesResolver authoritiesResolver;

    // ~ API
    // =====================================================================================

    @Override
    public Object loadUserBySAML(SAMLCredential credential) {

        if (credential == null) {
            throw new RuntimeException("SAMLCredential has not been set.");
        }

        LOG.info("Start user authentication...");

        if (LOG.isDebugEnabled()) {
            // I dont want to read this object every time.
            String str = ToStringBuilder.reflectionToString(credential);
            LOG.debug("SAML credential is:\n{}", str);
        }

        try {
            // Create the user
            RehabstodUser webCertUser = createUser(credential);

            LOG.info("End user authentication...SUCCESS");
            return webCertUser;

        } catch (Exception e) {
            LOG.error("End user authentication...FAIL");
            if (e instanceof AuthenticationException) {
                throw e;
            }

            LOG.error("Error building user {}, failed with message {}", getAssertion(credential).getHsaId(), e.getMessage());
            throw new RuntimeException(getAssertion(credential).getHsaId(), e);
        }
    }

    // ~ Protected scope
    // =====================================================================================

    protected SakerhetstjanstAssertion getAssertion(SAMLCredential credential) {
        return getAssertion(credential.getAuthenticationAssertion());
    }

    protected List<Vardgivare> getAuthorizedVardgivare(String hsaId) {
        LOG.debug("Retrieving authorized units from HSA...");

        try {
            return hsaOrganizationsService.getAuthorizedEnheterForHosPerson(hsaId);

        } catch (Exception e) {
            LOG.error("Failed retrieving authorized units from HSA for user {}, error message {}", hsaId, e.getMessage());
            throw new HsaServiceException(hsaId, e);
        }
    }

    protected List<PersonInformationType> getPersonInfo(String hsaId) {
        LOG.debug("Retrieving user information from HSA...");

        List<PersonInformationType> hsaPersonInfo;
        try {
            hsaPersonInfo = hsaPersonService.getHsaPersonInfo(hsaId);
            if (hsaPersonInfo == null || hsaPersonInfo.isEmpty()) {
                LOG.info("Call to web service getHsaPersonInfo did not return any info for user '{}'", hsaId);
            }

        } catch (Exception e) {
            LOG.error("Failed retrieving user information from HSA for user {}, error message {}", hsaId, e.getMessage());
            throw new HsaServiceException(hsaId, e);
        }

        return hsaPersonInfo;
    }

    protected String compileName(String fornamn, String mellanOchEfterNamn) {

        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotBlank(fornamn)) {
            sb.append(fornamn);
        }

        if (StringUtils.isNotBlank(mellanOchEfterNamn)) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(mellanOchEfterNamn);
        }

        return sb.toString();
    }

    protected HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    // ~ Package scope
    // =====================================================================================

    RehabstodUser createUser(SAMLCredential credential) {
        LOG.debug("Creating Webcert user object...");

        String hsaId = getAssertion(credential).getHsaId();
        List<PersonInformationType> personInfo = getPersonInfo(hsaId);
        List<Vardgivare> authorizedVardgivare = getAuthorizedVardgivare(hsaId);

        try {
            assertMIU(credential);
            assertAuthorizedVardgivare(hsaId, authorizedVardgivare);

            HttpServletRequest request = getCurrentRequest();
            Role role = authoritiesResolver.resolveRole(credential, request);
            LOG.debug("User role is set to {}", role);

            return createRehabstodUser(role, credential, authorizedVardgivare, personInfo);

        } catch (MissingMedarbetaruppdragException e) {
            // monitoringLogService.logMissingMedarbetarUppdrag(getAssertion(credential).getHsaId());
            throw e;
        }

    }

    SakerhetstjanstAssertion getAssertion(Assertion assertion) {
        if (assertion == null) {
            throw new IllegalArgumentException("Assertion parameter cannot be null");
        }

        return new SakerhetstjanstAssertion(assertion);
    }

    // ~ Private scope
    // =====================================================================================

    private void assertAuthorizedVardgivare(String hsaId, List<Vardgivare> authorizedVardgivare) {
        LOG.debug("Assert user has authorization to one or more 'vårdenheter'");

        // if user does not have access to any vardgivare, we have to reject authentication
        if (authorizedVardgivare == null || authorizedVardgivare.isEmpty()) {
            throw new MissingMedarbetaruppdragException(hsaId);
        }
    }

    private void assertMIU(SAMLCredential credential) {
        LOG.debug("Assert 'medarbetaruppdrag (MIU)'");

        // if user has authenticated with other contract than 'Vård och behandling', we have to reject her
        if (!VARD_OCH_BEHANDLING.equals(getAssertion(credential).getMedarbetaruppdragType())) {
            throw new MissingMedarbetaruppdragException(getAssertion(credential).getHsaId());
        }
    }

    private RehabstodUser createRehabstodUser(Role role, SAMLCredential credential, List<Vardgivare> authorizedVardgivare,
            List<PersonInformationType> personInfo) {
        LOG.debug("Decorate/populate user object with additional information");

        SakerhetstjanstAssertion sa = getAssertion(credential);

        // Create the WebCert user object injection user's privileges
        RehabstodUser user = new RehabstodUser(sa.getHsaId(), sa.getFornamn() + " " + sa.getMellanOchEfternamn());

        user.setHsaId(sa.getHsaId());
        user.setNamn(compileName(sa.getFornamn(), sa.getMellanOchEfternamn()));
        user.setVardgivare(authorizedVardgivare);

        // Set role and privileges
        user.setRoles(AuthoritiesResolverUtil.toMap(role));

        // Förskrivarkod is sensitiv information, not allowed to store real value
        user.setForskrivarkod("0000000");

        // Set user's authentication scheme
        user.setAuthenticationScheme(sa.getAuthenticationScheme());

        decorateRehabstodUserWithAdditionalInfo(user, credential, personInfo);
        clearMottagningarFromUser(user);
        decorateRehabstodUserWithDefaultVardenhet(user, credential);

        return user;
    }

    private void clearMottagningarFromUser(RehabstodUser user) {
        for (Vardgivare vg : user.getVardgivare()) {
            for (Vardenhet ve : vg.getVardenheter()) {
                ve.setMottagningar(null);
            }
        }
    }

    private void decorateRehabstodUserWithAdditionalInfo(RehabstodUser user, SAMLCredential credential, List<PersonInformationType> hsaPersonInfo) {

        List<String> legitimeradeYrkesgrupper = extractLegitimeradeYrkesgrupper(hsaPersonInfo);
        List<String> befattningar = extractBefattningar(hsaPersonInfo);
        String titel = extractTitel(hsaPersonInfo);

        user.setLegitimeradeYrkesgrupper(legitimeradeYrkesgrupper);
        user.setBefattningar(befattningar);
        user.setTitel(titel);
    }

    private void decorateRehabstodUserWithDefaultVardenhet(RehabstodUser user, SAMLCredential credential) {

        // Get HSA id for the selected MIU
        String medarbetaruppdragHsaId = getAssertion(credential).getEnhetHsaId();

        boolean changeSuccess;

        if (StringUtils.isNotBlank(medarbetaruppdragHsaId)) {
            changeSuccess = user.changeValdVardenhet(medarbetaruppdragHsaId);
        } else {
            LOG.error("Assertion did not contain any 'medarbetaruppdrag', defaulting to use one of the Vardenheter present in the user");
            changeSuccess = setFirstVardenhetOnFirstVardgivareAsDefault(user);
        }

        if (!changeSuccess) {
            LOG.error("When logging in user '{}', unit with HSA-id {} could not be found in users MIUs", user.getHsaId(), medarbetaruppdragHsaId);
            throw new MissingMedarbetaruppdragException(user.getHsaId());
        }

        LOG.debug("Setting care unit '{}' as default unit on user '{}'", user.getValdVardenhet().getId(), user.getHsaId());
    }

    private List<String> extractBefattningar(List<PersonInformationType> hsaPersonInfo) {
        Set<String> befattningar = new TreeSet<>();

        for (PersonInformationType userType : hsaPersonInfo) {
            if (userType.getPaTitle() != null) {
                List<String> hsaTitles = userType.getPaTitle().stream().map(paTitle -> paTitle.getPaTitleName()).collect(Collectors.toList());
                befattningar.addAll(hsaTitles);
            }
        }

        return new ArrayList<>(befattningar);
    }

    /**
     * Tries to use title attribute, otherwise resorts to healthcareProfessionalLicenses.
     */
    private String extractTitel(List<PersonInformationType> hsaPersonInfo) {
        Set<String> titleSet = new HashSet<>();
        for (PersonInformationType pit : hsaPersonInfo) {
            if (pit.getTitle() != null && pit.getTitle().trim().length() > 0) {
                titleSet.add(pit.getTitle());
            } else if (pit.getHealthCareProfessionalLicence() != null && pit.getHealthCareProfessionalLicence().size() > 0) {
                titleSet.addAll(pit.getHealthCareProfessionalLicence());
            }
        }
        return titleSet.stream().sorted().collect(Collectors.joining(", "));
    }

    private List<String> extractLegitimeradeYrkesgrupper(List<PersonInformationType> hsaUserTypes) {
        Set<String> lygSet = new TreeSet<>();

        for (PersonInformationType userType : hsaUserTypes) {
            if (userType.getPaTitle() != null) {
                List<String> hsaTitles = userType.getPaTitle().stream().map(paTitle -> paTitle.getPaTitleName()).collect(Collectors.toList());
                lygSet.addAll(hsaTitles);
            }
        }

        return new ArrayList<>(lygSet);
    }

    private boolean setFirstVardenhetOnFirstVardgivareAsDefault(RehabstodUser user) {
        Vardgivare firstVardgivare = user.getVardgivare().get(0);
        user.setValdVardgivare(firstVardgivare);

        Vardenhet firstVardenhet = firstVardgivare.getVardenheter().get(0);
        user.setValdVardenhet(firstVardenhet);

        return true;
    }

    @Autowired
    public void setAuthoritiesResolver(AuthoritiesResolver authoritiesResolver) {
        this.authoritiesResolver = authoritiesResolver;
    }
}
