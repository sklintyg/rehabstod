package se.inera.intyg.rehabstod.service.sjukfall.pu;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.infra.integration.pu.services.PUService;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhetRS;
import se.inera.intyg.schemas.contract.Personnummer;

import java.util.Iterator;
import java.util.List;

/**
 * Created by eriklupander on 2017-09-05.
 */
@Service
public class SjukfallPuServiceImpl implements SjukfallPuService {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallPuServiceImpl.class);

    @Autowired
    private PUService puService;

    @Autowired
    private UserService userService;

    @Override
    public void enrichWithPatientNamesAndFilterSekretess(List<SjukfallEnhetRS> sjukfallList) {

        RehabstodUser user = userService.getUser();

        Iterator<SjukfallEnhetRS> i = sjukfallList.iterator();

        while (i.hasNext()) {
            SjukfallEnhetRS item = i.next();

            Personnummer pnr = Personnummer.createValidatedPersonnummerWithDash(item.getPatient().getId()).orElse(null);
            if (pnr == null) {
                i.remove();
                LOG.warn("Problem parsing a personnummer when looking up patient in PU service. Removing from list of sjukfall.");
                continue;
            }

            PersonSvar personSvar = puService.getPerson(pnr);
            if (personSvar.getStatus() == PersonSvar.Status.FOUND) {
                if (personSvar.getPerson().isSekretessmarkering()) {

                    // RS-US-GE-002: Om användaren EJ är läkare ELLER om intyget utfärdades på annan VE, då får vi ej visa
                    // sjukfall för s-märkt patient.
                    if (!user.isLakare() || !item.getVardEnhetId().equalsIgnoreCase(user.getValdVardenhet().getId())) {
                        i.remove();
                    }
                } else {
                    item.getPatient()
                            .setNamn(Joiner.on(' ').skipNulls().join(personSvar.getPerson().getFornamn(),
                                    personSvar.getPerson().getMellannamn(),
                                    personSvar.getPerson().getEfternamn()));
                }
            } else if (personSvar.getStatus() == PersonSvar.Status.ERROR) {
                throw new IllegalStateException("Could not contact PU service, not showing any sjukfall.");
            } else {
                LOG.warn("Patient with personnummer '{}' was not found in PU-service, not including in list of sjukfall", pnr.getPnrHash());
                // i.remove();
                item.getPatient().setNamn("Namn okänt");
            }
        }
    }
}
