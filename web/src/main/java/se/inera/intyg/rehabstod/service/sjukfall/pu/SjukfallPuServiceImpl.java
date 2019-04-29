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
package se.inera.intyg.rehabstod.service.sjukfall.pu;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.infra.integration.pu.services.PUService;
import se.inera.intyg.infra.security.common.model.AuthoritiesConstants;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;
import se.inera.intyg.schemas.contract.Personnummer;

/**
 * Created by eriklupander on 2017-09-05.
 */
@Service
public class SjukfallPuServiceImpl implements SjukfallPuService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    @Autowired
    private PUService puService;

    @Autowired
    private UserService userService;

    @Override
    public void filterSekretessForSummary(List<SjukfallEnhet> sjukfallList) {
        RehabstodUser user = userService.getUser();
        Map<Personnummer, PersonSvar> personSvarMap = fetchPersons(sjukfallList);

        // Iterate over 'sjukfallList' and remove invalid items
        Iterator<SjukfallEnhet> i = sjukfallList.iterator();
        while (i.hasNext()) {
            SjukfallEnhet item = i.next();

            // If invalid personnummer, remove the last element returned by the iterator
            Optional<Personnummer> pnr = getPersonnummerOfOptional(item.getPatient().getId());
            if (!pnr.isPresent()) {
                LOG.warn("Removing item from list of sjukfall. Problem parsing personnummer '{}' returned by PU service.",
                        item.getPatient().getId());
                i.remove();
                continue;
            }

            // Explicitly null out patient name after we're done getting the pnr for querying,
            // no leaks from here since we're ignoring PU-service problems.
            item.getPatient().setNamn(null);

            // Parse response from PU service
            PersonSvar personSvar = personSvarMap.get(pnr.get());
            if (personSvar == null) {
                LOG.info("Removing item from list of sjukfall. PU service returned an empty response for person '{}'",
                        pnr.get().getPersonnummerHash());
                i.remove();
            } else {
                if (personSvar.getStatus() == PersonSvar.Status.FOUND) {
                    if (personSvar.getPerson().isSekretessmarkering()) {
                        // RS-US-GE-002: Om användaren EJ är läkare ELLER om intyget utfärdades på annan VE,
                        // då får vi ej visa sjukfall för s-märkt patient.
                        if (!hasLakareRoleAndIsLakare(user, item.getLakare().getHsaId())
                                || !userService.isUserLoggedInOnEnhetOrUnderenhet(item.getVardEnhetId())) {
                            i.remove();
                        }
                    } else if (personSvar.getPerson().isAvliden()) {
                        // The patient is dead...remove...
                        i.remove();
                    }
                } else if (personSvar.getStatus() == PersonSvar.Status.ERROR) {
                    throw new IllegalStateException("Could not contact PU service, not showing any sjukfall.");
                } else {
                    LOG.info("Removing item from list of sjukfall. PU service returned an unexpected status response for person '{}'",
                            pnr.get().getPersonnummerHash());
                    i.remove();
                }
            }
        }
    }

    @Override
    public List<IntygData> filterSekretessForPatientHistory(List<IntygData> intygsData, String vardgivareId, String enhetsId) {
        String pnr = intygsData.get(0).getPatientId();
        Personnummer personnummer = getPersonnummer(pnr);

        List<IntygData> filteredIntyg = intygsData;

        PersonSvar personSvar = puService.getPerson(personnummer);
        if (personSvar.getStatus() == PersonSvar.Status.FOUND) {
            if (personSvar.getPerson().isSekretessmarkering()) {
                filteredIntyg = filterOnVardgivareAndEnhet(intygsData, vardgivareId, enhetsId);
            }
        } else if (personSvar.getStatus() == PersonSvar.Status.ERROR) {
            throw new IllegalStateException("Could not contact PU service, not showing any sjukfall.");
        } else {
            filteredIntyg = filterOnVardgivareAndEnhet(intygsData, vardgivareId, enhetsId);
        }

        return filteredIntyg;
    }

    @Override
    public void enrichWithPatientNamesAndFilterSekretess(List<SjukfallEnhet> sjukfallList) {
        RehabstodUser user = userService.getUser();
        Map<Personnummer, PersonSvar> personSvarMap = fetchPersons(sjukfallList);

        // Iterate over 'sjukfallList' and remove invalid items
        Iterator<SjukfallEnhet> i = sjukfallList.iterator();
        while (i.hasNext()) {
            SjukfallEnhet item = i.next();

            // If invalid personnummer, remove the last element returned by the iterator
            Optional<Personnummer> pnr = getPersonnummerOfOptional(item.getPatient().getId());
            if (!pnr.isPresent()) {
                i.remove();
                LOG.warn("Problem parsing personnummer '{}' returned by PU service. Removing from list of sjukfall.",
                        item.getPatient().getId());
                continue;
            }

            // Parse response from PU service
            PersonSvar personSvar = personSvarMap.get(pnr.get());
            if (personSvar != null && personSvar.getStatus() == PersonSvar.Status.FOUND) {
                if (personSvar.getPerson().isSekretessmarkering()) {

                    // RS-US-GE-002: RS-15 => Om patienten är sekretessmarkerad, skall namnet bytas ut mot placeholder.
                    item.getPatient().setNamn(SEKRETESS_SKYDDAD_NAME_PLACEHOLDER);

                    // RS-US-GE-002: Om användaren EJ är läkare ELLER om intyget utfärdades på annan VE, då får vi ej visa
                    // sjukfall för s-märkt patient. Dessutom kan det vara en läkare med roll REHABKOORDINATOR.
                    if (!hasLakareRoleAndIsLakare(user, item.getLakare().getHsaId())
                            || !userService.isUserLoggedInOnEnhetOrUnderenhet(item.getVardEnhetId())) {
                        i.remove();
                    }

                } else if (personSvar.getPerson().isAvliden()) {
                    i.remove();
                } else {
                    item.getPatient().setNamn(joinNames(personSvar));
                }
            } else if (personSvar != null && personSvar.getStatus() == PersonSvar.Status.ERROR) {
                throw new IllegalStateException("Could not contact PU service, not showing any sjukfall.");
            } else {
                item.getPatient().setNamn(SEKRETESS_SKYDDAD_NAME_UNKNOWN);
            }
        }
    }

    private Map<Personnummer, PersonSvar> fetchPersons(List<SjukfallEnhet> sjukfallList) {
        if (sjukfallList.isEmpty()) {
            return new HashMap<>();
        }

        // Call the PU service to get patient information.
        // If an error occur when calling the PU service an empty map will be returned.
        Map<Personnummer, PersonSvar> personSvarMap = puService.getPersons(getPersonnummerList(sjukfallList));

        // Empty map indicates an error when calling the PU service,
        // clear the list of sjukfall.
        if (personSvarMap.isEmpty()) {
            throw new IllegalStateException("Could not contact PU service, not showing any sjukfall.");
        }
        return personSvarMap;
    }

    @Override
    public void enrichWithPatientNameAndFilterSekretess(List<SjukfallPatient> patientSjukfall) {
        if (patientSjukfall.size() == 0) {
            return;
        }

        if (patientSjukfall.get(0).getIntyg().size() == 0) {
            throw new IllegalStateException("Cannot process sjukfall not consisting of at least one intyg.");
        }

        PatientData firstPatientDataItem = patientSjukfall.get(0).getIntyg().get(0);
        String pnr = firstPatientDataItem.getPatient().getId();
        Personnummer personnummer = getPersonnummer(pnr);

        // Call PU service
        PersonSvar personSvar = puService.getPerson(personnummer);

        if (personSvar.getStatus() == PersonSvar.Status.FOUND) {
            // If patient is deceased, we shouldn't be here at all.
            if (personSvar.getPerson().isAvliden()) {
                throw new IllegalStateException("Cannot display sjukfall for deceased patient.");
            }

            RehabstodUser user = userService.getUser();

            if (personSvar.getPerson().isSekretessmarkering()) {
                if (!(hasLakareRoleAndIsLakare(user, firstPatientDataItem.getLakare().getHsaId())
                        && userService.isUserLoggedInOnEnhetOrUnderenhet(firstPatientDataItem.getVardenhetId()))) {
                    throw new IllegalStateException("Cannot show patient details for patient having sekretessmarkering");
                }
                // Uppdatera namnet på samtliga ingående intyg i sjukfallet till "Sekretessmarkering"
                // om användaren får lov att se sjukfallen.
                patientSjukfall.stream()
                        .flatMap(ps -> ps.getIntyg().stream())
                        .forEach(i -> i.getPatient().setNamn(SEKRETESS_SKYDDAD_NAME_PLACEHOLDER));
            } else {
                // Uppdatera namnet på samtliga ingående intyg i sjukfallet om patienten EJ är s-märkt.
                patientSjukfall.stream()
                        .flatMap(ps -> ps.getIntyg().stream())
                        .forEach(i -> i.getPatient().setNamn(joinNames(personSvar)));
            }

        } else if (personSvar.getStatus() == PersonSvar.Status.ERROR) {
            throw new IllegalStateException("Could not contact PU service, not showing any sjukfall.");
        } else {
            patientSjukfall.stream()
                    .flatMap(ps -> ps.getIntyg().stream())
                    .forEach(i -> i.getPatient().setNamn("Namn okänt"));
        }
    }

    /**
     * Method will return as list of unique and valid Personnummer parsing 'sjukfallList'.
     * Any invalid patient ID in sjukfallList will be filtered.
     */
    @VisibleForTesting
    List<Personnummer> getPersonnummerList(List<SjukfallEnhet> sjukfallList) {
                                    LOG.debug("Building a list of Personnummer by extracting patient IDs from a 'sjukfall' list.");
        return getPersonnummerListOfOptionals(sjukfallList).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Optional<Personnummer>> getPersonnummerListOfOptionals(List<SjukfallEnhet> sjukfallList) {
        return sjukfallList.stream()
                .map(se -> getPersonnummerOfOptional(se.getPatient().getId()))
                .collect(Collectors.toList());
    }

    private Optional<Personnummer> getPersonnummerOfOptional(String pnr) {
        Personnummer personnummer = null;
        try {
            personnummer = getPersonnummer(pnr);
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }

        return Optional.ofNullable(personnummer);
    }

    private Personnummer getPersonnummer(String pnr) {
        Personnummer personnummer = Personnummer.createPersonnummer(pnr)
                .orElseThrow(() -> new RuntimeException("Found unparsable personnummer '" + pnr + "'"));

        if (!personnummer.verifyControlDigit()) {
            throw new RuntimeException("Found personnummer '" + personnummer.getPersonnummerHash() + "' with invalid control digit");
        }

        return personnummer;
    }

    private List<IntygData> filterOnVardgivareAndEnhet(List<IntygData> intygsData, String vardgivareId, String enhetsId) {
        return intygsData.stream()
                .filter(intygData ->
                        intygData.getVardgivareId().equals(vardgivareId)
                                && intygData.getVardenhetId().equals(enhetsId))
                .collect(Collectors.toList());
    }

    // This is a hack to sort out the requirement that a Doctor MAY have systemRole making the Doctor a REHABKOORDINATOR.
    // In that particular case, the "isLakare()" will still return true, but the ROLE of the user will be REHABKOORDINATOR.
    // This method explicitly checks to the user is both a doc and has the requisite role.
    private boolean hasLakareRoleAndIsLakare(RehabstodUser user, String issuingDoctorHsaId) {
        return (user.isLakare() && user.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE))
                || (user.isLakare() && !user.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE)
                && user.getHsaId().equalsIgnoreCase(issuingDoctorHsaId));
    }

    private String joinNames(PersonSvar personSvar) {
        return Joiner.on(' ').skipNulls()
                .join(personSvar.getPerson().getFornamn(),
                        personSvar.getPerson().getMellannamn(),
                        personSvar.getPerson().getEfternamn());
    }

}
