package se.inera.privatlakarportal.service.postnummer.model;

/**
 * Created by pebe on 2015-08-12.
 */
public class Omrade {

    String postnummer;
    String postort;
    String kommun;
    String lan;

    public Omrade(String postnummer, String postort, String kommun, String lan) {
        this.postnummer = postnummer;
        this.postort = postort;
        this.kommun = kommun;
        this.lan = lan;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Omrade)) {
            return false;
        }
        Omrade other = (Omrade)o;
        return postnummer.equals(other.postnummer) &&
               postort.equals(other.postort) &&
               kommun.equals(other.kommun) &&
               lan.equals(other.lan);
    }

    @Override
    public int hashCode() {
        int result = postnummer.hashCode();
        result = 31 * result + postort.hashCode();
        result = 31 * result + kommun.hashCode();
        result = 31 * result + lan.hashCode();
        return result;
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
