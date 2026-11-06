package rentit;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Creates a JDBC connection to the shared RentIT MySQL database. */
public final class DatabaseConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/rentit_db?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

    private DatabaseConnection() { }

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("db.properties")) {
            properties.load(input);
        } catch (IOException ignored) {
            // Environment variables may be used instead.
        }

        String url = setting("RENTIT_DB_URL", "db.url", DEFAULT_URL, properties);
        String user = setting("RENTIT_DB_USER", "db.user", null, properties);
        String password = setting("RENTIT_DB_PASSWORD", "db.password", "", properties);

        if (user == null || user.trim().isEmpty()) {
            throw new SQLException("MySQL username missing. Configure db.properties.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String setting(String environmentName, String propertyName,
                                  String fallback, Properties properties) {
        String value = System.getenv(environmentName);
        if (value == null || value.trim().isEmpty()) {
            value = properties.getProperty(propertyName);
        }
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}
