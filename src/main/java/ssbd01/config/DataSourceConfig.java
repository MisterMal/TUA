package ssbd01.config;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.sql.Connection;

@DataSourceDefinition(
    name = "java:app/jdbc/ssbd01admin",
    className = "org.postgresql.ds.PGSimpleDataSource",
    user = "ssbd01admin",
    password = "admin",
    serverName = "ssbd_db",
    portNumber = 5432,
    databaseName = "ssbd01",
    initialPoolSize = 1,
    minPoolSize = 0,
    maxPoolSize = 1,
    maxIdleTime = 10)
@DataSourceDefinition(
    name = "java:app/jdbc/ssbd01auth",
    className = "org.postgresql.ds.PGSimpleDataSource",
    user = "ssbd01glassfish",
    password = "glassfishpassword",
    //        serverName = "postgres",
    serverName = "ssbd_db",
    portNumber = 5432,
    databaseName = "ssbd01")
@DataSourceDefinition(
    name = "java:app/jdbc/ssbd01mok",
    className = "org.postgresql.ds.PGSimpleDataSource",
    user = "ssbd01mok",
    password = "mokpassword",
    //        serverName = "postgres",
    serverName = "ssbd_db",
    portNumber = 5432,
    databaseName = "ssbd01",
    isolationLevel = Connection.TRANSACTION_READ_COMMITTED)
@DataSourceDefinition(
    name = "java:app/jdbc/ssbd01moa",
    className = "org.postgresql.ds.PGSimpleDataSource",
    user = "ssbd01moa",
    password = "moapassword",
    serverName = "ssbd_db",
    portNumber = 5432,
    databaseName = "ssbd01",
    isolationLevel = Connection.TRANSACTION_READ_COMMITTED)
@Stateless
public class DataSourceConfig {

  @PersistenceContext(unitName = "ssbd01adminPU")
  private EntityManager em;

}
