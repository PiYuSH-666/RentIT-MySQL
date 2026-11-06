package rentit.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Icon;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Rectangle;

import rentit.AuthenticationManager;
import rentit.Bike;
import rentit.BookingManager;
import rentit.Car;
import rentit.CreditWallet;
import rentit.RentalManager;
import rentit.Scooter;
import rentit.Vehicle;

public class RentITFrame extends JFrame {

    // =====================================================
    // COLORS
    // =====================================================

    private final Color RENTIT_BLUE =
            new Color(22, 119, 232);

    private final Color RENTIT_NAVY =
            new Color(17, 31, 55);

    private final Color TEXT_GRAY =
            new Color(96, 112, 132);

    private final Color BORDER_BLUE =
            new Color(214, 229, 247);

    private final Color LIGHT_BLUE =
            new Color(239, 247, 255);

    private final Color FOOTER_NAVY =
            new Color(7, 31, 64);

    // =====================================================
    // MAIN FIELDS
    // =====================================================

    private JPanel footerPanel;

    private CardLayout cardLayout;

    private JPanel contentPanel;

    private RentalManager rentalManager;

    private BookingManager bookingManager;

    private CreditWallet wallet;

    private BookingsPanel bookingsPanel;

    private VehiclesPanel vehiclesPanel;

    private JButton homeNavButton;

    private JButton vehiclesNavButton;

    private JButton bookingsNavButton;

    private JButton logoutNavButton;

