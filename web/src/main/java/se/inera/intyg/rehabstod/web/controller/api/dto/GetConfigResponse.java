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
package se.inera.intyg.rehabstod.web.controller.api.dto;

import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;

import java.util.List;

/**
 * Created by marced on 2016-01-18.
 */
public class GetConfigResponse {
    private List<DiagnosKapitel> diagnosKapitelList;
    private String statistikTjanstBaseUrl;

    public GetConfigResponse(List<DiagnosKapitel> diagnosKapitelList, String statistikTjanstBaseUrl) {
        this.diagnosKapitelList = diagnosKapitelList;
        this.statistikTjanstBaseUrl = statistikTjanstBaseUrl;
    }

    public List<DiagnosKapitel> getDiagnosKapitelList() {
        return diagnosKapitelList;
    }

    public String getStatistikTjanstBaseUrl() {
        return statistikTjanstBaseUrl;
    }

    public void setStatistikTjanstBaseUrl(String statistikTjanstBaseUrl) {
        this.statistikTjanstBaseUrl = statistikTjanstBaseUrl;
    }
}
