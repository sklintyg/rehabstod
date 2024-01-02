/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.stub;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;
import se.riv.informationsecurity.authorization.consent.v2.ActorType;

/**
 * Created by Magnus Ekstrand on 2018-10-10.
 */
public class SamtyckestjanstStubRestApi {

    @Autowired
    private SamtyckestjanstStubStore store;

    @PUT
    @Path("/consent/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addConsentForPerson(
        @PathParam("personId") String personId,
        @QueryParam("vardgivareId") String vgHsaId,
        @QueryParam("vardenhetId") String veHsaId,
        @QueryParam("employeeId") String userHsaId,
        @QueryParam("from") String from,
        @QueryParam("to") String to) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(personId));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(vgHsaId));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(veHsaId));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(userHsaId));

        LocalDateTime consentFrom = Strings.isNullOrEmpty(from) ? LocalDate.now().atStartOfDay() : LocalDate.parse(from).atStartOfDay();
        LocalDateTime consentTo = Strings.isNullOrEmpty(to) ? null : LocalDate.parse(to).atStartOfDay();

        ActorType actorType = new ActorType();
        actorType.setEmployeeId(userHsaId);

        ActionType actionType = new ActionType();
        actionType.setRegisteredBy(actorType);
        actionType.setRegistrationDate(LocalDateTime.now());
        actionType.setRequestDate(LocalDateTime.now());
        actionType.setRequestedBy(actorType);

        String assertionId = UUID.randomUUID().toString();
        Personnummer pnr = parsePersonId(personId);

        ConsentData consentData = new ConsentData.Builder(assertionId, vgHsaId, veHsaId, pnr.getPersonnummer(), actionType)
            .employeeId(userHsaId)
            .consentFrom(consentFrom)
            .consentTo(consentTo)
            .build();

        store.add(consentData);
        return Response.ok().build();
    }

    @DELETE
    @Path("/consent/{personId}")
    public Response removeConsentForPerson(@PathParam("personId") String personId) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(personId));

        Personnummer pnr = parsePersonId(personId);
        store.remove(pnr.getPersonnummer());
        return Response.ok().build();
    }

    @DELETE
    @Path("/consent")
    public Response removeAllConsent() {
        store.removeAll();
        return Response.ok().build();
    }

    @GET
    @Path("/consent/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConsentForPerson(
        @PathParam("personId") String personId,
        @QueryParam("vardgivareId") String vgHsaId,
        @QueryParam("vardenhetId") String veHsaId
    ) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(personId));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(vgHsaId));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(veHsaId));

        Personnummer pnr = parsePersonId(personId);
        return Response.ok(store.hasConsent(vgHsaId, veHsaId, pnr.getPersonnummer(), LocalDate.now())).build();
    }

    @GET
    @Path("/consent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllConsent() {
        return Response.ok(store.getAll()).build();
    }

    private Personnummer parsePersonId(String personId) {
        return Personnummer.createPersonnummer(personId)
            .orElseThrow(() -> new IllegalStateException("Invalid personnummer!"));
    }

}
