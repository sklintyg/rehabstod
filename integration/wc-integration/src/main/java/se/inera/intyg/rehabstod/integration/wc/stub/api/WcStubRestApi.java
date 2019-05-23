/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.wc.stub.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.integration.wc.stub.WcStubStore;

/**
 * Created by marced on 2010-05-20.
 *
 * Provides a simple "REST" API for activating or inactivating the WC stub.
 *
 * If inactive, a RuntimeException will be thrown to fake problems.
 */
@Profile({ "rhs-wc-stub" })
@RestController
@RequestMapping("/api/stub/wc-api")
public class WcStubRestApi {

    @Autowired
    private WcStubStore wcStubStore;

    @RequestMapping(method = RequestMethod.GET, path = "/active")
    public ResponseEntity<String> activate() {
        boolean lastState = wcStubStore.isActive();
        wcStubStore.setActive(true);
        return new ResponseEntity<>("Set to 'true', previous state was '" + lastState + "'", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/inactive")
    public ResponseEntity<String> deactivate() {
        boolean lastState = wcStubStore.isActive();
        wcStubStore.setActive(false);
        return new ResponseEntity<>("Set to 'false', previous state was '" + lastState + "'", HttpStatus.OK);
    }

}
