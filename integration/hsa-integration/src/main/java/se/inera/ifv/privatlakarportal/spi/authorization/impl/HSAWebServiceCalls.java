/*
 * Inera Medcert - Sjukintygsapplikation
 *
 * Copyright (C) 2010-2011 Inera AB (http://www.inera.se)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package se.inera.ifv.privatlakarportal.spi.authorization.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3.wsaddressing10.AttributedURIType;

import se.inera.ifv.hsaws.v3.HsaWsFault;
import se.inera.ifv.hsaws.v3.HsaWsResponderInterface;
import se.inera.ifv.hsawsresponder.v3.GetHospLastUpdateResponseType;
import se.inera.ifv.hsawsresponder.v3.GetHospLastUpdateType;
import se.inera.ifv.hsawsresponder.v3.GetHospPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetHospPersonType;
import se.inera.ifv.hsawsresponder.v3.HandleCertifierResponseType;
import se.inera.ifv.hsawsresponder.v3.HandleCertifierType;
import se.inera.ifv.hsawsresponder.v3.PingResponseType;
import se.inera.ifv.hsawsresponder.v3.PingType;

import com.google.common.base.Throwables;
import se.inera.intyg.rehabstod.common.monitoring.util.HashUtility;

public class HSAWebServiceCalls {

    @Autowired
    private HsaWsResponderInterface hsaWebServiceClient;

    private static final Logger LOG = LoggerFactory.getLogger(HSAWebServiceCalls.class);

    private AttributedURIType logicalAddressHeader = new AttributedURIType();

    private AttributedURIType messageId = new AttributedURIType();

    /**
     * @param hsaLogicalAddress the hsaLogicalAddress to set
     */
    public void setHsaLogicalAddress(String hsaLogicalAddress) {
        logicalAddressHeader.setValue(hsaLogicalAddress);
    }

    /**
     * Help method to test access to HSA.
     *
     * @throws Exception
     */
    public void callPing() {

        try {
            PingType pingtype = new PingType();
            PingResponseType response = hsaWebServiceClient.ping(logicalAddressHeader, messageId, pingtype);
            LOG.debug("Response:" + response.getMessage());

        } catch (HsaWsFault ex) {
            LOG.warn("Exception={}", ex.getMessage());
            Throwables.propagate(ex);
        }
    }

    public HandleCertifierResponseType callHandleCertifier(HandleCertifierType parameters) {
        try {
            return hsaWebServiceClient.handleCertifier(logicalAddressHeader, messageId, parameters);
        } catch (HsaWsFault ex) {
            LOG.error("Failed to call callHandleCertifier with certifierId '{}'", parameters.getCertifierId());
            Throwables.propagate(ex);
            return null;
        }
    }

    public GetHospPersonResponseType callGetHospPerson(GetHospPersonType parameters) {
        try {
            return hsaWebServiceClient.getHospPerson(logicalAddressHeader, messageId, parameters);
        } catch (HsaWsFault ex) {
            LOG.error("Failed to call callGetHospPerson with id '{}'", HashUtility.hash(parameters.getPersonalIdentityNumber()));
            Throwables.propagate(ex);
            return null;
        }
    }

    public GetHospLastUpdateResponseType callGetHospLastUpdate(GetHospLastUpdateType parameters) {
        try {
            return hsaWebServiceClient.getHospLastUpdate(logicalAddressHeader, messageId, parameters);
        } catch (HsaWsFault ex) {
            LOG.error("Failed to call callGetHospLastUpdate");
            Throwables.propagate(ex);
            return null;
        }
    }
}
