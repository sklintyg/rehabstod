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
package se.inera.intyg.rehabstod.infrastructure.integration.it.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.integration.intygstjanst")
public record IntygstjanstProperties(
    String scheme,
    String baseUrl,
    int port,
    String hostUrl,
    String logicalAddress,
    String listSickLeavesForPersonUrl,
    ServiceTimeouts service,
    RestTimeouts rest) {

  public record ServiceTimeouts(int connectionTimeout, int receiveTimeout) {}

  public record RestTimeouts(int connectionRequestTimeout, int connectionTimeout) {}
}
