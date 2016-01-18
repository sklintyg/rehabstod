package se.inera.privatlakarportal.service;

import se.inera.privatlakarportal.auth.PrivatlakarUser;
import se.inera.privatlakarportal.service.model.User;

public interface UserService {
    public PrivatlakarUser getUser();
    public User getUserWithStatus();
}
