/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.web.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.logging.HashPatientIdHelper;
import se.inera.intyg.rehabstod.service.user.UserService;

/**
 * Created by marced on 2016-08-23.
 */
public class PdlConsentGivenAssuranceFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PdlConsentGivenAssuranceFilter.class);

    @Autowired
    private UserService userService;

    private String ignoredUrls;
    private List<String> ignoredUrlsList;

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        if (ignoredUrls == null) {
            LOG.warn("No ignored Urls are configured!");
        } else {
            ignoredUrlsList = Arrays.asList(ignoredUrls.split(","));
            LOG.info("Configured ignored urls as:" + ignoredUrlsList.stream().map(Object::toString).collect(Collectors.joining(", ")));
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException {
        LOG.error("User accessed " + HashPatientIdHelper.fromUrl(request.getRequestURI()) + " but has not given consent to PDL logging.");
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        RehabstodUser user = getUser();
        boolean shouldNotFilterIf = (user != null && user.isPdlConsentGiven()) || isIgnoredUrl(request);
        LOG.debug("shouldNotFilter " + HashPatientIdHelper.fromUrl(request.getRequestURI()) + " = " + shouldNotFilterIf);
        return shouldNotFilterIf;
    }

    private boolean isIgnoredUrl(HttpServletRequest request) {
        String url = request.getRequestURI();
        boolean shouldIgnore = ignoredUrlsList.stream().filter(s -> url.contains(s)).count() > 0;
        LOG.debug("shouldIgnore " + url + " = " + shouldIgnore);
        return shouldIgnore;
    }

    public String getIgnoredUrls() {
        return ignoredUrls;
    }

    public void setIgnoredUrls(String ignoredUrls) {
        this.ignoredUrls = ignoredUrls;
    }

    private RehabstodUser getUser() {
        return userService.getUser();
    }
}
