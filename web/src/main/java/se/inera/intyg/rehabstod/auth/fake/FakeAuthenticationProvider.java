/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.auth.fake;

import static se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion.ENHET_HSA_ID_ATTRIBUTE;
import static se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion.FORNAMN_ATTRIBUTE;
import static se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion.FORSKRIVARKOD_ATTRIBUTE;
import static se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion.HSA_ID_ATTRIBUTE;
import static se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion.MEDARBETARUPPDRAG_ID;
import static se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion.MEDARBETARUPPDRAG_TYPE;
import static se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion.MELLAN_OCH_EFTERNAMN_ATTRIBUTE;
import static se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion.SYSTEM_ROLE_ATTRIBUTE;
import static se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion.TITEL_ATTRIBUTE;
import static se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion.TITEL_KOD_ATTRIBUTE;
import static se.inera.intyg.rehabstod.auth.SakerhetstjanstAssertion.VARD_OCH_BEHANDLING;

import java.util.ArrayList;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.AttributeStatementBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import se.inera.intyg.rehabstod.auth.BaseFakeAuthenticationProvider;

/**
 * @author andreaskaltenbach
 */
public class FakeAuthenticationProvider extends BaseFakeAuthenticationProvider {

    private SAMLUserDetailsService userDetails;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (!(authentication instanceof FakeAuthenticationToken)) {
            throw new AssertionError("Unexpected type: " + authentication);
        }
        FakeAuthenticationToken token = (FakeAuthenticationToken) authentication;

        SAMLCredential credential = createSamlCredential(token);
        Object details = userDetails.loadUserBySAML(credential);

        ExpiringUsernameAuthenticationToken result = new ExpiringUsernameAuthenticationToken(null, details, credential,
                new ArrayList<GrantedAuthority>());
        result.setDetails(details);

        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FakeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setUserDetails(SAMLUserDetailsService userDetails) {
        this.userDetails = userDetails;
    }

    private SAMLCredential createSamlCredential(FakeAuthenticationToken token) {
        FakeCredentials fakeCredentials = (FakeCredentials) token.getCredentials();

        Assertion assertion = new AssertionBuilder().buildObject();

        attachAuthenticationContext(assertion, FAKE_AUTHENTICATION_SITHS_CONTEXT_REF);

        AttributeStatement attributeStatement = new AttributeStatementBuilder().buildObject();
        assertion.getAttributeStatements().add(attributeStatement);

        addAttribute(attributeStatement, HSA_ID_ATTRIBUTE, fakeCredentials.getHsaId());
        addAttribute(attributeStatement, FORNAMN_ATTRIBUTE, fakeCredentials.getFornamn());
        addAttribute(attributeStatement, MELLAN_OCH_EFTERNAMN_ATTRIBUTE, fakeCredentials.getEfternamn());
        addAttribute(attributeStatement, ENHET_HSA_ID_ATTRIBUTE, fakeCredentials.getEnhetId());
        addAttribute(attributeStatement, MEDARBETARUPPDRAG_TYPE, VARD_OCH_BEHANDLING);
        addAttribute(attributeStatement, MEDARBETARUPPDRAG_ID, fakeCredentials.getEnhetId());
        addAttribute(attributeStatement, FORSKRIVARKOD_ATTRIBUTE, fakeCredentials.getForskrivarKod());

        if (fakeCredentials.getSystemRoles() != null) {
            for (String sr: fakeCredentials.getSystemRoles()) {
                addAttribute(attributeStatement, SYSTEM_ROLE_ATTRIBUTE, sr);
            }
        }


        if (fakeCredentials.isLakare()) {
            addAttribute(attributeStatement, TITEL_ATTRIBUTE, "Läkare");
        }
        if (fakeCredentials.isTandlakare()) {
            addAttribute(attributeStatement, TITEL_ATTRIBUTE, "Tandläkare");
        }

        addAttribute(attributeStatement, TITEL_KOD_ATTRIBUTE, fakeCredentials.getBefattningsKod());

        NameID nameId = new NameIDBuilder().buildObject();
        nameId.setValue(token.getCredentials().toString());
        return new SAMLCredential(nameId, assertion, "fake-idp", "webcert");
    }

    private void addAttribute(AttributeStatement attributeStatement, String attributeName, String attributeValue) {
        if (attributeName == null || attributeValue == null) {
            return;
        }

        attributeStatement.getAttributes().add(createAttribute(attributeName, attributeValue));
    }

}
