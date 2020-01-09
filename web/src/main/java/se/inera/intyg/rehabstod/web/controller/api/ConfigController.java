/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.dynamiclink.model.DynamicLink;
import se.inera.intyg.infra.dynamiclink.service.DynamicLinkService;
import se.inera.intyg.infra.integration.ia.services.IABannerService;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetConfigResponse;

/**
 * Created by marced on 2016-02-09.
 */
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private static final String WEBCERT_VIEW_INTYG_URL_TEMPLATE = "webcert.view.urltemplate";
    private static final String WEBCERT_VIEW_INTYG_URL_LOGOUT = "webcert.view.urllogout";
    private static final String STATISTIK_SSO_URL = "statistik.sso.url";
    private static final String PROJECT_VERSION_PROPERTY = "project.version";

    @Autowired
    private DiagnosKapitelService diagnosKapitelService;

    @Autowired
    private DynamicLinkService dynamicLinkService;

    @Autowired
    private IABannerService iaBannerService;

    /**
     * Note - using Environment injection instead of @Value since the latter has some issues when injected into the
     * context of this @RestController.
     */
    @Autowired
    private Environment env;

    @RequestMapping(value = "")
    public GetConfigResponse getConfig() {

        GetConfigResponse configResponse = new GetConfigResponse();
        configResponse.setDiagnosKapitelList(diagnosKapitelService.getDiagnosKapitelList());
        configResponse.setWebcertViewIntygTemplateUrl(env.getProperty(WEBCERT_VIEW_INTYG_URL_TEMPLATE));
        configResponse.setWebcertViewIntygLogoutUrl(env.getProperty(WEBCERT_VIEW_INTYG_URL_LOGOUT));
        configResponse.setStatistikSsoUrl(env.getProperty(STATISTIK_SSO_URL));
        configResponse.setVersion(env.getProperty(PROJECT_VERSION_PROPERTY));
        configResponse.setBanners(iaBannerService.getCurrentBanners());

        return configResponse;
    }

    @RequestMapping(value = "/links", produces = "application/json")
    public Map<String, DynamicLink> getDynamicLinks() {
        return dynamicLinkService.getAllAsMap();
    }
}
