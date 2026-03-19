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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.stub;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
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
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;
import se.riv.informationsecurity.authorization.consent.v2.ActorType;

@RestController
@Profile("rhs-samtyckestjanst-stub")
@RequestMapping("/api/stub/samtyckestjanst-api")
public class SamtyckestjanstStubRestApi {

  private final SamtyckestjanstStubStore store;

  public SamtyckestjanstStubRestApi(SamtyckestjanstStubStore store) {
    this.store = store;
  }

  @PutMapping(value = "/consent/{personId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> addConsentForPerson(
      @PathVariable("personId") String personId,
      @RequestParam("vardgivareId") String vgHsaId,
      @RequestParam("vardenhetId") String veHsaId,
      @RequestParam("employeeId") String userHsaId,
      @RequestParam(value = "from", required = false) String from,
      @RequestParam(value = "to", required = false) String to) {

    Preconditions.checkArgument(!Strings.isNullOrEmpty(personId));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(vgHsaId));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(veHsaId));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(userHsaId));

    LocalDateTime consentFrom =
        Strings.isNullOrEmpty(from)
            ? LocalDate.now().atStartOfDay()
            : LocalDate.parse(from).atStartOfDay();
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

    ConsentData consentData =
        new ConsentData.Builder(assertionId, vgHsaId, veHsaId, pnr.getPersonnummer(), actionType)
            .employeeId(userHsaId)
            .consentFrom(consentFrom)
            .consentTo(consentTo)
            .build();

    store.add(consentData);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/consent/{personId}")
  public ResponseEntity<Void> removeConsentForPerson(@PathVariable("personId") String personId) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(personId));

    Personnummer pnr = parsePersonId(personId);
    store.remove(pnr.getPersonnummer());
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/consent")
  public ResponseEntity<Void> removeAllConsent() {
    store.removeAll();
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/consent/{personId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Boolean> getConsentForPerson(
      @PathVariable("personId") String personId,
      @RequestParam("vardgivareId") String vgHsaId,
      @RequestParam("vardenhetId") String veHsaId) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(personId));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(vgHsaId));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(veHsaId));

    Personnummer pnr = parsePersonId(personId);
    return ResponseEntity.ok(
        store.hasConsent(vgHsaId, veHsaId, pnr.getPersonnummer(), LocalDate.now()));
  }

  @GetMapping(value = "/consent", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getAllConsent() {
    return ResponseEntity.ok(store.getAll());
  }

  private Personnummer parsePersonId(String personId) {
    return Personnummer.createPersonnummer(personId)
        .orElseThrow(() -> new IllegalStateException("Invalid personnummer!"));
  }
}
