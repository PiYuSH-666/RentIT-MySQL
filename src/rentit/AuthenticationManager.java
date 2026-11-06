package rentit;

import java.io.IOException;

/** Handles account sign-up and login using the MySQL customer table. */
public class AuthenticationManager {
    private Customer currentCustomer;
    private CreditWallet currentWallet;

    public AuthenticationManager() {
        currentCustomer = null;
        currentWallet = null;
    }

    public boolean signUp(String name, String username, String password) {
        if (name == null || name.trim().isEmpty()
                || username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            return false;
        }

        String cleanUsername = username.trim();
        Customer customer = new Customer(name.trim(), cleanUsername,
                PasswordUtil.hashPassword(password), 5000.00);
        try {
            if (!CustomerStorage.addCustomer(customer)) return false;
            currentCustomer = customer;
            currentWallet = new CreditWallet(customer.getCredits(), cleanUsername);
            return true;
        } catch (IOException e) {
            System.err.println("Sign-up failed: " + e.getMessage());
            return false;
        }
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) return false;
        try {
            Customer customer = CustomerStorage.findCustomer(username.trim());
            if (customer == null) return false;
            String enteredHash = PasswordUtil.hashPassword(password);
            if (!customer.getPassword().equals(enteredHash)) return false;

            currentCustomer = customer;
            currentWallet = new CreditWallet(customer.getCredits(), customer.getUsername());
            return true;
        } catch (IOException e) {
            System.err.println("Login failed: " + e.getMessage());
            return false;
        }
    }

    /** Database writes happen immediately; never save a stale cached account list on logout. */
    public void saveCurrentCustomer() {
        if (currentWallet != null) currentWallet.refreshSavedCredits();
    }

    public void logout() {
        saveCurrentCustomer();
        currentCustomer = null;
        currentWallet = null;
    }

    public Customer getCurrentCustomer() { return currentCustomer; }
    public CreditWallet getCurrentWallet() { return currentWallet; }
    public boolean isLoggedIn() { return currentCustomer != null; }
}
