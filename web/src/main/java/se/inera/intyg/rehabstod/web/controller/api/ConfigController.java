/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.dynamiclink.model.DynamicLink;
import se.inera.intyg.infra.dynamiclink.service.DynamicLinkService;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetConfigResponse;

import java.util.Map;

/**
 * Created by marced on 2016-02-09.
 */
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private static final String STATISTIK_SJUNET_HOST_URL = "statistik.sjunet.host.url";

    @Autowired
    private DiagnosKapitelService diagnosKapitelService;

    @Autowired
    private DynamicLinkService dynamicLinkService;

    /**
     * Note - using Environment injection instead of @Value since the latter has some issues when injected into the
     * context of this @RestController.
     */
    @Autowired
    private Environment env;

    @RequestMapping(value = "")
    public GetConfigResponse getConfig() {
        if (!env.containsProperty(STATISTIK_SJUNET_HOST_URL)) {
            throw new IllegalStateException("Missing property '" + STATISTIK_SJUNET_HOST_URL + "'");
        }
        return new GetConfigResponse(diagnosKapitelService.getDiagnosKapitelList(), env.getProperty(STATISTIK_SJUNET_HOST_URL));
    }

    @RequestMapping(value = "/links", produces = "application/json")
    public Map<String, DynamicLink> getDynamicLinks() {
        return dynamicLinkService.getAllAsMap();
    }
}
