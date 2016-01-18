package se.inera.intyg.rehabstod.auth;

import java.io.Serializable;

/**
 * Fake container for approx. CGI SAML ticket attributes.
 *
 * Created by eriklupander on 2015-06-16.
 */
public class FakeElegCredentials implements Serializable {

    private static final long serialVersionUID = -463449872733458242L;

    // Subject_SerialNumber
    private String personId;

    // Subject_GivenName
    private String firstName;

    // Subject_Surname
    private String lastName;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