    private AuthenticationManager authenticationManager;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public RentITFrame(
            AuthenticationManager authenticationManager) {

        this.authenticationManager =
                authenticationManager;

        setTitle(
                "RentIT - Vehicle Rental"
        );

        setSize(
                1200,
                720
        );

        setMinimumSize(
                new Dimension(
                        1000,
                        650
                )
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        // =================================================
        // BACKEND
        // =================================================

        rentalManager =
                createRentalManager();

        bookingManager =
                new BookingManager();

        bookingManager.setCustomer(
                authenticationManager
                        .getCurrentCustomer()
                        .getUsername()
        );

        wallet =
                authenticationManager
                        .getCurrentWallet();

        loadSavedBookings();

        // =================================================
        // MAIN
        // =================================================

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout()
                );

        mainPanel.setBackground(
                Color.WHITE
        );

        // =================================================
        // NAVIGATION
        // =================================================

        mainPanel.add(
                createNavigationBar(),
                BorderLayout.NORTH
        );

        // =================================================
        // CONTENT
        // =================================================

        cardLayout =
                new CardLayout();

        contentPanel =
                new JPanel(
                        cardLayout
                );

        contentPanel.setBackground(
                Color.WHITE
        );

        // Home is scrollable
        contentPanel.add(
                createScrollableHome(),
                "HOME"
        );

        // Existing Vehicles page
        vehiclesPanel =
                new VehiclesPanel(
                        rentalManager,
                        bookingManager,
                        wallet
                );

        contentPanel.add(
                vehiclesPanel,
                "VEHICLES"
        );

        // Existing Bookings page
        bookingsPanel =
                new BookingsPanel(
                        bookingManager,
                        wallet,
                        vehiclesPanel
                );

        contentPanel.add(
                bookingsPanel,
                "BOOKINGS"
        );

        mainPanel.add(
                contentPanel,
                BorderLayout.CENTER
        );

        // =================================================
        // FOOTER
        // =================================================

        footerPanel =
                createFooter();

        mainPanel.add(
                footerPanel,
                BorderLayout.SOUTH
        );

        // Keep footer hidden in normal window.
        footerPanel.setVisible(
                false
        );

        add(mainPanel);

        // =================================================
        // MAXIMIZED FOOTER BEHAVIOUR
        // =================================================

        addWindowStateListener(
                e -> updateFooterVisibility(e)
        );

        // =================================================
        // SAVE CUSTOMER
        // =================================================

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent e) {

                        authenticationManager
                                .saveCurrentCustomer();
                    }
                }
        );
    }

    // =====================================================
    // SCROLLABLE HOME
    // =====================================================

    private JScrollPane createScrollableHome() {

        JPanel home =
                createHomePanel();

        JScrollPane scrollPane =
                new JScrollPane(
                        home
                );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(18);

        styleScrollBar(
                scrollPane.getVerticalScrollBar()
        );

        return scrollPane;
    }

    // =====================================================
    // HOME
    // =====================================================

    private JPanel createHomePanel() {

        JPanel home =
                new JPanel(
                        new BorderLayout()
                );

        home.setBackground(
                Color.WHITE
        );

        home.add(
                new HeroPanel(),
                BorderLayout.NORTH
        );

        JPanel lower =
                new JPanel(
                        new BorderLayout()
                );

        lower.setBackground(
                Color.WHITE
        );

        lower.add(
                createCategorySection(),
                BorderLayout.NORTH
        );

        lower.add(
                createFeaturedSection(),
                BorderLayout.CENTER
        );

        home.add(
                lower,
                BorderLayout.CENTER
        );

        return home;
    }

    // =====================================================
    // HERO
    // =====================================================

    private class HeroPanel
            extends JPanel {

        private BufferedImage heroImage;

        public HeroPanel() {

            setPreferredSize(
                    new Dimension(
                            1200,
                            405
                    )
            );

            setBackground(
                    LIGHT_BLUE
            );

            heroImage =
                    loadImage(
                            "images/hero.jpg"
                    );
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width =
                    getWidth();

            int height =
                    getHeight();

            if (heroImage != null) {

                double imageRatio =
                        (double)
                                heroImage.getWidth()
                                / heroImage.getHeight();

                double panelRatio =
                        (double)
                                width
                                / height;

                int drawWidth;

                int drawHeight;

                if (imageRatio > panelRatio) {

                    drawHeight =
                            height;

                    drawWidth =
                            (int)
                                    (
                                            height
                                                    * imageRatio
                                    );

                } else {

                    drawWidth =
                            width;

                    drawHeight =
                            (int)
                                    (
                                            width
                                                    / imageRatio
                                    );
                }

                int x =
                        (
                                width
                                        - drawWidth
                        ) / 2;

                int y =
                        (
                                height
                                        - drawHeight
                        ) / 2;

                g2.drawImage(
                        heroImage,
                        x,
                        y,
                        drawWidth,
                        drawHeight,
                        null
                );

            } else {

                GradientPaint gradient =
                        new GradientPaint(
                                0,
                                0,
                                new Color(
                                        220,
                                        239,
                                        255
                                ),
                                width,
                                height,
                                Color.WHITE
                        );

                g2.setPaint(
                        gradient
                );

                g2.fillRect(
                        0,
                        0,
                        width,
                        height
                );
            }

            // Light white overlay
            g2.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            0.32f
                    )
            );

            g2.setColor(
                    Color.WHITE
            );

            g2.fillRect(
                    0,
                    0,
                    width,
                    height
            );

            g2.setComposite(
                    AlphaComposite.SrcOver
            );

            // =================================================
            // EXPLORE LINE
            // =================================================

            int left =
                    78;

            g2.setColor(
                    RENTIT_BLUE
            );

            g2.fillRoundRect(
                    left,
                    43,
                    28,
                    4,
                    4,
                    4
            );

            g2.setColor(
                    TEXT_GRAY
            );

            g2.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            15
                    )
            );

            g2.drawString(
                    "EXPLORE  •  DRIVE  •  RENT",
                    left + 42,
                    48
            );

            // =================================================
            // TITLE
            // =================================================

            g2.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            48
                    )
            );

            g2.setColor(
                    RENTIT_NAVY
            );

            g2.drawString(
                    "Your Journey",
                    left,
                    112
            );

            g2.setColor(
                    RENTIT_BLUE
            );

            g2.drawString(
                    "Starts Here",
                    left,
                    166
            );

            // =================================================
            // DESCRIPTION
            // =================================================

            g2.setColor(
                    TEXT_GRAY
            );

            g2.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            18
                    )
            );

            g2.drawString(
                    "Premium cars, bikes and scooters for every journey.",
                    left,
                    202
            );

            g2.drawString(
                    "Reliable. Affordable. Always Ready with RentIT.",
                    left,
                    228
            );

            // =================================================
            // BROWSE BUTTON
            // =================================================

            int buttonX =
                    left;

            int buttonY =
                    252;

            int buttonWidth =
                    245;

            int buttonHeight =
                    46;

            g2.setColor(
                    RENTIT_BLUE
            );

            g2.fillRoundRect(
                    buttonX,
                    buttonY,
                    buttonWidth,
                    buttonHeight,
                    12,
                    12
            );

            g2.setColor(
                    Color.WHITE
            );

            g2.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            15
                    )
            );

            g2.drawString(
                    "Browse Vehicles  →",
                    buttonX + 40,
                    buttonY + 29
            );

            // =================================================
            // HERO FEATURE ITEMS
            // =================================================

            drawHeroFeature(
                    g2,
                    80,
                    330,
                    "✓",
                    "Trusted & Secure",
                    "Safe and reliable rentals"
            );

            drawHeroFeature(
                    g2,
                    305,
                    330,
                    "₹",
                    "Affordable Rates",
                    "Great value for money"
            );

            drawHeroFeature(
                    g2,
                    535,
                    330,
                    "●",
                    "24/7 Support",
                    "We're always here"
            );

            g2.dispose();
        }

        @Override
        public void addNotify() {

            super.addNotify();

            addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseClicked(
                                MouseEvent e) {

                            if (e.getX() >= 65
                                    && e.getX() <= 340
                                    && e.getY() >= 245
                                    && e.getY() <= 315) {

                                vehiclesPanel
                                        .setSelectedCategory(
                                                "All"
                                        );

                                cardLayout.show(
                                        contentPanel,
                                        "VEHICLES"
                                );

                                setActiveNavigation(
                                        vehiclesNavButton
                                );
                            }
                        }
                    }
            );
        }
    }

    // =====================================================
    // HERO FEATURE
    // =====================================================

    private void drawHeroFeature(
            Graphics2D g2,
            int x,
            int y,
            String icon,
            String title,
            String subtitle) {

        g2.setColor(
                new Color(
                        255,
                        255,
                        255,
                        220
                )
        );

        g2.fillRoundRect(
                x,
                y,
                190,
                54,
                12,
                12
        );

        g2.setColor(
                RENTIT_BLUE
        );

        if ("✓".equals(icon)) {
            g2.setStroke(
                    new BasicStroke(
                            2.6f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            g2.drawLine(
                    x + 14,
                    y + 28,
                    x + 20,
                    y + 34
            );

            g2.drawLine(
                    x + 20,
                    y + 34,
                    x + 31,
                    y + 20
            );
        } else {
            g2.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            20
                    )
            );

            g2.drawString(
                    icon,
                    x + 15,
                    y + 33
            );
        }

        g2.setColor(
                RENTIT_NAVY
        );

        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        g2.drawString(
                title,
                x + 50,
                y + 23
        );

        g2.setColor(
                TEXT_GRAY
        );

        g2.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        10
                )
        );

        g2.drawString(
                subtitle,
                x + 50,
                y + 40
        );
    }

    // =====================================================
    // CATEGORY SECTION
    // =====================================================

    private JPanel createCategorySection() {

        JPanel section =
                new JPanel(
                        new BorderLayout()
                );

        section.setBackground(
                Color.WHITE
        );

        section.setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        70,
                        8,
                        70
                )
        );

        JLabel title =
                new JLabel(
                        "Explore by Category"
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        23
                )
        );

        title.setForeground(
                RENTIT_NAVY
        );

        JLabel subtitle =
                new JLabel(
                        "Find the perfect ride for your needs"
                );

        subtitle.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        subtitle.setForeground(
                TEXT_GRAY
        );

        JPanel heading =
                new JPanel(
                        new BorderLayout()
                );

        heading.setOpaque(false);

        heading.add(
                title,
                BorderLayout.NORTH
        );

        heading.add(
                subtitle,
                BorderLayout.SOUTH
        );

        section.add(
                heading,
                BorderLayout.NORTH
        );

        JPanel cards =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                22,
                                0
                        )
                );

        cards.setOpaque(false);

        cards.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        0,
                        8,
                        0
                )
        );

        cards.add(
                createCategoryCard(
                        "Cars",
                        "Comfortable and stylish for your every journey",
                        "images/car.png",
                        "Car"
                )
        );

        cards.add(
                createCategoryCard(
                        "Bikes",
                        "Power and performance for the open road",
                        "images/bike.png",
                        "Bike"
                )
        );

        cards.add(
                createCategoryCard(
                        "Scooters",
                        "Quick and convenient for your daily commute",
                        "images/scooter.png",
                        "Scooter"
                )
        );

        section.add(
                cards,
                BorderLayout.CENTER
        );

        return section;
    }

    // =====================================================
    // CATEGORY CARD
    // =====================================================

    private JPanel createCategoryCard(
            String title,
            String description,
            String imagePath,
            String category) {

        return new CategoryCard(
                title,
                description,
                imagePath,
                category
        );
    }

    private class CategoryCard
            extends JPanel {

        private final String title;

        private final String description;

        private final String category;

        private final BufferedImage image;

        private boolean hovered;

        public CategoryCard(
                String title,
                String description,
                String imagePath,
                String category) {

            this.title =
                    title;

            this.description =
                    description;

            this.category =
                    category;

            image =
                    loadImage(
                            imagePath
                    );

            setOpaque(false);

            setPreferredSize(
                    new Dimension(
                            320,
                            115
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

                            hovered =
                                    true;

                            repaint();
                        }

                        @Override
                        public void mouseExited(
                                MouseEvent e) {

                            hovered =
                                    false;

                            repaint();
                        }

                        @Override
                        public void mouseClicked(
                                MouseEvent e) {

                            vehiclesPanel
                                    .setSelectedCategory(
                                            category
                                    );

                            cardLayout.show(
                                    contentPanel,
                                    "VEHICLES"
                            );

                            setActiveNavigation(
                                    vehiclesNavButton
                            );
                        }
                    }
            );
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width =
                    getWidth();

            int height =
                    getHeight();

            // Shadow
            g2.setColor(
                    new Color(
                            0,
                            0,
                            0,
                            hovered ? 25 : 14
                    )
            );

            g2.fillRoundRect(
                    2,
                    4,
                    width - 4,
                    height - 5,
                    16,
                    16
            );

            // Background
            g2.setColor(
                    hovered
                            ? new Color(
                                    248,
                                    252,
                                    255
                            )
                            : new Color(
                                    248,
                                    251,
                                    255
                            )
            );

            g2.fillRoundRect(
                    0,
                    0,
                    width - 1,
                    height - 6,
                    16,
                    16
            );

            // BLUE HOVER OUTLINE
            g2.setColor(
                    hovered
                            ? RENTIT_BLUE
                            : BORDER_BLUE
            );

            g2.setStroke(
                    new BasicStroke(
                            hovered
                                    ? 1.8f
                                    : 1.0f
                    )
            );

            g2.drawRoundRect(
                    0,
                    0,
                    width - 1,
                    height - 6,
                    16,
                    16
            );

            // Image
            if (image != null) {

                double ratio =
                        (double)
                                image.getWidth()
                                / image.getHeight();

                int imageWidth =
                        105;

                int imageHeight =
                        (int)
                                (
                                        imageWidth
                                                / ratio
                                );

                if (imageHeight > 80) {

                    imageHeight =
                            80;

                    imageWidth =
                            (int)
                                    (
                                            imageHeight
                                                    * ratio
                                    );
                }

                int imageX =
                        18;

                int imageY =
                        10
                                + (
                                78
                                        - imageHeight
                        ) / 2;

                g2.drawImage(
                        image,
                        imageX,
                        imageY,
                        imageWidth,
                        imageHeight,
                        null
                );
            }

            // Text
            int textX =
                    145;

            g2.setColor(
                    RENTIT_NAVY
            );

            g2.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            18
                    )
            );

            g2.drawString(
                    title,
                    textX,
                    42
            );

            g2.setColor(
                    TEXT_GRAY
            );

            g2.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            12
                    )
            );

            String firstLine =
                    description;

            if (description.length() > 31) {

                int split =
                        description.lastIndexOf(
                                " ",
                                31
                        );

                if (split > 0) {

                    firstLine =
                            description.substring(
                                    0,
                                    split
                            );

                    String secondLine =
                            description.substring(
                                    split + 1
                            );

                    g2.drawString(
                            firstLine,
                            textX,
                            64
                    );

                    g2.drawString(
                            secondLine,
                            textX,
                            81
                    );

                } else {

                    g2.drawString(
                            description,
                            textX,
                            64
                    );
                }

            } else {

                g2.drawString(
                        description,
                        textX,
                        64
                );
            }

            // Arrow
            g2.setColor(
                    Color.WHITE
            );

            g2.fillOval(
                    width - 58,
                    35,
                    36,
                    36
            );

            g2.setColor(
                    RENTIT_BLUE
            );

            g2.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            20
                    )
            );

            g2.drawString(
                    "→",
                    width - 49,
                    59
            );

            g2.dispose();
        }
    }

    // =====================================================
    // FEATURED SECTION
    // =====================================================

    private JPanel createFeaturedSection() {

        JPanel section =
                new JPanel(
                        new BorderLayout()
                );

        section.setBackground(
                Color.WHITE
        );

        section.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        70,
                        25,
                        70
                )
        );

        JPanel heading =
                new JPanel(
                        new BorderLayout()
                );

        heading.setOpaque(false);

        JPanel text =
                new JPanel(
                        new BorderLayout()
                );

        text.setOpaque(false);

        JLabel title =
                new JLabel(
                        "Featured Vehicles"
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        23
                )
        );

        title.setForeground(
                RENTIT_NAVY
        );

        JLabel subtitle =
                new JLabel(
                        "Popular choices from our fleet"
                );

        subtitle.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        subtitle.setForeground(
                TEXT_GRAY
        );

        text.add(
                title,
                BorderLayout.NORTH
        );

        text.add(
                subtitle,
                BorderLayout.SOUTH
        );

        heading.add(
                text,
                BorderLayout.WEST
        );

        JButton viewAll =
                new JButton(
                        "View All Vehicles  →"
                );

        viewAll.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        viewAll.setForeground(
                RENTIT_BLUE
        );

        viewAll.setBorderPainted(
                false
        );

        viewAll.setContentAreaFilled(
                false
        );

        viewAll.setFocusPainted(
                false
        );

        viewAll.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        viewAll.addActionListener(
                e -> {

                    vehiclesPanel
                            .setSelectedCategory(
                                    "All"
                            );

                    cardLayout.show(
                            contentPanel,
                            "VEHICLES"
                    );

                    setActiveNavigation(
                            vehiclesNavButton
                    );
                }
        );

        heading.add(
                viewAll,
                BorderLayout.EAST
        );

        section.add(
                heading,
                BorderLayout.NORTH
        );

        JPanel cards =
                new JPanel(
                        new GridLayout(
                                1,
                                6,
                                14,
                                0
                        )
                );

        cards.setOpaque(false);

        // Use ACTUAL backend prices.
        cards.add(
                createFeaturedCard(
                        "Maruti Swift",
                        "Hatchback  |  5 Seater",
                        "₹1,200 / day",
                        "images/maruti_swift.jpg"
                )
        );

        cards.add(
                createFeaturedCard(
                        "Honda City",
                        "Sedan  |  5 Seater",
                        "₹1,800 / day",
                        "images/honda_city.jpg"
                )
        );

        cards.add(
                createFeaturedCard(
                        "Hyundai Creta",
                        "SUV  |  5 Seater",
                        "₹2,500 / day",
                        "images/hyundai_creta.jpg"
                )
        );

        cards.add(
                createFeaturedCard(
                        "Yamaha MT-15",
                        "Bike  |  155cc",
                        "₹800 / day",
                        "images/yamaha_mt15.jpg"
                )
        );

        cards.add(
                createFeaturedCard(
                        "Royal Enfield Classic 350",
                        "Bike  |  350cc",
                        "₹1,000 / day",
                        "images/royal_enfield_classic_350.jpg"
                )
        );

        cards.add(
                createFeaturedCard(
                        "Honda Activa 6G",
                        "Scooter  |  110cc",
                        "₹500 / day",
                        "images/honda_activa_6g.jpg"
                )
        );

        section.add(
                cards,
                BorderLayout.CENTER
        );

        return section;
    }

    // =====================================================
    // FEATURED CARD
    // HOVER + VIEW DETAILS
    // =====================================================

    private JPanel createFeaturedCard(
            String name,
            String specs,
            String price,
            String imagePath) {

        return new FeaturedCard(
                name,
                specs,
                price,
                imagePath
        );
    }

    private class FeaturedCard
            extends JPanel {

        private final String name;

        private final String specs;

        private final String price;

        private final BufferedImage image;

        private boolean hovered;

        public FeaturedCard(
                String name,
                String specs,
                String price,
                String imagePath) {

            this.name =
                    name;

            this.specs =
                    specs;

            this.price =
                    price;

            this.image =
                    loadImage(
                            imagePath
                    );

            setOpaque(false);

            setPreferredSize(
                    new Dimension(
                            175,
                            205
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

                        @Override
                        public void mouseClicked(
                                MouseEvent e) {

                            showFeaturedVehicleDetails(name);
                        }
                    }
            );
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width =
                    getWidth();

            int height =
                    getHeight();

            // Subtle shadow
            g2.setColor(
                    new Color(
                            0,
                            0,
                            0,
                            hovered ? 22 : 14
                    )
            );

            g2.fillRoundRect(
                    2,
                    4,
                    width - 4,
                    height - 7,
                    12,
                    12
            );

            // Card
            g2.setColor(
                    hovered
                            ? new Color(248, 252, 255)
                            : Color.WHITE
            );

            g2.fillRoundRect(
                    0,
                    0,
                    width - 1,
                    height - 7,
                    12,
                    12
            );

            g2.setColor(
                    hovered
                            ? RENTIT_BLUE
                            : BORDER_BLUE
            );

            g2.setStroke(
                    new BasicStroke(
                            hovered ? 1.8f : 1f
                    )
            );

            g2.drawRoundRect(
                    0,
                    0,
                    width - 1,
                    height - 7,
                    12,
                    12
            );

            // Image
            if (image != null) {

                double ratio =
                        (double)
                                image.getWidth()
                                / image.getHeight();

                int imageWidth =
                        140;

                int imageHeight =
                        (int)
                                (
                                        imageWidth
                                                / ratio
                                );

                if (imageHeight > 82) {

                    imageHeight =
                            82;

                    imageWidth =
                            (int)
                                    (
                                            imageHeight
                                                    * ratio
                                    );
                }

                int imageX =
                        (
                                width
                                        - imageWidth
                        ) / 2;

                g2.drawImage(
                        image,
                        imageX,
                        7,
                        imageWidth,
                        imageHeight,
                        null
                );

            } else {

                // Graceful fallback if .jpg
                // has not been added yet.
                g2.setColor(
                        LIGHT_BLUE
                );

                g2.fillRoundRect(
                        25,
                        18,
                        width - 50,
                        65,
                        10,
                        10
                );

                g2.setColor(
                        RENTIT_BLUE
                );

                g2.setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                12
                        )
                );

                g2.drawString(
                        "RentIT",
                        width / 2 - 20,
                        55
                );
            }

            // Name
            g2.setColor(
                    RENTIT_NAVY
            );

            g2.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            13
                    )
            );

            g2.drawString(
                    name,
                    12,
                    112
            );

            // Specs
            g2.setColor(
                    TEXT_GRAY
            );

            g2.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            11
                    )
            );

            g2.drawString(
                    specs,
                    12,
                    131
            );

            // Price
            g2.setColor(
                    RENTIT_NAVY
            );

            g2.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            14
                    )
            );

            g2.drawString(
                    price,
                    12,
                    156
            );

            // View Details
            int bx =
                    10;

            int by =
                    168;

            int bw =
                    width - 20;

            int bh =
                    28;

            g2.setColor(
                    Color.WHITE
            );

            g2.fillRoundRect(
                    bx,
                    by,
                    bw,
                    bh,
                    8,
                    8
            );

            g2.setColor(
                    RENTIT_BLUE
            );

            g2.drawRoundRect(
                    bx,
                    by,
                    bw,
                    bh,
                    8,
                    8
            );

            g2.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            10
                    )
            );

            g2.drawString(
                    "View Details  →",
                    bx + 27,
                    by + 19
            );

            g2.dispose();
        }
    }

    // =====================================================
    // FEATURED VEHICLE DETAILS
    // =====================================================

    private void showFeaturedVehicleDetails(
            String vehicleName) {

        if (vehiclesPanel == null
                || rentalManager == null) {
            return;
        }

        Vehicle selectedVehicle = null;

        for (Vehicle vehicle : rentalManager.getVehicles()) {

            if (vehicle.getModel()
                    .equalsIgnoreCase(vehicleName)) {

                selectedVehicle = vehicle;
                break;
            }
        }

        if (selectedVehicle == null) {
            return;
        }

        // Reuse the exact same vehicle-details dialog
        // used by the Vehicles page.
        vehiclesPanel.showVehicleDetailsFor(
                selectedVehicle
        );
    }

    private void addDetailRow(
            JPanel panel,
            String label,
            String value) {

        JLabel labelComponent =
                new JLabel(label);

        labelComponent.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        labelComponent.setForeground(
                TEXT_GRAY
        );

        JLabel valueComponent =
                new JLabel(value);

        valueComponent.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        valueComponent.setForeground(
                RENTIT_NAVY
        );

        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    // =====================================================
    // NAVIGATION BAR
    // =====================================================

    private JPanel createNavigationBar() {

        JPanel navigation =
                new JPanel(
                        new BorderLayout()
                );

        navigation.setPreferredSize(
                new Dimension(
                        1200,
                        72
                )
        );

        navigation.setBackground(
                Color.WHITE
        );

        navigation.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        BORDER_BLUE
                )
        );

        // =================================================
        // LOGO
        // =================================================

        JPanel logoPanel =
                new JPanel(
                        new BorderLayout()
                );

        logoPanel.setOpaque(false);

        logoPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        4,
                        35,
                        4,
                        0
                )
        );

        BufferedImage logoImage =
                loadImage(
                        "images/logo.png"
                );

        if (logoImage != null) {

            JLabel logo =
                    new JLabel(
                            createScaledIcon(
                                    cropLogo(
                                            logoImage
                                    ),
                                    175,
                                    58
                            )
                    );

            logoPanel.add(
                    logo,
                    BorderLayout.CENTER
            );

        } else {

            JLabel fallback =
                    new JLabel(
                            "RentIT"
                    );

            fallback.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            30
                    )
            );

            fallback.setForeground(
                    RENTIT_NAVY
            );

            logoPanel.add(
                    fallback,
                    BorderLayout.CENTER
            );
        }

        navigation.add(
                logoPanel,
                BorderLayout.WEST
        );

        // =================================================
        // CENTER NAV
        // =================================================

        JPanel center =
                new JPanel();

        center.setOpaque(false);

        homeNavButton =
                createNavButton(
                        "Home",
                        new NavIcon(
                                NavIcon.HOME
                        ),
                        true
                );

        vehiclesNavButton =
                createNavButton(
                        "Vehicles",
                        new NavIcon(
                                NavIcon.CAR
                        ),
                        false
                );

        bookingsNavButton =
                createNavButton(
                        "My Bookings",
                        new NavIcon(
                                NavIcon.CALENDAR
                        ),
                        false
                );

        center.add(
                homeNavButton
        );

        center.add(
                vehiclesNavButton
        );

        center.add(
                bookingsNavButton
        );

        navigation.add(
                center,
                BorderLayout.CENTER
        );

        // =================================================
        // RIGHT
        // =================================================

        JPanel right =
                new JPanel();

        right.setOpaque(false);

        right.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        0,
                        28
                )
        );

        right.add(
                createWelcomeButton()
        );

        logoutNavButton =
                createLogoutButton();

        right.add(
                logoutNavButton
        );

        navigation.add(
                right,
                BorderLayout.EAST
        );

        return navigation;
    }

    // =====================================================
    // NAV BUTTON
    // =====================================================

    private JButton createNavButton(
            String text,
            Icon icon,
            boolean active) {

        JButton button =
                new JButton(
                        text,
                        icon
                );

        button.setFont(
                new Font(
                        "Arial",
                        active
                                ? Font.BOLD
                                : Font.PLAIN,
                        14
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
                        : Color.WHITE
        );

        button.setFocusPainted(
                false
        );

        button.setBorderPainted(
                false
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setHorizontalTextPosition(
                SwingConstants.RIGHT
        );

        button.setIconTextGap(
                8
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        16,
                        10,
                        16
                )
        );

        if (text.equals("Home")) {

            button.addActionListener(
                    e -> {

                        cardLayout.show(
                                contentPanel,
                                "HOME"
                        );

                        setActiveNavigation(
                                homeNavButton
                        );
                    }
            );

        } else if (
                text.equals("Vehicles")) {

            button.addActionListener(
                    e -> {

                        vehiclesPanel
                                .setSelectedCategory(
                                        "All"
                                );

                        cardLayout.show(
                                contentPanel,
                                "VEHICLES"
                        );

                        setActiveNavigation(
                                vehiclesNavButton
                        );
                    }
            );

        } else {

            button.addActionListener(
                    e -> {

                        bookingsPanel
                                .refreshBookings();

                        cardLayout.show(
                                contentPanel,
                                "BOOKINGS"
                        );

                        setActiveNavigation(
                                bookingsNavButton
                        );
                    }
            );
        }

        return button;
    }

    // =====================================================
    // WELCOME CAPSULE
    // =====================================================

    private JButton createWelcomeButton() {

        String name =
                "Customer";

        if (authenticationManager
                .getCurrentCustomer() != null) {

            name =
                    authenticationManager
                            .getCurrentCustomer()
                            .getName();
        }

        JButton button =
                new JButton(
                        "Welcome, " + name,
                        new NavIcon(
                                NavIcon.USER
                        )
                );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(
                Color.WHITE
        );

        button.setBackground(
                RENTIT_BLUE
        );

        button.setFocusPainted(
                false
        );

        button.setBorderPainted(
                false
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setIconTextGap(
                7
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        9,
                        15,
                        9,
                        17
                )
        );

        return button;
    }

    // =====================================================
    // LOGOUT CAPSULE
    // =====================================================

    private JButton createLogoutButton() {

        JButton button =
                new JButton(
                        "Logout",
                        new NavIcon(
                                NavIcon.LOGOUT
                        )
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

                        g2.setColor(Color.WHITE);

                        g2.fillRoundRect(
                                0,
                                0,
                                getWidth() - 1,
                                getHeight() - 1,
                                22,
                                22
                        );

                        g2.setColor(
                                BORDER_BLUE
                        );

                        g2.setStroke(
                                new BasicStroke(1.2f)
                        );

                        g2.drawRoundRect(
                                0,
                                0,
                                getWidth() - 1,
                                getHeight() - 1,
                                22,
                                22
                        );

                        g2.dispose();

                        super.paintComponent(g);
                    }
                };

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(
                RENTIT_NAVY
        );

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setIconTextGap(7);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        15,
                        8,
                        15
                )
        );

        button.addActionListener(
                e -> logout()
        );

        return button;
    }

    // =====================================================
    // ACTIVE NAV
    // =====================================================

    private void setActiveNavigation(
            JButton activeButton) {

        JButton[] buttons = {
                homeNavButton,
                vehiclesNavButton,
                bookingsNavButton
        };

        for (JButton button :
                buttons) {

            boolean active =
                    button == activeButton;

            button.setBackground(
                    active
                            ? RENTIT_BLUE
                            : Color.WHITE
            );

            button.setForeground(
                    active
                            ? Color.WHITE
                            : RENTIT_NAVY
            );

            button.setFont(
                    new Font(
                            "Arial",
                            active
                                    ? Font.BOLD
                                    : Font.PLAIN,
                            14
                    )
            );

            Icon icon =
                    button.getIcon();

            if (icon instanceof NavIcon) {

                ((NavIcon) icon)
                        .setActive(
                                active
                        );
            }
        }

        for (JButton button :
                buttons) {

            button.repaint();
        }
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    private void logout() {

        authenticationManager
                .saveCurrentCustomer();

        authenticationManager
                .logout();

        dispose();

        AuthenticationFrame frame =
                new AuthenticationFrame();

        frame.setVisible(true);
    }

    // =====================================================
    // FOOTER
    // =====================================================

    private JPanel createFooter() {

        JPanel footer =
                new JPanel(
                        new BorderLayout()
                );

        footer.setPreferredSize(
                new Dimension(
                        1200,
                        88
                )
        );

        footer.setBackground(
                FOOTER_NAVY
        );

        footer.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        70,
                        12,
                        70
                )
        );

        // Left side: RentIT branding + student information
        JPanel left =
                new JPanel(
                        new java.awt.FlowLayout(
                                java.awt.FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        left.setOpaque(false);

        BufferedImage logoImage =
                loadImage(
                        "images/logo.png"
                );

        if (logoImage != null) {

            JLabel logo =
                    new JLabel(
                            createScaledIcon(
                                    cropLogo(logoImage),
                                    92,
                                    42
                            )
                    );

            left.add(logo);
        }

        JLabel divider =
                new JLabel(" | ");

        divider.setForeground(
                new Color(
                        180,
                        198,
                        220
                )
        );

        left.add(divider);

        JLabel information =
                new JLabel(
                        "PIYUSH  |  APPLICATION NO - 2502052271"
                                + "  |  Manipal University Jaipur"
                                + "  |  Rajasthan"
                                + "  |  PIN: XXXXX"
                                + "  |  Contact: +91 8XXXXXXX"
                );

        information.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        information.setForeground(
                new Color(
                        220,
                        230,
                        242
                )
        );

        left.add(information);

        footer.add(
                left,
                BorderLayout.WEST
        );

        JLabel copyright =
                new JLabel(
                        "© 2025 RentIT. All rights reserved."
                );

        copyright.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        copyright.setForeground(
                new Color(
                        220,
                        230,
                        242
                )
        );

        footer.add(
                copyright,
                BorderLayout.EAST
        );

        return footer;
    }

    // =====================================================
    // FOOTER VISIBILITY
    // =====================================================

    private void updateFooterVisibility(
            WindowEvent e) {

        boolean maximized =
                (
                        e.getNewState()
                                & JFrame.MAXIMIZED_BOTH
                )
                        == JFrame.MAXIMIZED_BOTH;

        footerPanel.setVisible(
                maximized
        );

        revalidate();

        repaint();
    }

    // =====================================================
    // SCROLLBAR STYLE
    // =====================================================

    private void styleScrollBar(JScrollBar scrollBar) {

        scrollBar.setPreferredSize(
                new Dimension(12, 0)
        );

        scrollBar.setUnitIncrement(18);

        scrollBar.setOpaque(true);

        scrollBar.setBackground(
                new Color(245, 247, 250)
        );

        scrollBar.setBorder(
                BorderFactory.createEmptyBorder()
        );

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
                            JComponent c,
                            Rectangle trackBounds) {

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

                        g2.fillRect(
                                trackBounds.x,
                                trackBounds.y,
                                trackBounds.width,
                                trackBounds.height
                        );

                        g2.dispose();
                    }

                    @Override
                    protected void paintThumb(
                            Graphics g,
                            JComponent c,
                            Rectangle thumbBounds) {

                        if (!scrollbar.isEnabled()) {
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
                                thumbBounds.width - 4,
                                thumbBounds.height,
                                6,
                                6
                        );

                        g2.dispose();
                    }

                    private JButton createZeroButton() {

                        JButton button =
                                new JButton();

                        button.setPreferredSize(
                                new Dimension(0, 0)
                        );

                        button.setMinimumSize(
                                new Dimension(0, 0)
                        );

                        button.setMaximumSize(
                                new Dimension(0, 0)
                        );

                        button.setVisible(false);

                        return button;
                    }
                }
        );
    }

    // =====================================================
    // CUSTOM NAV ICON
    // =====================================================

    private class NavIcon
            implements Icon {

        static final int HOME = 1;

        static final int CAR = 2;

        static final int CALENDAR = 3;

        static final int USER = 4;

        static final int LOGOUT = 5;

        private final int type;

        private boolean active;

        NavIcon(
                int type) {

            this.type =
                    type;
        }

        void setActive(
                boolean active) {

            this.active =
                    active;
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
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2 =
                    (Graphics2D)
                            g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color color =
                    active
                            ? Color.WHITE
                            : RENTIT_NAVY;

            g2.setColor(
                    color
            );

            g2.setStroke(
                    new BasicStroke(
                            1.8f
                    )
            );

            int cx =
                    x + 10;

            int cy =
                    y + 10;

            if (type == HOME) {

                int[] xs = {
                        cx - 8,
                        cx,
                        cx + 8
                };

                int[] ys = {
                        cy - 1,
                        cy - 8,
                        cy - 1
                };

                g2.drawPolyline(
                        xs,
                        ys,
                        3
                );

                g2.drawRoundRect(
                        cx - 6,
                        cy - 1,
                        12,
                        9,
                        2,
                        2
                );

                g2.drawLine(
                        cx,
                        cy + 8,
                        cx,
                        cy + 2
                );

            } else if (type == CAR) {

                g2.drawRoundRect(
                        cx - 8,
                        cy + 1,
                        16,
                        7,
                        3,
                        3
                );

                g2.drawLine(
                        cx - 5,
                        cy + 1,
                        cx - 3,
                        cy - 4
                );

                g2.drawLine(
                        cx - 3,
                        cy - 4,
                        cx + 4,
                        cy - 4
                );

                g2.drawLine(
                        cx + 4,
                        cy - 4,
                        cx + 6,
                        cy + 1
                );

                g2.fillOval(
                        cx - 6,
                        cy + 5,
                        4,
                        4
                );

                g2.fillOval(
                        cx + 2,
                        cy + 5,
                        4,
                        4
                );

            } else if (type == CALENDAR) {

                g2.drawRoundRect(
                        cx - 7,
                        cy - 6,
                        14,
                        14,
                        2,
                        2
                );

                g2.drawLine(
                        cx - 7,
                        cy - 1,
                        cx + 7,
                        cy - 1
                );

                g2.drawLine(
                        cx - 4,
                        cy - 9,
                        cx - 4,
                        cy - 4
                );

                g2.drawLine(
                        cx + 4,
                        cy - 9,
                        cx + 4,
                        cy - 4
                );

                g2.fillOval(
                        cx - 4,
                        cy + 2,
                        2,
                        2
                );

                g2.fillOval(
                        cx + 1,
                        cy + 2,
                        2,
                        2
                );

            } else if (type == USER) {

                g2.drawOval(
                        cx - 7,
                        cy - 7,
                        14,
                        14
                );

                g2.fillOval(
                        cx - 3,
                        cy - 4,
                        6,
                        6
                );

                g2.drawArc(
                        cx - 5,
                        cy + 1,
                        10,
                        7,
                        0,
                        180
                );

            } else if (type == LOGOUT) {

                g2.drawRoundRect(
                        cx - 7,
                        cy - 7,
                        9,
                        14,
                        2,
                        2
                );

                g2.drawLine(
                        cx - 2,
                        cy,
                        cx + 8,
                        cy
                );

                g2.drawLine(
                        cx + 4,
                        cy - 4,
                        cx + 8,
                        cy
                );

                g2.drawLine(
                        cx + 8,
                        cy,
                        cx + 4,
                        cy + 4
                );
            }

            g2.dispose();
        }
    }

    // =====================================================
    // IMAGE LOADING
    // =====================================================

    private BufferedImage loadImage(
            String path) {

        try {

            File file =
                    new File(path);

            if (file.exists()) {

                return ImageIO.read(
                        file
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Could not load image: "
                            + path
            );
        }

        return null;
    }

    // =====================================================
    // LOGO CROP
    // =====================================================

    private BufferedImage cropLogo(
            BufferedImage original) {

        int width =
                original.getWidth();

        int height =
                original.getHeight();

        int top = 0;

        int bottom =
                height - 1;

        for (
                int y = 0;
                y < height;
                y++) {

            long brightness = 0;

            for (
                    int x = 0;
                    x < width;
                    x++) {

                int rgb =
                        original.getRGB(
                                x,
                                y
                        );

                int r =
                        (rgb >> 16)
                                & 0xFF;

                int gr =
                        (rgb >> 8)
                                & 0xFF;

                int b =
                        rgb & 0xFF;

                brightness +=
                        (
                                r
                                        + gr
                                        + b
                        ) / 3;
            }

            double average =
                    (double)
                            brightness
                            / width;

            if (average > 180) {

                top = y;

                break;
            }
        }

        for (
                int y = height - 1;
                y >= 0;
                y--) {

            long brightness = 0;

            for (
                    int x = 0;
                    x < width;
                    x++) {

                int rgb =
                        original.getRGB(
                                x,
                                y
                        );

                int r =
                        (rgb >> 16)
                                & 0xFF;

                int gr =
                        (rgb >> 8)
                                & 0xFF;

                int b =
                        rgb & 0xFF;

                brightness +=
                        (
                                r
                                        + gr
                                        + b
                        ) / 3;
            }

            double average =
                    (double)
                            brightness
                            / width;

            if (average > 180) {

                bottom = y;

                break;
            }
        }

        int cropHeight =
                bottom
                        - top
                        + 1;

        if (cropHeight <= 0) {

            return original;
        }

        return original.getSubimage(
                0,
                top,
                width,
                cropHeight
        );
    }

    // =====================================================
    // SCALE IMAGE
    // =====================================================

    private ImageIcon createScaledIcon(
            BufferedImage image,
            int width,
            int height) {

        double ratio =
                (double)
                        image.getWidth()
                        / image.getHeight();

        int newWidth =
                width;

        int newHeight =
                (int)
                        (
                                width
                                        / ratio
                        );

        if (newHeight > height) {

            newHeight =
                    height;

            newWidth =
                    (int)
                            (
                                    height
                                            * ratio
                            );
        }

        java.awt.Image scaled =
                image.getScaledInstance(
                        newWidth,
                        newHeight,
                        java.awt.Image.SCALE_SMOOTH
                );

        return new ImageIcon(
                scaled
        );
    }

    // =====================================================
    // RENTAL MANAGER
    // =====================================================

    private RentalManager createRentalManager() {

        RentalManager manager =
                new RentalManager();

        // =================================================
        // CARS
        // =================================================

        manager.addVehicle(
                new Car(
                        "CAR001",
                        "Maruti Swift",
                        1200,
                        3,
                        "Petrol",
                        "Manual",
                        5,
                        "Hatchback"
                )
        );

        manager.addVehicle(
                new Car(
                        "CAR002",
                        "Honda City",
                        1800,
                        2,
                        "Petrol",
                        "Automatic",
                        5,
                        "Sedan"
                )
        );

        manager.addVehicle(
                new Car(
                        "CAR003",
                        "Hyundai Creta",
                        2500,
                        1,
                        "Diesel",
                        "Automatic",
                        5,
                        "SUV"
                )
        );

        // =================================================
        // BIKES
        // =================================================

        manager.addVehicle(
                new Bike(
                        "BIKE001",
                        "Yamaha MT-15",
                        800,
                        2,
                        "Petrol",
                        "Manual",
                        2,
                        "Street Bike"
                )
        );

        manager.addVehicle(
                new Bike(
                        "BIKE002",
                        "Royal Enfield Classic 350",
                        1000,
                        2,
                        "Petrol",
                        "Manual",
                        2,
                        "Cruiser"
                )
        );

        manager.addVehicle(
                new Bike(
                        "BIKE003",
                        "KTM Duke 250",
                        1200,
                        1,
                        "Petrol",
                        "Manual",
                        2,
                        "Naked Bike"
                )
        );

        // =================================================
        // SCOOTERS
        // =================================================

        manager.addVehicle(
                new Scooter(
                        "SCOOTER001",
                        "Honda Activa 6G",
                        500,
                        3,
                        "Petrol",
                        "Automatic",
                        2,
                        "Scooter"
                )
        );

        manager.addVehicle(
                new Scooter(
                        "SCOOTER002",
                        "TVS Jupiter",
                        500,
                        2,
                        "Petrol",
                        "Automatic",
                        2,
                        "Scooter"
                )
        );

        manager.addVehicle(
                new Scooter(
                        "SCOOTER003",
                        "Ola S1",
                        700,
                        2,
                        "Electric",
                        "Automatic",
                        2,
                        "Electric Scooter"
                )
        );

        // Load the shared fleet availability after all vehicles exist.
        manager.loadSavedAvailability();

        return manager;
    }

    // =====================================================
    // BOOKINGS
    // =====================================================

    private void loadSavedBookings() {

        bookingManager.loadBookings(
                rentalManager,
                wallet
        );
    }

    // =====================================================
    // MAIN
    // =====================================================

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