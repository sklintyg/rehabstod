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
package se.inera.intyg.rehabstod.infrastructure.integration.ia.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.rehabstod.infrastructure.integration.ia.config.properties.IntygsadminProperties;
import se.inera.intyg.rehabstod.infrastructure.integration.ia.constants.IaCacheConstants;
import se.inera.intyg.rehabstod.infrastructure.integration.ia.model.Application;
import se.inera.intyg.rehabstod.infrastructure.integration.ia.model.Banner;
import se.inera.intyg.rehabstod.logging.MdcLogConstants;
import se.inera.intyg.rehabstod.logging.PerformanceLogging;

@Service
@Profile("!ia-stub")
public class IABannerServiceImpl implements IABannerService {

  private static final Logger LOG = LoggerFactory.getLogger(IABannerServiceImpl.class);

  @Qualifier("iaRestTemplate") private final RestTemplate restTemplate;

  private final String iaUrl;

  public IABannerServiceImpl(
      @Qualifier("iaRestTemplate") RestTemplate restTemplate, IntygsadminProperties props) {
    this.restTemplate = restTemplate;
    this.iaUrl = props.url();
  }

  @Override
  @Cacheable(value = IaCacheConstants.IA_CACHE_NAME, key = "'" + IaCacheConstants.CACHE_KEY + "'")
  public List<Banner> getCurrentBanners() {
    return Collections.emptyList();
  }

  @Override
  @CachePut(value = IaCacheConstants.IA_CACHE_NAME, key = "'" + IaCacheConstants.CACHE_KEY + "'")
  @PerformanceLogging(eventAction = "load-banners", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public List<Banner> loadBanners(Application application) {
    String url = iaUrl + "/actuator/banner/" + application.toString();
    LOG.debug("Loading banner from {}", url);
    Banner[] banners = restTemplate.getForObject(url, Banner[].class);
    if (banners == null) {
      return Collections.emptyList();
    }
    LocalDateTime today = LocalDateTime.now();
    return Arrays.stream(banners)
        .filter(b -> b.getDisplayFrom().isBefore(today) && b.getDisplayTo().isAfter(today))
        .collect(Collectors.toList());
  }
}
