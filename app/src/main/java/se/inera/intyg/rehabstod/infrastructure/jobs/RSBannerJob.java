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
package se.inera.intyg.rehabstod.infrastructure.jobs;

import java.util.List;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.inera.intyg.rehabstod.infrastructure.integration.ia.model.Application;
import se.inera.intyg.rehabstod.infrastructure.integration.ia.model.Banner;
import se.inera.intyg.rehabstod.infrastructure.integration.ia.services.IABannerService;

/** Load banners from IntygsAdmin. */
@Component
public class RSBannerJob {

  private static final Logger LOG = LoggerFactory.getLogger(RSBannerJob.class);

  @Autowired private IABannerService iaBannerService;

  @Scheduled(cron = "${app.integration.intygsadmin.cron}")
  @SchedulerLock(name = "BannerJob.run", lockAtLeastFor = "PT30S", lockAtMostFor = "PT10M")
  public void run() {
    List<Banner> banners = iaBannerService.loadBanners(Application.REHABSTOD);
    LOG.debug("Loaded banners from IA, found {} banners", banners.size());
  }
}
