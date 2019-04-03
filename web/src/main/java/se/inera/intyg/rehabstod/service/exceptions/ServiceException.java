/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.exceptions;

/**
 * Abstract superclass for all exceptions related to a Service object
 * being invalid for whatever reason.
 *
 * @author by Magnus Ekstrand on 2016-04-12.
 */
public abstract class ServiceException extends RuntimeException {

    // ~ Constructors
    // ===================================================================================================

    /**
     * Constructs an {@code ServiceException} with the specified message and root
     * cause.
     *
     * @param msg the detail message
     * @param t the root cause
     */
    public ServiceException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * Constructs an {@code ServiceException} with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    public ServiceException(String msg) {
        super(msg);
    }

}
