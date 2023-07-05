/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

import com.sun.istack.NotNull;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.service.monitoring.error.ErrorLogService;
import se.inera.intyg.rehabstod.web.controller.api.dto.ErrorDataDTO;

@RestController
@RequestMapping("/api/log")
public class LogErrorController {

    private static final Logger LOG = LoggerFactory.getLogger(LogErrorController.class);
    private final ErrorLogService errorLogService;

    public LogErrorController(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    @RequestMapping(value = "/error", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response logError(@RequestBody @NotNull ErrorDataDTO errorDataDTO) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Logging error with id: '{}'", errorDataDTO.getErrorData().getErrorId());
        }
        errorLogService.logError(errorDataDTO.getErrorData());
        return Response.ok().build();
    }
}
