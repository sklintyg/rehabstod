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
package se.inera.intyg.rehabstod.web.controller.api.dto;

import java.util.List;
import se.inera.intyg.infra.integration.ia.model.Banner;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;

/**
 * Created by marced on 2016-01-18.
 */
public class GetConfigResponse {

    private List<DiagnosKapitel> diagnosKapitelList;
    private String webcertViewIntygTemplateUrl;
    private String webcertViewIntygLogoutUrl;
    private String version;
    private List<Banner> banners;

    public List<DiagnosKapitel> getDiagnosKapitelList() {
        return diagnosKapitelList;
    }

    public void setDiagnosKapitelList(List<DiagnosKapitel> diagnosKapitelList) {
        this.diagnosKapitelList = diagnosKapitelList;
    }

    public String getWebcertViewIntygTemplateUrl() {
        return webcertViewIntygTemplateUrl;
    }

    public void setWebcertViewIntygTemplateUrl(String webcertViewIntygTemplateUrl) {
        this.webcertViewIntygTemplateUrl = webcertViewIntygTemplateUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setWebcertViewIntygLogoutUrl(String webcertViewIntygLogoutUrl) {
        this.webcertViewIntygLogoutUrl = webcertViewIntygLogoutUrl;
    }

    public String getWebcertViewIntygLogoutUrl() {
        return webcertViewIntygLogoutUrl;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }
}
