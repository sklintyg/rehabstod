package se.inera.privatlakarportal.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.inera.privatlakarportal.persistence.model.HospUppdatering;

/**
 * Created by pebe on 2015-09-03.
 */
public interface HospUppdateringRepository extends JpaRepository<HospUppdatering, Integer> {

    @Query("SELECT h from HospUppdatering h WHERE h.id = 1")
    HospUppdatering findSingle();

}
