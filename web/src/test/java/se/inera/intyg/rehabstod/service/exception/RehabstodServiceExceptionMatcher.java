package se.inera.intyg.rehabstod.service.exception;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import se.inera.intyg.rehabstod.common.exception.RehabstodErrorCodeEnum;
import se.inera.intyg.rehabstod.common.exception.RehabstodServiceException;

/**
 * Created by pebe on 2015-08-17.
 */
public class RehabstodServiceExceptionMatcher extends TypeSafeMatcher<RehabstodServiceException> {

    public static RehabstodServiceExceptionMatcher hasErrorCode(RehabstodErrorCodeEnum errorCode) {
        return new RehabstodServiceExceptionMatcher(errorCode);
    }

    private RehabstodErrorCodeEnum foundErrorCode;
    private final RehabstodErrorCodeEnum expectedErrorCode;

    private RehabstodServiceExceptionMatcher(RehabstodErrorCodeEnum expectedErrorCode) {
        this.expectedErrorCode = expectedErrorCode;
    }

    @Override
    protected boolean matchesSafely(final RehabstodServiceException exception) {
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
