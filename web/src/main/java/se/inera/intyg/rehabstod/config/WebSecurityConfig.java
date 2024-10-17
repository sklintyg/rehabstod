package se.inera.intyg.rehabstod.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import se.inera.intyg.rehabstod.auth.CsrfCookieFilter;
import se.inera.intyg.rehabstod.auth.SpaCsrfTokenRequestHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${saml.logout.success.url}")
    private String samlLogoutSuccessUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(request -> request.
                requestMatchers("/metrics").permitAll().
                requestMatchers("/services/**").permitAll().
                requestMatchers("/api/config/**").permitAll().
                requestMatchers("/api/log/**").permitAll().
                requestMatchers("/api/session-auth-check/**").permitAll().
                anyRequest().fullyAuthenticated()
            )
            .exceptionHandling(exceptionConfigurer -> exceptionConfigurer
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
            )
            .csrf(csrfConfigurer -> csrfConfigurer
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            )
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
            .logout(logout -> logout.logoutSuccessUrl(samlLogoutSuccessUrl));

        return http.getOrBuild();
    }
}
