package se.inera.privatlakarportal.common.service;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import se.inera.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.privatlakarportal.common.model.RegistrationStatus;
import se.inera.privatlakarportal.persistence.model.Privatlakare;

@Service
public class MailServiceImpl implements MailService {

    @Value("${mail.admin}")
    private String adminEpost;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.content.approved.body}")
    private String approvedBody;

    @Value("${mail.content.rejected.body}")
    private String notApprovedBody;

    @Value("${mail.content.pending.body}")
    private String awaitingHospBody;

    @Value("${mail.content.approved.subject}")
    private String approvedSubject;

    @Value("${mail.content.rejected.subject}")
    private String notApprovedSubject;

    @Value("${mail.content.pending.subject}")
    private String awaitingHospSubject;

    @Value("${mail.admin.content.hsa.subject}")
    private String hsaGenerationMailSubject;

    @Value("${mail.admin.content.hsa.body}")
    private String hsaGenerationMailBody;

    private static final String INERA_LOGO = "inera_logo.png";

    private static final String BOTTOM_BODY_CONTENT = "<br/><br/><span><img src='cid:inera_logo'></span>";

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Async
    public void sendHsaGenerationStatusEmail() {
        try {
            LOG.info("Sending hsa-generation-status email to {}", adminEpost);
            MimeMessage message = createHsaGenerationStatusMessage(adminEpost);
            message.saveChanges();
            mailSender.send(message);
        } catch (MessagingException | PrivatlakarportalServiceException | MailException | IOException e) {
            LOG.error("Error while sending registration status email with message {}", e.getMessage(), e);
            throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendRegistrationStatusEmail(RegistrationStatus status, Privatlakare privatlakare) {
        try {
            LOG.info("Sending registration status email to {}", privatlakare.getEpost());
            MimeMessage message = createStatusMessage(status, privatlakare);
            message.saveChanges();
            mailSender.send(message);
        } catch (MessagingException | PrivatlakarportalServiceException | MailException | IOException e) {
            LOG.error("Error while sending registration status email with message {}", e.getMessage(), e);
            throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, e.getMessage());
        }
    }

    private MimeMessage createHsaGenerationStatusMessage(String epost) throws AddressException, MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(epost));
        buildEmailContent(message, hsaGenerationMailSubject, hsaGenerationMailBody);
        return message;
    }

    private MimeMessage createStatusMessage(RegistrationStatus status, Privatlakare privatlakare) throws MessagingException,
            PrivatlakarportalServiceException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(privatlakare.getEpost()));
        buildEmailContent(message, messageSubjectFromRegistrationStatus(status), messageBodyFromRegistrationStatus(status));
        return message;
    }

    private String messageSubjectFromRegistrationStatus(RegistrationStatus status) {
        String htmlSubjectString = null;
        // Set correct subject depending on status
        switch (status) {
        case AUTHORIZED:
            htmlSubjectString = approvedSubject;
            break;
        case NOT_AUTHORIZED:
            htmlSubjectString = notApprovedSubject;
            break;
        case NOT_STARTED:
            // Ignored
            break;
        case WAITING_FOR_HOSP:
            htmlSubjectString = awaitingHospSubject;
            break;
        default:
            throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Something unforseen happened while sending registration verification email.");
        }
        return htmlSubjectString;
    }

    private String messageBodyFromRegistrationStatus(RegistrationStatus status) {
        String htmlBodyString = null;

        // Set correct content depending on status
        switch (status) {
        case AUTHORIZED:
            htmlBodyString = approvedBody;
            break;
        case NOT_AUTHORIZED:
            htmlBodyString = notApprovedBody;
            break;
        case NOT_STARTED:
            // Ignored
            break;
        case WAITING_FOR_HOSP:
            htmlBodyString = awaitingHospBody;
            break;
        default:
            throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Something unforseen happened while sending registration verification email.");
        }
        return htmlBodyString;
    }

    private void buildEmailContent(MimeMessage message, String subjectText, String bodyText) throws MessagingException, IOException {
        String htmlBodyString = bodyText;
        htmlBodyString += BOTTOM_BODY_CONTENT;

        // Use mimeHelper to set content
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setSubject(subjectText);
        helper.setText(htmlBodyString, true);
        final InputStreamSource imageSource = new ByteArrayResource(getLogo());
        helper.addInline("inera_logo", imageSource, "image/png");
    }

    private byte[] getLogo() throws IOException {
        InputStream is = null;
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(INERA_LOGO);
        byte[] bytes = IOUtils.toByteArray(is);
        LOG.debug("Getting file as bytes");
        return bytes;
    }
}
