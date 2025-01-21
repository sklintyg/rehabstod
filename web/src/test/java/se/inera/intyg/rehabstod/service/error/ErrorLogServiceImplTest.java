/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.service.error;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.monitoring.error.ErrorLogServiceImpl;
import se.inera.intyg.rehabstod.web.controller.api.dto.ErrorLogRequestDTO;

@ExtendWith(MockitoExtension.class)
class ErrorLogServiceImplTest {

    @Mock
    private MonitoringLogService monitoringLogService;
    @InjectMocks
    private ErrorLogServiceImpl errorLogService;

    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ERROR_ID = "errorId";
    private static final String ERROR_CODE = "errorCode";
    private static final String STACK_TRACE = "stackTrace";
    private static final String NO_STACK_TRACE = "NO_STACK_TRACE";


    @Test
    void shallLogError() {
        var request = new ErrorLogRequestDTO();
        request.setMessage(ERROR_MESSAGE);
        request.setErrorId(ERROR_ID);
        request.setErrorCode(ERROR_CODE);
        request.setStackTrace(STACK_TRACE);

        errorLogService.logError(request);
        verify(monitoringLogService).logClientError(ERROR_ID, ERROR_CODE, ERROR_MESSAGE, STACK_TRACE);
    }

    @Test
    void shallLogErrorWithoutStackTrace() {
        var request = new ErrorLogRequestDTO();
        request.setMessage(ERROR_MESSAGE);
        request.setErrorId(ERROR_ID);
        request.setErrorCode(ERROR_CODE);

        errorLogService.logError(request);
        verify(monitoringLogService).logClientError(ERROR_ID, ERROR_CODE, ERROR_MESSAGE, NO_STACK_TRACE);
    }
}
