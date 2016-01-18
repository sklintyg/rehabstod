package se.inera.privatlakarportal.persistence.repository.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDateTime;
import se.inera.privatlakarportal.persistence.model.*;

public class PrivatelakareTestUtil {

    public static Privatlakare buildPrivatlakare(String personId, int hsaCounter, boolean isLakare) {
        return buildPrivatlakare(
            personId,
            "SE000000000000-WEBCERT0000"+hsaCounter,
            "Tolvan Tolvansson",
            "test@example.com",
            "4444444444",
            "postadress",
            "postnummer",
            "postort",
            "2015-08-01",
            isLakare);
    }

    public static Privatlakare buildPrivatlakare(
            String personId, String hsaId, String fullstandigtNamn, String epost, String telefonnummer,
            String postadress, String postnummer, String postort, String registreringsdatum, boolean isLakare) {
        Privatlakare privatlakare = new Privatlakare();

        privatlakare.setPersonId(personId);
        privatlakare.setHsaId(hsaId);
        privatlakare.setFullstandigtNamn(fullstandigtNamn);
        privatlakare.setEpost(epost);
        privatlakare.setTelefonnummer(telefonnummer);
        privatlakare.setPostadress(postadress);
        privatlakare.setPostnummer(postnummer);
        privatlakare.setPostort(postort);
        privatlakare.setForskrivarKod("0000000");

        privatlakare.setAgarform("Privat");
        privatlakare.setGodkandAnvandare(true);
        privatlakare.setArbetsplatsKod("0000000000");

        privatlakare.setLan("Län");
        privatlakare.setKommun("Kommun");

        privatlakare.setEnhetsId(hsaId);
        privatlakare.setEnhetsNamn("Enhetsnamn");
        if (isLakare) {
            privatlakare.setEnhetStartdatum(LocalDateTime.parse("2015-06-01"));
            privatlakare.setEnhetSlutDatum(LocalDateTime.parse("2015-06-02"));
        }

        privatlakare.setVardgivareId(hsaId);
        privatlakare.setVardgivareNamn("Vardgivarnamn");
        if (isLakare) {
            privatlakare.setVardgivareStartdatum(LocalDateTime.parse("2015-06-03"));
            privatlakare.setVardgivareSlutdatum(LocalDateTime.parse("2015-06-04"));
        }

        Set<Befattning> befattningar = new HashSet<Befattning>();
        befattningar.add(new Befattning(privatlakare, "Befattning kod 1"));
        befattningar.add(new Befattning(privatlakare, "Befattning kod 2"));
        privatlakare.setBefattningar(befattningar);

        Set<LegitimeradYrkesgrupp> legitimeradeYrkesgrupper = new HashSet<LegitimeradYrkesgrupp>();
        if (isLakare) {
            legitimeradeYrkesgrupper.add(new LegitimeradYrkesgrupp(privatlakare, "Läkare", "LK"));
        }
        privatlakare.setLegitimeradeYrkesgrupper(legitimeradeYrkesgrupper);

        List<Specialitet> specialiteter = new ArrayList<Specialitet>();
        specialiteter.add(new Specialitet(privatlakare, "Specialitet kod 1", "Specialitet namn 1"));
        specialiteter.add(new Specialitet(privatlakare, "Specialitet kod 2", "Specialitet namn 2"));
        privatlakare.setSpecialiteter(specialiteter);

        Set<Verksamhetstyp> verksamhetsTyper = new HashSet<Verksamhetstyp>();
        verksamhetsTyper.add(new Verksamhetstyp(privatlakare, "Verksamhetstyp 1"));
        verksamhetsTyper.add(new Verksamhetstyp(privatlakare, "Verksamhetstyp 2"));
        privatlakare.setVerksamhetstyper(verksamhetsTyper);

        Set<Vardform> vardformer = new HashSet<Vardform>();
        vardformer.add(new Vardform(privatlakare, "Vardform 1"));
        vardformer.add(new Vardform(privatlakare, "Vardform 2"));
        privatlakare.setVardformer(vardformer);

        privatlakare.setRegistreringsdatum(LocalDateTime.parse(registreringsdatum));

        return privatlakare;
    }



}
