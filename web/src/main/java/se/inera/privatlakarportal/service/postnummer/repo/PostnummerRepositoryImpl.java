package se.inera.privatlakarportal.service.postnummer.repo;

import org.springframework.stereotype.Component;
import se.inera.privatlakarportal.service.postnummer.model.Omrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pebe on 2015-08-12.
 */
@Component
public class PostnummerRepositoryImpl implements PostnummerRepository {

    private Map<String, List<Omrade>> postnummerRepository = new HashMap<String, List<Omrade>>();

    @Override
    public List<Omrade> getOmradeByPostnummer(String postnummer) {
        return postnummerRepository.get(postnummer);
    }

    @Override
    public int nbrOfPostnummer() {
        return postnummerRepository.size();
    }

    void addPostnummer(String postnummer, Omrade omrade) {
        if (postnummerRepository.containsKey(postnummer)) {
            postnummerRepository.get(postnummer).add(omrade);
        } else {
            List<Omrade> omradeList = new ArrayList<Omrade>();
            omradeList.add(omrade);
            postnummerRepository.put(postnummer, omradeList);
        }
    }
}
