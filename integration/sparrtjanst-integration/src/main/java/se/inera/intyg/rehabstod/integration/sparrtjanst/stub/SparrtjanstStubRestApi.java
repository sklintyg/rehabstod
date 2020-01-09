/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.sparrtjanst.stub;

import java.time.LocalDate;
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

/**
 * Created by marced on 2018-10-01.
 */
public class SparrtjanstStubRestApi {

    @Autowired
    private SparrtjanstStubStore store;

    @PUT
    @Path("/person/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBlocksForPerson(
        @PathParam("personId") String personId,
        @QueryParam("from") String from,
        @QueryParam("to") String to,
        @QueryParam("vardgivare") String vardgivare,
        @QueryParam("vardenhet") String vardenhet) {
        store.add(new BlockData(personId, LocalDate.parse(from), LocalDate.parse(to), vardgivare, vardenhet));
        return Response.ok().build();
    }

    @DELETE
    @Path("/person/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeBlocksForPerson(@PathParam("personId") String personId) {
        store.remove(personId);
        return Response.ok().build();
    }

    @DELETE
    @Path("/person")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeAllBlocks() {
        store.removeAll();
        return Response.ok().build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBlocks() {
        return Response.ok(store.getAll()).build();

    }

}
