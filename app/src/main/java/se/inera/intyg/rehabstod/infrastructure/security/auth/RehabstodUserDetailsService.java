/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.infrastructure.security.auth;

import static se.inera.intyg.rehabstod.infrastructure.security.authorities.AuthoritiesResolverUtil.toMap;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import se.inera.intyg.rehabstod.application.util.StringUtil;
import se.inera.intyg.rehabstod.infrastructure.integration.hsatk.model.HsaSystemRole;
import se.inera.intyg.rehabstod.infrastructure.integration.hsatk.model.PersonInformation;
import se.inera.intyg.rehabstod.infrastructure.integration.hsatk.model.PersonInformation.PaTitle;
import se.inera.intyg.rehabstod.infrastructure.integration.hsatk.model.legacy.UserAuthorizationInfo;
import se.inera.intyg.rehabstod.infrastructure.integration.hsatk.model.legacy.UserCredentials;
import se.inera.intyg.rehabstod.infrastructure.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.rehabstod.infrastructure.integration.hsatk.services.legacy.HsaOrganizationsService;
import se.inera.intyg.rehabstod.infrastructure.integration.hsatk.services.legacy.HsaPersonService;
import se.inera.intyg.rehabstod.infrastructure.integration.hsatk.util.HsaAttributeExtractor;
import se.inera.intyg.rehabstod.infrastructure.repository.AnvandarPreferenceRepository;
import se.inera.intyg.rehabstod.infrastructure.repository.model.AnvandarPreference;
import se.inera.intyg.rehabstod.infrastructure.security.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.infrastructure.security.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.infrastructure.security.auth.exceptions.MissingUnitWithRehabSystemRoleException;
import se.inera.intyg.rehabstod.infrastructure.security.auth.util.SystemRolesParser;
import se.inera.intyg.rehabstod.infrastructure.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.rehabstod.infrastructure.security.common.model.AuthenticationMethod;
import se.inera.intyg.rehabstod.infrastructure.security.common.model.IntygUser;
import se.inera.intyg.rehabstod.infrastructure.security.common.model.Privilege;
import se.inera.intyg.rehabstod.infrastructure.security.common.model.UserOrigin;
import se.inera.intyg.rehabstod.infrastructure.security.exception.HsaServiceException;
import se.inera.intyg.rehabstod.infrastructure.security.exception.MissingHsaEmployeeInformation;
import se.inera.intyg.rehabstod.infrastructure.security.exception.MissingMedarbetaruppdragException;
import se.inera.intyg.rehabstod.logging.AuthenticationLogger;

/**
 * @author andreaskaltenbach
 */
@Service
public class RehabstodUserDetailsService {

  private static final Logger LOG = LoggerFactory.getLogger(RehabstodUserDetailsService.class);

  private static final String SPACE = " ";

  public static final String PDL_CONSENT_GIVEN = "user_pdl_consent_given";

  @Autowired
  private AnvandarPreferenceRepository anvandarPreferenceRepository;

  @Autowired
  private RehabstodUnitChangeService rehabstodUnitChangeService;

  @Autowired(required = false)
  private Optional<UserOrigin> userOrigin;

  @Autowired
  private HsaOrganizationsService hsaOrganizationsService;

  @Autowired
  private HsaPersonService hsaPersonService;

  @Autowired
  private AuthenticationLogger monitoringLogService;

  @Autowired
  public void setCommonAuthoritiesResolver(CommonAuthoritiesResolver commonAuthoritiesResolver) {
    this.commonAuthoritiesResolver = commonAuthoritiesResolver;
  }

  private CommonAuthoritiesResolver commonAuthoritiesResolver;

  private final HsaAttributeExtractor hsaAttributeExtractor = new HsaAttributeExtractor();

  // =====================================================================================
  // ~ Public scope
  // =====================================================================================

