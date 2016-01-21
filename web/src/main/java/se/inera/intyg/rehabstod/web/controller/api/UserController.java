package se.inera.intyg.rehabstod.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.model.User;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetUserResponse;

/**
 * Created by pebe on 2015-08-21.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "")
    public GetUserResponse getUser() {
        RehabstodUser user = userService.getUser();
        return new GetUserResponse(new User(user, false));
    }

}
