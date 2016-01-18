package se.inera.privatlakarportal.common.service.stub;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

public class MailStubStore {
    private Map<String, String> store = new HashMap<String, String>();

    public void addMail(String id, String mail) {
        store.put(id, mail);
    }

    public Map<String, String> getMails() throws IOException, MessagingException {
        return store;
    }

    public void clear() {
        store.clear();
    }
}
