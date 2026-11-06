package rentit.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import rentit.Booking;
import rentit.BookingManager;
import rentit.CreditWallet;

public class BookingsPanel extends JPanel {

    // RentIT brand colors
    private final Color RENTIT_BLUE =
            new Color(22, 119, 232);

    private final Color RENTIT_NAVY =
            new Color(17, 24, 39);

    private final Color TEXT_GRAY =
            new Color(100, 110, 125);

    private final Color BORDER_BLUE =
            new Color(216, 231, 248);

    private BookingManager bookingManager;
    private JPanel bookingsList;
    private CreditWallet wallet;
    private JLabel creditsLabel;
    private VehiclesPanel vehiclesPanel;

    public BookingsPanel(
            BookingManager bookingManager,
            CreditWallet wallet,
            VehiclesPanel vehiclesPanel) {

        this.bookingManager = bookingManager;
        this.wallet = wallet;
        this.vehiclesPanel = vehiclesPanel;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // =================================================
        // HEADER
        // =================================================

        JPanel headerPanel =
                new JPanel(new BorderLayout());

        headerPanel.setBackground(Color.WHITE);

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        70,
                        15,
                        70
                )
        );

        // Title
        JLabel title =
                new JLabel("My Bookings");

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        30
                )
        );

        title.setForeground(RENTIT_NAVY);

        headerPanel.add(
                title,
                BorderLayout.WEST
        );

        // =================================================
        // CREDIT AREA
        // =================================================

        JPanel creditsPanel =
                new JPanel();

        creditsPanel.setLayout(
                new BoxLayout(
                        creditsPanel,
                        BoxLayout.X_AXIS
                )
        );

        creditsPanel.setBackground(Color.WHITE);

        creditsLabel =
                new JLabel(
                        "Available Credits: ₹"
                                + wallet.getCredits()
                );

        creditsLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        creditsLabel.setForeground(RENTIT_BLUE);

        JButton addCreditsButton =
                new JButton("+ Add Credits");

        addCreditsButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        addCreditsButton.setForeground(Color.WHITE);
        addCreditsButton.setBackground(RENTIT_BLUE);
        addCreditsButton.setFocusPainted(false);
        addCreditsButton.setBorder(
                BorderFactory.createEmptyBorder(
                        9,
                        16,
                        9,
                        16
                )
        );

        addCreditsButton.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );

        addCreditsButton.addActionListener(
                e -> showAddCreditsDialog()
        );

        creditsPanel.add(creditsLabel);

        creditsPanel.add(
                Box.createRigidArea(
                        new Dimension(15, 0)
                )
        );

        creditsPanel.add(addCreditsButton);

        headerPanel.add(
                creditsPanel,
                BorderLayout.EAST
        );

        add(
                headerPanel,
                BorderLayout.NORTH
        );

        // =================================================
        // BOOKING LIST
        // =================================================

        bookingsList =
                new PatternPanel();

        bookingsList.setLayout(
                new BoxLayout(
                        bookingsList,
                        BoxLayout.Y_AXIS
                )
        );

        bookingsList.setBackground(Color.WHITE);
        bookingsList.setOpaque(false);

        bookingsList.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        70,
                        30,
                        70
                )
        );

        ArrayList<Booking> bookings =
                bookingManager.getBookings();

        if (bookings.isEmpty()) {

            JLabel emptyLabel =
                    new JLabel(
                            "No bookings found."
                    );

            emptyLabel.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            16
                    )
            );

            emptyLabel.setForeground(TEXT_GRAY);

            emptyLabel.setAlignmentX(
                    LEFT_ALIGNMENT
            );

            bookingsList.add(emptyLabel);

        } else {

            for (Booking booking : bookings) {

                bookingsList.add(
                        createBookingCard(booking)
                );

                bookingsList.add(
                        Box.createRigidArea(
                                new Dimension(
                                        0,
                                        15
                                )
                        )
                );
            }
        }

        JScrollPane scrollPane =
                new JScrollPane(
                        bookingsList
                );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollPane.setBackground(Color.WHITE);

        scrollPane.getViewport()
                .setBackground(new Color(245, 249, 255));

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(18);

        styleScrollBar(
                scrollPane.getVerticalScrollBar()
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );
    }

    // =====================================================
    // ADD CREDITS DIALOG
    // =====================================================

    private void showAddCreditsDialog() {

        Window owner =
                SwingUtilities.getWindowAncestor(this);

        JDialog dialog =
                new JDialog(
                        owner,
                        "Add Credits",
                        java.awt.Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setSize(430, 330);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel =
                new JPanel();

        mainPanel.setLayout(
                new BoxLayout(
                        mainPanel,
                        BoxLayout.Y_AXIS
                )
        );

        mainPanel.setBackground(Color.WHITE);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );

        // Title
        JLabel title =
                new JLabel("Add Credits");

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        title.setForeground(RENTIT_NAVY);

        title.setAlignmentX(
                LEFT_ALIGNMENT
        );

        mainPanel.add(title);

        mainPanel.add(
                Box.createRigidArea(
                        new Dimension(0, 8)
                )
        );

        JLabel subtitle =
                new JLabel(
                        "Choose an amount or enter your own."
                );

        subtitle.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        subtitle.setForeground(TEXT_GRAY);

        subtitle.setAlignmentX(
                LEFT_ALIGNMENT
        );

        mainPanel.add(subtitle);

        mainPanel.add(
                Box.createRigidArea(
                        new Dimension(0, 20)
                )
        );

        // =================================================
        // PRESET BUTTONS
        // =================================================

        JPanel presetPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                10,
                                0
                        )
                );

        presetPanel.setBackground(Color.WHITE);
        presetPanel.setAlignmentX(LEFT_ALIGNMENT);

        JTextField amountField =
                new JTextField();

        amountField.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        amountField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_BLUE
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                10,
                                8,
                                10
                        )
                )
        );

        JButton button500 =
                createAmountButton("₹500");

        JButton button1000 =
                createAmountButton("₹1,000");

        JButton button2000 =
                createAmountButton("₹2,000");

        button500.addActionListener(
                e -> amountField.setText("500")
        );

        button1000.addActionListener(
                e -> amountField.setText("1000")
        );

        button2000.addActionListener(
                e -> amountField.setText("2000")
        );

        presetPanel.add(button500);
        presetPanel.add(button1000);
        presetPanel.add(button2000);

        mainPanel.add(presetPanel);

        mainPanel.add(
                Box.createRigidArea(
                        new Dimension(0, 18)
                )
        );

        // =================================================
        // CUSTOM AMOUNT
        // =================================================

        JLabel customLabel =
                new JLabel("Custom amount");

        customLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        customLabel.setForeground(RENTIT_NAVY);

        customLabel.setAlignmentX(
                LEFT_ALIGNMENT
        );

        mainPanel.add(customLabel);

        mainPanel.add(
                Box.createRigidArea(
                        new Dimension(0, 7)
                )
        );

        amountField.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        amountField.setAlignmentX(
                LEFT_ALIGNMENT
        );

        mainPanel.add(amountField);

        mainPanel.add(
                Box.createRigidArea(
                        new Dimension(0, 22)
                )
        );

        // =================================================
        // ACTION BUTTONS
        // =================================================

        JPanel buttonPanel =
                new JPanel(
                        new BorderLayout()
                );

        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(LEFT_ALIGNMENT);

        JButton cancelButton =
                new JButton("Cancel");

        cancelButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        cancelButton.setForeground(TEXT_GRAY);
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        cancelButton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_BLUE
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                18,
                                8,
                                18
                        )
                )
        );

        cancelButton.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );

        cancelButton.addActionListener(
                e -> dialog.dispose()
        );

        JButton addButton =
                new JButton("Add Credits");

        addButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        addButton.setForeground(Color.WHITE);
        addButton.setBackground(RENTIT_BLUE);
        addButton.setFocusPainted(false);

        addButton.setBorder(
                BorderFactory.createEmptyBorder(
                        9,
                        18,
                        9,
                        18
                )
        );

        addButton.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );

        addButton.addActionListener(
                e -> {

                    String text =
                            amountField.getText().trim();

                    if (text.isEmpty()) {

                        RentITDialog.showMessage(
                                dialog,
                                "Invalid Amount",
                                "Please enter a credit amount.",
                                "OK"
                        );

                        return;
                    }

                    try {

                        double amount =
                                Double.parseDouble(text);

                        if (amount <= 0) {

                            RentITDialog.showMessage(
                                    dialog,
                                    "Invalid Amount",
                                    "Credit amount must be greater than ₹0.",
                                    "OK"
                            );

                            return;
                        }

                        wallet.addCredits(amount);

                        refreshBookings();

                        dialog.dispose();

                        RentITDialog.showMessage(
                                this,
                                "Credits Added",
                                "₹" + formatAmount(amount)
                                        + " credits added successfully.",
                                "OK"
                        );

                    } catch (NumberFormatException ex) {

                        RentITDialog.showMessage(
                                dialog,
                                "Invalid Amount",
                                "Please enter a valid number.",
                                "OK"
                        );
                    }
                }
        );

        buttonPanel.add(
                cancelButton,
                BorderLayout.WEST
        );

        buttonPanel.add(
                addButton,
                BorderLayout.EAST
        );

        mainPanel.add(buttonPanel);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    // =====================================================
    // PRESET AMOUNT BUTTON
    // =====================================================

    private JButton createAmountButton(
            String text) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(RENTIT_BLUE);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_BLUE
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                10,
                                9,
                                10
                        )
                )
        );

        button.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    // =====================================================
    // FORMAT CREDIT AMOUNT
    // =====================================================

    private String formatAmount(
            double amount) {

        if (amount == Math.floor(amount)) {
            return String.valueOf(
                    (long) amount
            );
        }

        return String.valueOf(amount);
    }

    // =====================================================
    // BOOKING CARD
    // =====================================================

    private JPanel createBookingCard(
            Booking booking) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_BLUE
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                20,
                                18,
                                20
                        )
                )
        );

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        220
                )
        );

        // =================================================
        // VEHICLE NAME
        // =================================================

        JLabel vehicleLabel =
                new JLabel(
                        booking.getVehicle().getModel()
                );

        vehicleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );

        vehicleLabel.setForeground(RENTIT_NAVY);

        // =================================================
        // BOOKING DETAILS
        // =================================================

        JPanel detailsPanel =
                new JPanel();

        detailsPanel.setLayout(
                new BoxLayout(
                        detailsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        detailsPanel.setBackground(Color.WHITE);

        detailsPanel.add(
                createDetailLabel(
                        "Customer: "
                                + booking.getCustomerName()
                )
        );

        detailsPanel.add(
                createDetailLabel(
                        "Vehicle Type: "
                                + booking.getVehicle()
                                        .getVehicleType()
                )
        );

        detailsPanel.add(
                createDetailLabel(
                        "Days: "
                                + booking.getDays()
                )
        );

        detailsPanel.add(
                createDetailLabel(
                        "Pickup: "
                                + booking.getPickupLocation()
                )
        );

        detailsPanel.add(
                createDetailLabel(
                        "Return: "
                                + booking.getReturnLocation()
                )
        );

        // =================================================
        // PRICE
        // =================================================

        JLabel priceLabel =
                new JLabel(
                        "₹"
                                + booking.getTotalPrice()
                );

        priceLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );

        priceLabel.setForeground(RENTIT_BLUE);

        priceLabel.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        card.add(
                vehicleLabel,
                BorderLayout.NORTH
        );

        card.add(
                detailsPanel,
                BorderLayout.CENTER
        );

        card.add(
                priceLabel,
                BorderLayout.EAST
        );

        // =================================================
        // CANCEL BUTTON
        // =================================================

        JButton cancelButton =
                new JButton("Cancel Booking");

        cancelButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        cancelButton.setForeground(Color.WHITE);

        cancelButton.setBackground(RENTIT_BLUE);

        cancelButton.setFocusPainted(false);

        cancelButton.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        16,
                        8,
                        16
                )
        );

        cancelButton.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );

        cancelButton.addActionListener(e -> {

            boolean confirmed =
                    RentITDialog.showConfirm(
                            this,
                            "Cancel Booking",
                            "Are you sure you want to cancel this booking?\n"
                                    + "₹" + booking.getTotalPrice()
                                    + " will be refunded to your credits.",
                            "Yes, Cancel",
                            "No"
                    );

            if (confirmed) {

                if (bookingManager.cancelBooking(booking)) {

                    bookingManager.removeBooking(booking);
                    bookingManager.saveBookings();
                    refreshBookings();
                    vehiclesPanel.refreshVehicles();

                    RentITDialog.showMessage(
                            this,
                            "Booking Cancelled",
                            "Booking cancelled successfully.\n"
                                    + "₹" + booking.getTotalPrice()
                                    + " has been refunded.",
                            "OK"
                    );

                } else {
                    RentITDialog.showMessage(
                            this,
                            "Cancellation Not Completed",
                            "The booking could not be cancelled. Please try again.",
                            "OK"
                    );
                }
            }
        });

        card.add(
                cancelButton,
                BorderLayout.SOUTH
        );

        return card;
    }

    // =====================================================
    // DETAIL LABEL
    // =====================================================

    private JLabel createDetailLabel(
            String text) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        label.setForeground(TEXT_GRAY);

        label.setBorder(
                BorderFactory.createEmptyBorder(
                        3,
                        0,
                        3,
                        0
                )
        );

        return label;
    }

    // =====================================================
    // REFRESH BOOKINGS
    // =====================================================

    public void refreshBookings() {

        // Pick up bookings and wallet changes made by another RentIT instance.
        bookingManager.refreshFromStorage();
        wallet.refreshSavedCredits();

        creditsLabel.setText(
                "Available Credits: ₹"
                        + wallet.getCredits()
        );

        bookingsList.removeAll();

        ArrayList<Booking> bookings =
                bookingManager.getBookings();

        if (bookings.isEmpty()) {

            JLabel emptyLabel =
                    new JLabel(
                            "No bookings found."
                    );

            emptyLabel.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            16
                    )
            );

            emptyLabel.setForeground(TEXT_GRAY);

            emptyLabel.setAlignmentX(
                    LEFT_ALIGNMENT
            );

            bookingsList.add(emptyLabel);

        } else {

            for (Booking booking : bookings) {

                bookingsList.add(
                        createBookingCard(booking)
                );

                bookingsList.add(
                        Box.createRigidArea(
                                new Dimension(
                                        0,
                                        15
                                )
                        )
                );
            }
        }

        bookingsList.revalidate();
        bookingsList.repaint();
    }

    // =====================================================
    // MODERN SCROLLBAR
    // =====================================================

    private void styleScrollBar(JScrollBar scrollBar) {

        scrollBar.setPreferredSize(
                new Dimension(
                        12,
                        Integer.MAX_VALUE
                )
        );

        scrollBar.setOpaque(false);

        scrollBar.setUI(
                new BasicScrollBarUI() {

                    @Override
                    protected JButton createDecreaseButton(
                            int orientation) {
                        return createZeroButton();
                    }

                    @Override
                    protected JButton createIncreaseButton(
                            int orientation) {
                        return createZeroButton();
                    }

                    @Override
                    protected void paintTrack(
                            Graphics g,
                            javax.swing.JComponent c,
                            java.awt.Rectangle trackBounds) {

                        Graphics2D g2 =
                                (Graphics2D) g.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        g2.setColor(
                                new Color(232, 239, 248)
                        );

                        int x =
                                trackBounds.x + 4;

                        int width = 4;

                        g2.fillRoundRect(
                                x,
                                trackBounds.y,
                                width,
                                trackBounds.height,
                                8,
                                8
                        );

                        g2.dispose();
                    }

                    @Override
                    protected void paintThumb(
                            Graphics g,
                            javax.swing.JComponent c,
                            java.awt.Rectangle thumbBounds) {

                        if (thumbBounds.isEmpty()
                                || !scrollBar.isEnabled()) {
                            return;
                        }

                        Graphics2D g2 =
                                (Graphics2D) g.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        g2.setColor(
                                RENTIT_BLUE
                        );

                        int x =
                                thumbBounds.x + 2;

                        int width =
                                Math.max(
                                        7,
                                        thumbBounds.width - 4
                                );

                        g2.fillRoundRect(
                                x,
                                thumbBounds.y,
                                width,
                                thumbBounds.height,
                                10,
                                10
                        );

                        g2.dispose();
                    }

                    private JButton createZeroButton() {

                        JButton button =
                                new JButton();

                        button.setPreferredSize(
                                new Dimension(
                                        0,
                                        0
                                )
                        );

                        button.setMinimumSize(
                                new Dimension(
                                        0,
                                        0
                                )
                        );

                        button.setMaximumSize(
                                new Dimension(
                                        0,
                                        0
                                )
                        );

                        return button;
                    }
                }
        );
    }

    // =====================================================
    // SUBTLE BLUE BACKGROUND PATTERN
    // =====================================================

    private class PatternPanel extends JPanel {

        PatternPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    new Color(247, 250, 255)
            );

            g2.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

            // Soft diagonal blue pattern.
            g2.setColor(
                    new Color(229, 239, 252)
            );

            for (int x = -getHeight();
                 x < getWidth() + getHeight();
                 x += 95) {

                g2.drawLine(
                        x,
                        0,
                        x + getHeight(),
                        getHeight()
                );
            }

            // Very subtle circular accents.
            g2.setColor(
                    new Color(235, 244, 255)
            );

            for (int x = 45;
                 x < getWidth();
                 x += 180) {

                for (int y = 45;
                     y < getHeight();
                     y += 180) {

                    g2.drawOval(
                            x,
                            y,
                            55,
                            55
                    );
                }
            }

            g2.dispose();

            super.paintComponent(g);
        }
    }

}