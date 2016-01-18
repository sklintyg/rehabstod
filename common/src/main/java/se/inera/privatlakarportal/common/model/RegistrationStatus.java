package se.inera.privatlakarportal.common.model;

/**
 * Created by pebe on 2015-08-25.
 */
public enum RegistrationStatus {
    NOT_STARTED,

    // Om användaren inte fanns i HSA:s HOSP visar systemet information om att verifiering har påbörats. HSA lagrar användarens personnummer så att information om denne inkluderas nästa gång information hämtas från HOSP. Användningsfallet avslutas.
    WAITING_FOR_HOSP,

    // Om läkaren fanns i HSA:s HOSP men inte har en giltig läkarlegitimation visar systemet information om att denne inte är behörig.
    NOT_AUTHORIZED,

    // Om användaren fanns i HSA:s HOSP och har en giltig läkarlegitimation godkänner systemet användaren (HoS-personal.godkänd sätts till ”True”) och systemet visar info om att denne kan börja använda systemet.
    AUTHORIZED
}
