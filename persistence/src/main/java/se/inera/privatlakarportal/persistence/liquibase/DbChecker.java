package se.inera.privatlakarportal.persistence.liquibase;

import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.List;

public class DbChecker {
    
    private static final Logger LOG = LoggerFactory.getLogger(DbChecker.class);

    private DataSource dataSource;
    
    private String script;
    
    public DbChecker(DataSource dataSource, String script) {
        super();
        this.dataSource = dataSource;
        this.script = script;
    }

    @PostConstruct
    public void checkDb() {
        DatabaseConnection connection = null;
        try {
            connection = new JdbcConnection(dataSource.getConnection());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
            Liquibase liquibase = new Liquibase(script, new ClassLoaderResourceAccessor(), database);
            LOG.info("Checking database: {} URL:{}", database.getDatabaseProductName(), database.getConnection().getURL());
            List<ChangeSet> changeSets = liquibase.listUnrunChangeSets(null, null);
            if (!changeSets.isEmpty()) {
                StringBuilder errors = new StringBuilder();
                for (ChangeSet changeSet : changeSets) {
                    errors.append('>').append(changeSet.toString()).append('\n');
                }
                throw new Error("Database version mismatch. Check liquibase status. Errors:\n" + errors.toString() + database.getDatabaseProductName() + ", " + database);
            }
        } catch (liquibase.exception.LiquibaseException e) {
            throw new Error("Database not ok, aborting startup.", e);
        } catch (SQLException e) {
            throw new Error("Database not ok, aborting startup.", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (DatabaseException e) {
                    LOG.info("Could not close DatabaseConnection in DbChecker, {}", e);
                    throw new Error("Could not close DatabaseConnection in DbChecker", e);
                }
            }
        }
        LOG.info("Liquibase ok");
    }
}
