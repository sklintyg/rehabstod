package se.inera.privatlakarportal.hsa.stub;

import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HsaServiceStub {

    private Map<String, HsaHospPerson> personMap = new HashMap<String, HsaHospPerson>();

    private LocalDateTime hospLastUpdate;

    public HsaHospPerson getHospPerson(String personId) {
        return personMap.get(personId);
    }

    public void addHospPerson(HsaHospPerson hospPerson) {
        personMap.put(hospPerson.getPersonalIdentityNumber(), hospPerson);
        hospLastUpdate = new LocalDateTime();
    }

    public void removeHospPerson(String id) {
        personMap.remove(id);
        hospLastUpdate = new LocalDateTime();
    }

    public LocalDateTime getHospLastUpdate() {
        return hospLastUpdate;
    }
}
