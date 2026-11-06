package rentit;

public class Car extends Vehicle {

    public Car(String vehicleId, String model, double pricePerDay,
               int availableCount, String fuelType,
               String transmission, int seats, String bodyType) {

        super(vehicleId, model, pricePerDay, availableCount,
              fuelType, transmission, seats, bodyType);
    }

    @Override
    public String getVehicleType() {
        return "Car";
    }
}