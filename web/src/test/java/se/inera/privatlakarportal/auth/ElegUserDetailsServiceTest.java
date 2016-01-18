package se.inera.privatlakarportal.auth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.saml2.core.NameID;
import org.springframework.security.saml.SAMLCredential;

import se.inera.privatlakarportal.pu.services.PUService;

/**
 * Created by eriklupander on 2015-06-25.
 */
@RunWith(MockitoJUnitRunner.class)
public class ElegUserDetailsServiceTest extends BaseSAMLCredentialTest {

    private static final String LOCAL_ENTITY_ID = "localEntityId";
    private static final String REMOTE_ENTITY_ID = "remoteEntityId";

    @Mock
    private PUService puService;

    @InjectMocks 
    private ElegUserDetailsService elegService;

    @BeforeClass
    public static void readSamlAssertions() throws Exception {
        bootstrapSamlAssertions();
    }

    @Test
    public void testLoadUserBySAML() {

        NameID nameId = mock(NameID.class);
        PrivatlakarUser priv = (PrivatlakarUser) elegService.loadUserBySAML(new SAMLCredential(nameId, assertionPrivatlakare, REMOTE_ENTITY_ID, LOCAL_ENTITY_ID));

        assertEquals("197705232382", priv.getPersonalIdentityNumber());
        assertEquals("Frida Kranstege", priv.getName());
    }
}
