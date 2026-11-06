package rentit;

import java.io.IOException;

public abstract class Vehicle implements Rentable {
    private String vehicleId;
    private String model;
    private double pricePerDay;
    private int availableCount;
    private String fuelType;
    private String transmission;
    private int seats;
    private String bodyType;

    public Vehicle(String vehicleId, String model, double pricePerDay,
                   int availableCount, String fuelType,
                   String transmission, int seats, String bodyType) {
        this.vehicleId = vehicleId;
        this.model = model;
        this.pricePerDay = pricePerDay;
        this.availableCount = availableCount;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.seats = seats;
        this.bodyType = bodyType;
    }

    public String getVehicleId() { return vehicleId; }
    public String getModel() { return model; }
    public double getPricePerDay() { return pricePerDay; }
    public int getAvailableCount() { return availableCount; }
    public String getFuelType() { return fuelType; }
    public String getTransmission() { return transmission; }
    public int getSeats() { return seats; }
    public String getBodyType() { return bodyType; }
    public abstract String getVehicleType();

    /** Legacy standalone reservation. Normal UI bookings use BookingManager transaction. */
    public void bookVehicle() throws VehicleNotAvailableException {
        try {
            int newCount = VehicleAvailabilityStorage.reserveVehicle(this);
            if (newCount < 0) throw new VehicleNotAvailableException("Vehicle is not available for booking.");
            setAvailableCount(newCount);
        } catch (IOException e) {
            throw new VehicleNotAvailableException("Could not update vehicle availability. Please try again.");
        }
    }

    /** Legacy standalone release. Normal UI cancellations use BookingManager transaction. */
    public boolean cancelBooking() {
        try {
            setAvailableCount(VehicleAvailabilityStorage.releaseVehicle(this));
            return true;
        } catch (IOException e) {
            System.err.println("Error returning vehicle: " + e.getMessage());
            return false;
        }
    }

    void setAvailableCount(int availableCount) {
        this.availableCount = Math.max(0, availableCount);
    }

    @Override
    public void rent() {
        System.out.println("Vehicle rented: " + model);
    }
}
