package rentit.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import rentit.AuthenticationManager;

public class LoginPanel extends JPanel {

    // =====================================================
    // COLORS
    // =====================================================

    private final Color RENTIT_BLUE =
            new Color(22, 119, 232);

    private final Color RENTIT_BLUE_DARK =
            new Color(15, 96, 190);

    private final Color RENTIT_NAVY =
            new Color(17, 24, 39);

    private final Color TEXT_GRAY =
            new Color(100, 110, 125);

    private final Color BORDER_BLUE =
            new Color(205, 220, 238);

    private final Color FOCUS_BLUE =
            new Color(22, 119, 232);

    private final Color WARNING_ORANGE =
            new Color(245, 158, 11);

    private final Color ERROR_RED =
            new Color(220, 60, 70);

    // =====================================================
    // BACKEND / CALLBACKS
    // =====================================================

    private AuthenticationManager authenticationManager;

    private Runnable onLoginSuccess;
    private Runnable onSignup;

    // =====================================================
    // FIELDS
    // =====================================================

    private RoundedTextField usernameField;
    private RoundedPasswordField passwordField;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public LoginPanel(
            AuthenticationManager authenticationManager,
            Runnable onLoginSuccess,
            Runnable onSignup) {

        this.authenticationManager =
                authenticationManager;

        this.onLoginSuccess =
                onLoginSuccess;

        this.onSignup =
                onSignup;

        setLayout(new BorderLayout());
        setOpaque(false);

        createUI();
    }

    // =====================================================
    // CREATE UI
    // =====================================================

    private void createUI() {

        BackgroundPanel centerPanel =
                new BackgroundPanel();

        centerPanel.setLayout(
                new GridBagLayout()
        );

        centerPanel.setOpaque(false);

        RoundedPanel card =
                new RoundedPanel();

        card.setLayout(
                new GridBagLayout()
        );

        card.setPreferredSize(
                new Dimension(470, 570)
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1;

        // -------------------------------------------------
        // LOGO
        // -------------------------------------------------

        gbc.gridy = 0;

        gbc.insets =
                new Insets(0, 0, 2, 0);

        card.add(
                createLogoLabel(),
                gbc
        );

        // -------------------------------------------------
        // TITLE
        // -------------------------------------------------

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 2, 0);

        JLabel title =
                new JLabel(
                        "Welcome back",
                        SwingConstants.CENTER
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        27
                )
        );

        title.setForeground(
                RENTIT_NAVY
        );

        card.add(title, gbc);

