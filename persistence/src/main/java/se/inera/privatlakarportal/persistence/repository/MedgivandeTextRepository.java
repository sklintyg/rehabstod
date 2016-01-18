package se.inera.privatlakarportal.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.inera.privatlakarportal.persistence.model.MedgivandeText;

/**
 * Created by pebe on 2015-09-09.
 */
public interface MedgivandeTextRepository extends JpaRepository<MedgivandeText, Long> {

    @Query("SELECT m from MedgivandeText m WHERE m.version = (SELECT MAX(m2.version) FROM MedgivandeText m2)")
    MedgivandeText findLatest();
}
