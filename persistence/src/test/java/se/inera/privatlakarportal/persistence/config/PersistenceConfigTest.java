package se.inera.privatlakarportal.persistence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:config/test.properties")
public class PersistenceConfigTest {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInTest() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