  public RehabstodUser buildUserPrincipal(String employeeHsaId, String authenticationScheme) {
    return buildUserPrincipal(employeeHsaId, null, authenticationScheme);
  }

  public RehabstodUser buildUserPrincipal(
      String employeeHsaId, String unitId, String authenticationScheme) {
    LOG.debug("Creating user object...");

    final var personInfo = getPersonInfo(employeeHsaId);
    final var userAuthorizationInfo = getAuthorizedVardgivare(employeeHsaId);

    try {
      assertEmployee(employeeHsaId, personInfo);
      assertAuthorizedVardgivare(employeeHsaId, userAuthorizationInfo.getVardgivare());

      final var intygUser =
          createIntygUser(employeeHsaId, authenticationScheme, userAuthorizationInfo, personInfo);
      // Clean out förskrivarkod
      intygUser.setForskrivarkod("0000000");

      RehabstodUser rehabstodUser =
          new RehabstodUser(
              intygUser, isPdlConsentGiven(intygUser.getHsaId()), intygUser.isLakare());

      RehabstodUserPreferences preferences =
          RehabstodUserPreferences.fromBackend(
              anvandarPreferenceRepository.getAnvandarPreference(intygUser.getHsaId()));
      rehabstodUser.setPreferences(preferences);

      final String savedDefaultLoginHsaUnitId =
          rehabstodUser.getPreferences().get(Preference.DEFAULT_LOGIN_HSA_UNIT_ID);
      boolean usedDefaultUnit = false;
      if (!StringUtil.isNullOrEmpty(savedDefaultLoginHsaUnitId)) {
        if (!rehabstodUnitChangeService.changeValdVardenhet(
            savedDefaultLoginHsaUnitId, rehabstodUser)) {
          LOG.info(
              "User "
                  + rehabstodUser.getHsaId()
                  + " had "
                  + savedDefaultLoginHsaUnitId
                  + " as default_login_hsa_unit_id - but failed to change to that unit (no longer access to it?). "
                  + "Deleting the faulty preference now.");
          final AnvandarPreference defaultUnitPref =
              anvandarPreferenceRepository.findByHsaIdAndKey(
                  rehabstodUser.getHsaId(),
                  Preference.DEFAULT_LOGIN_HSA_UNIT_ID.getBackendKeyName());
          anvandarPreferenceRepository.delete(defaultUnitPref);
        } else {
          usedDefaultUnit = true;
          LOG.debug(
              "Setting default_login_hsa_unit_id "
                  + savedDefaultLoginHsaUnitId
                  + " for User "
                  + rehabstodUser.getHsaId());
        }
      }

      if (!StringUtil.isNullOrEmpty(unitId)) {
        rehabstodUnitChangeService.changeValdVardenhet(unitId, rehabstodUser);
      }

      // INTYG-5068: Explicitly changing vardenhet on session creation to possibly apply
      // REHABKOORDINATOR role for this unit in case the user is LAKARE and has systemRole Rehab-
      // for the current unit. Only performed if there is a unit selected.
      if (!usedDefaultUnit && rehabstodUser.getValdVardenhet() != null) {
        rehabstodUnitChangeService.changeValdVardenhet(
            rehabstodUser.getValdVardenhet().getId(), rehabstodUser);
      }

      if (rehabstodUser.getValdVardenhet() != null) {
        rehabstodUser.setFeatures(
            commonAuthoritiesResolver.getFeatures(
                Arrays.asList(
                    rehabstodUser.getValdVardenhet().getId(),
                    rehabstodUser.getValdVardgivare().getId())));
      }

      return rehabstodUser;

    } catch (MissingMedarbetaruppdragException e) {
      monitoringLogService.logMissingMedarbetarUppdrag(employeeHsaId);
      LOG.error("Missing medarbetaruppdrag. This needs to be fixed!!!");
      throw e;
    }
  }

  // =====================================================================================
  // ~ Inlined from BaseUserDetailsService
  // =====================================================================================

