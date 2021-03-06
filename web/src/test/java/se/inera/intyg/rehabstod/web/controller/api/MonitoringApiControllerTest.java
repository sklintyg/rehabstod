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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.inera.intyg.infra.monitoring.logging.UserAgentInfo;
import se.inera.intyg.infra.monitoring.logging.UserAgentParser;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.web.controller.api.dto.MonitoringRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.MonitoringRequest.MonitoringRequestEvent;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringApiControllerTest {

    protected static final String OS_FAMILY = "OS_FAMILY";
    protected static final String OS_VERSION = "OS_VERSION";
    private static final String BROWSERNAME = "lynx";
    private static final String BROWSER_VERSION = "1.0";
    private static final String WIDTH = "100";
    private static final String HEIGHT = "200";
    @Mock
    private MonitoringLogService monitoringService;
    @Mock
    private UserAgentParser userAgentParser;
    @InjectMocks
    private MonitoringApiController controller = new MonitoringApiController();


    @Test
    public void testMonitoring() {
        when(userAgentParser.parse(anyString())).thenReturn(new UserAgentInfo(BROWSERNAME, BROWSER_VERSION, OS_FAMILY, OS_VERSION));
        MonitoringRequest request = new MonitoringRequest();
        request.setEvent(MonitoringRequestEvent.SCREEN_RESOLUTION);
        request.setInfo(ImmutableMap.of(MonitoringRequest.WIDTH, WIDTH, MonitoringRequest.HEIGHT, HEIGHT));

        final ResponseEntity response = controller.monitoring(request, "some userAgent header");

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        verify(monitoringService).logBrowserInfo(BROWSERNAME, BROWSER_VERSION, OS_FAMILY, OS_VERSION, WIDTH, HEIGHT);
    }
}