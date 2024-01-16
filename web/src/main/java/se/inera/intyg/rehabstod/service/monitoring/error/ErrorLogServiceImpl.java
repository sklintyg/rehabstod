/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.service.monitoring.error;

import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.web.controller.api.dto.ErrorLogRequestDTO;

@Service
public class ErrorLogServiceImpl implements ErrorLogService {

    private final MonitoringLogService monitoringLogService;

    public ErrorLogServiceImpl(MonitoringLogService monitoringLogService) {
        this.monitoringLogService = monitoringLogService;
    }

    @Override
    public void logError(ErrorLogRequestDTO errorLogRequestDTO) {
        monitoringLogService.logClientError(
            errorLogRequestDTO.getErrorId(),
            errorLogRequestDTO.getErrorCode(),
            errorLogRequestDTO.getMessage(),
            getStackTrace(errorLogRequestDTO.getStackTrace())
        );
    }

    private String getStackTrace(String stackTrace) {
        return isDefined(stackTrace) ? stackTrace : "NO_STACK_TRACE";
    }

    private boolean isDefined(String value) {
        return value != null && !value.isEmpty() && !value.isBlank();
    }
}
