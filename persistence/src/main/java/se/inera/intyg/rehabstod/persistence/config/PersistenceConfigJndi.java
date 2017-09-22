/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.persistence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiTemplate;
import se.inera.intyg.rehabstod.persistence.liquibase.DbChecker;

import javax.naming.NamingException;
import javax.sql.DataSource;

@Configuration
@Profile("!dev")
@ComponentScan("se.inera.intyg.rehabstod.persistence")
@EnableJpaRepositories(basePackages = "se.inera.intyg.rehabstod.persistence")
public class PersistenceConfigJndi extends PersistenceConfig {

    // CHECKSTYLE:OFF EmptyBlock
    @Bean(destroyMethod = "close")
    DataSource jndiDataSource() {
        DataSource dataSource = null;
        JndiTemplate jndi = new JndiTemplate();
        try {
            dataSource = (DataSource) jndi.lookup("java:comp/env/jdbc/rehabstod");
        } catch (NamingException e) {
        }
        return dataSource;
    }
    // CHECKSTYLE:ON EmptyBlock

    @Bean(name = "dbUpdate")
    DbChecker checkDb(DataSource dataSource) {
        return new DbChecker(dataSource, "changelog/changelog.xml");
    }
}
