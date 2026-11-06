# RentIT — MySQL Vehicle Rental System

## Repository contents

This repository contains the MySQL-integrated version of RentIT, a Java Swing vehicle-rental application developed for CSC2104. The submission includes the Java source code, required application resources, the MySQL database setup script (`rentit_mysql_setup.sql`), a database configuration template (`db.properties.example`), and this documentation. The actual `db.properties` file is intentionally excluded because it contains machine-specific database credentials.


**Course:** CSC2104 — Object Oriented Programming  
**Project:** RentIT — Vehicle Rental Management System  
**Application type:** Java desktop application using Swing  
**Database:** MySQL through JDBC (MySQL Connector/J)

## 1. Project overview

RentIT is a Java OOP mini-project for browsing and renting cars, bikes and scooters. Customers can create an account, log in, view vehicles, make bookings, pay using wallet credits, view their bookings and cancel bookings. The desktop interface is built with Java Swing. Customer accounts, wallet credits, bookings and shared vehicle availability are stored in a MySQL database so that data remains available after the application closes and can be shared by multiple RentIT application instances connected to the same database.

The vehicle catalogue (vehicle names, models, prices and related details) is defined in the Java application. MySQL stores the shared available quantity for each vehicle ID, along with customer and booking records.

## 2. Main features

- Customer sign-up and login.
- Browse cars, bikes and scooters.
- View vehicle information and daily rental rates.
- Create bookings for a selected number of days.
- Calculate the total rental price.
- Pay using the customer's RentIT wallet credits.
- View the logged-in customer's bookings.
- Cancel a booking and receive the corresponding wallet refund.
- Store customer, wallet, booking and vehicle-availability data in MySQL.
- Use database transactions and conditional updates to protect shared booking data when multiple application instances use the same database.

## 3. Technologies and tools

- Java (JDK compatible with the source code).
- Eclipse IDE for Java development.
- Java Swing and AWT for the graphical user interface and layouts.
- MySQL Server for relational data storage.
- MySQL Workbench as a graphical tool for running SQL scripts and inspecting tables.
- JDBC (Java Database Connectivity) to connect Java to MySQL.
- MySQL Connector/J as the JDBC driver.
- Java collections, including `ArrayList`, for in-memory object management.

All software used for this student project can be used without a paid subscription. MySQL Workbench is a database client; the MySQL Server service must also be running for the application to connect to a local database.

## 4. Object-oriented programming concepts demonstrated

| OOP concept | How it appears in RentIT |
|---|---|
| Classes and objects | `Vehicle`, `Car`, `Bike`, `Scooter`, `Booking`, `Customer` and manager/storage classes. |
| Encapsulation | Object state is held in fields and accessed or changed through methods. |
| Inheritance | `Car`, `Bike` and `Scooter` extend the common `Vehicle` class. |
| Abstraction | `Vehicle` provides a common representation and shared vehicle behaviour. |
| Interface | `Rentable` defines a rental-related contract. |
| Polymorphism | Different vehicle subclasses can be handled through a common `Vehicle` reference. |
| Composition / has-a relationship | A `Booking` is associated with a vehicle and wallet-related booking operations. |
| Collections | `ArrayList` is used to manage collections of vehicles and bookings. |
| Exception handling | Booking and validation errors are handled using exceptions. |
| Custom exceptions | `InvalidBookingException`, `InsufficientCreditsException` and `VehicleNotAvailableException`. |
| Separation of responsibilities | UI, business logic, storage and database connection responsibilities are separated into classes/packages. |

## 5. Database design

The setup script `rentit_mysql_setup.sql` creates the database `rentit_db` and these three InnoDB tables:

### `customers`

Stores customer account details:

- `username` — primary key.
- `name` — customer's display name.
- `password_hash` — stored password hash, not the entered plain-text password.
- `credits` — wallet balance stored as `DECIMAL`.

### `vehicle_availability`

Stores shared stock counts:

- `vehicle_id` — primary key matching a vehicle ID in the Java catalogue.
- `available_count` — number of currently available units.

A check constraint is included to disallow negative counts on MySQL versions that enforce `CHECK` constraints. The booking code also uses a conditional update that only decrements a row when `available_count > 0`.

### `bookings`

Stores rental booking records:

- `booking_id` — auto-increment primary key.
- `customer_username` — foreign key referencing `customers.username`.
- Customer name, vehicle ID and model, rental days, pickup and return locations, total price and creation timestamp.

The SQL script uses `CREATE DATABASE IF NOT EXISTS` and `CREATE TABLE IF NOT EXISTS`. It creates missing database objects and does not intentionally delete existing rows.