  protected UserAuthorizationInfo getAuthorizedVardgivare(String employeeHsaId) {
    LOG.debug("Retrieving authorized units from HSA...");
    try {
      return hsaOrganizationsService.getAuthorizedEnheterForHosPerson(employeeHsaId);
    } catch (Exception e) {
      LOG.error(
          "Failure retrieving authorized units from HSA for user {}, error message {}",
          employeeHsaId,
          e.getMessage());
      throw new HsaServiceException(employeeHsaId, e);
    }
  }

  protected List<PersonInformation> getPersonInfo(String employeeHsaId) {
    LOG.debug("Retrieving user information from HSA...");
    List<PersonInformation> hsaPersonInfo;
    try {
      hsaPersonInfo = hsaPersonService.getHsaPersonInfo(employeeHsaId);
      if (hsaPersonInfo == null || hsaPersonInfo.isEmpty()) {
        LOG.info(
            "Call to web service getHsaPersonInfo did not return any info for user '{}'",
            employeeHsaId);
      }
    } catch (Exception e) {
      LOG.error(
          "Failed retrieving user information from HSA for user {}, error message {}",
          employeeHsaId,
          e.getMessage());
      throw new HsaServiceException(employeeHsaId, e);
    }
    return hsaPersonInfo;
  }

  private IntygUser createIntygUser(
      String employeeHsaId,
      String authenticationScheme,
      UserAuthorizationInfo userAuthorizationInfo,
      List<PersonInformation> personInfo) {
    LOG.debug("Decorate/populate user object with additional information");
    final var intygUser = new IntygUser(employeeHsaId);
    decorateIntygUserWithBasicInfo(
        intygUser, userAuthorizationInfo, personInfo, authenticationScheme);
    decorateIntygUserWithAdditionalInfo(intygUser, personInfo);
    decorateIntygUserWithAuthenticationMethod(intygUser, authenticationScheme);
    decorateIntygUserWithRoleAndAuthorities(
        intygUser, personInfo, userAuthorizationInfo.getUserCredentials());
    decorateIntygUserWithSystemRoles(intygUser, userAuthorizationInfo.getUserCredentials());
    decorateIntygUserWithDefaultVardenhet(intygUser);
    decorateIntygUserWithAvailableFeatures(intygUser);
    return intygUser;
  }

  private void decorateIntygUserWithBasicInfo(
      IntygUser intygUser,
      UserAuthorizationInfo userAuthorizationInfo,
      List<PersonInformation> personInfo,
      String authenticationScheme) {
    intygUser.setFornamn(personInfo.getFirst().getGivenName());
    intygUser.setEfternamn(personInfo.getFirst().getMiddleAndSurName());
    intygUser.setNamn(
        compileName(personInfo.getFirst().getGivenName(), personInfo.get(0).getMiddleAndSurName()));
    intygUser.setVardgivare(userAuthorizationInfo.getVardgivare());
    intygUser.setSekretessMarkerad(
        personInfo.stream()
            .anyMatch(pi -> pi.getProtectedPerson() != null && pi.getProtectedPerson()));
    // Förskrivarkod is sensitive — overwritten after role resolution
    intygUser.setForskrivarkod(
        userAuthorizationInfo.getUserCredentials().getPersonalPrescriptionCode());
    intygUser.setAuthenticationScheme(authenticationScheme);
    userOrigin.ifPresent(
        origin ->
            intygUser.setOrigin(
                commonAuthoritiesResolver
                    .getRequestOrigin(origin.resolveOrigin(getCurrentRequest()))
                    .getName()));
    intygUser.setMiuNamnPerEnhetsId(userAuthorizationInfo.getCommissionNamePerCareUnit());
  }

