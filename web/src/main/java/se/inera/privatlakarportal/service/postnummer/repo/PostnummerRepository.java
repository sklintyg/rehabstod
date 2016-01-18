package se.inera.privatlakarportal.service.postnummer.repo;

import se.inera.privatlakarportal.service.postnummer.model.Omrade;

import java.util.List;

/**
 * Created by pebe on 2015-08-12.
 */
public interface PostnummerRepository {
    List<Omrade> getOmradeByPostnummer(String postnummer);

    int nbrOfPostnummer();
}
