package rentit;

public class Customer {

    private String name;
    private String username;
    private String password;
    private double credits;

    public Customer(String name, String username,
                    String password, double credits) {

        this.name = name;
        this.username = username;
        this.password = password;
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }
}