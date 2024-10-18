package se.inera.intyg.rehabstod.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import se.inera.intyg.infra.security.common.cookie.IneraCookieSerializer;
import se.inera.intyg.rehabstod.auth.CsrfCookieFilter;
import se.inera.intyg.rehabstod.auth.SpaCsrfTokenRequestHandler;

@Configuration
@EnableWebSecurity
@EnableRedisHttpSession
public class WebSecurityConfig {

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

    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
        final var registration = RelyingPartyRegistrations
            .fromMetadataLocation(samlIdpMetadataLocation)
            .registrationId("siths")
            .assertionConsumerServiceLocation(assertionConsumerServiceLocation)
            .singleLogoutServiceLocation(singleLogoutServiceLocation)
            .singleLogoutServiceResponseLocation(singleLogoutServiceResponseLocation)
            .build();
        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RelyingPartyRegistrationRepository relyingPartyRegistrationRepository)
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
            .saml2Login(saml2 -> saml2
                .relyingPartyRegistrationRepository(relyingPartyRegistrationRepository)
                .defaultSuccessUrl(samlLoginSuccessUrl, samlLoginSuccessUrlAlwaysUse)
            )
            .csrf(csrfConfigurer -> csrfConfigurer
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                .ignoringRequestMatchers("/api/testability/**", "/services/**")
            )
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
            .logout(logout -> logout.logoutSuccessUrl(samlLogoutSuccessUrl));

        return http.build();
    }

    @Bean
    public DefaultCookieSerializer cookieSerializer() {
        return new IneraCookieSerializer(true);
    }
}
