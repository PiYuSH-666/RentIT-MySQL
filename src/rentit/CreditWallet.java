package rentit;

import java.io.IOException;

/** Wallet facade. Logged-in balances are always persisted in MySQL. */
public class CreditWallet {
    private double credits;
    private String username;

    public CreditWallet() {
        this(5000);
    }

    public CreditWallet(double startingCredits) {
        credits = startingCredits;
        username = null;
    }

    public CreditWallet(double startingCredits, String username) {
        credits = startingCredits;
        this.username = username;
    }

    public double getCredits() {
        if (username != null) refreshSavedCredits();
        return credits;
    }

    public boolean addCredits(double amount) {
        if (amount <= 0) return false;
        if (username == null) {
            credits += amount;
            return true;
        }
        try {
            credits = CustomerStorage.adjustCredits(username, amount);
            return true;
        } catch (IOException | InsufficientCreditsException e) {
            System.err.println("Could not add credits: " + e.getMessage());
            return false;
        }
    }

    public void deductCredits(double amount) throws InsufficientCreditsException {
        if (amount <= 0) return;
        if (username == null) {
            if (amount > credits) {
                throw new InsufficientCreditsException("Insufficient credits to complete the booking.");
            }
            credits -= amount;
            return;
        }
        try {
            credits = CustomerStorage.adjustCredits(username, -amount);
        } catch (IOException e) {
            throw new InsufficientCreditsException("Could not save your balance. Please try again.");
        }
    }

    public void refreshCredits(double currentCredits) {
        credits = currentCredits;
    }

    public void refreshSavedCredits() {
        if (username == null || username.trim().isEmpty()) return;
        try {
            credits = CustomerStorage.getCredits(username);
        } catch (IOException e) {
            System.err.println("Could not refresh wallet: " + e.getMessage());
        }
    }

    public String getUsername() {
        return username;
    }
}
