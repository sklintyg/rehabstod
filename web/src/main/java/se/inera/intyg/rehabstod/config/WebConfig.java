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

import java.util.List;
import java.util.Properties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan({
    "se.inera.intyg.rehabstod.web",
    "se.inera.intyg.rehabstod.integration.wc.stub.api",
    "se.inera.intyg.rehabstod.integration.srs.stub.api",
    "se.inera.intyg.rehabstod.logging",
    "se.inera.intyg.rehabstod.web.controller.api",
})
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
    for (HttpMessageConverter converter : converters) {
      if (converter instanceof MappingJackson2HttpMessageConverter) {
        MappingJackson2HttpMessageConverter jsonConverter =
            (MappingJackson2HttpMessageConverter) converter;
        jsonConverter.setObjectMapper(new CustomObjectMapper());
      }
    }
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // Disable browser caching of all /api requests
    WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
    Properties cacheMappings = new Properties();
    cacheMappings.setProperty("/api/**", "0");
    webContentInterceptor.setCacheMappings(cacheMappings);
    registry.addInterceptor(webContentInterceptor);
  }
}