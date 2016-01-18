package se.inera.privatlakarportal.hsa.services;

import org.joda.time.LocalDateTime;
import se.inera.ifv.hsawsresponder.v3.GetHospPersonResponseType;

public interface HospPersonService {

    boolean addToCertifier(String personId, String certifierId);

    boolean removeFromCertifier(String personId, String certifierId, String reason);

    GetHospPersonResponseType getHospPerson(String personId);

    LocalDateTime getHospLastUpdate();
}
