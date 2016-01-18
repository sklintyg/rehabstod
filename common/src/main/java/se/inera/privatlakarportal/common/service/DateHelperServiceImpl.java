package se.inera.privatlakarportal.common.service;

import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * Created by pebe on 2015-09-10.
 */
@Service
public class DateHelperServiceImpl implements DateHelperService {

    /*
     * Used for mocking now() in unit tests
     */
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
