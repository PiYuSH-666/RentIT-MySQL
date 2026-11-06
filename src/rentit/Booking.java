package rentit;

/** A booking record. Persistence and resource updates are coordinated by BookingManager. */
public class Booking {
    private long bookingId;
    private String customerName;
    private Vehicle vehicle;
    private int days;
    private double totalPrice;
    private String pickupLocation;
    private String returnLocation;
    private CreditWallet wallet;

    /** Creates a validated booking request; it does not mutate wallet or availability. */
    public Booking(String customerName, Vehicle vehicle, int days,
                   String pickupLocation, String returnLocation, CreditWallet wallet)
            throws VehicleNotAvailableException, InsufficientCreditsException, InvalidBookingException {
        validate(customerName, vehicle, days, pickupLocation, returnLocation);
        this.customerName = customerName;
        this.vehicle = vehicle;
        this.days = days;
        this.pickupLocation = pickupLocation;
        this.returnLocation = returnLocation;
        this.wallet = wallet;
        this.totalPrice = vehicle.getPricePerDay() * days;
        if (wallet != null && wallet.getCredits() < totalPrice) {
            throw new InsufficientCreditsException("Insufficient credits to complete the booking.");
        }
    }

    /** Constructor used when a booking is restored from MySQL. */
    public Booking(long bookingId, String customerName, Vehicle vehicle, int days,
                   String pickupLocation, String returnLocation, double totalPrice,
                   CreditWallet wallet) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.vehicle = vehicle;
        this.days = days;
        this.pickupLocation = pickupLocation;
        this.returnLocation = returnLocation;
        this.totalPrice = totalPrice;
        this.wallet = wallet;
    }

    /** Compatibility constructor used by older file-based project code. */
    public Booking(String customerName, Vehicle vehicle, int days,
                   String pickupLocation, String returnLocation, double totalPrice,
                   CreditWallet wallet) {
        this(0L, customerName, vehicle, days, pickupLocation, returnLocation, totalPrice, wallet);
    }

    private static void validate(String customerName, Vehicle vehicle, int days,
                                 String pickup, String returning) throws InvalidBookingException {
        if (customerName == null || customerName.trim().isEmpty() || vehicle == null || days <= 0) {
            throw new InvalidBookingException("Customer, vehicle and a positive number of days are required.");
        }
        if (pickup == null || pickup.trim().isEmpty()
                || returning == null || returning.trim().isEmpty()) {
            throw new InvalidBookingException("Pickup and return locations are required.");
        }
    }

    public long getBookingId() { return bookingId; }
    public void setBookingId(long bookingId) { this.bookingId = bookingId; }
    public String getCustomerName() { return customerName; }
    public Vehicle getVehicle() { return vehicle; }
    public int getDays() { return days; }
    public double getTotalPrice() { return totalPrice; }
    public String getPickupLocation() { return pickupLocation; }
    public String getReturnLocation() { return returnLocation; }

    /**
     * Legacy method retained for source compatibility.
     * In the MySQL version, call BookingManager.cancelBooking() instead so that
     * the booking, vehicle count and refund share one database transaction.
     */
    @Deprecated
    public void cancelBooking() {
        throw new UnsupportedOperationException("Use BookingManager.cancelBooking() for transactional cancellation.");
    }
}
