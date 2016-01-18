package se.inera.privatlakarportal.integration.terms.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.privatlakarportal.integration.terms.services.dto.Terms;
import se.riv.infrastructure.directory.privatepractitioner.terms.v1.AvtalType;

import javax.xml.ws.WebServiceException;

/**
 * Created by pebe on 2015-08-25.
 */
@Service
public class WebcertTermsServiceImpl implements WebcertTermsService {

    private static final Logger LOG = LoggerFactory.getLogger(WebcertTermsService.class);

    @Autowired
    private TermsWebServiceCalls client;

    @Override
    public Terms getTerms() {
        try {
            AvtalType avtalType = client.getPrivatePractitionerTerms().getAvtal();

            if (avtalType == null) {
                LOG.error("getAvtal is null in getPrivatePractitionerTerms");
                throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.EXTERNAL_ERROR, "Unable to lookup terms");
            }

            return new Terms(avtalType.getAvtalText(), avtalType.getAvtalVersion(), avtalType.getAvtalVersionDatum());
        }
        catch(WebServiceException e) {
            LOG.error("WebServiceException '{}' in getPrivatePractitionerTerms", e.getMessage());
            throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.EXTERNAL_ERROR, "Unable to lookup terms");
        }
    }
}
