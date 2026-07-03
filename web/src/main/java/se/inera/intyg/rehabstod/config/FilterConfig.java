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
package se.inera.intyg.rehabstod.config;

import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_CHECK_URI;
import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_EXTEND;
import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_REQUEST_MAPPING;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.inera.intyg.rehabstod.auth.RSSecurityHeadersFilter;
import se.inera.intyg.rehabstod.logging.MdcServletFilter;
import se.inera.intyg.rehabstod.logging.MdcUserServletFilter;
import se.inera.intyg.rehabstod.security.filter.PrincipalUpdatedFilter;
import se.inera.intyg.rehabstod.security.filter.RequestContextHolderUpdateFilter;
import se.inera.intyg.rehabstod.security.filter.SessionTimeoutFilter;
import se.inera.intyg.rehabstod.web.filters.PdlConsentGivenAssuranceFilter;
import se.inera.intyg.rehabstod.web.filters.UnitSelectedAssuranceFilter;

@Configuration
public class FilterConfig {

  // Order 2 — updates RequestContextHolder after spring-session sets up the session
  private static final int ORDER_REQUEST_CONTEXT_HOLDER = 2;
  // Order 3 — MDC correlation IDs for logging
  private static final int ORDER_MDC_SERVLET = 3;
  // Order 5 — custom session timeout (runs before Spring Security so it can pre-invalidate)
  private static final int ORDER_SESSION_TIMEOUT = 5;
  // Order 7 — detects principal changes and touches the Redis session so changes are persisted
  // Must be directly after Spring Security (order 6) so SecurityContextHolder is still populated.
  private static final int ORDER_PRINCIPAL_UPDATED = 7;
  // Order 8 — MDC user details for logging (after security context is established)
  private static final int ORDER_MDC_USER = 8;
  // Order 9 — verifies a unit is selected before allowing /api/* access
  private static final int ORDER_UNIT_SELECTED = 9;
  // Order 10 — verifies PDL consent before allowing /api/* access
  private static final int ORDER_PDL_CONSENT = 10;
  // Order 100 — security response headers (last)
  private static final int ORDER_SECURITY_HEADERS = 100;

  // --- Order 2 ---
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

  // --- Order 3 ---
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

  // --- Order 5 ---
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

  // --- Order 7 ---
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

  // --- Order 8 ---
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

  // --- Order 9 ---
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

  // --- Order 10 ---
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

  // --- Order 100 ---
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
