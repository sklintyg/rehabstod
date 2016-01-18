package se.inera.intyg.rehabstod.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.model.User;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetUserResponse;

/**
 * Created by pebe on 2015-08-21.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    //@Autowired
    //private UserService userService;
    //TODO: Add rehabstodUser properties such as careunits etc

    @RequestMapping(value = "")
    public GetUserResponse getUser() {
        return new GetUserResponse(new User(new RehabstodUser("111111", "kalle banan"), false));
    }

}
