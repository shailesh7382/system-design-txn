package com.example.liquibasecheck;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.database.jvm.JdbcConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;
import java.io.PrintWriter;

public class LiquibaseRunner {
    private static final Logger log = LoggerFactory.getLogger(LiquibaseRunner.class);

    public static void main(String[] args) {
        log.info("Starting Liquibase migration runner");

        if (args.length == 0 || args[0] == null || args[0].isBlank()) {
            log.error("No command provided. Use: update | rollback <tag|count> | status");
            System.exit(2);
        }

        String command = args[0].toLowerCase();
        // Shift args for DB config
        String[] dbArgs = new String[args.length > 1 ? args.length - 1 : 0];
        if (args.length > 1) System.arraycopy(args, 1, dbArgs, 0, args.length - 1);

        String defaultDb = "h2";
        String dbType = getArg(dbArgs, 0).orElse(System.getenv().getOrDefault("DB_TYPE", defaultDb)).toLowerCase();

        String url, user, pass, driver, changelog;
        changelog = getArg(dbArgs, 4).orElse(System.getenv().getOrDefault("CHANGELOG", "db/changelog/db.changelog-master.sql"));

        if ("clickhouse".equals(dbType)) {
            url = getArg(dbArgs, 1).orElse(System.getenv().getOrDefault("DB_URL", "jdbc:clickhouse://localhost:8123/default"));
            user = getArg(dbArgs, 2).orElse(System.getenv().getOrDefault("DB_USER", "default"));
            pass = getArg(dbArgs, 3).orElse(System.getenv().getOrDefault("DB_PASS", ""));
            driver = "com.clickhouse.jdbc.ClickHouseDriver";
        } else if ("oracle".equals(dbType)) {
            url = getArg(dbArgs, 1).orElse(System.getenv().getOrDefault("DB_URL", "jdbc:oracle:thin:@localhost:1521:xe"));
            user = getArg(dbArgs, 2).orElse(System.getenv().getOrDefault("DB_USER", "system"));
            pass = getArg(dbArgs, 3).orElse(System.getenv().getOrDefault("DB_PASS", "oracle"));
            driver = "oracle.jdbc.OracleDriver";
        } else {
            url = getArg(dbArgs, 1).orElse(System.getenv().getOrDefault("DB_URL", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"));
            user = getArg(dbArgs, 2).orElse(System.getenv().getOrDefault("DB_USER", "sa"));
            pass = getArg(dbArgs, 3).orElse(System.getenv().getOrDefault("DB_PASS", ""));
            driver = "org.h2.Driver";
        }

        log.info("Command: {}", command);
        log.info("Database type: {}", dbType);
        log.info("JDBC URL: {}", url);
        log.info("Username: {}", user);
        log.info("Changelog file: {}", changelog);
        log.debug("JDBC Driver: {}", driver);

        try {
            log.info("Loading JDBC driver: {}", driver);
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            log.error("JDBC Driver class not found: {}", driver, e);
            System.exit(2);
        }

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            log.info("Successfully connected to the database");
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));
            Liquibase liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);

            switch (command) {
                case "update":
                    log.info("Starting Liquibase update");
                    liquibase.update((String) null);
                    log.info("Migration completed successfully.");
                    break;
                case "rollback":
                    String rollbackArg = getArg(dbArgs, 5).orElse(null);
                    if (rollbackArg == null) {
                        log.error("No rollback tag or count provided.");
                        System.exit(3);
                    }
                    if (rollbackArg.matches("\\d+")) {
                        int count = Integer.parseInt(rollbackArg);
                        log.info("Rolling back last {} changesets", count);
                        liquibase.rollback(count, (String) null);
                    } else {
                        log.info("Rolling back to tag '{}'", rollbackArg);
                        liquibase.rollback(rollbackArg, (String) null);
                    }
                    log.info("Rollback completed successfully.");
                    break;
                case "status":
                    log.info("Checking for pending changesets...");
                    try (PrintWriter writer = new PrintWriter(System.out, true)) {
                        liquibase.reportStatus(true, "", writer);
                    }
                    log.info("Status check completed.");
                    break;
                default:
                    log.error("Unknown command: {}", command);
                    System.exit(4);
            }
        } catch (Exception e) {
            log.error("Liquibase operation failed: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private static Optional<String> getArg(String[] args, int idx) {
        return (args != null && args.length > idx && args[idx] != null && !args[idx].isBlank())
                ? Optional.of(args[idx])
                : Optional.empty();
    }
}
