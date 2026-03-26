/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.infrastructure.config;

import static se.inera.intyg.rehabstod.application.api.SessionStatusController.SESSION_STATUS_CHECK_URI;
import static se.inera.intyg.rehabstod.application.api.SessionStatusController.SESSION_STATUS_EXTEND;
import static se.inera.intyg.rehabstod.application.api.SessionStatusController.SESSION_STATUS_REQUEST_MAPPING;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.inera.intyg.rehabstod.infrastructure.security.auth.PdlConsentGivenAssuranceFilter;
import se.inera.intyg.rehabstod.infrastructure.security.auth.RSSecurityHeadersFilter;
import se.inera.intyg.rehabstod.infrastructure.security.auth.UnitSelectedAssuranceFilter;
import se.inera.intyg.rehabstod.infrastructure.security.filter.PrincipalUpdatedFilter;
import se.inera.intyg.rehabstod.infrastructure.security.filter.RequestContextHolderUpdateFilter;
import se.inera.intyg.rehabstod.infrastructure.security.filter.SessionTimeoutFilter;
import se.inera.intyg.rehabstod.infrastructure.logging.MdcServletFilter;
import se.inera.intyg.rehabstod.infrastructure.logging.MdcUserServletFilter;

@Configuration
public class FilterConfig {

  // Order constants — gaps of 10 allow future insertion without reordering.
  // Filters auto-registered by Spring Boot fill the lowest order values:
  //   CharacterEncodingFilter  → Ordered.HIGHEST_PRECEDENCE       (auto)
  //   springSessionRepository  → Ordered.HIGHEST_PRECEDENCE + 50  (auto)
  //   springSecurityFilterChain → 0 (Spring Security default)     (auto)
  //
  // Our custom filters are placed around the auto-registered ones to preserve
  // the original execution order from ApplicationInitializer.

  private static final int ORDER_REQUEST_CONTEXT_HOLDER = -90;
  private static final int ORDER_MDC_SERVLET = -80;
  private static final int ORDER_SESSION_TIMEOUT = -70;
  // springSecurityFilterChain is at order 0 (auto)
  private static final int ORDER_MDC_USER = 10;
  private static final int ORDER_PRINCIPAL_UPDATED = 20;
  private static final int ORDER_UNIT_SELECTED = 30;
  private static final int ORDER_PDL_CONSENT = 40;
  private static final int ORDER_SECURITY_HEADERS = 100;

  // --- Filter #3 ---
  @Bean
  public FilterRegistrationBean<RequestContextHolderUpdateFilter>
  requestContextHolderUpdateFilterRegistration() {
    FilterRegistrationBean<RequestContextHolderUpdateFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(new RequestContextHolderUpdateFilter());
    reg.addUrlPatterns("/*");
    reg.setOrder(ORDER_REQUEST_CONTEXT_HOLDER);
    reg.setName("requestContextHolderUpdateFilter");
    return reg;
  }

  // --- Filter #4 ---
  @Bean
  public FilterRegistrationBean<MdcServletFilter> mdcServletFilterRegistration(
      MdcServletFilter mdcServletFilter) {
    FilterRegistrationBean<MdcServletFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(mdcServletFilter);
    reg.addUrlPatterns("/*");
    reg.setOrder(ORDER_MDC_SERVLET);
    reg.setName("mdcServletFilter");
    return reg;
  }

  // --- Filter #5 ---
  @Bean
  public FilterRegistrationBean<SessionTimeoutFilter> sessionTimeoutFilterRegistration() {
    SessionTimeoutFilter filter = new SessionTimeoutFilter();
    filter.setSkipRenewSessionUrls(SESSION_STATUS_CHECK_URI);
    FilterRegistrationBean<SessionTimeoutFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(filter);
    reg.addUrlPatterns("/*");
    reg.setOrder(ORDER_SESSION_TIMEOUT);
    reg.setName("sessionTimeoutFilter");
    return reg;
  }

  // --- Filter #7 ---
  @Bean
  public FilterRegistrationBean<MdcUserServletFilter> mdcUserServletFilterRegistration(
      MdcUserServletFilter mdcUserServletFilter) {
    FilterRegistrationBean<MdcUserServletFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(mdcUserServletFilter);
    reg.addUrlPatterns("/*");
    reg.setOrder(ORDER_MDC_USER);
    reg.setName("mdcUserServletFilter");
    return reg;
  }

  // --- Filter #8 ---
  @Bean
  public FilterRegistrationBean<PrincipalUpdatedFilter> principalUpdatedFilterRegistration(
      PrincipalUpdatedFilter principalUpdatedFilter) {
    FilterRegistrationBean<PrincipalUpdatedFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(principalUpdatedFilter);
    reg.addUrlPatterns("/*");
    reg.setOrder(ORDER_PRINCIPAL_UPDATED);
    reg.setName("principalUpdatedFilter");
    return reg;
  }

  // --- Filter #9 ---
  @Bean
  public FilterRegistrationBean<UnitSelectedAssuranceFilter>
  unitSelectedAssuranceFilterRegistration(
      UnitSelectedAssuranceFilter unitSelectedAssuranceFilter) {
    unitSelectedAssuranceFilter.setIgnoredUrls(
        SESSION_STATUS_CHECK_URI + ",/api/config,/api/user,/api/user/andraenhet");
    FilterRegistrationBean<UnitSelectedAssuranceFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(unitSelectedAssuranceFilter);
    reg.addUrlPatterns("/api/*");
    reg.setOrder(ORDER_UNIT_SELECTED);
    reg.setName("unitSelectedAssuranceFilter");
    return reg;
  }

  // --- Filter #10 ---
  @Bean
  public FilterRegistrationBean<PdlConsentGivenAssuranceFilter>
  pdlConsentGivenAssuranceFilterRegistration(
      PdlConsentGivenAssuranceFilter pdlConsentGivenAssuranceFilter) {
    pdlConsentGivenAssuranceFilter.setIgnoredUrls(
        SESSION_STATUS_CHECK_URI
            + ","
            + SESSION_STATUS_REQUEST_MAPPING
            + SESSION_STATUS_EXTEND
            + ",/api/config,/api/user,/api/user/giveconsent,/api/sjukfall/summary"
            + ",/api/stub,/api/sickleaves,/api/lu,/api/testability,/api/log/error");
    FilterRegistrationBean<PdlConsentGivenAssuranceFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(pdlConsentGivenAssuranceFilter);
    reg.addUrlPatterns("/api/*");
    reg.setOrder(ORDER_PDL_CONSENT);
    reg.setName("pdlConsentGivenAssuranceFilter");
    return reg;
  }

  // --- Filter #12 ---
  @Bean
  public FilterRegistrationBean<RSSecurityHeadersFilter> securityHeadersFilterRegistration() {
    FilterRegistrationBean<RSSecurityHeadersFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(new RSSecurityHeadersFilter());
    reg.addUrlPatterns("/*");
    reg.setOrder(ORDER_SECURITY_HEADERS);
    reg.setName("securityHeadersFilter");
    reg.setMatchAfter(true);
    return reg;
  }
}