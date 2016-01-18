package se.inera.privatlakarportal.auth;

import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Component;

import se.inera.privatlakarportal.pu.services.PUService;


/**
 * Created by eriklupander on 2015-06-16.
 */
@Component
public class ElegUserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(ElegUserDetailsService.class);

    @Autowired
    PUService puService;

    @Override
    public Object loadUserBySAML(SAMLCredential samlCredential) {
        try {
            String personId = getAuthenticationAttribute(samlCredential, CgiElegAssertion.PERSON_ID_ATTRIBUTE);
            String fornamn = getAuthenticationAttribute(samlCredential, CgiElegAssertion.FORNAMN_ATTRIBUTE);
            String efternamn = getAuthenticationAttribute(samlCredential, CgiElegAssertion.MELLAN_OCH_EFTERNAMN_ATTRIBUTE);
            String namn = fornamn + " " + efternamn;

            PrivatlakarUser privatlakarUser = new PrivatlakarUser(personId, namn);

            if (samlCredential.getAuthenticationAssertion() != null) {
                String authnContextClassRef = samlCredential.getAuthenticationAssertion().getAuthnStatements().get(0).getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef();
                privatlakarUser.setAuthenticationScheme(authnContextClassRef);
            }

            return privatlakarUser;
        } catch (AuthenticationException e) {
                LOG.error("Got AuthenticationException, with message {}", e.getMessage());
                throw e;
        } catch (Exception e) {
            LOG.error("Error building user {}, failed with message {}", e.getMessage());
            throw e;
        }
    }

    private String getAuthenticationAttribute(SAMLCredential samlCredential, String attributeName) {
        for (AttributeStatement attributeStatement : samlCredential.getAuthenticationAssertion().getAttributeStatements()) {
            for (Attribute attribute : attributeStatement.getAttributes()) {
                if (attribute.getName().equals(attributeName)) {
                    for (XMLObject xmlObject : attribute.getAttributeValues()) {
                        if (xmlObject instanceof XSString && ((XSString) xmlObject).getValue() != null) {
                            return ((XSString) xmlObject).getValue();
                        } else if (xmlObject.getDOM() != null) {
                            return xmlObject.getDOM().getTextContent();
                        }
                        throw new IllegalArgumentException("Cannot parse SAML2 response attribute '" + attributeName + "', is not XSString or DOM is null");
                    }
                }
            }
        }
        throw new IllegalArgumentException("Could not extract attribute '" + attributeName + "' from SAMLCredential.");
    }

}
