package se.inera.privatlakarportal.persistence.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@Profile("dev")
@ComponentScan("se.inera.privatlakarportal.persistence")
@EnableJpaRepositories(basePackages = "se.inera.privatlakarportal.persistence")
public class PersistenceConfigDev extends PersistenceConfig {

    @Value("${db.driver}")
    private String databaseDriver;
    @Value("${db.url}")
    private String databaseUrl;
    @Value("${db.username}")
    private String databaseUsername;
    @Value("${db.password}")
    private String databasePassword;
    @Value("${db.httpPort}")
    private String databaseHttpPort;
    @Value("${db.tcpPort}")
    private String databaseTcpPort;

    @Bean(destroyMethod = "stop")
    Server createTcpServer() throws SQLException {
        Server server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", databaseTcpPort);
        server.start();
        return server;
    }

    @Bean(destroyMethod = "stop")
    Server createWebServer() throws SQLException {
        Server server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", databaseHttpPort);
        server.start();
        return server;
    }

    @Bean(destroyMethod = "close")
    DataSource standaloneDataSource() {
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(databaseDriver);
        dataSourceConfig.setJdbcUrl(databaseUrl);
        dataSourceConfig.setUsername(databaseUsername);
        dataSourceConfig.setPassword(databasePassword);

        return new HikariDataSource(dataSourceConfig);
    }

    @Bean(name = "dbUpdate")
    SpringLiquibase initDb(DataSource dataSource) {
        SpringLiquibase springLiquibase = new SpringLiquibase();
        springLiquibase.setDataSource(dataSource);
        springLiquibase.setChangeLog("classpath:changelog/changelog.xml");
        return springLiquibase;
    }
}
