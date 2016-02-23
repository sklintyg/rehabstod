/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.web.controller.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.common.logmessages.ActivityType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesException;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RestController
@RequestMapping("/api/sjukfall")
public class SjukfallController {

    @Autowired
    private SjukfallService sjukfallService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Sjukfall> getSjukfallForCareUnit(@RequestBody GetSjukfallRequest request) {

        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }

        String enhetsId = user.getValdVardenhet().getId();
        String hsaId = user.getHsaId();
        Urval urval = user.getUrval();
        List<InternalSjukfall> sjukfall = sjukfallService.getSjukfall(enhetsId, hsaId, urval, request);

        // PDL-logging based on which sjukfall that are abou to be displayed to user.
        List<InternalSjukfall> toLog = user.getPdlActivityStore().getActivitiesNotInStore(enhetsId, sjukfall, ActivityType.READ);
        logService.logSjukfallData(toLog);
        user.getPdlActivityStore().addActivitiesToStore(enhetsId, toLog, ActivityType.READ);

        return sjukfall.stream().map(sf -> sf.getSjukfall()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public SjukfallSummary getUnitCertificateSummary() {
        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }

        String enhetsId = user.getValdVardenhet().getId();
        return sjukfallService.getSummary(enhetsId);
    }

}
