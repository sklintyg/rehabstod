/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.exception;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import se.inera.intyg.rehabstod.common.exception.RehabstodErrorCodeEnum;
import se.inera.intyg.rehabstod.common.exception.RehabstodServiceException;

/**
 * Created by pebe on 2015-08-17.
 */
public final class RehabstodServiceExceptionMatcher extends TypeSafeMatcher<RehabstodServiceException> {

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
