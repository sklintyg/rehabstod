package se.inera.privatlakarportal.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import se.inera.privatlakarportal.common.service.MailService;
import se.inera.privatlakarportal.common.service.stub.MailServiceStub;
import se.inera.privatlakarportal.common.service.stub.MailStubStore;

@Configuration
@Profile({"dev", "mail-stub"})
@PropertySource("classpath:default.properties")
public class MailServiceStubConfig {

    @Bean 
    public MailService mailService() {
        return new MailServiceStub();
    }

    @Bean
    public JavaMailSender mailSender() {
        return new JavaMailSenderImpl();
    }

    @Bean
    MailStubStore mailstore() {
        return new MailStubStore();
    }
}
