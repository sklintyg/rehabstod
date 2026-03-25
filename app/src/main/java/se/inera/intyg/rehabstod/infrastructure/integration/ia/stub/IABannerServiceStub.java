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
package se.inera.intyg.rehabstod.infrastructure.integration.ia.stub;

import java.util.Collections;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.infrastructure.integration.ia.constants.IaCacheConstants;
import se.inera.intyg.rehabstod.infrastructure.integration.ia.model.Application;
import se.inera.intyg.rehabstod.infrastructure.integration.ia.model.Banner;
import se.inera.intyg.rehabstod.infrastructure.integration.ia.services.IABannerService;

@Service
@Profile("ia-stub")
public class IABannerServiceStub implements IABannerService {

  @Override
  @Cacheable(value = IaCacheConstants.IA_CACHE_NAME, key = "'" + IaCacheConstants.CACHE_KEY + "'")
  public List<Banner> getCurrentBanners() {
    return Collections.emptyList();
  }

  @Override
  public List<Banner> loadBanners(Application application) {
    return getCurrentBanners();
  }
}
