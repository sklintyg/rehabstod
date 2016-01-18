package se.inera.privatlakarportal.auth;

import java.io.Serializable;

/**
 * Created by pebe on 2015-08-11.
 */
public class PrivatlakarUser implements Serializable{

    private static final long serialVersionUID = 8711015219408194075L;

    private String personalIdentityNumber;
    private String name;
    private String authenticationScheme;
    private boolean nameFromPuService;

    public PrivatlakarUser(String personalIdentityNumber, String name) {
        this.personalIdentityNumber = personalIdentityNumber;
        this.name = name;
        nameFromPuService = false;
    }

    public String getPersonalIdentityNumber() {
        return personalIdentityNumber;
    }

    public String getName() {
        return name;
    }

    public String getAuthenticationScheme() {
        return authenticationScheme;
    }

    public void setAuthenticationScheme(String authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }

    public boolean isNameFromPuService() {
        return nameFromPuService;
    }

    public void updateNameFromPuService(String name) {
        this.name = name;
        nameFromPuService = true;
    }

}
