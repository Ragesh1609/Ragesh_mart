package com.ragesh.rageshmart.listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Section 2, rule 5: connection pool lifecycle owned by a single ServletContextListener.
 * No DriverManager.getConnection() calls anywhere else in the app (Singleton pattern -
 * Section 12). Also applies schema.sql on startup and seeds demo accounts.
 */
@WebListener
public class DataSourceListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(DataSourceListener.class);
    private static HikariDataSource dataSource;

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource not initialized - is DataSourceListener registered?");
        }
        return dataSource.getConnection();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties props = loadConfig();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url", "jdbc:h2:./data/rageshmart;DB_CLOSE_DELAY=-1"));
        config.setUsername(props.getProperty("db.user", "sa"));
        config.setPassword(props.getProperty("db.password", ""));
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.size", "10")));
        config.setPoolName("RageshMartPool");

        dataSource = new HikariDataSource(config);
        log.info("HikariCP pool initialized: {}", config.getJdbcUrl());

        runStartupSql();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null) {
            dataSource.close();
            log.info("HikariCP pool closed");
        }
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream in = DataSourceListener.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                log.warn("config.properties not found on classpath - using defaults (dev/local H2)");
            }
        } catch (IOException e) {
            log.error("Failed to load config.properties", e);
        }
        return props;
    }

    /** Applies schema.sql idempotently, then seeds demo accounts with real bcrypt hashes. */
    private void runStartupSql() {
        try (Connection conn = getConnection()) {
            String schema = readResource("schema.sql");
            try (Statement st = conn.createStatement()) {
                for (String stmt : schema.split(";")) {
                    if (!stmt.trim().isEmpty()) {
                        st.execute(stmt);
                    }
                }
            }
            SeedDataInitializer.seedIfEmpty(conn);
        } catch (Exception e) {
            log.error("Startup schema/seed execution failed", e);
        }
    }

    private String readResource(String name) throws IOException {
        try (InputStream in = DataSourceListener.class.getClassLoader().getResourceAsStream(name)) {
            if (in == null) throw new IOException("Resource not found: " + name);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
