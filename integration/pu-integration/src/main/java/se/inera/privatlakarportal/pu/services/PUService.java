package se.inera.privatlakarportal.pu.services;

import com.google.common.annotations.VisibleForTesting;
import se.inera.privatlakarportal.pu.model.PersonSvar;

public interface PUService {

    PersonSvar getPerson(String personId);

    @VisibleForTesting
    void clearCache();
}
