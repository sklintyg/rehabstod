/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.infrastructure.integration.sparrtjanst.stub;

import java.time.LocalDate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("rhs-sparrtjanst-stub")
@RequestMapping("/api/stub/sparrtjanst-api")
public class SparrtjanstStubRestApi {

  private final SparrtjanstStubStore store;

  public SparrtjanstStubRestApi(SparrtjanstStubStore store) {
    this.store = store;
  }

  @PutMapping(value = "/person/{personId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> addBlocksForPerson(
      @PathVariable("personId") String personId,
      @RequestParam("from") String from,
      @RequestParam("to") String to,
      @RequestParam("vardgivare") String vardgivare,
      @RequestParam("vardenhet") String vardenhet) {
    store.add(
        new BlockData(personId, LocalDate.parse(from), LocalDate.parse(to), vardgivare, vardenhet));
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/person/{personId}")
  public ResponseEntity<Void> removeBlocksForPerson(@PathVariable("personId") String personId) {
    store.remove(personId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/person")
  public ResponseEntity<Void> removeAllBlocks() {
    store.removeAll();
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getAllBlocks() {
    return ResponseEntity.ok(store.getAll());
  }
}
