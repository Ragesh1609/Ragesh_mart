package com.ragesh.rageshmart.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Thin seam between DAOs and the connection pool so DAOs never call
 * DriverManager.getConnection() directly and so unit tests can swap in an
 * embedded jdbc:h2:mem:test provider without touching production wiring.
 */
public interface DataSourceProvider {
    Connection getConnection() throws SQLException;
}
