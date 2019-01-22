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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by marced on 2016-01-18.
 */
public class GetConfigResponse {
    private List<DiagnosKapitel> diagnosKapitelList;
    private String webcertViewIntygTemplateUrl;
    private String version;
    private String defaultIDP;
    private String defaultAlias;
    private Map<String, String> idpMap = new HashMap<>();



//    public GetConfigResponse(List<DiagnosKapitel> diagnosKapitelList, String webcertViewIntygTemplateUrl,
//            String version, String defaultIDP, String defaMap<String, String> idpMap) {
//        this.diagnosKapitelList = diagnosKapitelList;
//        this.webcertViewIntygTemplateUrl = webcertViewIntygTemplateUrl;
//        this.version = version;
//        this.defaultIDP = defaultIDP;
//        this.idpMap = idpMap;
//    }

    public List<DiagnosKapitel> getDiagnosKapitelList() {
        return diagnosKapitelList;
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

    public String getDefaultIDP() {
        return defaultIDP;
    }

    public void setDefaultIDP(String defaultIDP) {
        this.defaultIDP = defaultIDP;
    }

    public Map<String, String> getIdpMap() {
        return idpMap;
    }

    public void setIdpMap(Map<String, String> idpMap) {
        this.idpMap = idpMap;
    }

    public static final class GetConfigResponseBuilder {
        private List<DiagnosKapitel> diagnosKapitelList;
        private String webcertViewIntygTemplateUrl;
        private String version;
        private String defaultIDP;
        private String defaultAlias;
        private Map<String, String> idpMap = new HashMap<>();

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

        public GetConfigResponseBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        public GetConfigResponseBuilder withDefaultIDP(String defaultIDP) {
            this.defaultIDP = defaultIDP;
            return this;
        }

        public GetConfigResponseBuilder withDefaultAlias(String defaultAlias) {
            this.defaultAlias = defaultAlias;
            return this;
        }

        public GetConfigResponseBuilder withIdpMap(Map<String, String> idpMap) {
            this.idpMap = idpMap;
            return this;
        }

        public GetConfigResponse build() {
            GetConfigResponse getConfigResponse = new GetConfigResponse();
            getConfigResponse.setWebcertViewIntygTemplateUrl(webcertViewIntygTemplateUrl);
            getConfigResponse.setVersion(version);
            getConfigResponse.setDefaultIDP(defaultIDP);
            getConfigResponse.setIdpMap(idpMap);
            getConfigResponse.diagnosKapitelList = this.diagnosKapitelList;
            getConfigResponse.defaultAlias = this.defaultAlias;
            return getConfigResponse;
        }
    }
}
