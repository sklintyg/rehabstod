package se.inera.privatlakarportal.persistence.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by pebe on 2015-09-09.
 */
@Entity
@Table(name = "MEDGIVANDETEXT")
public class MedgivandeText {

    @Id
    @Column(name = "VERSION")
    private Long version;

    @Lob
    @Column(name = "MEDGIVANDE_TEXT")
    private String medgivandeText;

    @Column(name = "DATUM", nullable = true)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime datum;

    @JsonManagedReference(value = "medgivandeText")
    @OneToMany(mappedBy="medgivandeText", cascade = CascadeType.ALL)
    private Set<Medgivande> medgivande;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MedgivandeText that = (MedgivandeText) o;

        if (version == null) {
            return false;
        } else {
            return version.equals(that.version);
        }
    }

    @Override
    public int hashCode() {
        return version != null ? version.hashCode() : 0;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getMedgivandeText() {
        return medgivandeText;
    }

    public void setMedgivandeText(String medgivandeText) {
        this.medgivandeText = medgivandeText;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }

    public Set<Medgivande> getMedgivande() {
        return medgivande;
    }

    public void setMedgivande(Set<Medgivande> medgivande) {
        this.medgivande = medgivande;
    }
}
