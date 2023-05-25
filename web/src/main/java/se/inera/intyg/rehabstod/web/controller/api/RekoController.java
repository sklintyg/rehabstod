/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.web.controller.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.service.sjukfall.SetRekoStatusToSickLeaveService;
import se.inera.intyg.rehabstod.web.controller.api.dto.SetRekoStatusToSickLeaveRequestDTO;

import javax.ws.rs.core.Response;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reko")
public class RekoController {

    private final SetRekoStatusToSickLeaveService setRekoStatusToSickLeaveService;

    public RekoController(SetRekoStatusToSickLeaveService setRekoStatusToSickLeaveService) {
        this.setRekoStatusToSickLeaveService = setRekoStatusToSickLeaveService;
    }

    @RequestMapping(value = "/set", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response getSickLeavesForUnit(@RequestBody SetRekoStatusToSickLeaveRequestDTO request) {
        setRekoStatusToSickLeaveService.set(
                request.getPatientId(),
                request.getStatus(),
                LocalDate.parse(request.getSickLeaveTimestamp()).atStartOfDay()
        );

        return Response.ok().build();
    }
}
