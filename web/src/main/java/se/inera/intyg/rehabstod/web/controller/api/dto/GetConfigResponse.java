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

import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;

import java.util.List;

/**
 * Created by marced on 2016-01-18.
 */
public class GetConfigResponse {
    private List<DiagnosKapitel> diagnosKapitelList;
    private String webcertViewIntygTemplateUrl;
    private String webcertViewIntygLogoutUrl;
    private String version;

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

    public static final class GetConfigResponseBuilder {
        private List<DiagnosKapitel> diagnosKapitelList;
        private String webcertViewIntygTemplateUrl;
        private String webcertViewIntygLogoutUrl;
        private String version;

        private GetConfigResponseBuilder() {
        }

        public static GetConfigResponseBuilder aGetConfigResponse() {
            return new GetConfigResponseBuilder();
        }

        public GetConfigResponseBuilder withDiagnosKapitelList(List<DiagnosKapitel> diagnosKapitelList) {
            this.diagnosKapitelList = diagnosKapitelList;
            return this;
        }

        public GetConfigResponseBuilder withWebcertViewIntygTemplateUrl(String webcertViewIntygTemplateUrl) {
            this.webcertViewIntygTemplateUrl = webcertViewIntygTemplateUrl;
            return this;
        }

        public GetConfigResponseBuilder withWebcertViewIntygLogoutUrl(String webcertViewIntygLogoutUrl) {
            this.webcertViewIntygLogoutUrl = webcertViewIntygLogoutUrl;
            return this;
        }

        public GetConfigResponseBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        public GetConfigResponse build() {
            GetConfigResponse getConfigResponse = new GetConfigResponse();
            getConfigResponse.setDiagnosKapitelList(diagnosKapitelList);
            getConfigResponse.setWebcertViewIntygTemplateUrl(webcertViewIntygTemplateUrl);
            getConfigResponse.setWebcertViewIntygLogoutUrl(webcertViewIntygLogoutUrl);
            getConfigResponse.setVersion(version);
            return getConfigResponse;
        }
    }
}
