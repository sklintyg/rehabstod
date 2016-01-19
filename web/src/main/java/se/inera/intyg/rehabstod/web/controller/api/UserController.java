package se.inera.intyg.rehabstod.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @RequestMapping(value = "")
    public GetUserResponse getUser() {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        RehabstodUser user = (RehabstodUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return new GetUserResponse(new User(user, false));
    }

}
