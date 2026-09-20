package com.ragesh.rageshmart.dao;

import com.ragesh.rageshmart.dao.impl.UserDAOImpl;
import com.ragesh.rageshmart.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Section 9: DAO tests execute against an embedded H2 instance
 * (jdbc:h2:mem:test;DB_CLOSE_DELAY=-1), initialized from schema.sql per test run.
 */
class UserDAOImplTest {

    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private Connection sharedConnection; // keeps the in-memory DB alive for the test
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws Exception {
        sharedConnection = DriverManager.getConnection(JDBC_URL, "sa", "");
        applySchema(sharedConnection);
        userDAO = new UserDAOImpl(() -> DriverManager.getConnection(JDBC_URL, "sa", ""));
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement st = sharedConnection.createStatement()) {
            st.execute("DROP ALL OBJECTS");
        }
        sharedConnection.close();
    }

    @Test
    void insertAndFindById_roundTrips() {
        User user = newUser("alice@example.com");
        User saved = userDAO.insert(user);

        assertNotNull(saved.getId());
        Optional<User> found = userDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("alice@example.com", found.get().getEmail());
        assertEquals(User.Role.BUYER, found.get().getRole());
    }

    @Test
    void findByEmail_caseAndExistence() {
        userDAO.insert(newUser("bob@example.com"));

        assertTrue(userDAO.existsByEmail("bob@example.com"));
        assertFalse(userDAO.existsByEmail("nobody@example.com"));
        assertTrue(userDAO.findByEmail("bob@example.com").isPresent());
    }

    @Test
    void duplicateEmail_violatesUniqueConstraint() {
        userDAO.insert(newUser("dup@example.com"));
        assertThrows(RuntimeException.class, () -> userDAO.insert(newUser("dup@example.com")));
    }

    @Test
    void findAll_returnsInsertedUsers() {
        userDAO.insert(newUser("u1@example.com"));
        userDAO.insert(newUser("u2@example.com"));
        assertEquals(2, userDAO.findAll().size());
    }

    private User newUser(String email) {
        User user = new User();
        user.setName("Test User");
        user.setEmail(email);
        user.setPasswordHash("dummy-hash-not-used-in-this-test");
        user.setRole(User.Role.BUYER);
        return user;
    }

    private void applySchema(Connection conn) throws SQLException, IOException {
        String schema = readResource("schema.sql");
        try (Statement st = conn.createStatement()) {
            for (String stmt : schema.split(";")) {
                if (!stmt.trim().isEmpty()) {
                    st.execute(stmt);
                }
            }
        }
    }

    private String readResource(String name) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(name)) {
            assertNotNull(in, "schema.sql must be on the test classpath");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
