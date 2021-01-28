/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.web.controller.api;

import static se.inera.intyg.rehabstod.web.controller.api.dto.MonitoringRequest.HEIGHT;
import static se.inera.intyg.rehabstod.web.controller.api.dto.MonitoringRequest.WIDTH;

import javax.ws.rs.core.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.monitoring.logging.UserAgentInfo;
import se.inera.intyg.infra.monitoring.logging.UserAgentParser;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.web.controller.api.dto.MonitoringRequest;

/**
 * Created by marced on 2019-09-20.
 */
@RestController
@RequestMapping("/js-monitoring-api")
public class MonitoringApiController {

    @Autowired
    private MonitoringLogService monitoringService;

    @Autowired
    private UserAgentParser userAgentParser;

    @RequestMapping(value = "/log", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity monitoring(@RequestBody MonitoringRequest request, @RequestHeader(HttpHeaders.USER_AGENT) String userAgent) {
        if (request == null || !request.isValid()) {
            return ResponseEntity.badRequest().build();
        }

        switch (request.getEvent()) {
            case SCREEN_RESOLUTION:
                final UserAgentInfo userAgentInfo = userAgentParser.parse(userAgent);
                monitoringService
                    .logBrowserInfo(userAgentInfo.getBrowserName(),
                        userAgentInfo.getBrowserVersion(),
                        userAgentInfo.getOsFamily(),
                        userAgentInfo.getOsVersion(),
                        request.getInfo().get(WIDTH),
                        request.getInfo().get(HEIGHT));
                break;
            default:
                return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

}
