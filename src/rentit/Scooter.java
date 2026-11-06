package rentit;

public class Scooter extends Vehicle {

    public Scooter(String vehicleId, String model, double pricePerDay,
                   int availableCount, String fuelType,
                   String transmission, int seats, String bodyType) {

        super(vehicleId, model, pricePerDay, availableCount,
              fuelType, transmission, seats, bodyType);
    }

    @Override
    public String getVehicleType() {
        return "Scooter";
    }
}