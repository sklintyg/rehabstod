package se.inera.privatlakarportal.common.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RegistrationTest {

    @Test
    public void testRegistrationIsValid() {
        Registration registration = new Registration();
        registration.setAdress("Testadress");
        registration.setAgarForm("Testagarform");
        registration.setArbetsplatskod("Kod");
        registration.setBefattning("Befattning");
        registration.setEpost("test@test.se");
        registration.setKommun("Kommun");
        registration.setLan("Län");
        registration.setPostnummer("12345");
        registration.setPostort("postort");
        registration.setTelefonnummer("12343455");
        registration.setVardform("Vårdform");
        registration.setVerksamhetensNamn("Verksamhetsnamn");
        registration.setVerksamhetstyp("Typ");

        assertTrue(registration.checkIsValid());
    }

}
