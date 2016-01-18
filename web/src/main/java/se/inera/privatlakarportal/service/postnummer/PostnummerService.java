package se.inera.privatlakarportal.service.postnummer;

import se.inera.privatlakarportal.service.postnummer.model.Omrade;

import java.util.List;

/**
 * Created by pebe on 2015-08-12.
 */
public interface PostnummerService {
    List<Omrade> getOmradeByPostnummer(String postnummer);
}
