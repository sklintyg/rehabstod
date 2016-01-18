package se.inera.privatlakarportal.integration.terms.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Throwables;
import org.springframework.stereotype.Service;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerterms.v1.rivtabp21.GetPrivatePractitionerTermsResponderInterface;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionertermsresponder.v1.GetPrivatePractitionerTermsResponseType;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionertermsresponder.v1.GetPrivatePractitionerTermsType;

@Service
public class TermsWebServiceCalls {

    @Autowired
    private GetPrivatePractitionerTermsResponderInterface termsWebServiceClient;

    private static final Logger LOG = LoggerFactory.getLogger(TermsWebServiceCalls.class);

    public GetPrivatePractitionerTermsResponseType getPrivatePractitionerTerms() {
        try {
            GetPrivatePractitionerTermsType parameters = new GetPrivatePractitionerTermsType();
            return termsWebServiceClient.getPrivatePractitionerTerms("", parameters);
        } catch (Exception ex) {
            LOG.error("Failed to call getPrivatePractitionerTerms");
            Throwables.propagate(ex);
            return null;
        }
    }

}