        // -------------------------------------------------
        // SUBTITLE
        // -------------------------------------------------

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 16, 0);

        JLabel subtitle =
                new JLabel(
                        "Login to continue to RentIT",
                        SwingConstants.CENTER
                );

        subtitle.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        subtitle.setForeground(
                TEXT_GRAY
        );

        card.add(subtitle, gbc);

        // -------------------------------------------------
        // USERNAME LABEL
        // -------------------------------------------------

        gbc.gridy++;

        gbc.insets =
                new Insets(4, 0, 5, 0);

        card.add(
                createLabel("Username"),
                gbc
        );

        // -------------------------------------------------
        // USERNAME FIELD
        // -------------------------------------------------

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 7, 0);

        usernameField =
                createTextField(
                        "Enter your username"
                );

        card.add(
                usernameField,
                gbc
        );

        // -------------------------------------------------
        // PASSWORD LABEL
        // -------------------------------------------------

        gbc.gridy++;

        gbc.insets =
                new Insets(8, 0, 5, 0);

        card.add(
                createLabel("Password"),
                gbc
        );

        // -------------------------------------------------
        // PASSWORD FIELD
        // -------------------------------------------------

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 5, 0);

        passwordField =
                createPasswordField(
                        "Enter your password"
                );

        card.add(
                passwordField,
                gbc
        );

        // -------------------------------------------------
        // SHOW PASSWORD
        // -------------------------------------------------

        gbc.gridy++;

        gbc.insets =
                new Insets(2, 0, 8, 0);

        JCheckBox showPassword =
                new JCheckBox(
                        "Show password"
                );

        showPassword.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        showPassword.setForeground(
                TEXT_GRAY
        );

        showPassword.setOpaque(false);
        showPassword.setFocusPainted(false);
        showPassword.setBorderPainted(false);

        showPassword.setIcon(
                new CheckBoxIcon(false)
        );

        showPassword.setSelectedIcon(
                new CheckBoxIcon(true)
        );

        showPassword.setIconTextGap(8);

        showPassword.addActionListener(
                e -> {

                    if (showPassword.isSelected()) {

                        passwordField.setEchoChar(
                                (char) 0
                        );

                    } else {

                        passwordField.setEchoChar(
                                '\u2022'
                        );
                    }

                    passwordField.repaint();
                }
        );

        card.add(
                showPassword,
                gbc
        );

        // -------------------------------------------------
        // LOGIN BUTTON
        // -------------------------------------------------

        gbc.gridy++;

        gbc.insets =
                new Insets(8, 0, 8, 0);

        JButton loginButton =
                new JButton(
                        "Login  →"
                );

        stylePrimaryButton(
                loginButton
        );

        loginButton.addActionListener(
                e -> handleLogin()
        );

        card.add(
                loginButton,
                gbc
        );

        // -------------------------------------------------
        // OR
        // -------------------------------------------------

        gbc.gridy++;

        gbc.insets =
                new Insets(3, 0, 4, 0);

        JLabel orLabel =
                new JLabel(
                        "────────  OR  ────────",
                        SwingConstants.CENTER
                );

        orLabel.setForeground(
                new Color(
                        145,
                        155,
                        170
                )
        );

        orLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        card.add(
                orLabel,
                gbc
        );

        // -------------------------------------------------
        // SIGNUP BUTTON
        // -------------------------------------------------

        gbc.gridy++;

        gbc.insets =
                new Insets(7, 0, 0, 0);

        JButton signupButton =
                new JButton(
                        "Create a new account"
                );

        styleSecondaryButton(
                signupButton
        );

        signupButton.addActionListener(
                e -> showSignup()
        );

        card.add(
                signupButton,
                gbc
        );

        centerPanel.add(card);

        add(
                centerPanel,
                BorderLayout.CENTER
        );
    }

    // =====================================================
    // LOGO
    // =====================================================

    private JLabel createLogoLabel() {

        File logoFile =
                new File(
                        "images/logo.png"
                );

        if (logoFile.exists()) {

            try {

                BufferedImage image =
                        ImageIO.read(
                                logoFile
                        );

                int width = 185;

                int height =
                        (int)
                        (
                                image.getHeight()
                                *
                                (
                                        width
                                        /
                                        (double)
                                        image.getWidth()
                                )
                        );

                ImageIcon icon =
                        new ImageIcon(
                                image.getScaledInstance(
                                        width,
                                        height,
                                        java.awt.Image.SCALE_SMOOTH
                                )
                        );

                JLabel logo =
                        new JLabel(
                                icon,
                                SwingConstants.CENTER
                        );

                logo.setPreferredSize(
                        new Dimension(
                                300,
                                82
                        )
                );

                return logo;

            } catch (Exception e) {
                // Use text fallback.
            }
        }

        JLabel logo =
                new JLabel(
                        "RentIT",
                        SwingConstants.CENTER
                );

        logo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        38
                )
        );

        logo.setForeground(
                RENTIT_NAVY
        );

        logo.setPreferredSize(
                new Dimension(
                        300,
                        82
                )
        );

        return logo;
    }

    // =====================================================
    // LABEL
    // =====================================================

    private JLabel createLabel(
            String text) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        label.setForeground(
                RENTIT_NAVY
        );

        return label;
    }

    // =====================================================
    // TEXT FIELD
    // =====================================================

    private RoundedTextField createTextField(
            String placeholder) {

        RoundedTextField field =
                new RoundedTextField(
                        placeholder
                );

        field.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        field.setForeground(
                RENTIT_NAVY
        );

        field.setPreferredSize(
                new Dimension(
                        350,
                        46
                )
        );

        return field;
    }

    // =====================================================
    // PASSWORD FIELD
    // =====================================================

    private RoundedPasswordField createPasswordField(
            String placeholder) {

        RoundedPasswordField field =
                new RoundedPasswordField(
                        placeholder
                );

        field.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        field.setForeground(
                RENTIT_NAVY
        );

        field.setEchoChar(
                '\u2022'
        );

        field.setPreferredSize(
                new Dimension(
                        350,
                        46
                )
        );

        return field;
    }

    // =====================================================
    // PRIMARY BUTTON
    // =====================================================

    private void stylePrimaryButton(
            JButton button) {

        button.setBackground(
                RENTIT_BLUE
        );

        button.setForeground(
                Color.WHITE
        );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        button.setPreferredSize(
                new Dimension(
                        350,
                        48
                )
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e) {

                        button.setBackground(
                                RENTIT_BLUE_DARK
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e) {

                        button.setBackground(
                                RENTIT_BLUE
                        );
                    }
                }
        );
    }

    // =====================================================
    // SECONDARY BUTTON
    // =====================================================

    private void styleSecondaryButton(
            JButton button) {

        button.setBackground(
                Color.WHITE
        );

        button.setForeground(
                RENTIT_BLUE
        );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        button.setPreferredSize(
                new Dimension(
                        350,
                        46
                )
        );

        button.setFocusPainted(false);
        button.setOpaque(true);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                RENTIT_BLUE,
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                0,
                                10,
                                0,
                                10
                        )
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e) {

                        button.setBackground(
                                new Color(
                                        244,
                                        249,
                                        255
                                )
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e) {

                        button.setBackground(
                                Color.WHITE
                        );
                    }
                }
        );
    }

    // =====================================================
    // LOGIN
    // =====================================================

    private void handleLogin() {

        String username =
                usernameField
                        .getText()
                        .trim();

        String password =
                new String(
                        passwordField
                                .getPassword()
                );

        // -------------------------------------------------
        // EMPTY FIELDS
        // -------------------------------------------------

        if (username.isEmpty()
                || password.isEmpty()) {

            showModernDialog(
                    "Login required",
                    "Please enter your username and password.",
                    "warning"
            );

            return;
        }

        // -------------------------------------------------
        // AUTHENTICATION
        // -------------------------------------------------

        boolean success =
                authenticationManager.login(
                        username,
                        password
                );

        if (success) {

            onLoginSuccess.run();

        } else {

            showModernDialog(
                    "Login failed",
                    "Invalid username or password.",
                    "error"
            );
        }
    }

    // =====================================================
    // SIGNUP
    // =====================================================

    private void showSignup() {

        onSignup.run();
    }

    // =====================================================
    // MODERN DIALOG
    // =====================================================

    private void showModernDialog(
            String title,
            String message,
            String type) {

        Window owner =
                SwingUtilities.getWindowAncestor(
                        this
                );

        JDialog dialog =
                new JDialog(
                        owner,
                        title,
                        Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setSize(
                440,
                240
        );

        dialog.setResizable(false);

        dialog.setLocationRelativeTo(
                owner
        );

        RoundedPanel panel =
                new RoundedPanel();

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        28,
                        30,
                        22,
                        30
                )
        );

        panel.setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );

        // -------------------------------------------------
        // ICON
        // -------------------------------------------------

        boolean error =
                "error".equals(type);

        boolean warning =
                "warning".equals(type);

        Color iconColor;

        if (error) {

            iconColor = ERROR_RED;

        } else if (warning) {

            iconColor = WARNING_ORANGE;

        } else {

            iconColor = new Color(
                    34,
                    180,
                    100
            );
        }

        JPanel iconCircle =
                new DialogIconPanel(
                        iconColor,
                        error ? "error"
                               : warning ? "warning"
                                         : "success"
                );

        iconCircle.setPreferredSize(
                new Dimension(
                        55,
                        55
                )
        );

        iconCircle.setLayout(
                new BorderLayout()
        );

        // -------------------------------------------------
        // TEXT
        // -------------------------------------------------

        JPanel textPanel =
                new JPanel();

        textPanel.setOpaque(false);

        textPanel.setLayout(
                new BorderLayout(
                        0,
                        6
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );

        titleLabel.setForeground(
                RENTIT_NAVY
        );

        JLabel messageLabel =
                new JLabel(
                        "<html><div style='width:285px;'>"
                        + message
                        + "</div></html>"
                );

        messageLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        messageLabel.setForeground(
                TEXT_GRAY
        );

        textPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        textPanel.add(
                messageLabel,
                BorderLayout.CENTER
        );

        JPanel content =
                new JPanel(
                        new BorderLayout(
                                15,
                                0
                        )
                );

        content.setOpaque(false);

        content.add(
                iconCircle,
                BorderLayout.WEST
        );

        content.add(
                textPanel,
                BorderLayout.CENTER
        );

        panel.add(
                content,
                BorderLayout.CENTER
        );

        // -------------------------------------------------
        // OK BUTTON
        // -------------------------------------------------

        JButton okButton =
                new JButton("OK");

        stylePrimaryButton(
                okButton
        );

        okButton.setPreferredSize(
                new Dimension(
                        105,
                        40
                )
        );

        okButton.addActionListener(
                e -> dialog.dispose()
        );

        JPanel buttonPanel =
                new JPanel();

        buttonPanel.setOpaque(false);

        buttonPanel.add(
                okButton
        );

        panel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        dialog.add(panel);

        dialog.setVisible(true);
    }

    // =====================================================
    // BACKGROUND PANEL
    // =====================================================

    private class BackgroundPanel
            extends JPanel {

        private BufferedImage backgroundImage;

        public BackgroundPanel() {

            backgroundImage =
                    loadBackground();

            setOpaque(false);
        }

        private BufferedImage loadBackground() {

            try {

                File file =
                        new File(
                                "images/auth_background.jpg"
                        );

                if (file.exists()) {

                    return ImageIO.read(
                            file
                    );
                }

            } catch (Exception e) {
                // Use fallback background.
            }

            return null;
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 =
                    (Graphics2D)
                    g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width =
                    getWidth();

            int height =
                    getHeight();

            // -------------------------------------------------
            // AUTOMOTIVE IMAGE
            // -------------------------------------------------

            if (backgroundImage != null) {

                double imageRatio =
                        backgroundImage.getWidth()
                        /
                        (double)
                        backgroundImage.getHeight();

                double panelRatio =
                        width
                        /
                        (double)
                        height;

                int drawWidth;
                int drawHeight;

                if (imageRatio > panelRatio) {

                    drawHeight = height;

                    drawWidth =
                            (int)
                            (
                                    height
                                    * imageRatio
                            );

                } else {

                    drawWidth = width;

                    drawHeight =
                            (int)
                            (
                                    width
                                    / imageRatio
                            );
                }

                int x =
                        (width - drawWidth) / 2;

                int y =
                        (height - drawHeight) / 2;

                g2.drawImage(
                        backgroundImage,
                        x,
                        y,
                        drawWidth,
                        drawHeight,
                        null
                );

                // Soft blue-white overlay.
                g2.setColor(
                        new Color(
                                235,
                                245,
                                255,
                                155
                        )
                );

                g2.fillRect(
                        0,
                        0,
                        width,
                        height
                );

            } else {

                // -------------------------------------------------
                // FALLBACK BACKGROUND
                // -------------------------------------------------

                g2.setColor(
                        new Color(
                                245,
                                248,
                                252
                        )
                );

                g2.fillRect(
                        0,
                        0,
                        width,
                        height
                );

                g2.setColor(
                        new Color(
                                215,
                                233,
                                255,
                                150
                        )
                );

                g2.fillOval(
                        -140,
                        -120,
                        430,
                        430
                );

                g2.fillOval(
                        width - 300,
                        height - 260,
                        470,
                        470
                );
            }

            g2.dispose();
        }
    }

    // =====================================================
    // ROUNDED CARD
    // =====================================================

    private class RoundedPanel
            extends JPanel {

        public RoundedPanel() {

            setOpaque(false);

            setBorder(
                    BorderFactory.createEmptyBorder(
                            32,
                            50,
                            30,
                            50
                    )
            );
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D)
                    g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width =
                    getWidth();

            int height =
                    getHeight();

            // -------------------------------------------------
            // SHADOW
            // -------------------------------------------------

            g2.setColor(
                    new Color(
                            0,
                            0,
                            0,
                            25
                    )
            );

            g2.fillRoundRect(
                    6,
                    8,
                    width - 12,
                    height - 12,
                    30,
                    30
            );

            // -------------------------------------------------
            // WHITE CARD
            // -------------------------------------------------

            g2.setColor(
                    Color.WHITE
            );

            g2.fillRoundRect(
                    0,
                    0,
                    width - 8,
                    height - 8,
                    30,
                    30
            );

            // -------------------------------------------------
            // BORDER
            // -------------------------------------------------

            g2.setColor(
                    new Color(
                            225,
                            233,
                            242
                    )
            );

            g2.setStroke(
                    new BasicStroke(
                            1f
                    )
            );

            g2.drawRoundRect(
                    0,
                    0,
                    width - 9,
                    height - 9,
                    30,
                    30
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =====================================================
    // ROUNDED TEXT FIELD
    // =====================================================

    private class RoundedTextField
            extends JTextField {

        private String placeholder;

        private boolean focused;

        public RoundedTextField(
                String placeholder) {

            this.placeholder =
                    placeholder;

            setOpaque(false);

            setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            16,
                            0,
                            16
                    )
            );

            addFocusListener(
                    new java.awt.event.FocusAdapter() {

                        @Override
                        public void focusGained(
                                java.awt.event.FocusEvent e) {

                            focused = true;
                            repaint();
                        }

                        @Override
                        public void focusLost(
                                java.awt.event.FocusEvent e) {

                            focused = false;
                            repaint();
                        }
                    }
            );
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D)
                    g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width =
                    getWidth();

            int height =
                    getHeight();

            g2.setColor(
                    Color.WHITE
            );

            g2.fillRoundRect(
                    0,
                    0,
                    width - 1,
                    height - 1,
                    14,
                    14
            );

            g2.setColor(
                    focused
                            ? FOCUS_BLUE
                            : BORDER_BLUE
            );

            g2.setStroke(
                    new BasicStroke(
                            focused ? 1.6f : 1f
                    )
            );

            g2.drawRoundRect(
                    0,
                    0,
                    width - 1,
                    height - 1,
                    14,
                    14
            );

            g2.dispose();

            super.paintComponent(g);

            if (getText().isEmpty()
                    && !focused) {

                Graphics2D placeholderGraphics =
                        (Graphics2D)
                        g.create();

                placeholderGraphics.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                placeholderGraphics.setFont(
                        getFont()
                );

                placeholderGraphics.setColor(
                        new Color(
                                160,
                                170,
                                185
                        )
                );

                placeholderGraphics.drawString(
                        placeholder,
                        16,
                        getHeight() / 2
                                + getFontMetrics(
                                        getFont()
                                ).getAscent() / 2
                                - 2
                );

                placeholderGraphics.dispose();
            }
        }
    }

    // =====================================================
    // ROUNDED PASSWORD FIELD
    // =====================================================

    private class RoundedPasswordField
            extends JPasswordField {

        private String placeholder;

        private boolean focused;

        public RoundedPasswordField(
                String placeholder) {

            this.placeholder =
                    placeholder;

            setOpaque(false);

            setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            16,
                            0,
                            16
                    )
            );

            addFocusListener(
                    new java.awt.event.FocusAdapter() {

                        @Override
                        public void focusGained(
                                java.awt.event.FocusEvent e) {

                            focused = true;
                            repaint();
                        }

                        @Override
                        public void focusLost(
                                java.awt.event.FocusEvent e) {

                            focused = false;
                            repaint();
                        }
                    }
            );
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D)
                    g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width =
                    getWidth();

            int height =
                    getHeight();

            g2.setColor(
                    Color.WHITE
            );

            g2.fillRoundRect(
                    0,
                    0,
                    width - 1,
                    height - 1,
                    14,
                    14
            );

            g2.setColor(
                    focused
                            ? FOCUS_BLUE
                            : BORDER_BLUE
            );

            g2.setStroke(
                    new BasicStroke(
                            focused ? 1.6f : 1f
                    )
            );

            g2.drawRoundRect(
                    0,
                    0,
                    width - 1,
                    height - 1,
                    14,
                    14
            );

            g2.dispose();

            super.paintComponent(g);

            if (getPassword().length == 0
                    && !focused) {

                Graphics2D placeholderGraphics =
                        (Graphics2D)
                        g.create();

                placeholderGraphics.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                placeholderGraphics.setFont(
                        getFont()
                );

                placeholderGraphics.setColor(
                        new Color(
                                160,
                                170,
                                185
                        )
                );

                placeholderGraphics.drawString(
                        placeholder,
                        16,
                        getHeight() / 2
                                + getFontMetrics(
                                        getFont()
                                ).getAscent() / 2
                                - 2
                );

                placeholderGraphics.dispose();
            }
        }
    }

    // =====================================================
    // CUSTOM CHECKBOX ICON
    // =====================================================

    private class CheckBoxIcon
            implements Icon {

        private boolean selected;

        public CheckBoxIcon(
                boolean selected) {

            this.selected =
                    selected;
        }

        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }

        @Override
        public void paintIcon(
                java.awt.Component c,
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

            if (selected) {

                g2.setColor(
                        RENTIT_BLUE
                );

                g2.fillRoundRect(
                        x,
                        y,
                        17,
                        17,
                        5,
                        5
                );

                g2.setColor(
                        Color.WHITE
                );

                g2.setStroke(
                        new BasicStroke(
                                2f
                        )
                );

                g2.drawLine(
                        x + 4,
                        y + 9,
                        x + 7,
                        y + 12
                );

                g2.drawLine(
                        x + 7,
                        y + 12,
                        x + 13,
                        y + 5
                );

            } else {

                g2.setColor(
                        Color.WHITE
                );

                g2.fillRoundRect(
                        x,
                        y,
                        17,
                        17,
                        5,
                        5
                );

                g2.setColor(
                        BORDER_BLUE
                );

                g2.setStroke(
                        new BasicStroke(
                                1.2f
                        )
                );

                g2.drawRoundRect(
                        x,
                        y,
                        17,
                        17,
                        5,
                        5
                );
            }

            g2.dispose();
        }
    }

    // =====================================================
    // CIRCLE PANEL FOR DIALOG ICON
    // =====================================================

    private class CirclePanel
            extends JPanel {

        private Color color;

        public CirclePanel(
                Color color) {

            this.color = color;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D)
                    g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);

            g2.fillOval(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }
 // =====================================================
 // MODERN DIALOG ICON
 // =====================================================

 private class DialogIconPanel extends JPanel {

     private Color color;
     private String type;

     public DialogIconPanel(
             Color color,
             String type) {

         this.color = color;
         this.type = type;

         setOpaque(false);
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

         int width =
                 getWidth();

         int height =
                 getHeight();

         // -------------------------------------------------
         // CIRCLE
         // -------------------------------------------------

         g2.setColor(color);

         g2.fillOval(
                 0,
                 0,
                 width - 1,
                 height - 1
         );

         // -------------------------------------------------
         // ICON
         // -------------------------------------------------

         g2.setColor(
                 Color.WHITE
         );

         g2.setStroke(
                 new BasicStroke(
                         3f,
                         BasicStroke.CAP_ROUND,
                         BasicStroke.JOIN_ROUND
                 )
         );

         int centerX =
                 width / 2;

         int centerY =
                 height / 2;

         // -------------------------------------------------
         // ERROR: DRAW X
         // -------------------------------------------------

         if ("error".equals(type)) {

             g2.drawLine(
                     centerX - 9,
                     centerY - 9,
                     centerX + 9,
                     centerY + 9
             );

             g2.drawLine(
                     centerX + 9,
                     centerY - 9,
                     centerX - 9,
                     centerY + 9
             );

         }

         // -------------------------------------------------
         // SUCCESS: DRAW CHECK
         // -------------------------------------------------

         else if ("success".equals(type)) {

             g2.drawLine(
                     centerX - 10,
                     centerY,
                     centerX - 3,
                     centerY + 7
             );

             g2.drawLine(
                     centerX - 3,
                     centerY + 7,
                     centerX + 11,
                     centerY - 8
             );

         }

         // -------------------------------------------------
         // WARNING: DRAW !
         // -------------------------------------------------

         else {

             g2.setFont(
                     new Font(
                             "Arial",
                             Font.BOLD,
                             25
                     )
             );

             String text = "!";

             int textWidth =
                     g2.getFontMetrics()
                        .stringWidth(text);

             int textHeight =
                     g2.getFontMetrics()
                        .getAscent();

             g2.drawString(
                     text,
                     centerX - textWidth / 2,
                     centerY
                             + textHeight / 2
                             - 2
             );
         }

         g2.dispose();
     }
 }
}