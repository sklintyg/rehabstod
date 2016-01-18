package se.inera.privatlakarportal.common.service.stub;

import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stub/mails")
@Profile({"dev","mail-stub"})
public class MailStubController {

    @Autowired
    private MailStubStore mailStore;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public Map<String, String> getMails() throws MessagingException, IOException {
        return mailStore.getMails();
    }

    @RequestMapping(value = "/clear", method = RequestMethod.DELETE)
    public Response deleteMailbox() throws IOException, MessagingException {
        mailStore.getMails().clear();
        return Response.ok().build();
    }
}
