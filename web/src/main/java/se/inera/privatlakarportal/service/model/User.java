package se.inera.privatlakarportal.service.model;

import se.inera.privatlakarportal.auth.PrivatlakarUser;
import se.inera.privatlakarportal.pu.model.PersonSvar;
import se.inera.privatlakarportal.common.model.RegistrationStatus;

/**
 * Created by pebe on 2015-08-25.
 */
public class User {

    private String personalIdentityNumber;
    private String name;
    private boolean nameFromPuService;
    private boolean nameUpdated;
    private String authenticationScheme;
    private PersonSvar.Status personSvarStatus;
    private RegistrationStatus status;

    public User(PrivatlakarUser privatlakarUser, PersonSvar.Status personSvarStatus, RegistrationStatus status, boolean nameUpdated) {
        personalIdentityNumber = privatlakarUser.getPersonalIdentityNumber();
        name = privatlakarUser.getName();
        nameFromPuService = privatlakarUser.isNameFromPuService();
        authenticationScheme = privatlakarUser.getAuthenticationScheme();
        this.personSvarStatus = personSvarStatus;
        this.status = status;
        this.nameUpdated = nameUpdated;
    }

    public String getPersonalIdentityNumber() {
        return personalIdentityNumber;
    }

    public String getName() {
        return name;
    }

    public boolean isNameFromPuService() {
        return nameFromPuService;
    }

    public boolean isNameUpdated() {
        return nameUpdated;
    }

    public String getAuthenticationScheme() {
        return authenticationScheme;
    }

    public PersonSvar.Status getPersonSvarStatus() {
        return personSvarStatus;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

}
