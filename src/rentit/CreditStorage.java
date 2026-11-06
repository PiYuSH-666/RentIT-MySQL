package rentit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CreditStorage {

    private static final String FILE_NAME = "credits.txt";

    // Save the current wallet balance
    public static void saveCredits(CreditWallet wallet) {

        try (FileWriter writer = new FileWriter(FILE_NAME)) {

            writer.write(
                    String.valueOf(wallet.getCredits())
            );

        } catch (IOException e) {

            System.out.println(
                    "Error saving credits: "
                    + e.getMessage()
            );
        }
    }

    // Load the saved balance.
    // If no saved balance exists, return the default ₹5000.
    public static double loadCredits() {

        try (
            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(FILE_NAME)
                    )
        ) {

            String line = reader.readLine();

            if (line != null && !line.trim().isEmpty()) {

                return Double.parseDouble(
                        line.trim()
                );
            }

        } catch (IOException e) {

            // No credits.txt yet.
            // First launch uses the default balance.

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid credit data. "
                    + "Using default credits."
            );
        }

        return 5000;
    }
}