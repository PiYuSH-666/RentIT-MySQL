package rentit;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Coordinates booking, stock reservation and wallet deduction in one transaction.
 * InnoDB row updates serialize competing app instances for the same vehicle/account.
 */
public class BookingManager {
    private final ArrayList<Booking> bookings = new ArrayList<>();
    private String customerUsername;
    private RentalManager rentalManager;
    private CreditWallet currentWallet;

    public void setCustomer(String username) {
        customerUsername = username == null || username.trim().isEmpty() ? null : username.trim();
        bookings.clear();
    }

    public ArrayList<Booking> getBookings() { return new ArrayList<>(bookings); }
    public void addBooking(Booking booking) { bookings.add(booking); }
    public void removeBooking(Booking booking) { bookings.remove(booking); }

    public void displayBookings() {
        for (Booking booking : bookings) {
            System.out.println(booking.getCustomerName() + " - "
                    + booking.getVehicle().getModel() + " - " + booking.getDays()
                    + " days - ₹" + booking.getTotalPrice());
        }
    }

    public void createBooking(Booking booking, CreditWallet wallet)
            throws VehicleNotAvailableException, InsufficientCreditsException, InvalidBookingException {
        requireCustomer();
        String reserve = "UPDATE vehicle_availability SET available_count=available_count-1 "
                + "WHERE vehicle_id=? AND available_count>0";
        String debit = "UPDATE customers SET credits=credits-? WHERE username=? AND credits>=?";
        String insert = "INSERT INTO bookings (customer_username,customer_name,vehicle_id,vehicle_model,"
                + "days,pickup_location,return_location,total_price) VALUES (?,?,?,?,?,?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int remaining;
                try (PreparedStatement statement = connection.prepareStatement(reserve)) {
                    statement.setString(1, booking.getVehicle().getVehicleId());
                    if (statement.executeUpdate() != 1) {
                        throw new VehicleNotAvailableException("Vehicle is no longer available. Please refresh and try another one.");
                    }
                    remaining = readVehicleCount(connection, booking.getVehicle().getVehicleId());
                }

                try (PreparedStatement statement = connection.prepareStatement(debit)) {
                    statement.setBigDecimal(1, BigDecimal.valueOf(booking.getTotalPrice()));
                    statement.setString(2, customerUsername);
                    statement.setBigDecimal(3, BigDecimal.valueOf(booking.getTotalPrice()));
                    if (statement.executeUpdate() != 1) {
                        throw new InsufficientCreditsException("Insufficient credits to complete the booking.");
                    }
                }

                long id;
                try (PreparedStatement statement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, customerUsername);
                    statement.setString(2, booking.getCustomerName());
                    statement.setString(3, booking.getVehicle().getVehicleId());
                    statement.setString(4, booking.getVehicle().getModel());
                    statement.setInt(5, booking.getDays());
                    statement.setString(6, booking.getPickupLocation());
                    statement.setString(7, booking.getReturnLocation());
                    statement.setBigDecimal(8, BigDecimal.valueOf(booking.getTotalPrice()));
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("MySQL did not return a booking ID.");
                        id = keys.getLong(1);
                    }
                }

                double newBalance = readCredits(connection, customerUsername);
                connection.commit();

                booking.setBookingId(id);
                booking.getVehicle().setAvailableCount(remaining);
                wallet.refreshCredits(newBalance);
                currentWallet = wallet;
                bookings.add(booking);
            } catch (VehicleNotAvailableException | InsufficientCreditsException | SQLException e) {
                rollback(connection);
                if (e instanceof VehicleNotAvailableException) throw (VehicleNotAvailableException) e;
                if (e instanceof InsufficientCreditsException) throw (InsufficientCreditsException) e;
                throw new InvalidBookingException("Booking could not be saved. Please try again. " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new InvalidBookingException("Could not connect to MySQL to complete the booking.");
        }
    }

    /** Returns false when this booking was already cancelled in another app instance. */
    public boolean cancelBooking(Booking booking) {
        requireCustomer();
        String delete = "DELETE FROM bookings WHERE booking_id=? AND customer_username=?";
        String restoreVehicle = "UPDATE vehicle_availability SET available_count=available_count+1 WHERE vehicle_id=?";
        String refund = "UPDATE customers SET credits=credits+? WHERE username=?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int deleted;
                try (PreparedStatement statement = connection.prepareStatement(delete)) {
                    statement.setLong(1, booking.getBookingId());
                    statement.setString(2, customerUsername);
                    deleted = statement.executeUpdate();
                }
                if (deleted != 1) {
                    connection.rollback();
                    refreshFromStorage();
                    return false;
                }

                int count;
                try (PreparedStatement statement = connection.prepareStatement(restoreVehicle)) {
                    statement.setString(1, booking.getVehicle().getVehicleId());
                    if (statement.executeUpdate() != 1) throw new SQLException("Vehicle availability row missing.");
                }
                count = readVehicleCount(connection, booking.getVehicle().getVehicleId());

                try (PreparedStatement statement = connection.prepareStatement(refund)) {
                    statement.setBigDecimal(1, BigDecimal.valueOf(booking.getTotalPrice()));
                    statement.setString(2, customerUsername);
                    if (statement.executeUpdate() != 1) throw new SQLException("Customer account missing.");
                }
                double newBalance = readCredits(connection, customerUsername);
                connection.commit();

                booking.getVehicle().setAvailableCount(count);
                if (currentWallet != null) currentWallet.refreshCredits(newBalance);
                bookings.removeIf(item -> item.getBookingId() == booking.getBookingId());
                return true;
            } catch (SQLException e) {
                rollback(connection);
                System.err.println("Cancellation failed: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Could not connect to MySQL to cancel booking: " + e.getMessage());
            return false;
        }
    }

    /** Legacy method: all writes are now performed immediately in createBooking/cancelBooking. */
    public void saveBookings() { }

    public void loadBookings(RentalManager manager, CreditWallet wallet) {
        this.rentalManager = manager;
        this.currentWallet = wallet;
        refreshFromStorage();
    }

    /** Reloads only this customer's rows; never rewrites another customer's data. */
    public void refreshFromStorage() {
        if (customerUsername == null || rentalManager == null) return;
        ArrayList<Booking> latest = new ArrayList<>();
        String sql = "SELECT booking_id,customer_name,vehicle_id,days,pickup_location,return_location,total_price "
                + "FROM bookings WHERE customer_username=? ORDER BY booking_id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerUsername);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Vehicle vehicle = rentalManager.findVehicleById(result.getString("vehicle_id"));
                    if (vehicle != null) {
                        latest.add(new Booking(result.getLong("booking_id"),
                                result.getString("customer_name"), vehicle,
                                result.getInt("days"), result.getString("pickup_location"),
                                result.getString("return_location"), result.getDouble("total_price"),
                                currentWallet));
                    }
                }
            }
            bookings.clear();
            bookings.addAll(latest);
        } catch (SQLException e) {
            System.err.println("Could not refresh bookings: " + e.getMessage());
        }
    }

    private void requireCustomer() {
        if (customerUsername == null) throw new IllegalStateException("No logged-in customer is set.");
    }

    private static int readVehicleCount(Connection connection, String vehicleId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT available_count FROM vehicle_availability WHERE vehicle_id=?")) {
            statement.setString(1, vehicleId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) throw new SQLException("Vehicle availability row missing.");
                return result.getInt(1);
            }
        }
    }

    private static double readCredits(Connection connection, String username) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT credits FROM customers WHERE username=?")) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) throw new SQLException("Customer account missing.");
                return result.getDouble(1);
            }
        }
    }

    private static void rollback(Connection connection) {
        try { connection.rollback(); } catch (SQLException ignored) { }
    }
}
