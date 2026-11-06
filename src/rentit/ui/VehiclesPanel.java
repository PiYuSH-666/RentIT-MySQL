package rentit.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import rentit.Booking;
import rentit.BookingManager;
import rentit.InsufficientCreditsException;
import rentit.InvalidBookingException;
import rentit.CreditWallet;
import rentit.RentalManager;
import rentit.Vehicle;
import rentit.VehicleNotAvailableException;

public class VehiclesPanel extends JPanel {

    // =====================================================
    // RENTIT BRAND COLORS
    // =====================================================

    private final Color RENTIT_BLUE = new Color(22, 119, 232);
    private final Color RENTIT_NAVY = new Color(10, 29, 58);
    private final Color TEXT_GRAY = new Color(92, 108, 130);
    private final Color BORDER_BLUE = new Color(220, 231, 245);
    private final Color LIGHT_BLUE = new Color(245, 249, 255);
    private final Color TAB_BACKGROUND = new Color(248, 251, 255);

    // =====================================================
    // BACKEND REFERENCES
    // =====================================================

    private RentalManager rentalManager;
    private BookingManager bookingManager;
    private CreditWallet wallet;

    // =====================================================
    // UI FIELDS
    // =====================================================

    private JPanel cardsPanel;
    private JTextField searchField;
    private String selectedCategory = "All";

    // Cache loaded and resized vehicle images so search/filter refreshes
    // do not read the same JPG files from disk repeatedly.
    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    private final Map<String, javax.swing.ImageIcon> scaledImageCache =
            new HashMap<>();

    private JButton allButton;
    private JButton carsButton;
    private JButton bikesButton;
    private JButton scootersButton;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /*
     * Main constructor used by the current RentITFrame.
     * The backend objects are reused; this class only changes
     * the presentation of the vehicle list.
     */
    public VehiclesPanel(
            RentalManager manager,
            BookingManager bookingManager,
            CreditWallet wallet) {

        this.rentalManager = manager;
        this.bookingManager = bookingManager;
        this.wallet = wallet;

        buildUI();
    }

    /*
     * Compatibility constructor for older code.
     */
    public VehiclesPanel(RentalManager manager) {
        this(manager, null, null);
    }

    // =====================================================
    // BUILD UI
    // =====================================================

