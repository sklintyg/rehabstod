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

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Magnus Ekstrand on 2018-10-19.
 */
public class RegisterExtendedConsentResponse implements Serializable {

    public enum ResponseCode { OK, ERROR }

    private static final long serialVersionUID = -5797795536344707201L;

    private ResponseCode responseCode;
    private String responseMessage;

    private String registeredBy;

    public RegisterExtendedConsentResponse() {
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegisterExtendedConsentResponse)) {
            return false;
        }
        RegisterExtendedConsentResponse that = (RegisterExtendedConsentResponse) o;
        return responseCode == that.responseCode
            && Objects.equals(responseMessage, that.responseMessage)
            && Objects.equals(registeredBy, that.registeredBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseCode, responseMessage, registeredBy);
    }

}
