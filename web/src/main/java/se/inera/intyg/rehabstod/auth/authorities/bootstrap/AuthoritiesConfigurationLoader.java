/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.auth.authorities.bootstrap;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConfiguration;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesException;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;

/**
 * The authorities configuration is read from a YAML file which is
 * injected into the constructor upon creating an object of this class.
 *
 * The YAML file is parsed and the resulting configuration can be fetched
 * by calling the getConfiguration() method.
 */
@Component("AuthoritiesConfigurationLoader")
public class AuthoritiesConfigurationLoader implements InitializingBean {

    private String authoritiesConfigurationFile;

    private AuthoritiesConfiguration authoritiesConfiguration;

    /**
     * Constructor taking a path to the authorities configuration file.
     */
    @Autowired
    public AuthoritiesConfigurationLoader(@Value("${authorities.configuration.file}") String authoritiesConfigurationFile) {
        Assert.notNull(authoritiesConfigurationFile,
                "Illegal argument: authoritiesConfigurationFile cannot be null. Argument must resolve to a configuration file");
        this.authoritiesConfigurationFile = authoritiesConfigurationFile;
    }

    // ~ Public scope
    // ======================================================================================================

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>
     * This method allows the bean instance to perform initialization only possible when all bean properties have been
     * set and to throw an exception in the event of misconfiguration.
     *
     * @throws Exception
     *             in the event of misconfiguration (such
     *             as failure to set an essential property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet() {

        Resource resource = getResource(authoritiesConfigurationFile);
        String path = null;

        try {
            if (resource.getURI() == null) {
                throw new AuthoritiesException("Could not load authorities configuration file. Path to file is null.");
            }

            path = resource.getURI().getPath();
            authoritiesConfiguration = loadConfiguration(Paths.get(path));
        } catch (IOException ioe) {
            throw new AuthoritiesException(format("Could not load authorities configuration file %s", path), ioe);
        }

    }

    /**
     * Gets the loaded authorities configuration.
     */
    public AuthoritiesConfiguration getConfiguration() {
        return this.authoritiesConfiguration;
    }

    // ~ Private scope
    // ======================================================================================================

    private Resource getResource(String location) {
        PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
        return r.getResource(location);
    }

    /*
     * Sonar analysis of the bytecode produced by a 'try-with-resource block' is problematic, as the bytecode produced
     * and analyzed will contain nested try catch finally blocks. Full branch coverage seem to be very hard to achieve.
     * Even though all lines below are covered, sonar reports only 2/8 branches covered for the try clause.
     */
    @SuppressWarnings("common-java:InsufficientBranchCoverage")
    private AuthoritiesConfiguration loadConfiguration(Path path) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(path)) {
            return yaml.loadAs(in, AuthoritiesConfiguration.class);
        }
    }

}
