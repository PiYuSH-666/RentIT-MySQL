package rentit.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import rentit.AuthenticationManager;

public class AuthenticationFrame
        extends JFrame {

    private AuthenticationManager authenticationManager;

    private CardLayout cardLayout;
    private JPanel contentPanel;

    public AuthenticationFrame() {

        authenticationManager =
                new AuthenticationManager();

        setTitle(
                "RentIT - Welcome"
        );

        setSize(
                new Dimension(
                        1100,
                        700
                )
        );

        setMinimumSize(
                new Dimension(
                        900,
                        600
                )
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        cardLayout =
                new CardLayout();

        contentPanel =
                new JPanel(
                        cardLayout
                );

        contentPanel.setBackground(
                Color.WHITE
        );

        LoginPanel loginPanel =
                new LoginPanel(
                        authenticationManager,
                        this::openRentIT,
                        this::showSignup
                );

        SignupPanel signupPanel =
                new SignupPanel(
                        authenticationManager,
                        this::showLogin,
                        this::openRentIT
                );

        contentPanel.add(
                loginPanel,
                "LOGIN"
        );

        contentPanel.add(
                signupPanel,
                "SIGNUP"
        );

        add(contentPanel);

        cardLayout.show(
                contentPanel,
                "LOGIN"
        );
    }

    private void showSignup() {

        cardLayout.show(
                contentPanel,
                "SIGNUP"
        );
    }

    private void showLogin() {

        cardLayout.show(
                contentPanel,
                "LOGIN"
        );
    }

    private void openRentIT() {

        RentITFrame rentITFrame =
                new RentITFrame(
                        authenticationManager
                );

        rentITFrame.setVisible(true);

        dispose();
    }

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                () -> {

                    AuthenticationFrame frame =
                            new AuthenticationFrame();

                    frame.setVisible(true);
                }
        );
    }
}