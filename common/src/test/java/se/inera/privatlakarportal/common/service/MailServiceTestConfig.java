package se.inera.privatlakarportal.common.service;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import se.inera.privatlakarportal.common.service.stub.JavaMailSenderAroundAdvice;
import se.inera.privatlakarportal.common.service.stub.MailStore;

@Configuration
@Profile("dev")
@PropertySource({"classpath:MailServiceTest/test.properties"})
@EnableAspectJAutoProxy
public class MailServiceTestConfig {

    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.protocol}")
    private String protocol;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.defaultEncoding}")
    private String defaultEncoding;

    @Value("${mail.port}")
    private String port;

    @Value("${mail.smtps.auth}")
    private boolean smtpsAuth;

    @Value("${mail.smtps.starttls.enable}")
    private boolean smtpsStarttlsEnable;

    @Value("${mail.smtps.debug}")
    private boolean smtpsDebug;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
       return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean 
    public MailService mailService() {
        return new MailServiceImpl();
    }

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        int intPort = Integer.parseInt(port);
        mailSender.setHost(mailHost);
        mailSender.setDefaultEncoding(defaultEncoding);
        mailSender.setProtocol(protocol);
        mailSender.setPort(intPort);

        if (!username.isEmpty()) {
            mailSender.setUsername(username);
        }
        if (!password.isEmpty()) {
            mailSender.setPassword(password);
        }

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail."+protocol+".port", intPort);
        javaMailProperties.put("mail."+protocol+".auth", smtpsAuth);
        javaMailProperties.put("mail."+protocol+".starttls.enable", smtpsStarttlsEnable);
        javaMailProperties.put("mail."+protocol+".debug", smtpsDebug);
        javaMailProperties.put("mail."+protocol+".socketFactory.fallback", true);
        mailSender.setJavaMailProperties(javaMailProperties);
        return mailSender;
    }

    @Bean
    JavaMailSenderAroundAdvice javaMailSenderAroundAdvice() {
        return new JavaMailSenderAroundAdvice();
    }

    @Bean
    MailStore mailstore() {
        return new MailStore();
        
    }
}
