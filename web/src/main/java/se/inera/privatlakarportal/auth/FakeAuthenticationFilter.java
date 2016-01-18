package se.inera.privatlakarportal.auth;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author andreaskaltenbach
 */
public class FakeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(FakeAuthenticationFilter.class);

    protected FakeAuthenticationFilter() {
        super("/fake");
        LOG.error("FakeAuthentication enabled. DO NOT USE IN PRODUCTION");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        }
        String parameter = request.getParameter("userJsonDisplay");
        // we manually encode the json parameter
        String json = URLDecoder.decode(parameter, "UTF-8");

        return performFakeElegAuthentication(json);
    }

    private Authentication performFakeElegAuthentication(String json) {
        try {
            FakeElegCredentials fakeElegCredentials = new ObjectMapper().readValue(json, FakeElegCredentials.class);
            LOG.info("Detected fake credentials " + fakeElegCredentials);
            return getAuthenticationManager().authenticate(new FakeElegAuthenticationToken(fakeElegCredentials));
        } catch (IOException e) {
            String message = "Failed to parse JSON for fake E-leg: " + json;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

}