## 6. Setup requirements

The computer running RentIT must have the following software and configuration:

1. A compatible JDK.
2. Eclipse IDE for Java Developers (or another Java IDE).
3. MySQL Server and a MySQL account with permission to create/use the `rentit_db` database and its tables.
4. MySQL Workbench (recommended for easily executing the setup script; another MySQL client can also be used).
5. MySQL Connector/J JAR added to the Java project's build path/module path as appropriate.

**Important:** `localhost` means the computer on which RentIT is running. A database hosted on one student's PC is not automatically available on a lecturer's or another student's computer. To run the project on another computer, that computer needs its own MySQL setup and database, or the application must be configured to connect to an accessible shared MySQL server.

## 7. Create the database

1. Start the MySQL Server service.
2. Open MySQL Workbench and connect to the local MySQL instance.
3. Open `rentit_mysql_setup.sql`.
4. Execute the complete script using the lightning-bolt Execute button.
5. Refresh the SCHEMAS panel.
6. Confirm that `rentit_db` contains `customers`, `vehicle_availability` and `bookings`.

The database tables can be verified with:

```sql
USE rentit_db;
SHOW TABLES;
SELECT * FROM customers;
SELECT * FROM vehicle_availability ORDER BY vehicle_id;
SELECT * FROM bookings ORDER BY booking_id;
```

The database is initially created without customer accounts or bookings. A customer account can be registered through the RentIT sign-up screen. The application initializes the availability rows for its vehicle catalogue when it starts; do not manually reset availability while real/demo bookings exist unless you are intentionally resetting all related test data consistently.

## 8. Add MySQL Connector/J in Eclipse

1. Download the free MySQL Connector/J ZIP from the official MySQL downloads page: <https://dev.mysql.com/downloads/connector/j/>.
2. Extract the ZIP and locate the `mysql-connector-j-...jar` file.
3. In Eclipse, right-click the RentIT project in Package Explorer.
4. Select **Build Path → Configure Build Path...**.
5. Open the **Libraries** tab and choose the appropriate module path/classpath used by the project.
6. Click **Add External JARs...**, select the Connector/J JAR, then click **Apply and Close**.

Do not put the Connector/J JAR inside the `src` source folder.

If the project contains `module-info.java`, it should include the required modules used by the project, including:

```java
module RentIT {
    requires java.desktop;
    requires java.sql;
}
```

If the existing module descriptor has additional valid requirements or exports, preserve them rather than replacing the whole file blindly.

## 9. Configure the database connection (important)

The repository contain `db.properties.example`, should rename it to `db.properties` file and follow rest of steps.

1. Copy `db.properties.example`.
2. Rename the copy to exactly `db.properties` (make sure Windows has not added `.txt`).
3. Edit `db.user` and `db.password` to match the MySQL account on the computer running RentIT.
4. Put `db.properties` in the project root, beside `.project` and `src`.
5. Keep `db.properties` excluded in `.gitignore`. Never commit or share the file containing your real database password.

Example configuration:

