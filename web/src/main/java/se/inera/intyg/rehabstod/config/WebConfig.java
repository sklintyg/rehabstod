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
package se.inera.intyg.rehabstod.config;

import java.util.List;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import se.inera.intyg.rehabstod.common.integration.json.CustomObjectMapper;

@EnableWebMvc
@Configuration
@ComponentScan({ "se.inera.intyg.rehabstod.web", "se.inera.intyg.rehabstod.common.service.stub" })
public class WebConfig extends WebMvcConfigurerAdapter {

    private static final int SECONDS_IN_HOUR = 3600;
    private static final int HOURS_IN_DAY = 24;
    private static final int DAYS_TO_CACHE = 15;

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setOrder(0);
        viewResolver.setViewClass(InternalResourceView.class);
        viewResolver.setPrefix("/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Bean
    public ViewResolver jspViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setOrder(1);
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        int cachePeriodInDays = SECONDS_IN_HOUR * HOURS_IN_DAY * DAYS_TO_CACHE;
        registry.addResourceHandler("/index.html").addResourceLocations("/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/").setCachePeriod(cachePeriodInDays);
        registry.addResourceHandler("/robots.txt").addResourceLocations("/").setCachePeriod(cachePeriodInDays);
        registry.addResourceHandler("/bower_components/**").addResourceLocations("/bower_components/");
        registry.addResourceHandler("/pubapp/**").addResourceLocations("/pubapp/");
        registry.addResourceHandler("/app/**").addResourceLocations("/app/");
        registry.addResourceHandler("/components/**").addResourceLocations("/components/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
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
