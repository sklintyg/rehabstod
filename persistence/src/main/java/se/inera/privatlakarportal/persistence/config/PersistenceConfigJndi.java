package se.inera.privatlakarportal.persistence.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiTemplate;

import se.inera.privatlakarportal.persistence.liquibase.DbChecker;

@Configuration
@Profile("!dev")
@ComponentScan("se.inera.privatlakarportal.persistence")
@EnableJpaRepositories(basePackages = "se.inera.privatlakarportal.persistence")
public class PersistenceConfigJndi extends PersistenceConfig {

    @Bean(destroyMethod = "close")
    DataSource jndiDataSource() {
        DataSource dataSource = null;
        JndiTemplate jndi = new JndiTemplate();
        try {
            dataSource = (DataSource) jndi.lookup("java:comp/env/jdbc/privatlakarportal");
        } catch (NamingException e) {

        }
        return dataSource;
    }

    @Bean(name = "dbUpdate")
    DbChecker checkDb(DataSource dataSource) {
        return new DbChecker(dataSource, "changelog/changelog.xml");
    }
}
