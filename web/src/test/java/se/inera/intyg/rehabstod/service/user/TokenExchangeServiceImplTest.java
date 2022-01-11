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
package se.inera.intyg.rehabstod.service.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import javax.xml.transform.stream.StreamSource;
import org.apache.cxf.staxutils.StaxUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.NameID;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import se.inera.intyg.rehabstod.auth.RehabstodUserTokens;

@RunWith(MockitoJUnitRunner.class)
public class TokenExchangeServiceImplTest {

    private final static String TOKEN_EXCHANGE_URL = "http://localhost:1234/test";
    private final static String DUMMY = "dummy";
    private final static String ACCESS_TOKEN = "abc123";
    private final static String REFRESH_TOKEN = "bc1321";
    private final static int EXPIRES_IN_SECONDS = 3600;
    private final static String TOKEN_JSON = "{\"access_token\": \"" + ACCESS_TOKEN + "\", \"refresh_token\": \"" + REFRESH_TOKEN
        + "\", \"expires_in\": " + EXPIRES_IN_SECONDS + "}";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TokenExchangeServiceImpl tokenExchangeServiceImpl = new TokenExchangeServiceImpl();

    @BeforeClass
    public static void init() throws Exception {
        DefaultBootstrap.bootstrap();
    }

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(tokenExchangeServiceImpl, "tokenExchangeEndpointUrl", TOKEN_EXCHANGE_URL);
        ReflectionTestUtils.setField(tokenExchangeServiceImpl, "clientId", DUMMY);
        ReflectionTestUtils.setField(tokenExchangeServiceImpl, "clientSecret", DUMMY);

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
            .thenReturn(new ResponseEntity(TOKEN_JSON, HttpStatus.OK));
    }

    @Test
    public void testExchangeSuccessful() throws Exception {

        SAMLCredential samlCredential = createSamlCredential("saml-assertion-uppdragslos.xml");
        LocalDateTime leastExpireTime = LocalDateTime.now().plusSeconds(EXPIRES_IN_SECONDS);

        RehabstodUserTokens exchange = tokenExchangeServiceImpl.exchange(samlCredential);
        assertEquals(ACCESS_TOKEN, exchange.getAccessToken());
        assertEquals(REFRESH_TOKEN, exchange.getRefreshToken());
        assertTrue(!leastExpireTime.isAfter(exchange.getAccessTokenExpiration()));

    }

    @Test(expected = TokenServiceException.class)
    public void testExchangeNoSAMLCredential() {
        tokenExchangeServiceImpl.exchange(null);
    }

    @Test
    public void testRefreshSuccessful() {
        LocalDateTime leastExpireTime = LocalDateTime.now().plusSeconds(EXPIRES_IN_SECONDS);

        RehabstodUserTokens exchange = tokenExchangeServiceImpl.refresh(new RehabstodUserTokens());
        assertEquals(ACCESS_TOKEN, exchange.getAccessToken());
        assertEquals(REFRESH_TOKEN, exchange.getRefreshToken());
        assertTrue(!leastExpireTime.isAfter(exchange.getAccessTokenExpiration()));
    }

    @Test(expected = TokenServiceException.class)
    public void testRefreshNoTokens() {
        tokenExchangeServiceImpl.refresh(null);
    }

    private SAMLCredential createSamlCredential(String filename) throws Exception {
        Document doc = StaxUtils.read(new StreamSource(new ClassPathResource(
            "TokenExchangeServiceImplTest/" + filename).getInputStream()));
        UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(Assertion.DEFAULT_ELEMENT_NAME);

        Assertion assertion = (Assertion) unmarshaller.unmarshall(doc.getDocumentElement());
        NameID nameId = assertion.getSubject().getNameID();
        return new SAMLCredential(nameId, assertion, "remoteId", "localId");
    }
}
