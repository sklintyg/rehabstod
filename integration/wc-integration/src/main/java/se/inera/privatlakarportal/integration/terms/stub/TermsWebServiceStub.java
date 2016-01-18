package se.inera.privatlakarportal.integration.terms.stub;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerterms.v1.rivtabp21.GetPrivatePractitionerTermsResponderInterface;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionertermsresponder.v1.GetPrivatePractitionerTermsResponseType;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionertermsresponder.v1.GetPrivatePractitionerTermsType;
import se.riv.infrastructure.directory.privatepractitioner.terms.v1.AvtalType;

/**
 * Created by pebe on 2015-08-25.
 */
public class TermsWebServiceStub implements GetPrivatePractitionerTermsResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(TermsWebServiceStub.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public GetPrivatePractitionerTermsResponseType getPrivatePractitionerTerms(String s, GetPrivatePractitionerTermsType getPrivatePractitionerTermsType) {

        AvtalType avtalType = new AvtalType();
        avtalType.setAvtalVersion(1);
        avtalType.setAvtalVersionDatum(LocalDateTime.parse("2015-09-30"));

        String fileEncoding = "UTF-8";
        String fileUrl = "classpath:bootstrap-webcertvillkor/webcertvillkor.html";

        LOG.debug("Loading terms file '{}' using encoding '{}'", fileUrl, fileEncoding);

        String avtalText;
        try {
            Resource resource = resourceLoader.getResource(fileUrl);

            if (!resource.exists()) {
                LOG.error("Could not load avtal file since the resource '{}' does not exist", fileUrl);
            } else {
                avtalText = FileUtils.readFileToString(resource.getFile());
                avtalType.setAvtalText(avtalText);
            }

        } catch (IOException ioe) {
            LOG.error("IOException occured when loading avtal file '{}'", fileUrl);
            throw new RuntimeException("Error occured when loading avtal file", ioe);
        }

        GetPrivatePractitionerTermsResponseType response = new GetPrivatePractitionerTermsResponseType();
        response.setAvtal(avtalType);

        return response;
    }
}
