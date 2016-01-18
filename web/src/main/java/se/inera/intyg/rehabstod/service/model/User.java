package se.inera.intyg.rehabstod.service.model;

import se.inera.intyg.rehabstod.auth.RehabstodUser;

/**
 * Created by pebe on 2015-08-25.
 */
public class User {

    private String personalIdentityNumber;
    private String name;
    private boolean nameFromPuService;
    private boolean nameUpdated;
    private String authenticationScheme;


    public User(RehabstodUser rehabstodUser, boolean nameUpdated) {
        personalIdentityNumber = rehabstodUser.getPersonalIdentityNumber();
        name = rehabstodUser.getName();
        nameFromPuService = rehabstodUser.isNameFromPuService();
        authenticationScheme = rehabstodUser.getAuthenticationScheme();

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


}
