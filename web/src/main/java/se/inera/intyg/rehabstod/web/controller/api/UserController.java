package se.inera.intyg.rehabstod.web.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesException;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.ChangeSelectedUnitRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetUserResponse;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "", method = RequestMethod.GET)
    public GetUserResponse getUser() {
        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        } else {
            return new GetUserResponse(user);
        }
    }

    /**
     * Changes the selected care unit in the security context for the logged in user.
     *
     * @param changeSelectedEnhetRequest
     * @return
     */
    @RequestMapping(value = "/andraenhet", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetUserResponse changeSelectedUnitOnUser(@RequestBody ChangeSelectedUnitRequest changeSelectedEnhetRequest) {

        RehabstodUser user = userService.getUser();

        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }
        LOG.debug("Attempting to change selected unit for user '{}', currently selected unit is '{}'", user.getHsaId(),
                user.getValdVardenhet().getId());

        boolean changeSuccess = user.changeValdVardenhet(changeSelectedEnhetRequest.getId());

        if (!changeSuccess) {
            throw new AuthoritiesException(String.format("Could not change active unit: Unit '%s' is not present in the MIUs for user '%s'",
                    changeSelectedEnhetRequest.getId(), user.getHsaId()));
        }

        LOG.debug("Selected vardenhet is now '{}'", user.getValdVardenhet().getId());

        return new GetUserResponse(user);
    }

}
