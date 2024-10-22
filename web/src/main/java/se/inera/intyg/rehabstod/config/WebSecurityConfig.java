package se.inera.intyg.rehabstod.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static se.inera.intyg.rehabstod.auth.AuthenticationConstants.AUTHN_METHOD;
import static se.inera.intyg.rehabstod.auth.AuthenticationConstants.EMPLOYEE_HSA_ID;

import com.google.common.base.Strings;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.opensaml.saml.saml2.core.impl.AudienceBuilder;
import org.opensaml.saml.saml2.core.impl.AudienceRestrictionBuilder;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.ConditionsBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml4AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.logout.OpenSaml4LogoutRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2LogoutRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import se.inera.intyg.infra.security.common.cookie.IneraCookieSerializer;
import se.inera.intyg.rehabstod.auth.CsrfCookieFilter;
import se.inera.intyg.rehabstod.auth.CustomAuthenticationFailureHandler;
import se.inera.intyg.rehabstod.auth.RehabstodUserDetailsService;
import se.inera.intyg.rehabstod.auth.Saml2AuthenticationToken;
import se.inera.intyg.rehabstod.auth.SpaCsrfTokenRequestHandler;

@Configuration
@EnableWebSecurity
@EnableRedisHttpSession
@Slf4j
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final RehabstodUserDetailsService rehabstodUserDetailsService;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Value("${saml.sp.entity.id}")
    private String samlEntityId;
    @Value("${saml.idp.metadata.location}")
    private String samlIdpMetadataLocation;
    @Value("${saml.sp.assertion.consumer.service.location}")
    private String assertionConsumerServiceLocation;
    @Value("${saml.sp.single.logout.service.location}")
    private String singleLogoutServiceLocation;
    @Value("${saml.sp.single.logout.service.response.location}")
    private String singleLogoutServiceResponseLocation;
    @Value("${saml.login.success.url}")
    private String samlLoginSuccessUrl;
    @Value("${saml.login.success.url.always.use}")
    private boolean samlLoginSuccessUrlAlwaysUse;
    @Value("${saml.logout.success.url}")
    private String samlLogoutSuccessUrl;
    @Value("${oidc.op.identity}")
    private String opIdentity;
    @Value("${oidc.client.id}")
    private String rpIdentity;
    @Value("${saml.keystore.type:PKCS12}")
    private String keyStoreType;
    @Value("${saml.keystore.file}")
    private String keyStorePath;
    @Value("${saml.keystore.alias}")
    private String keyAlias;
    @Value("${saml.keystore.password}")
    private String keyStorePassword;

    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository()
        throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, CertificateException {

        final var keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(new FileInputStream(ResourceUtils.getFile(keyStorePath)), keyStorePassword.toCharArray());
        final var appPrivateKey = (PrivateKey) keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
        final var appCertificate = (X509Certificate) keyStore.getCertificate(keyAlias);

        final var registration = RelyingPartyRegistrations
            .fromMetadataLocation(samlIdpMetadataLocation)
            .registrationId("siths")
            .entityId(samlEntityId)
            .assertionConsumerServiceLocation(assertionConsumerServiceLocation)
            .singleLogoutServiceLocation(singleLogoutServiceLocation)
            .singleLogoutServiceResponseLocation(singleLogoutServiceResponseLocation)
            .signingX509Credentials(signing ->
                signing.add(
                    Saml2X509Credential.signing(appPrivateKey, appCertificate)
                )
            )
            .build();
        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RelyingPartyRegistrationRepository relyingPartyRegistrationRepository,
        Saml2LogoutRequestResolver logoutRequestResolver)
        throws Exception {
        http
            .authorizeHttpRequests(request -> request.
                requestMatchers("/metrics").permitAll().
                requestMatchers("/api/testability/**").permitAll().
                requestMatchers("/services/**").permitAll().
                requestMatchers("/api/config/**").permitAll().
                requestMatchers("/api/log/**").permitAll().
                requestMatchers("/api/session-auth-check/**").permitAll().
                anyRequest().fullyAuthenticated()
            )
            .saml2Metadata(withDefaults())
            .saml2Login(saml2 -> saml2
                .relyingPartyRegistrationRepository(relyingPartyRegistrationRepository)
                .authenticationManager(
                    new ProviderManager(
                        getOpenSaml4AuthenticationProvider()
                    )
                )
                .failureHandler(customAuthenticationFailureHandler)
                .defaultSuccessUrl(samlLoginSuccessUrl, samlLoginSuccessUrlAlwaysUse)
            )
            .saml2Logout(saml2 -> saml2.logoutRequest(logout -> logout.logoutRequestResolver(logoutRequestResolver)))
            .logout(logout ->
                logout.logoutSuccessUrl(samlLogoutSuccessUrl)
            )
            .requestCache(cacheConfigurer -> cacheConfigurer
                .requestCache(
                    samlLoginSuccessUrlAlwaysUse
                        ? new NullRequestCache()
                        : new HttpSessionRequestCache()
                )
            )
            .exceptionHandling(exceptionConfigurer -> exceptionConfigurer
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
            )
            .csrf(csrfConfigurer -> csrfConfigurer
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                .ignoringRequestMatchers(
                    "/api/testability/**",
                    "/services/**"
                )
            )
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DefaultCookieSerializer cookieSerializer() {
        return new IneraCookieSerializer(true);
    }

    private OpenSaml4AuthenticationProvider getOpenSaml4AuthenticationProvider() {
        final var authenticationProvider = new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(responseToken -> {
            final var authentication = OpenSaml4AuthenticationProvider
                .createDefaultResponseAuthenticationConverter()
                .convert(responseToken);
            if (!(authentication != null && authentication.isAuthenticated())) {
                return null;
            }
            final var personId = getAttribute(authentication, EMPLOYEE_HSA_ID);
            final var authMethod = getAttribute(authentication, AUTHN_METHOD);
            final var principal = rehabstodUserDetailsService.buildUserPrincipal(personId, authMethod);
            final var saml2AuthenticationToken = new Saml2AuthenticationToken(principal, authentication);
            saml2AuthenticationToken.setAuthenticated(true);
            return saml2AuthenticationToken;
        });
        return authenticationProvider;
    }

    private String getAttribute(Saml2Authentication samlCredential, String attributeId) {
        final var principal = (DefaultSaml2AuthenticatedPrincipal) samlCredential.getPrincipal();
        final var attributes = principal.getAttributes();
        if (attributes.containsKey(attributeId)) {
            return (String) attributes.get(attributeId).get(0);
        }
        throw new IllegalArgumentException(
            "Could not extract attribute '" + attributeId + "' from Saml2Authentication.");
    }

    @Bean
    Saml2AuthenticationRequestResolver authenticationRequestResolver(RelyingPartyRegistrationRepository registrations) {
        RelyingPartyRegistrationResolver registrationResolver =
            new DefaultRelyingPartyRegistrationResolver(registrations);
        OpenSaml4AuthenticationRequestResolver authenticationRequestResolver =
            new OpenSaml4AuthenticationRequestResolver(registrationResolver);
        authenticationRequestResolver.setAuthnRequestCustomizer((context) -> {
                context.getAuthnRequest().setAttributeConsumingServiceIndex(1);
                context.getAuthnRequest().setConditions(buildConditions());
                context.getAuthnRequest().setRequestedAuthnContext(buildRequestedAuthnContext());
            }
        );
        return authenticationRequestResolver;
    }

    @Bean
    Saml2LogoutRequestResolver logoutRequestResolver(RelyingPartyRegistrationRepository registrations) {
        final var logoutRequestResolver = new OpenSaml4LogoutRequestResolver(registrations);
        logoutRequestResolver.setParametersConsumer(parameters -> {
            final var token = (Saml2AuthenticationToken) parameters.getAuthentication();
            final var principal = (DefaultSaml2AuthenticatedPrincipal) token.getSaml2Authentication().getPrincipal();
            final var name = principal.getName();
            final var format = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";
            final var logoutRequest = parameters.getLogoutRequest();
            final var nameId = logoutRequest.getNameID();
            nameId.setValue(name);
            nameId.setFormat(format);

            final var sessionIndex = new MySessionIndex("urn:oasis:names:tc:SAML:2.0:protocol", "SessionIndex", "saml2p");
            sessionIndex.setValue(principal.getSessionIndexes().getFirst());
            logoutRequest.getSessionIndexes().add(sessionIndex);
        });
        return logoutRequestResolver;
    }

    public class MySessionIndex extends XSStringImpl implements SessionIndex {

        public MySessionIndex(String namespaceURI, String elementLocalName, String namespacePrefix) {
            super(namespaceURI, elementLocalName, namespacePrefix);
        }
    }

    private Conditions buildConditions() {
        AudienceRestriction audienceRestriction = new AudienceRestrictionBuilder().buildObject();

        addAudienceIfApplicable(audienceRestriction, rpIdentity);
        addAudienceIfApplicable(audienceRestriction, opIdentity);

        Conditions conditions = new ConditionsBuilder().buildObject();
        conditions.getConditions().add(audienceRestriction);

        return conditions;
    }

    private void addAudienceIfApplicable(AudienceRestriction audienceRestriction, String audienceValue) {
        if (!Strings.isNullOrEmpty(audienceValue)) {
            Audience audience = new AudienceBuilder().buildObject();
            audience.setURI(audienceValue);
            audienceRestriction.getAudiences().add(audience);
        }
    }

    private RequestedAuthnContext buildRequestedAuthnContext() {
        final var authnContextClassRefBuilder = new AuthnContextClassRefBuilder();
        final var authnContextClassRefLoa2 = authnContextClassRefBuilder.buildObject(SAMLConstants.SAML20_NS,
            AuthnContextClassRef.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final var authnContextClassRefLoa3 = authnContextClassRefBuilder.buildObject(SAMLConstants.SAML20_NS,
            AuthnContextClassRef.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        authnContextClassRefLoa2.setURI("http://id.sambi.se/loa/loa2");
        authnContextClassRefLoa3.setURI("http://id.sambi.se/loa/loa3");

        final var requestedAuthnContextBuilder = new RequestedAuthnContextBuilder();
        final var requestedAuthnContext = requestedAuthnContextBuilder.buildObject();
        requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);
        requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRefLoa2);
        requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRefLoa3);

        return requestedAuthnContext;
    }
}
