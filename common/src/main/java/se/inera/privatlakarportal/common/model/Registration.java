package se.inera.privatlakarportal.common.model;

import org.apache.commons.lang.StringUtils;

/**
 * Created by pebe on 2015-08-06.
 */
public class Registration {
    // Step 1
    private String befattning;
    private String verksamhetensNamn;
    private String agarForm;
    private String vardform;
    private String verksamhetstyp;
    private String arbetsplatskod;

    // Step 2
    private String telefonnummer;
    private String epost;
    private String adress;
    private String postnummer;
    private String postort;
    private String kommun;
    private String lan;

    public boolean checkIsValid() {

        if (StringUtils.isBlank(befattning)) {
            return false;
        }
        if (StringUtils.isBlank(verksamhetensNamn)) {
            return false;
        }
        if (StringUtils.isBlank(agarForm)) {
            return false;
        }
        if (StringUtils.isBlank(vardform)) {
            return false;
        }
        if (StringUtils.isBlank(verksamhetstyp)) {
            return false;
        }
        if (StringUtils.isBlank(telefonnummer)) {
            return false;
        }
        if (StringUtils.isBlank(epost)) {
            return false;
        }
        if (StringUtils.isBlank(adress)) {
            return false;
        }
        if (StringUtils.isBlank(postnummer)) {
            return false;
        }
        if (StringUtils.isBlank(postort)) {
            return false;
        }
        if (StringUtils.isBlank(kommun)) {
            return false;
        }
        if (StringUtils.isBlank(lan)) {
            return false;
        }
        return true;
    }

    public String getBefattning() {
        return befattning;
    }

    public void setBefattning(String befattning) {
        this.befattning = befattning;
    }

    public String getVerksamhetensNamn() {
        return verksamhetensNamn;
    }

    public void setVerksamhetensNamn(String verksamhetensNamn) {
        this.verksamhetensNamn = verksamhetensNamn;
    }

    public String getAgarForm() {
        return agarForm;
    }

    public void setAgarForm(String agarForm) {
        this.agarForm = agarForm;
    }

    public String getVardform() {
        return vardform;
    }

    public void setVardform(String vardform) {
        this.vardform = vardform;
    }

    public String getVerksamhetstyp() {
        return verksamhetstyp;
    }

    public void setVerksamhetstyp(String verksamhetstyp) {
        this.verksamhetstyp = verksamhetstyp;
    }

    public String getArbetsplatskod() {
        return arbetsplatskod;
    }

    public void setArbetsplatskod(String arbetsplatskod) {
        this.arbetsplatskod = arbetsplatskod;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public String getEpost() {
        return epost;
    }

    public void setEpost(String epost) {
        this.epost = epost;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPostnummer() {
        return postnummer;
    }

    public void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    public String getPostort() {
        return postort;
    }

    public void setPostort(String postort) {
        this.postort = postort;
    }

    public String getKommun() {
        return kommun;
    }

    public void setKommun(String kommun) {
        this.kommun = kommun;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }
}
