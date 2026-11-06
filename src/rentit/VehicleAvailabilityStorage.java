package rentit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/** Shared fleet counts. INSERT IGNORE seeds a vehicle only once and never resets live stock. */
public final class VehicleAvailabilityStorage {
    private VehicleAvailabilityStorage() { }

    public static void loadAll(ArrayList<Vehicle> vehicles) {
        String seed = "INSERT IGNORE INTO vehicle_availability (vehicle_id, available_count) VALUES (?,?)";
        String read = "SELECT available_count FROM vehicle_availability WHERE vehicle_id=?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(seed)) {
                for (Vehicle vehicle : vehicles) {
                    statement.setString(1, vehicle.getVehicleId());
                    statement.setInt(2, vehicle.getAvailableCount());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
            try (PreparedStatement statement = connection.prepareStatement(read)) {
                for (Vehicle vehicle : vehicles) {
                    statement.setString(1, vehicle.getVehicleId());
                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) vehicle.setAvailableCount(result.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load vehicle availability from MySQL: " + e.getMessage(), e);
        }
    }

    /** Atomic standalone reservation for compatibility with older callers. */
    public static int reserveVehicle(Vehicle vehicle) throws IOException {
        String sql = "UPDATE vehicle_availability SET available_count=available_count-1 "
                + "WHERE vehicle_id=? AND available_count>0";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vehicle.getVehicleId());
            if (statement.executeUpdate() == 0) return -1;
            return readCount(connection, vehicle.getVehicleId());
        } catch (SQLException e) {
            throw new IOException("Could not reserve vehicle in MySQL.", e);
        }
    }

    /** Atomic standalone release for compatibility with older callers. */
    public static int releaseVehicle(Vehicle vehicle) throws IOException {
        String sql = "UPDATE vehicle_availability SET available_count=available_count+1 WHERE vehicle_id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vehicle.getVehicleId());
            if (statement.executeUpdate() == 0) throw new IOException("Vehicle was not found.");
            return readCount(connection, vehicle.getVehicleId());
        } catch (SQLException e) {
            throw new IOException("Could not release vehicle in MySQL.", e);
        }
    }

    public static void saveVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicle_availability SET available_count=? WHERE vehicle_id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, vehicle.getAvailableCount());
            statement.setString(2, vehicle.getVehicleId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not save vehicle availability.", e);
        }
    }

    private static int readCount(Connection connection, String id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT available_count FROM vehicle_availability WHERE vehicle_id=?")) {
            statement.setString(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) throw new SQLException("Vehicle availability row missing: " + id);
                return result.getInt(1);
            }
        }
    }
}
