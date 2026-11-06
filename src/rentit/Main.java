package rentit;

public class Main {

    public static void main(String[] args) {

        // Create car objects
        Car swift = new Car(
                "CAR001", "Maruti Swift", 1200, 3,
                "Petrol", "Manual", 5, "Hatchback"
        );

        Car city = new Car(
                "CAR002", "Honda City", 1800, 2,
                "Petrol", "Automatic", 5, "Sedan"
        );

        Car creta = new Car(
                "CAR003", "Hyundai Creta", 2500, 1,
                "Diesel", "Automatic", 5, "SUV"
        );

        // Create bike objects
        Bike mt15 = new Bike(
                "BIKE001", "Yamaha MT-15", 800, 2,
                "Petrol", "Manual", 2, "Street Bike"
        );

        Bike classic350 = new Bike(
                "BIKE002", "Royal Enfield Classic 350", 1000, 2,
                "Petrol", "Manual", 2, "Cruiser"
        );

        Bike duke250 = new Bike(
                "BIKE003", "KTM Duke 250", 1200, 1,
                "Petrol", "Manual", 2, "Naked Bike"
        );

        // Create scooter objects
        Scooter activa = new Scooter(
                "SCOOTER001", "Honda Activa 6G", 500, 3,
                "Petrol", "Automatic", 2, "Scooter"
        );

        Scooter jupiter = new Scooter(
                "SCOOTER002", "TVS Jupiter", 500, 2,
                "Petrol", "Automatic", 2, "Scooter"
        );

        Scooter olaS1 = new Scooter(
                "SCOOTER003", "Ola S1", 700, 2,
                "Electric", "Automatic", 2, "Electric Scooter"
        );

        // Create rental manager
        RentalManager manager = new RentalManager();

        // Add vehicles to the manager
        manager.addVehicle(swift);
        manager.addVehicle(city);
        manager.addVehicle(creta);

        manager.addVehicle(mt15);
        manager.addVehicle(classic350);
        manager.addVehicle(duke250);

        manager.addVehicle(activa);
        manager.addVehicle(jupiter);
        manager.addVehicle(olaS1);

        // Display available vehicles
        System.out.println("Available Vehicles:");
        manager.displayVehicles();

        // Find a vehicle by ID
        Vehicle foundVehicle = manager.findVehicleById("CAR001");

        if (foundVehicle != null) {
            System.out.println("Found vehicle: " + foundVehicle.getModel());
        } else {
            System.out.println("Vehicle not found.");
        }

        // Create credit wallet
        CreditWallet wallet = new CreditWallet();
        
        BookingManager bookingManager = new BookingManager();
        

        System.out.println("Starting credits: ₹" + wallet.getCredits());

        // Create a booking
        try {
        	Booking booking = new Booking(
        	        "Rahul",
        	        swift,
        	        2,
        	        "Jaipur City Center",
        	        "Jaipur Airport",
        	        wallet
        	);
        	
        	bookingManager.addBooking(booking);
        	
        	bookingManager.saveBookings();
        	
        	BookingManager loadedBookingManager = new BookingManager();

        	bookingManager.loadBookings(manager, wallet);
        	
        	System.out.println("\nLoaded Bookings From File:");
        	loadedBookingManager.displayBookings();

            System.out.println(
                    "Credits after booking: ₹" + wallet.getCredits()
            );

            System.out.println(
                    "Car available after booking: "
                    + swift.getAvailableCount()
            );

            System.out.println(
                    "Customer: " + booking.getCustomerName()
            );

            System.out.println(
                    "Vehicle: " + booking.getVehicle().getModel()
            );

            System.out.println(
                    "Days: " + booking.getDays()
            );

            System.out.println(
                    "Total price: ₹" + booking.getTotalPrice()
            );
            
            System.out.println(
                    "Pickup location: " + booking.getPickupLocation()
            );

            System.out.println(
                    "Return location: " + booking.getReturnLocation()
            );
            
            System.out.println("\nMy Bookings:");
            bookingManager.displayBookings();

            // Cancel booking
            booking.cancelBooking();

            System.out.println(
                    "Car available after cancellation: "
                    + swift.getAvailableCount()
            );

        } catch (VehicleNotAvailableException
                | InsufficientCreditsException
                | InvalidBookingException e) {

            System.out.println(e.getMessage());
        }

        // Interface polymorphism
        Rentable rentable = swift;
        rentable.rent();
    }
}