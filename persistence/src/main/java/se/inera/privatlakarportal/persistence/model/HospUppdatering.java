package se.inera.privatlakarportal.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

/**
 * Created by pebe on 2015-09-03.
 */
@Entity
@Table(name = "HOSP_UPPDATERING")
public class HospUppdatering {

    @SuppressWarnings("unused")
    @Id
    @Column(name="ID")
    private Long id;

    @Column(name = "SENASTE_HOSP_UPPDATERING", nullable = true)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime senasteHospUppdatering;

    public HospUppdatering() {
        id = 1L;
    }

    public HospUppdatering(LocalDateTime senasteHospUppdatering) {
        id = 1L;
        this.senasteHospUppdatering = senasteHospUppdatering;
    }

    public LocalDateTime getSenasteHospUppdatering() {
        return senasteHospUppdatering;
    }

    public void setSenasteHospUppdatering(LocalDateTime senasteHospUppdatering) {
        this.senasteHospUppdatering = senasteHospUppdatering;
    }
}
