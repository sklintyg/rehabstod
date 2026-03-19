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
package se.inera.intyg.rehabstod.integration.intygproxyservice.services.organization;

import jakarta.xml.ws.WebServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.hsatk.model.legacy.UserAuthorizationInfo;
import se.inera.intyg.rehabstod.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.rehabstod.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.rehabstod.integration.hsatk.services.legacy.HsaOrganizationsService;
import se.inera.intyg.rehabstod.integration.intygproxyservice.dto.authorization.GetCredentialInformationRequestDTO;
import se.inera.intyg.rehabstod.integration.intygproxyservice.dto.organization.GetUnitRequestDTO;
import se.inera.intyg.rehabstod.integration.intygproxyservice.services.authorization.GetCredentialInformationForPersonService;

@Slf4j
@Service
@RequiredArgsConstructor
public class HsaLegacyIntegrationOrganizationService implements HsaOrganizationsService {

  private final GetUnitService getUnitService;
  private final GetCredentialInformationForPersonService getCredentialInformationForPersonService;
  private final GetUserAuthorizationInfoService getUserAuthorizationInfoService;
  private final GetCareUnitService getCareUnitService;

  @Override
  public UserAuthorizationInfo getAuthorizedEnheterForHosPerson(String hosPersonHsaId) {
    final var credentialInformation =
        getCredentialInformationForPersonService.get(
            GetCredentialInformationRequestDTO.builder().personHsaId(hosPersonHsaId).build());

    return getUserAuthorizationInfoService.get(credentialInformation);
  }

  @Override
  public Vardenhet getVardenhet(String vardenhetHsaId) {
    return getCareUnitService.get(vardenhetHsaId);
  }

  @Override
  public Vardgivare getVardgivareInfo(String vardgivareHsaId) {
    final var unit = getUnitService.get(GetUnitRequestDTO.builder().hsaId(vardgivareHsaId).build());

    if (unit == null) {
      throw new WebServiceException("Could not get unit for unitHsaId " + vardgivareHsaId);
    }

    return new Vardgivare(unit.getUnitHsaId(), unit.getUnitName());
  }
}