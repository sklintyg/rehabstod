package se.inera.intyg.rehabstod.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesException;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetUnitCertificateSummaryResponse;

import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/unit-certificate-summary")
public class UnitCertificateSummaryController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public GetUnitCertificateSummaryResponse getUnitCertificateSummary() {
        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        } else {

            int total = ThreadLocalRandom.current().nextInt(0, 1000 + 1);

            int men = ThreadLocalRandom.current().nextInt(0, 100 + 1);

            int women = 100 - men;

            return new GetUnitCertificateSummaryResponse(total, men, women);
        }
    }
}