  private void decorateIntygUserWithAdditionalInfo(
      IntygUser intygUser, List<PersonInformation> hsaPersonInfo) {
    List<String> specialiseringar = hsaAttributeExtractor.extractSpecialiseringar(hsaPersonInfo);
    List<String> legitimeradeYrkesgrupper =
        hsaAttributeExtractor.extractLegitimeradeYrkesgrupper(hsaPersonInfo);
    List<String> befattningar = hsaAttributeExtractor.extractBefattningar(hsaPersonInfo);
    List<PaTitle> befattningskoder = hsaAttributeExtractor.extractBefattningsKoder(hsaPersonInfo);
    String titel = hsaAttributeExtractor.extractTitel(hsaPersonInfo);
    intygUser.setSpecialiseringar(specialiseringar);
    intygUser.setLegitimeradeYrkesgrupper(legitimeradeYrkesgrupper);
    intygUser.setBefattningar(befattningar);
    intygUser.setBefattningsKoder(befattningskoder);
    intygUser.setTitel(titel);
  }

  private void decorateIntygUserWithAuthenticationMethod(
      IntygUser intygUser, String authenticationScheme) {
    if (authenticationScheme.endsWith(":fake")) {
      intygUser.setAuthenticationMethod(AuthenticationMethod.FAKE);
    } else {
      intygUser.setAuthenticationMethod(AuthenticationMethod.SITHS);
    }
  }

  private void decorateIntygUserWithRoleAndAuthorities(
      IntygUser intygUser, List<PersonInformation> personInfo, UserCredentials userCredentials) {
    final var roleResolveResult =
        commonAuthoritiesResolver.resolveRole(
            intygUser, personInfo, getDefaultRole(), userCredentials);
    LOG.debug("User role is set to {}", roleResolveResult.getRole());
    intygUser.setRoles(toMap(roleResolveResult.getRole()));
    intygUser.setRoleTypeName(roleResolveResult.getRoleTypeName());
    intygUser.setAuthorities(
        toMap(roleResolveResult.getRole().getPrivileges(), Privilege::getName));
  }

  private void decorateIntygUserWithSystemRoles(
      IntygUser intygUser, UserCredentials userCredentials) {
    if (userCredentials != null && userCredentials.getHsaSystemRole() != null) {
      intygUser.setSystemRoles(
          userCredentials.getHsaSystemRole().stream()
              .map(RehabstodUserDetailsService::hsaSystemRoleAsString)
              .collect(Collectors.toList()));
    }
    // ROLE_KOORDINATOR must have a matching systemrole for each unit, or else it's removed
    if (intygUser.getRoles().containsKey(AuthoritiesConstants.ROLE_KOORDINATOR)) {
      removeEnheterMissingRehabKoordinatorRole(
          intygUser.getVardgivare(), intygUser.getSystemRoles(), intygUser.getHsaId());
    }
  }

  private void decorateIntygUserWithDefaultVardenhet(IntygUser intygUser) {
    // Only set a default enhet if there is only one (mottagningar doesnt count).
    // If no default vardenhet can be determined - let it be null and force user to select one.
    if (getTotaltAntalVardenheterExcludingMottagningar(intygUser) == 1) {
      setFirstVardenhetOnFirstVardgivareAsDefault(intygUser);
      LOG.debug(
          "Setting care unit '{}' as default unit on user '{}'",
          intygUser.getValdVardenhet().getId(),
          intygUser.getHsaId());
    }
  }

  public void decorateIntygUserWithAvailableFeatures(IntygUser intygUser) {
    List<String> hsaIds = new ArrayList<>();
    if (intygUser.getValdVardenhet() != null) {
      hsaIds.add(intygUser.getValdVardenhet().getId());
    }
    if (intygUser.getValdVardgivare() != null) {
      hsaIds.add(intygUser.getValdVardgivare().getId());
    }
    intygUser.setFeatures(commonAuthoritiesResolver.getFeatures(hsaIds));
  }