    private void buildUI() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Color.WHITE);

        page.add(createHeroSection(), BorderLayout.NORTH);
        page.add(createMainContent(), BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(page);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getViewport().setBackground(Color.WHITE);

        styleVehicleScrollBar(
                scrollPane.getVerticalScrollBar()
        );

        add(scrollPane, BorderLayout.CENTER);
    }

    // =====================================================
    // HERO
    // =====================================================

    private JPanel createHeroSection() {

        JPanel hero = new HeroPanel();

        hero.setPreferredSize(new Dimension(1200, 280));

        return hero;
    }

    private class HeroPanel extends JPanel {

        private BufferedImage backgroundImage;

        HeroPanel() {

            setBackground(LIGHT_BLUE);

            backgroundImage = loadImage(
                    "images/vehicles_hero.jpg"
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            if (backgroundImage != null) {

                double imageRatio =
                        (double) backgroundImage.getWidth()
                                / backgroundImage.getHeight();

                double panelRatio =
                        (double) width / height;

                int drawWidth;
                int drawHeight;

                if (imageRatio > panelRatio) {
                    drawHeight = height;
                    drawWidth = (int) (height * imageRatio);
                } else {
                    drawWidth = width;
                    drawHeight = (int) (width / imageRatio);
                }

                int x = (width - drawWidth) / 2;
                int y = (height - drawHeight) / 2;

                g2.drawImage(
                        backgroundImage,
                        x,
                        y,
                        drawWidth,
                        drawHeight,
                        null
                );

                // Keep the text readable.
                g2.setColor(new Color(255, 255, 255, 178));
                g2.fillRect(0, 0, width, height);

            } else {

                // Clean fallback until the user adds the hero image.
                g2.setColor(new Color(244, 249, 255));
                g2.fillRect(0, 0, width, height);

                g2.setColor(new Color(222, 237, 255));
                g2.fillOval(
                        width - 260,
                        -110,
                        390,
                        300
                );
            }

            int left = Math.max(60, (width - 1080) / 2);

            // Small blue line.
            g2.setColor(RENTIT_BLUE);
            g2.fillRoundRect(
                    left,
                    38,
                    28,
                    4,
                    4,
                    4
            );

            // Main heading.
            g2.setFont(new Font(
                    "Arial",
                    Font.BOLD,
                    40
            ));

            g2.setColor(RENTIT_NAVY);

            g2.drawString(
                    "Explore Our",
                    left,
                    91
            );

            int firstWidth =
                    g2.getFontMetrics().stringWidth(
                            "Explore Our"
                    );

            g2.setColor(RENTIT_BLUE);

            g2.drawString(
                    " Vehicles",
                    left + firstWidth,
                    91
            );

            // Subtitle.
            g2.setFont(new Font(
                    "Arial",
                    Font.PLAIN,
                    17
            ));

            g2.setColor(TEXT_GRAY);

            g2.drawString(
                    "Find the perfect car, bike or scooter for your next journey.",
                    left,
                    125
            );

            g2.dispose();
        }
    }

    // =====================================================
    // MAIN CONTENT
    // =====================================================

    private JPanel createMainContent() {

        JPanel content = new JPanel(new BorderLayout());

        content.setBackground(Color.WHITE);

        content.setBorder(
                BorderFactory.createEmptyBorder(
                        14,
                        64,
                        30,
                        64
                )
        );

        content.add(
                createToolbar(),
                BorderLayout.NORTH
        );

        cardsPanel = new JPanel();

        cardsPanel.setBackground(Color.WHITE);

        cardsPanel.setLayout(
                new GridLayout(
                        0,
                        3,
                        18,
                        18
                )
        );

        cardsPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        0,
                        0,
                        0
                )
        );

        content.add(
                cardsPanel,
                BorderLayout.CENTER
        );

        refreshVehicleCards();

        return content;
    }

    // =====================================================
    // TOOLBAR
    // =====================================================

    private JPanel createToolbar() {

        JPanel toolbar = new JPanel(new BorderLayout(16, 0));

        toolbar.setOpaque(false);

        JPanel tabs = new JPanel();

        tabs.setOpaque(false);

        allButton = createTabButton(
                "•   All Vehicles",
                true
        );

        carsButton = createTabButton(
                "•   Cars",
                false
        );

        bikesButton = createTabButton(
                "•   Bikes",
                false
        );

        scootersButton = createTabButton(
                "•   Scooters",
                false
        );

        ButtonGroup group = new ButtonGroup();

        group.add(allButton);
        group.add(carsButton);
        group.add(bikesButton);
        group.add(scootersButton);

        tabs.add(allButton);
        tabs.add(Box.createHorizontalStrut(8));
        tabs.add(carsButton);
        tabs.add(Box.createHorizontalStrut(8));
        tabs.add(bikesButton);
        tabs.add(Box.createHorizontalStrut(8));
        tabs.add(scootersButton);

        toolbar.add(
                tabs,
                BorderLayout.WEST
        );

        JPanel searchHolder =
                new JPanel(new BorderLayout());

        searchHolder.setOpaque(false);

        searchField = new JTextField();

        searchField.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        searchField.setForeground(TEXT_GRAY);
        searchField.setText("Search vehicles...");

        searchField.addFocusListener(
                new java.awt.event.FocusAdapter() {
                    @Override
                    public void focusGained(java.awt.event.FocusEvent e) {
                        if (searchField.getText().equals("Search vehicles...")) {
                            searchField.setText("");
                            searchField.setForeground(RENTIT_NAVY);
                        }
                    }
                    @Override
                    public void focusLost(java.awt.event.FocusEvent e) {
                        if (searchField.getText().trim().isEmpty()) {
                            searchField.setText("Search vehicles...");
                            searchField.setForeground(TEXT_GRAY);
                        }
                    }
                }
        );

        searchField.setPreferredSize(
                new Dimension(
                        270,
                        42
                )
        );

        searchField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_BLUE,
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                0,
                                14,
                                0,
                                14
                        )
                )
        );

        searchField.addActionListener(
                this::searchAction
        );

        searchField.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e) {
                                refreshVehicleCards();
                            }

                            @Override
                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e) {
                                refreshVehicleCards();
                            }

                            @Override
                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e) {
                                refreshVehicleCards();
                            }
                        }
                );

        searchHolder.add(
                searchField,
                BorderLayout.CENTER
        );

        toolbar.add(
                searchHolder,
                BorderLayout.EAST
        );

        return toolbar;
    }

    private JButton createTabButton(
            String text,
            boolean active) {

        JButton button = new JButton(text);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(
                active
                        ? Color.WHITE
                        : RENTIT_NAVY
        );

        button.setBackground(
                active
                        ? RENTIT_BLUE
                        : TAB_BACKGROUND
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setMargin(
                new Insets(
                        0,
                        16,
                        0,
                        16
                )
        );

        button.setPreferredSize(
                new Dimension(
                        145,
                        42
                )
        );

        button.addActionListener(
                e -> {

                    if (button == allButton) {
                        selectedCategory = "All";
                    } else if (button == carsButton) {
                        selectedCategory = "Car";
                    } else if (button == bikesButton) {
                        selectedCategory = "Bike";
                    } else {
                        selectedCategory = "Scooter";
                    }

                    updateTabStyles();
                    refreshVehicleCards();
                }
        );

        return button;
    }

    private void updateTabStyles() {

        setTabStyle(
                allButton,
                selectedCategory.equals("All")
        );

        setTabStyle(
                carsButton,
                selectedCategory.equals("Car")
        );

        setTabStyle(
                bikesButton,
                selectedCategory.equals("Bike")
        );

        setTabStyle(
                scootersButton,
                selectedCategory.equals("Scooter")
        );
    }

    private void setTabStyle(
            JButton button,
            boolean active) {

        button.setBackground(
                active
                        ? RENTIT_BLUE
                        : TAB_BACKGROUND
        );

        button.setForeground(
                active
                        ? Color.WHITE
                        : RENTIT_NAVY
        );
    }

    private void searchAction(ActionEvent e) {
        refreshVehicleCards();
    }

    // =====================================================
    // FILTER API USED BY RENTITFRAME
    // =====================================================

    public void setSelectedCategory(
            String category) {

        if (category == null) {
            category = "All";
        }

        if (category.equalsIgnoreCase("Cars")
                || category.equalsIgnoreCase("Car")) {

            selectedCategory = "Car";

        } else if (category.equalsIgnoreCase("Bikes")
                || category.equalsIgnoreCase("Bike")) {

            selectedCategory = "Bike";

        } else if (category.equalsIgnoreCase("Scooters")
                || category.equalsIgnoreCase("Scooter")) {

            selectedCategory = "Scooter";

        } else {

            selectedCategory = "All";
        }

        updateTabStyles();

        if (searchField != null) {
            searchField.setText("Search vehicles...");
            searchField.setForeground(TEXT_GRAY);
        }

        refreshVehicleCards();
    }

    // =====================================================
    // VEHICLE CARDS
    // =====================================================

    /**
     * Compatibility method used by BookingsPanel after a booking is cancelled.
     * It keeps the existing public API while the new UI uses refreshVehicleCards().
     */
    public void refreshVehicles() {
        // Another RentIT window may have booked or cancelled a vehicle.
        rentalManager.loadSavedAvailability();
        refreshVehicleCards();
    }

    private void refreshVehicleCards() {

        if (cardsPanel == null || rentalManager == null) {
            return;
        }

        // Another RentIT instance may have booked or cancelled a vehicle.
        // Reload the shared file before rendering the current in-memory fleet.
        rentalManager.loadSavedAvailability();

        cardsPanel.removeAll();

        String searchText = "";

        if (searchField != null) {
            searchText =
                    searchField.getText()
                            .trim()
                            .toLowerCase();
            if (searchText.equals("search vehicles...")) {
                searchText = "";
            }
        }

        ArrayList<Vehicle> vehicles =
                rentalManager.getVehicles();

        int shown = 0;

        for (Vehicle vehicle : vehicles) {

            boolean categoryMatches =
                    selectedCategory.equals("All")
                            || vehicle.getVehicleType()
                                    .equalsIgnoreCase(
                                            selectedCategory
                                    );

            boolean searchMatches =
                    searchText.isEmpty()
                            || vehicle.getModel()
                                    .toLowerCase()
                                    .contains(searchText)
                            || vehicle.getVehicleType()
                                    .toLowerCase()
                                    .contains(searchText)
                            || vehicle.getBodyType()
                                    .toLowerCase()
                                    .contains(searchText);

            if (categoryMatches && searchMatches) {

                cardsPanel.add(
                        createVehicleCard(vehicle)
                );

                shown++;
            }
        }

        if (shown == 0) {
            cardsPanel.setLayout(
                    new BorderLayout()
            );

            JPanel emptyPanel =
                    new JPanel(
                            new BorderLayout()
                    );

            emptyPanel.setBackground(Color.WHITE);

            JLabel empty =
                    new JLabel(
                            "No vehicles found.",
                            SwingConstants.CENTER
                    );

            empty.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            18
                    )
            );

            empty.setForeground(TEXT_GRAY);

            emptyPanel.add(
                    empty,
                    BorderLayout.CENTER
            );

            cardsPanel.add(
                    emptyPanel,
                    BorderLayout.CENTER
            );

        } else {

            cardsPanel.setLayout(
                    new GridLayout(
                            0,
                            3,
                            18,
                            18
                    )
            );
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createVehicleCard(
            Vehicle vehicle) {

        VehicleCard card =
                new VehicleCard(vehicle);

        return card;
    }

    // =====================================================
    // VEHICLES PAGE SCROLLBAR
    // =====================================================

    private void styleVehicleScrollBar(
            javax.swing.JScrollBar scrollBar) {

        scrollBar.setPreferredSize(
                new Dimension(12, 0)
        );

        scrollBar.setUnitIncrement(18);

        scrollBar.setOpaque(true);

        scrollBar.setBackground(
                new Color(
                        245,
                        247,
                        250
                )
        );

        scrollBar.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollBar.setUI(
                new javax.swing.plaf.basic.BasicScrollBarUI() {

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
                                new Color(
                                        242,
                                        244,
                                        247
                                )
                        );

                        g2.fillRoundRect(
                                trackBounds.x + 3,
                                trackBounds.y,
                                Math.max(
                                        1,
                                        trackBounds.width - 6
                                ),
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

                        if (!scrollbar.isEnabled()
                                || thumbBounds.isEmpty()) {
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

                        g2.fillRoundRect(
                                thumbBounds.x + 2,
                                thumbBounds.y,
                                Math.max(
                                        1,
                                        thumbBounds.width - 4
                                ),
                                thumbBounds.height,
                                8,
                                8
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

                        button.setVisible(false);

                        return button;
                    }
                }
        );
    }

    // =====================================================
    // MODERN VEHICLE CARD
    // =====================================================

    private class VehicleCard extends JPanel {

        private final Vehicle vehicle;
        private boolean hovered;

        VehicleCard(Vehicle vehicle) {

            this.vehicle = vehicle;

            setLayout(
                    new BorderLayout()
            );

            setOpaque(false);

            setPreferredSize(
                    new Dimension(
                            330,
                            350
                    )
            );

            setCursor(
                    new Cursor(
                            Cursor.HAND_CURSOR
                    )
            );

            addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseEntered(
                                MouseEvent e) {

                            hovered = true;
                            repaint();
                        }

                        @Override
                        public void mouseExited(
                                MouseEvent e) {

                            hovered = false;
                            repaint();
                        }
                    }
            );

            add(
                    createCardImageArea(),
                    BorderLayout.NORTH
            );

            add(
                    createCardInformation(),
                    BorderLayout.CENTER
            );

            add(
                    createCardButton(),
                    BorderLayout.SOUTH
            );
        }

        private JPanel createCardImageArea() {

            JPanel imageArea =
                    new JPanel(
                            new BorderLayout()
                    ) {

                        @Override
                        protected void paintComponent(
                                Graphics g) {

                            Graphics2D g2 =
                                    (Graphics2D) g.create();

                            g2.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON
                            );

                            // Soft patterned background behind the vehicle image.
                            // This keeps the vehicle JPG unchanged while giving each
                            // card the same polished RentIT visual style.
                            g2.setColor(
                                    new Color(
                                            244,
                                            249,
                                            255
                                    )
                            );

                            g2.fillRoundRect(
                                    0,
                                    0,
                                    getWidth(),
                                    getHeight(),
                                    18,
                                    18
                            );

                            // Clip the decorative pattern to the rounded image area.
                            java.awt.Shape oldClip = g2.getClip();
                            g2.setClip(
                                    new java.awt.geom.RoundRectangle2D.Double(
                                            0,
                                            0,
                                            getWidth(),
                                            getHeight(),
                                            18,
                                            18
                                    )
                            );

                            // Very light diagonal strokes.
                            g2.setColor(
                                    new Color(
                                            218,
                                            234,
                                            255,
                                            125
                                    )
                            );
                            g2.setStroke(
                                    new BasicStroke(
                                            2f,
                                            BasicStroke.CAP_ROUND,
                                            BasicStroke.JOIN_ROUND
                                    )
                            );

                            for (int x = -120; x < getWidth() + 160; x += 42) {
                                g2.drawLine(
                                        x,
                                        getHeight(),
                                        x + 95,
                                        0
                                );
                            }

                            // Soft circular shapes.
                            g2.setColor(
                                    new Color(
                                            205,
                                            226,
                                            251,
                                            105
                                    )
                            );

                            g2.fillOval(
                                    -32,
                                    54,
                                    78,
                                    78
                            );

                            g2.fillOval(
                                    getWidth() - 72,
                                    -28,
                                    92,
                                    92
                            );

                            g2.fillOval(
                                    getWidth() - 54,
                                    82,
                                    64,
                                    64
                            );

                            g2.fillOval(
                                    118,
                                    -42,
                                    70,
                                    70
                            );

                            // Small dotted clusters.
                            g2.setColor(
                                    new Color(
                                            175,
                                            211,
                                            247,
                                            115
                                    )
                            );

                            for (int row = 0; row < 5; row++) {
                                for (int col = 0; col < 5; col++) {
                                    int dx = 18 + (col * 8);
                                    int dy = 14 + (row * 8);

                                    if ((row + col) % 2 == 0) {
                                        g2.fillOval(
                                                dx,
                                                dy,
                                                3,
                                                3
                                        );
                                    }
                                }
                            }

                            for (int row = 0; row < 5; row++) {
                                for (int col = 0; col < 5; col++) {
                                    int dx =
                                            getWidth()
                                            - 58
                                            + (col * 8);
                                    int dy =
                                            54
                                            + (row * 8);

                                    if ((row + col) % 2 == 0) {
                                        g2.fillOval(
                                                dx,
                                                dy,
                                                3,
                                                3
                                        );
                                    }
                                }
                            }

                            // A few larger abstract rounded strokes.
                            g2.setColor(
                                    new Color(
                                            191,
                                            218,
                                            249,
                                            95
                                    )
                            );

                            g2.setStroke(
                                    new BasicStroke(
                                            7f,
                                            BasicStroke.CAP_ROUND,
                                            BasicStroke.JOIN_ROUND
                                    )
                            );

                            g2.drawLine(
                                    42,
                                    18,
                                    78,
                                    4
                            );

                            g2.drawLine(
                                    getWidth() - 88,
                                    getHeight() - 18,
                                    getWidth() - 42,
                                    getHeight() - 36
                            );

                            g2.setClip(oldClip);

                            g2.dispose();

                            super.paintComponent(g);
                        }
                    };

            imageArea.setOpaque(false);

            imageArea.setPreferredSize(
                    new Dimension(
                            330,
                            130
                    )
            );

            String imagePath =
                    getVehicleImagePath(vehicle);

            BufferedImage image =
                    loadImage(imagePath);

            JLabel imageLabel =
                    new JLabel();

            imageLabel.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            imageLabel.setVerticalAlignment(
                    SwingConstants.CENTER
            );

            if (image != null) {

                int maxWidth = 240;
                int maxHeight = 110;

                double ratio =
                        (double) image.getWidth()
                                / image.getHeight();

                int width = maxWidth;
                int height =
                        (int) (width / ratio);

                if (height > maxHeight) {
                    height = maxHeight;
                    width =
                            (int) (height * ratio);
                }

                String cacheKey =
                        imagePath + "|" + width + "x" + height;

                javax.swing.ImageIcon cachedIcon =
                        scaledImageCache.get(cacheKey);

                if (cachedIcon == null) {
                    java.awt.Image scaled =
                            image.getScaledInstance(
                                    width,
                                    height,
                                    java.awt.Image.SCALE_SMOOTH
                            );

                    cachedIcon =
                            new javax.swing.ImageIcon(scaled);

                    scaledImageCache.put(
                            cacheKey,
                            cachedIcon
                    );
                }

                imageLabel.setIcon(cachedIcon);

            } else {

                imageLabel.setText(
                        vehicle.getVehicleType()
                                .toUpperCase()
                );

                imageLabel.setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                14
                        )
                );

                imageLabel.setForeground(
                        RENTIT_BLUE
                );
            }

            JPanel badgeHolder =
                    new JPanel(
                            new BorderLayout()
                    );

            badgeHolder.setOpaque(false);

            badgeHolder.setBorder(
                    BorderFactory.createEmptyBorder(
                            12,
                            0,
                            0,
                            12
                    )
            );

            JLabel badge =
                    new JLabel(
                            "•  " + vehicle.getVehicleType()
                    );

            badge.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            badge.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            12
                    )
            );

            badge.setForeground(Color.WHITE);

            badge.setBackground(
                    new Color(
                            67,
                            154,
                            240
                    )
            );

            badge.setOpaque(true);

            badge.setBorder(
                    BorderFactory.createEmptyBorder(
                            6,
                            11,
                            6,
                            11
                    )
            );

            badgeHolder.add(
                    badge,
                    BorderLayout.EAST
            );

            imageArea.add(
                    imageLabel,
                    BorderLayout.CENTER
            );

            imageArea.add(
                    badgeHolder,
                    BorderLayout.NORTH
            );

            return imageArea;
        }

        private JPanel createCardInformation() {

            JPanel information =
                    new JPanel();

            information.setLayout(
                    new BoxLayout(
                            information,
                            BoxLayout.Y_AXIS
                    )
            );

            information.setOpaque(false);

            information.setBorder(
                    BorderFactory.createEmptyBorder(
                            10,
                            14,
                            8,
                            14
                    )
            );

            JLabel model =
                    new JLabel(
                            vehicle.getModel()
                    );

            model.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            17
                    )
            );

            model.setForeground(
                    RENTIT_NAVY
            );

            information.add(model);

            information.add(
                    Box.createVerticalStrut(7)
            );

            JPanel specs =
                    new JPanel(
                            new GridLayout(
                                    1,
                                    3,
                                    5,
                                    0
                            )
                    );

            specs.setOpaque(false);

            specs.add(
                    createSpecItem(
                            "SEAT",
                            vehicle.getSeats()
                                    + " Seater",
                            1
                    )
            );

            specs.add(
                    createSpecItem(
                            "FUEL",
                            vehicle.getFuelType(),
                            2
                    )
            );

            specs.add(
                    createSpecItem(
                            "GEAR",
                            vehicle.getTransmission(),
                            3
                    )
            );

            information.add(specs);

            information.add(
                    Box.createVerticalStrut(8)
            );

            JLabel price =
                    new JLabel(
                            formatPrice(
                                    vehicle.getPricePerDay()
                            )
                            + " / day"
                    );

            price.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            20
                    )
            );

            price.setForeground(
                    RENTIT_NAVY
            );

            information.add(price);

            return information;
        }

        private JPanel createSpecItem(
                String label,
                String value,
                int iconType) {

            JPanel item =
                    new JPanel(
                            new BorderLayout(
                                    4,
                                    0
                            )
                    );

            item.setOpaque(false);

            JLabel icon =
                    new JLabel(
                            new SpecIcon(
                                    iconType
                            )
                    );

            JLabel text =
                    new JLabel(
                            "<html><span style='font-size:10px;color:#64748B;'>"
                                    + label
                                    + "</span><br>"
                                    + "<span style='font-size:11px;color:#334155;'>"
                                    + value
                                    + "</span></html>"
                    );

            item.add(
                    icon,
                    BorderLayout.WEST
            );

            item.add(
                    text,
                    BorderLayout.CENTER
            );

            return item;
        }

        private JPanel createCardButton() {

            JPanel holder =
                    new JPanel(
                            new BorderLayout()
                    );

            holder.setOpaque(false);

            holder.setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            14,
                            14,
                            14
                    )
            );

            JButton button =
                    new JButton(
                            "View Details  →"
                    );

            button.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            13
                    )
            );

            button.setForeground(
                    RENTIT_BLUE
            );

            button.setBackground(
                    Color.WHITE
            );

            button.setFocusPainted(false);

            button.setCursor(
                    new Cursor(
                            Cursor.HAND_CURSOR
                    )
            );

            button.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    RENTIT_BLUE,
                                    1
                            ),
                            BorderFactory.createEmptyBorder(
                                    9,
                                    10,
                                    9,
                                    10
                            )
                    )
            );

            button.addActionListener(
                    e -> showVehicleDetails(
                            vehicle
                    )
            );

            holder.add(
                    button,
                    BorderLayout.CENTER
            );

            return holder;
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Soft shadow.
            g2.setColor(
                    new Color(
                            0,
                            0,
                            0,
                            hovered ? 24 : 12
                    )
            );

            g2.fillRoundRect(
                    2,
                    4,
                    getWidth() - 4,
                    getHeight() - 6,
                    18,
                    18
            );

            // Card background.
            g2.setColor(Color.WHITE);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 8,
                    18,
                    18
            );

            // Border.
            g2.setColor(
                    hovered
                            ? RENTIT_BLUE
                            : BORDER_BLUE
            );

            g2.setStroke(
                    new BasicStroke(
                            hovered ? 1.6f : 1f
                    )
            );

            g2.drawRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 8,
                    18,
                    18
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =====================================================
    // VEHICLE DETAILS DIALOG
    // =====================================================

    private void showVehicleDetails(Vehicle vehicle) {

        // Details may have been opened after another app instance changed availability.
        if (rentalManager != null) {
            rentalManager.loadSavedAvailability();
        }

        JDialog dialog =
                new JDialog(
                        SwingUtilities.getWindowAncestor(this),
                        vehicle.getModel()
                                + " - Details",
                        java.awt.Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setSize(
                800,
                455
        );

        dialog.setLocationRelativeTo(this);

        JPanel root =
                new JPanel(
                        new BorderLayout(
                                22,
                                0
                        )
                );

        root.setBackground(Color.WHITE);

        root.setBorder(
                BorderFactory.createEmptyBorder(
                        22,
                        24,
                        22,
                        24
                )
        );

        // -------------------------------------------------
        // IMAGE
        // -------------------------------------------------

        JPanel imagePanel =
                new JPanel(
                        new BorderLayout()
                );

        imagePanel.setBackground(
                new Color(
                        246,
                        250,
                        255
                )
        );

        imagePanel.setPreferredSize(
                new Dimension(300, 280)
        );

        imagePanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_BLUE
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                10,
                                10,
                                10
                        )
                )
        );

        BufferedImage image =
                loadImage(
                        getVehicleImagePath(
                                vehicle
                        )
                );

        JLabel imageLabel =
                new JLabel();

        imageLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        if (image != null) {

            double ratio =
                    (double) image.getWidth()
                            / image.getHeight();

            int width = 275;
            int height =
                    (int) (width / ratio);

            if (height > 235) {
                height = 235;
                width =
                        (int) (height * ratio);
            }

            imageLabel.setIcon(
                    new javax.swing.ImageIcon(
                            image.getScaledInstance(
                                    width,
                                    height,
                                    java.awt.Image.SCALE_SMOOTH
                            )
                    )
            );

        } else {

            imageLabel.setText(
                    vehicle.getVehicleType()
                            .toUpperCase()
            );

            imageLabel.setForeground(
                    RENTIT_BLUE
            );
        }

        imagePanel.add(
                imageLabel,
                BorderLayout.CENTER
        );

        root.add(
                imagePanel,
                BorderLayout.WEST
        );

        // -------------------------------------------------
        // DETAILS
        // -------------------------------------------------

        JPanel details =
                new JPanel();

        details.setLayout(
                new BoxLayout(
                        details,
                        BoxLayout.Y_AXIS
                )
        );

        details.setOpaque(false);

        JLabel title =
                new JLabel(
                        vehicle.getModel()
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        title.setForeground(
                RENTIT_NAVY
        );

        details.add(title);

        JLabel type =
                new JLabel(
                        vehicle.getVehicleType()
                                + "  •  "
                                + vehicle.getBodyType()
                );

        type.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        type.setForeground(TEXT_GRAY);

        details.add(type);

        details.add(
                Box.createVerticalStrut(14)
        );

        JLabel price =
                new JLabel(
                        formatPrice(
                                vehicle.getPricePerDay()
                        )
                                + " / day"
                );

        price.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        23
                )
        );

        price.setForeground(
                RENTIT_BLUE
        );

        details.add(price);

        details.add(
                Box.createVerticalStrut(18)
        );

        details.add(
                createDialogRow(
                        "Seats",
                        vehicle.getSeats()
                                + " Seater"
                )
        );

        details.add(
                createDialogRow(
                        "Fuel",
                        vehicle.getFuelType()
                )
        );

        details.add(
                createDialogRow(
                        "Transmission",
                        vehicle.getTransmission()
                )
        );

        details.add(
                createDialogRow(
                        "Availability",
                        vehicle.getAvailableCount()
                                + " vehicle(s)"
                )
        );

        details.add(
                createDialogRow(
                        "Body Type",
                        vehicle.getBodyType()
                )
        );

        details.add(
                Box.createVerticalGlue()
        );

        JPanel buttons =
                new JPanel(
                        new java.awt.FlowLayout(
                                java.awt.FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        buttons.setOpaque(false);

        JButton close =
                new JButton("Close");

        close.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        close.setForeground(
                RENTIT_NAVY
        );

        close.setBackground(Color.WHITE);

        close.setFocusPainted(false);

        close.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_BLUE
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                20,
                                9,
                                20
                        )
                )
        );

        close.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        close.addActionListener(
                e -> dialog.dispose()
        );

        JButton bookNow =
                new JButton("Book Now  →");

        bookNow.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        bookNow.setForeground(Color.WHITE);

        bookNow.setBackground(RENTIT_BLUE);

        bookNow.setFocusPainted(false);

        bookNow.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        22,
                        10,
                        22
                )
        );

        bookNow.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        bookNow.addActionListener(
                e -> {

                    dialog.dispose();

                    showBookingForm(
                            vehicle
                    );
                }
        );

        buttons.add(close);
        buttons.add(bookNow);

        details.add(buttons);

        root.add(
                details,
                BorderLayout.CENTER
        );

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }
    
    public void showVehicleDetailsFor(Vehicle vehicle) {
        showVehicleDetails(vehicle);
    }

    private JPanel createDialogRow(
            String label,
            String value) {

        JPanel row =
                new JPanel(
                        new BorderLayout()
                );

        row.setOpaque(false);

        row.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        9,
                        0
                )
        );

        JLabel left =
                new JLabel(label);

        left.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        left.setForeground(TEXT_GRAY);

        JLabel right =
                new JLabel(value);

        right.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        right.setForeground(RENTIT_NAVY);

        row.add(
                left,
                BorderLayout.WEST
        );

        row.add(
                right,
                BorderLayout.EAST
        );

        return row;
    }

    private void showBookingForm(
            Vehicle vehicle) {

        JDialog dialog =
                new JDialog();

        dialog.setTitle(
                "Book " + vehicle.getModel()
        );

        dialog.setModal(true);

        dialog.setSize(
                500,
                620
        );

        dialog.setLocationRelativeTo(this);

        dialog.setResizable(false);

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(
                                0,
                                15
                        )
                );

        mainPanel.setBackground(
                Color.WHITE
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );

        // -------------------------------------------------
        // HEADER
        // -------------------------------------------------

        JLabel titleLabel =
                new JLabel(
                        "Book your "
                        + vehicle.getModel()
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        titleLabel.setForeground(
                RENTIT_NAVY
        );

        JLabel subtitleLabel =
                new JLabel(
                        "Enter your rental details below."
                );

        subtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        subtitleLabel.setForeground(
                TEXT_GRAY
        );

        JPanel headerPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                1
                        )
                );

        headerPanel.setOpaque(false);

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        // -------------------------------------------------
        // FORM
        // -------------------------------------------------

        JPanel formPanel =
                new JPanel(
                        new GridLayout(
                                8,
                                1,
                                0,
                                5
                        )
                );

        formPanel.setBackground(
                Color.WHITE
        );

        // NAME

        JLabel nameLabel =
                createFormLabel(
                        "Your name"
                );

        JTextField nameField =
                createTextField(
                        "Enter your full name"
                );

        formPanel.add(nameLabel);
        formPanel.add(nameField);

        // DAYS

        JLabel daysLabel =
                createFormLabel(
                        "Rental duration"
                );

        JSpinner daysSpinner =
                new JSpinner(
                        new SpinnerNumberModel(
                                1,
                                1,
                                30,
                                1
                        )
                );

        daysSpinner.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        JFormattedTextField spinnerText =
                ((JSpinner.DefaultEditor)
                        daysSpinner.getEditor())
                        .getTextField();

        spinnerText.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                ));

        // Keep the number on the LEFT side
        spinnerText.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        formPanel.add(daysLabel);
        formPanel.add(daysSpinner);

        // PICKUP

        JLabel pickupLabel =
                createFormLabel(
                        "Pickup location"
                );

        JTextField pickupField =
                createTextField(
                        "Enter pickup location"
                );

        formPanel.add(pickupLabel);
        formPanel.add(pickupField);

        // RETURN

        JLabel returnLabel =
                createFormLabel(
                        "Return location"
                );

        JTextField returnField =
                createTextField(
                        "Enter return location"
                );

        formPanel.add(returnLabel);
        formPanel.add(returnField);

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        // -------------------------------------------------
        // SUMMARY
        // -------------------------------------------------

        JLabel totalLabel =
                new JLabel();

        totalLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );

        totalLabel.setForeground(
                RENTIT_BLUE
        );

        JLabel creditsLabel =
                new JLabel(
                        String.format(
                                "Available credits: ₹%.2f",
                                wallet.getCredits()
                        )
                );

        creditsLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        creditsLabel.setForeground(
                RENTIT_NAVY
        );

        JPanel summaryPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                1
                        )
                );

        summaryPanel.setBackground(
                LIGHT_BLUE
        );

        summaryPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_BLUE
                        ),
                        BorderFactory.createEmptyBorder(
                                12,
                                15,
                                12,
                                15
                        )
                )
        );

        summaryPanel.add(totalLabel);
        summaryPanel.add(creditsLabel);

        // -------------------------------------------------
        // LIVE TOTAL
        // -------------------------------------------------

        Runnable updateTotal =
                () -> {

                    try {

                        int days =
                                Integer.parseInt(
                                        spinnerText.getText().trim()
                                );

                        if (days < 1) {
                            return;
                        }

                        double total =
                                vehicle.getPricePerDay()
                                * days;

                        totalLabel.setText(
                                String.format(
                                        "Total: ₹%.2f",
                                        total
                                )
                        );

                        totalLabel.revalidate();
                        totalLabel.repaint();

                    } catch (NumberFormatException ex) {

                        // User may temporarily have an empty
                        // or incomplete value while typing.
                    }
                };

        // Initial total
        updateTotal.run();

        // Update when ▲ / ▼ is clicked
        daysSpinner.addChangeListener(
                e -> updateTotal.run()
        );

        // Update while the user types directly
        spinnerText.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {

                            private void update() {
                                updateTotal.run();
                            }

                            @Override
                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e) {
                                update();
                            }

                            @Override
                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e) {
                                update();
                            }

                            @Override
                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e) {
                                update();
                            }
                        }
                );

        // Also update when the user types directly
        spinnerText.addPropertyChangeListener(
                "value",
                e -> updateTotal.run()
        );

        // -------------------------------------------------
        // BOTTOM AREA
        // -------------------------------------------------

        JPanel bottomPanel =
                new JPanel(
                        new BorderLayout(
                                0,
                                12
                        )
                );

        bottomPanel.setOpaque(false);

        bottomPanel.add(
                summaryPanel,
                BorderLayout.NORTH
        );

        JPanel buttonPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                0
                        )
                );

        buttonPanel.setOpaque(false);

        JButton cancelButton =
                new JButton(
                        "Cancel"
                );

        JButton confirmButton =
                new JButton(
                        "Confirm Booking"
                );

        styleSecondaryButton(
                cancelButton
        );

        stylePrimaryButton(
                confirmButton
        );

        cancelButton.addActionListener(
                e -> dialog.dispose()
        );

        confirmButton.addActionListener(
                e -> {

                	String customerName =
                	        getFieldValue(nameField);

                	String pickupLocation =
                	        getFieldValue(pickupField);

                	String returnLocation =
                	        getFieldValue(returnField);

                    int days =
                            (Integer)
                            daysSpinner.getValue();

                    if (customerName.isEmpty()) {

                        RentITDialog.showMessage(
                                dialog,
                                "Missing Information",
                                "Please enter your name.",
                                "OK"
                        );

                        nameField.requestFocus();

                        return;
                    }

                    if (pickupLocation.isEmpty()) {

                        RentITDialog.showMessage(
                                dialog,
                                "Missing Information",
                                "Please enter the pickup location.",
                                "OK"
                        );

                        pickupField.requestFocus();

                        return;
                    }

                    if (returnLocation.isEmpty()) {

                        RentITDialog.showMessage(
                                dialog,
                                "Missing Information",
                                "Please enter the return location.",
                                "OK"
                        );

                        returnField.requestFocus();

                        return;
                    }

                    try {

                        Booking booking =
                                new Booking(
                                        customerName,
                                        vehicle,
                                        days,
                                        pickupLocation,
                                        returnLocation,
                                        wallet
                                );

                        // Vehicle stock, customer credits and booking row are
                        // committed together by one MySQL transaction.
                        bookingManager.createBooking(booking, wallet);

                        refreshVehicles();

                        dialog.dispose();

                        RentITDialog.showMessage(
                                this,
                                "Booking Confirmed",
                                "Your booking has been confirmed!\n\n"
                                        + "Vehicle: "
                                        + vehicle.getModel()
                                        + "\nDays: "
                                        + days
                                        + "\nTotal: ₹"
                                        + booking.getTotalPrice(),
                                "OK"
                        );

                    } catch (
                            VehicleNotAvailableException
                            | InsufficientCreditsException
                            | InvalidBookingException ex) {

                        // The failed attempt may mean another instance just booked
                        // the last available vehicle. Refresh the page immediately.
                        refreshVehicles();

                    	RentITDialog.showMessage(
                    	        this,
                    	        "Booking Failed",
                    	        ex.getMessage(),
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
                confirmButton,
                BorderLayout.EAST
        );

        bottomPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        dialog.add(mainPanel);

        dialog.setVisible(true);
    }

    // =====================================================
    // =====================================================
    // BOOKING FORM HELPERS
    // =====================================================

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(RENTIT_NAVY);
        label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(TEXT_GRAY);
        field.setText(placeholder);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_BLUE),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(RENTIT_NAVY);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_GRAY);
                }
            }
        });
        return field;
    }

    private String getFieldValue(JTextField field) {
        String value = field.getText().trim();
        if (value.equalsIgnoreCase("Enter your full name")
                || value.equalsIgnoreCase("Enter pickup location")
                || value.equalsIgnoreCase("Enter return location")) {
            return "";
        }
        return value;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(RENTIT_BLUE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(RENTIT_NAVY);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_BLUE),
                BorderFactory.createEmptyBorder(9, 18, 9, 18)
        ));
    }

    // IMAGE PATHS
    // =====================================================

    private String getVehicleImagePath(
            Vehicle vehicle) {

        String model =
                vehicle.getModel()
                        .toLowerCase();

        if (model.contains("swift")) {
            return "images/maruti_swift.jpg";
        }

        if (model.contains("city")) {
            return "images/honda_city.jpg";
        }

        if (model.contains("creta")) {
            return "images/hyundai_creta.jpg";
        }

        if (model.contains("mt-15")
                || model.contains("mt15")) {
            return "images/yamaha_mt15.jpg";
        }

        if (model.contains("classic 350")) {
            return "images/royal_enfield_classic_350.jpg";
        }

        if (model.contains("duke 250")) {
            return "images/ktm_duke_250.jpg";
        }

        if (model.contains("activa")) {
            return "images/honda_activa_6g.jpg";
        }

        if (model.contains("jupiter")) {
            return "images/tvs_jupiter.jpg";
        }

        if (model.contains("ola s1")) {
            return "images/ola_s1.jpg";
        }

        if (vehicle.getVehicleType()
                .equalsIgnoreCase("Car")) {
            return "images/car.png";
        }

        if (vehicle.getVehicleType()
                .equalsIgnoreCase("Bike")) {
            return "images/bike.png";
        }

        return "images/scooter.png";
    }

    // =====================================================
    // IMAGE LOADING
    // =====================================================

    private BufferedImage loadImage(
            String path) {

        if (path == null) {
            return null;
        }

        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        try {

            File file =
                    new File(path);

            if (file.exists()) {
                BufferedImage image =
                        ImageIO.read(file);

                imageCache.put(path, image);
                return image;
            }

        } catch (IOException e) {

            System.out.println(
                    "Could not load image: "
                            + path
            );
        }

        // Remember missing images too, so a missing file is not checked
        // from disk again every time the cards are refreshed.
        imageCache.put(path, null);
        return null;
    }

    // =====================================================
    // PRICE FORMATTING
    // =====================================================

    private String formatPrice(
            double price) {

        if (price == Math.rint(price)) {
            return "₹" + (long) price;
        }

        return "₹" + price;
    }

    // =====================================================
    // SMALL SPEC ICON
    // =====================================================

    private class SpecIcon implements javax.swing.Icon {

        private final int type;

        SpecIcon(int type) {
            this.type = type;
        }

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 20;
        }

        @Override
        public void paintIcon(
                java.awt.Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(RENTIT_NAVY);

            g2.setStroke(
                    new BasicStroke(
                            1.5f
                    )
            );

            int cx = x + 10;
            int cy = y + 10;

            if (type == 1) {

                // Seat icon.
                g2.drawOval(
                        cx - 4,
                        cy - 7,
                        8,
                        8
                );

                g2.drawArc(
                        cx - 7,
                        cy + 1,
                        14,
                        10,
                        0,
                        180
                );

            } else if (type == 2) {

                // Fuel icon.
                g2.drawRoundRect(
                        cx - 5,
                        cy - 7,
                        8,
                        14,
                        2,
                        2
                );

                g2.drawLine(
                        cx + 3,
                        cy - 5,
                        cx + 7,
                        cy - 2
                );

                g2.drawLine(
                        cx + 7,
                        cy - 2,
                        cx + 7,
                        cy + 4
                );

            } else {

                // Gear icon.
                g2.drawOval(
                        cx - 5,
                        cy - 5,
                        10,
                        10
                );

                g2.drawOval(
                        cx - 2,
                        cy - 2,
                        4,
                        4
                );

                g2.drawLine(
                        cx,
                        cy - 8,
                        cx,
                        cy - 5
                );

                g2.drawLine(
                        cx,
                        cy + 5,
                        cx,
                        cy + 8
                );

                g2.drawLine(
                        cx - 8,
                        cy,
                        cx - 5,
                        cy
                );

                g2.drawLine(
                        cx + 5,
                        cy,
                        cx + 8,
                        cy
                );
            }

            g2.dispose();
        }
    }
}
