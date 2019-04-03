/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.common.integration.json.CustomObjectMapper;
import se.inera.intyg.rehabstod.service.monitoring.HealthCheckService;
import se.inera.intyg.rehabstod.service.monitoring.dto.HealthStatus;

import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    @Autowired
    private HealthCheckService healthCheckService;

    private ObjectMapper objectMapper = new CustomObjectMapper();

    @RequestMapping(value = "/uptime")
    public Response getUpTimeStatus() {
        HealthStatus status = healthCheckService.checkUptime();
        String xml = buildXMLResponse(status);
        return Response.ok(xml).build();
    }

    @RequestMapping(value = "/usernumber")
    public Response getUpNumberOfUsers() {
        HealthStatus status = healthCheckService.checkNbrOfUsers();
        String xml = buildXMLResponse(status);
        return Response.ok(xml).build();
    }

    @RequestMapping(value = "/cachestats", produces = "application/json")
    public Response getCacheStatistcs() {
        return Response.serverError().entity("Statistics is not supported.").build();
    }

    private String buildXMLResponse(HealthStatus status) {
        return buildXMLResponse(status.isOk(), status.getMeasurement());
    }

    private String buildXMLResponse(boolean ok, long time) {
        StringBuilder sb = new StringBuilder();
        sb.append("<pingdom_http_custom_check>");
        sb.append("<status>" + (ok ? "OK" : "FAIL") + "</status>");
        sb.append("<response_time>" + time + "</response_time>");
        sb.append("</pingdom_http_custom_check>");
        return sb.toString();
    }
}
