package rentit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/** Customer data access. Each credit change is an atomic database update. */
public final class CustomerStorage {
    private CustomerStorage() { }

    public static ArrayList<Customer> loadCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        String sql = "SELECT name, username, password_hash, credits FROM customers ORDER BY username";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                customers.add(new Customer(
                        result.getString("name"),
                        result.getString("username"),
                        result.getString("password_hash"),
                        result.getDouble("credits")));
            }
            return customers;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load customers from MySQL: " + e.getMessage(), e);
        }
    }

    public static Customer findCustomer(String username) throws IOException {
        String sql = "SELECT name, username, password_hash, credits FROM customers WHERE username=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return null;
                return new Customer(result.getString("name"), result.getString("username"),
                        result.getString("password_hash"), result.getDouble("credits"));
            }
        } catch (SQLException e) {
            throw new IOException("Could not read customer from MySQL.", e);
        }
    }

    public static boolean addCustomer(Customer customer) throws IOException {
        String sql = "INSERT INTO customers (username, name, password_hash, credits) VALUES (?,?,?,?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customer.getUsername());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getPassword());
            statement.setDouble(4, customer.getCredits());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) return false;
            throw new IOException("Could not create customer in MySQL.", e);
        }
    }

    /**
     * Adds or deducts credits without reading and writing a stale in-memory balance.
     * A negative adjustment is rejected if the account does not have enough credits.
     */
    public static double adjustCredits(String username, double adjustment)
            throws IOException, InsufficientCreditsException {
        if (adjustment == 0) return getCredits(username);

        String update = "UPDATE customers SET credits = credits + ? "
                + "WHERE username = ? AND credits + ? >= 0";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setDouble(1, adjustment);
            statement.setString(2, username);
            statement.setDouble(3, adjustment);
            int changed = statement.executeUpdate();

            if (changed == 0) {
                if (findCustomer(username) == null) {
                    throw new IOException("Customer account was not found.");
                }
                throw new InsufficientCreditsException("Insufficient credits to complete this action.");
            }
            return getCredits(username);
        } catch (SQLException e) {
            throw new IOException("Could not update credits in MySQL.", e);
        }
    }

    public static double getCredits(String username) throws IOException {
        String sql = "SELECT credits FROM customers WHERE username=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) throw new IOException("Customer account was not found.");
                return result.getDouble("credits");
            }
        } catch (SQLException e) {
            throw new IOException("Could not read wallet balance from MySQL.", e);
        }
    }

    /** Compatibility method. It deliberately does not bulk-overwrite customer balances. */
    public static void saveCustomers(ArrayList<Customer> customers) {
        // Individual customer/account operations are saved directly to MySQL.
        // Bulk-writing a cached list could overwrite updates from another app instance.
    }

    /** Compatibility method for older project code. */
    public static void updateCredits(String username, double credits) {
        String sql = "UPDATE customers SET credits=? WHERE username=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, credits);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not update customer credits.", e);
        }
    }
}
