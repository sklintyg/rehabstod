package se.inera.privatlakarportal.web.controller.api.dto;

import se.inera.privatlakarportal.service.model.User;

/**
 * Created by pebe on 2015-08-21.
 */
public class GetUserResponse {

    private User user;

    public GetUserResponse(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
