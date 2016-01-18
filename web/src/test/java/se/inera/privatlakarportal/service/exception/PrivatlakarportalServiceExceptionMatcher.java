package se.inera.privatlakarportal.service.exception;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalServiceException;

/**
 * Created by pebe on 2015-08-17.
 */
public class PrivatlakarportalServiceExceptionMatcher extends TypeSafeMatcher<PrivatlakarportalServiceException> {

    public static PrivatlakarportalServiceExceptionMatcher hasErrorCode(PrivatlakarportalErrorCodeEnum errorCode) {
        return new PrivatlakarportalServiceExceptionMatcher(errorCode);
    }

    private PrivatlakarportalErrorCodeEnum foundErrorCode;
    private final PrivatlakarportalErrorCodeEnum expectedErrorCode;

    private PrivatlakarportalServiceExceptionMatcher(PrivatlakarportalErrorCodeEnum expectedErrorCode) {
        this.expectedErrorCode = expectedErrorCode;
    }

    @Override
    protected boolean matchesSafely(final PrivatlakarportalServiceException exception) {
        foundErrorCode = exception.getErrorCode();
        return foundErrorCode == expectedErrorCode;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(foundErrorCode)
                .appendText(" was not found instead of ")
                .appendValue(expectedErrorCode);
    }
}