```properties
db.url=jdbc:mysql://localhost:3306/rentit_db?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

Replace the example password with the credentials configured on the computer running RentIT. If the MySQL account has no password, leave `db.password=` blank. Database credentials must not be included in the repository or screenshots.

`DatabaseConnection` reads `db.properties` from the application's working directory. It also supports environment variables `RENTIT_DB_URL`, `RENTIT_DB_USER` and `RENTIT_DB_PASSWORD`. Environment variables take precedence over values in the properties file.

## 10. Import and run in Eclipse

### Import an existing project

1. Extract the downloaded project folder to the computer where RentIT will be run.
2. Open Eclipse IDE.
3. Select **File → Import... → General → Existing Projects into Workspace**.
4. Choose **Select root directory** and browse to the folder containing `.project`.
5. Select the detected RentIT project and click **Finish**.
6. If Eclipse reports build-path errors, confirm the JDK and Connector/J library are configured.
7. Refresh the project (select it and press F5) and check the Problems tab for compilation errors.

### Set the working directory

1. Open `rentit.ui.AuthenticationFrame.java` in the `src` folder.
2. Right-click inside the editor and choose **Run As → Run Configurations...**.
3. Select the Java Application configuration for `AuthenticationFrame`.
4. Open the **Arguments** tab.
5. Under **Working directory**, select **Other...** and choose the RentIT project root folder (the folder containing `db.properties`).
6. Click **Apply**.

### Start the application

Run `rentit.ui.AuthenticationFrame` as a **Java Application**. This is the Swing GUI entry point. `rentit.Main` is a console demonstration, not the normal GUI entry point.

Create a new account from the sign-up screen, then log in. The MySQL database must be running and the connection settings must be correct.

## 11. Booking transaction and multi-instance safety

The booking workflow in `BookingManager.createBooking` uses one InnoDB transaction:

1. Decrease a vehicle's available count only when the count is greater than zero.
2. Deduct wallet credits only when the customer has enough credits.
3. Insert the booking record.
4. Commit the transaction only after all operations succeed; otherwise roll back.

The conditional stock update is important when two application instances attempt to book the last available unit. The database coordinates updates to the shared row, so both instances cannot successfully decrement a count that is already zero.

Cancellation also uses a transaction: it deletes the specific booking for the logged-in customer, restores one unit of availability and refunds the corresponding credits. If the booking row was already deleted by another instance, the cancellation does not apply a second refund or stock restoration.

This design protects shared database records across separate RentIT processes. A Java `synchronized` method alone would not coordinate separate application processes. Multi-instance behaviour should be demonstrated using the test steps below; do not claim a test passed unless it was actually run.

## 12. Testing checklist

### Basic functional tests

- [ ] Eclipse builds with no errors.
- [ ] MySQL Server is running and the GUI opens.
- [ ] Sign up a new test customer.
- [ ] Confirm the customer row exists in `customers`.
- [ ] Close and reopen RentIT; log in using the same account.
- [ ] Confirm the wallet balance is retained.
- [ ] Book an available vehicle.
- [ ] Confirm a row appears in `bookings`, wallet credits decrease and availability decreases by one.
- [ ] Cancel the booking.
- [ ] Confirm the booking row is deleted, credits are refunded and availability increases by one.
- [ ] Restart RentIT and confirm persisted values remain correct.

### Two-instance concurrency test

1. Use a test vehicle and set its `available_count` to `1` in Workbench.
2. Create two different test customer accounts with enough wallet credits.
3. Launch two separate RentIT application processes and log in with the different accounts.
4. Have both customers attempt to book the same vehicle at nearly the same time.
5. Expect only one booking to succeed. The other should receive an unavailable-vehicle message.
6. Check the database:

```sql
SELECT * FROM vehicle_availability WHERE vehicle_id = 'CAR001';
SELECT * FROM bookings WHERE vehicle_id = 'CAR001';
SELECT username, credits FROM customers;
```

7. Confirm the available count is zero, only one booking was created for that last unit, and only the successful customer's credits were deducted.
8. Cancel the successful booking. If another window still shows the stale booking, attempt cancellation there and confirm it does not refund or restore stock a second time.

Use test accounts and test data. Avoid resetting stock counts while existing bookings are active, because manually changing stock without reconciling bookings can make the data inconsistent.

## 13. Troubleshooting

| Problem | What to check |
|---|---|
| `No suitable driver` | MySQL Connector/J JAR is missing from the project's build path/module path. |
| `Access denied` | Check the MySQL username and password in the local `db.properties`. |
| `Unknown database 'rentit_db'` | Run `rentit_mysql_setup.sql` in MySQL Workbench. |
| Connection refused / communications link failure | Start MySQL Server and confirm that Workbench can connect to the same host and port. |
| `db.properties` not found / username missing | Check its exact filename, project-root location and Eclipse working directory. |
| Project does not compile | Check the JDK, `module-info.java`, Connector/J library and Eclipse Problems tab. |

## 14. Security and project scope

- Do not upload `db.properties` with real credentials. Upload only `db.properties.example`.
- Do not use the MySQL `root` account for a deployed public service. For a real deployment, create a restricted database user and keep credentials on a protected server.
- The project retains the course-level password hashing implementation. SHA-256 demonstrates hashing but is not a recommended standalone password-storage method for production systems; production systems should use a password-hashing algorithm such as Argon2id or bcrypt with appropriate salts and parameters.
- RentIT is an educational desktop project. It is not a production payment platform or a hosted commercial rental service.
- The MySQL database is local unless deliberately configured otherwise. Sharing the source repository does not share the database contents or make a local database remotely accessible.

## 15. Academic project summary

RentIT demonstrates how Java OOP concepts can be combined with a Swing graphical interface and persistent relational storage. Java classes model customers, vehicles and bookings; inheritance and polymorphism support different vehicle types; the `Rentable` interface represents rental behaviour; manager and storage classes separate responsibilities; custom exceptions report expected booking errors; and JDBC connects the application to MySQL. InnoDB transactions keep booking, wallet and vehicle-availability updates consistent when operations succeed or fail.
