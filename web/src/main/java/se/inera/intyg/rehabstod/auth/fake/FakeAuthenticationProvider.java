/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.auth.fake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.AttributeStatementBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import se.inera.intyg.infra.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.infra.security.siths.BaseSakerhetstjanstAssertion;
import se.inera.intyg.rehabstod.auth.BaseFakeAuthenticationProvider;
import se.inera.intyg.rehabstod.auth.RehabstodUser;

/**
 * @author andreaskaltenbach
 */
public class FakeAuthenticationProvider extends BaseFakeAuthenticationProvider {

    private SAMLUserDetailsService userDetails;
    @Autowired
    private CommonAuthoritiesResolver commonAuthoritiesResolver;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (!(authentication instanceof FakeAuthenticationToken)) {
            throw new AssertionError("Unexpected type: " + authentication);
        }
        FakeAuthenticationToken token = (FakeAuthenticationToken) authentication;

        SAMLCredential credential = createSamlCredential(token);
        Object details = userDetails.loadUserBySAML(credential);

        // Hack in the forNamn and efterNamn if not present.
        addAbsentAttributesFromFakeCredentials(token, details);
        updateFeatures(details);

        ExpiringUsernameAuthenticationToken result = new ExpiringUsernameAuthenticationToken(null, details, credential,
            new ArrayList<>());
        result.setDetails(details);

        return result;
    }

    private void updateFeatures(Object details) {
        if (details instanceof IntygUser) {
            IntygUser user = (IntygUser) details;
            if (user.getValdVardenhet() != null) {
                user.setFeatures(commonAuthoritiesResolver
                    .getFeatures(Arrays.asList(user.getValdVardenhet().getId(), user.getValdVardgivare().getId())));
            } else {
                user.setFeatures(commonAuthoritiesResolver.getFeatures(Collections.emptyList()));
            }
        }
    }

    private void addAbsentAttributesFromFakeCredentials(FakeAuthenticationToken token, Object details) {
        if (details instanceof RehabstodUser) {
            RehabstodUser user = (RehabstodUser) details;
            FakeCredentials credentials = (FakeCredentials) token.getCredentials();
            if (user.getNamn() == null || user.getNamn().isEmpty()) {
                user.setNamn(credentials.getForNamn() + " "
                    + credentials.getEfterNamn());
            }

            if (credentials.isPdlConsentGiven() != null) {
                user.setPdlConsentGiven(credentials.isPdlConsentGiven());
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FakeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setUserDetails(SAMLUserDetailsService userDetails) {
        this.userDetails = userDetails;
    }

    private SAMLCredential createSamlCredential(FakeAuthenticationToken token) {

        Assertion assertion = new AssertionBuilder().buildObject();

        attachAuthenticationContext(assertion, FAKE_AUTHENTICATION_SITHS_CONTEXT_REF);

        AttributeStatement attributeStatement = new AttributeStatementBuilder().buildObject();
        assertion.getAttributeStatements().add(attributeStatement);
        attributeStatement.getAttributes()
            .add(createAttribute(BaseSakerhetstjanstAssertion.HSA_ID_ATTRIBUTE, ((FakeCredentials) token.getCredentials()).getHsaId()));

        NameID nameId = new NameIDBuilder().buildObject();
        nameId.setValue(token.getCredentials().toString());
        return new SAMLCredential(nameId, assertion, "fake-idp", "webcert");
    }

}
