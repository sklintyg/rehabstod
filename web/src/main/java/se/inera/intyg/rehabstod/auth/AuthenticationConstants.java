package se.inera.intyg.rehabstod.auth;

public final class AuthenticationConstants {

    public static final String AUTHN_METHOD = "urn:sambi:names:attribute:authnMethod";
    public static final String EMPLOYEE_HSA_ID = "http://sambi.se/attributes/1/employeeHsaId";

    private AuthenticationConstants() {
        throw new IllegalStateException("Utility class!");
    }
}