  private String compileName(String fornamn, String mellanOchEfterNamn) {
    StringBuilder sb = new StringBuilder();
    if (fornamn != null && !fornamn.isEmpty()) {
      sb.append(fornamn);
    }
    if (mellanOchEfterNamn != null && !mellanOchEfterNamn.isEmpty()) {
      if (!sb.isEmpty()) {
        sb.append(SPACE);
      }
      sb.append(mellanOchEfterNamn);
    }
    return sb.toString();
  }

  private String getDefaultRole() {
    return AuthoritiesConstants.ROLE_KOORDINATOR;
  }

  private HttpServletRequest getCurrentRequest() {
    return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
        .getRequest();
  }

  // =====================================================================================
  // ~ Private helpers
  // =====================================================================================

  private boolean isPdlConsentGiven(String hsaId) {
    AnvandarPreference pdlConsentGiven =
        anvandarPreferenceRepository.findByHsaIdAndKey(hsaId, PDL_CONSENT_GIVEN);
    return pdlConsentGiven != null && Boolean.valueOf(pdlConsentGiven.getValue());
  }

  private int getTotaltAntalVardenheterExcludingMottagningar(IntygUser intygUser) {
    return (int)
        intygUser.getVardgivare().stream().flatMap(vg -> vg.getVardenheter().stream()).count();
  }

  void removeEnheterMissingRehabKoordinatorRole(
      List<Vardgivare> authorizedVardgivare, List<String> systemRoles, String hsaId) {
    long unitsBefore =
        authorizedVardgivare.stream().mapToInt(vg -> vg.getVardenheter().size()).sum();

    List<String> rehabAuthorizedEnhetIds =
        SystemRolesParser.parseEnhetsIdsFromSystemRoles(systemRoles);

    authorizedVardgivare.forEach(
        vg -> vg.getVardenheter().removeIf(ve -> !rehabAuthorizedEnhetIds.contains(ve.getId())));

    authorizedVardgivare.removeIf(vg -> vg.getVardenheter().size() < 1);

    long unitsAfter =
        authorizedVardgivare.stream().mapToInt(vg -> vg.getVardenheter().size()).sum();

    LOG.debug(
        "removeEnheterMissingRehabKoordinatorRole rehabauthorized units are: ["
            + String.join(",", rehabAuthorizedEnhetIds)
            + "]. User units before filtering: "
            + unitsBefore
            + ", after: "
            + unitsAfter);

    if (unitsAfter < 1) {
      throw new MissingUnitWithRehabSystemRoleException(hsaId);
    }
  }

  private boolean setFirstVardenhetOnFirstVardgivareAsDefault(IntygUser intygUser) {
    for (Vardgivare vg : intygUser.getVardgivare()) {
      if (!vg.getVardenheter().isEmpty()) {
        intygUser.setValdVardgivare(vg);
        intygUser.setValdVardenhet(vg.getVardenheter().get(0));
        return true;
      }
    }
    return false;
  }

  private static String hsaSystemRoleAsString(HsaSystemRole systemRole) {
    if (systemRole.getSystemId() == null || systemRole.getSystemId().trim().isEmpty()) {
      return systemRole.getRole();
    } else {
      return systemRole.getSystemId() + ";" + systemRole.getRole();
    }
  }

  protected void assertAuthorizedVardgivare(
      String employeeHsaId, List<Vardgivare> authorizedVardgivare) {
    LOG.debug("Assert user has authorization to one or more 'vårdenheter'");
    if (authorizedVardgivare == null || authorizedVardgivare.isEmpty()) {
      throw new MissingMedarbetaruppdragException(employeeHsaId);
    }
  }

  private void assertEmployee(String employeeHsaId, List<PersonInformation> personInfo) {
    if (personInfo == null || personInfo.isEmpty()) {
      LOG.error(
          "Cannot authorize user with employeeHsaId '{}', no records found for Employee in HoSP.",
          employeeHsaId);
      throw new MissingHsaEmployeeInformation(employeeHsaId);
    }
  }
}