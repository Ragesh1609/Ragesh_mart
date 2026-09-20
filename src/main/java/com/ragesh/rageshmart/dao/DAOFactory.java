package com.ragesh.rageshmart.dao;

import com.ragesh.rageshmart.dao.impl.*;
import com.ragesh.rageshmart.listener.DataSourceListener;

/**
 * Factory pattern (Section 12): centralizes DAO construction so servlets/services
 * depend on the DAO interfaces, never on concrete impl classes directly.
 * Production code uses the HikariCP-backed pool; tests pass a different
 * DataSourceProvider (embedded jdbc:h2:mem:test) into the *Impl constructors directly.
 */
public final class DAOFactory {

    private static final DataSourceProvider PROD_PROVIDER = DataSourceListener::getConnection;

    private DAOFactory() { }

    public static UserDAO userDAO() {
        return new UserDAOImpl(PROD_PROVIDER);
    }

    public static ProductDAO productDAO() {
        return new ProductDAOImpl(PROD_PROVIDER);
    }

    public static CartDAO cartDAO() {
        return new CartDAOImpl(PROD_PROVIDER);
    }

    public static OrderDAO orderDAO() {
        return new OrderDAOImpl(PROD_PROVIDER);
    }

    public static ReviewDAO reviewDAO() {
        return new ReviewDAOImpl(PROD_PROVIDER);
    }
}
