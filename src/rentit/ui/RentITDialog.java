package rentit.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class RentITDialog {

    // =====================================================
    // RENTIT COLORS
    // =====================================================

    private static final Color RENTIT_BLUE =
            new Color(22, 119, 232);

    private static final Color RENTIT_NAVY =
            new Color(17, 24, 39);

    private static final Color TEXT_GRAY =
            new Color(100, 110, 125);

    private static final Color LIGHT_BLUE =
            new Color(238, 246, 255);

    private static final Color BORDER_BLUE =
            new Color(216, 231, 248);

    // =====================================================
    // MESSAGE DIALOG
    // =====================================================

    public static void showMessage(
            Component parent,
            String title,
            String message,
            String buttonText) {

        JDialog dialog =
                createDialog(parent, title);

        JPanel content =
                createContentPanel();

        JLabel icon =
                createIconLabel(
                        getDialogType(title)
                );

        JLabel messageLabel =
                createMessageLabel(message);

        JPanel messagePanel =
                new JPanel(new BorderLayout(15, 0));

        messagePanel.setOpaque(false);

        messagePanel.add(
                icon,
                BorderLayout.WEST
        );

        messagePanel.add(
                messageLabel,
                BorderLayout.CENTER
        );

        content.add(
                messagePanel,
                BorderLayout.CENTER
        );

        JButton button =
                createPrimaryButton(buttonText);

        button.addActionListener(
                e -> dialog.dispose()
        );

        JPanel buttonPanel =
                createButtonPanel();

        buttonPanel.add(button);

        content.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        dialog.add(content);

        dialog.pack();

        dialog.setSize(
                Math.max(dialog.getWidth(), 430),
                Math.max(dialog.getHeight(), 210)
        );

        dialog.setLocationRelativeTo(parent);

        dialog.setVisible(true);
    }

    // =====================================================
    // CONFIRMATION DIALOG
    // =====================================================

    public static boolean showConfirm(
            Component parent,
            String title,
            String message,
            String confirmText,
            String cancelText) {

        final boolean[] confirmed =
                { false };

        JDialog dialog =
                createDialog(parent, title);

        JPanel content =
                createContentPanel();

        JLabel icon =
                createIconLabel("confirm");

        JLabel messageLabel =
                createMessageLabel(message);

        JPanel messagePanel =
                new JPanel(new BorderLayout(15, 0));

        messagePanel.setOpaque(false);

        messagePanel.add(
                icon,
                BorderLayout.WEST
        );

        messagePanel.add(
                messageLabel,
                BorderLayout.CENTER
        );

        content.add(
                messagePanel,
                BorderLayout.CENTER
        );

        JButton cancelButton =
                createSecondaryButton(cancelText);

        JButton confirmButton =
                createPrimaryButton(confirmText);

        cancelButton.addActionListener(
                e -> {
                    confirmed[0] = false;
                    dialog.dispose();
                }
        );

        confirmButton.addActionListener(
                e -> {
                    confirmed[0] = true;
                    dialog.dispose();
                }
        );

        JPanel buttonPanel =
                createButtonPanel();

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        content.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        dialog.add(content);

        dialog.pack();

        dialog.setSize(
                Math.max(dialog.getWidth(), 470),
                Math.max(dialog.getHeight(), 230)
        );

        dialog.setLocationRelativeTo(parent);

        dialog.setVisible(true);

        return confirmed[0];
    }

    // =====================================================
    // CREATE DIALOG
    // =====================================================

    private static JDialog createDialog(
            Component parent,
            String title) {

        Window owner =
                SwingUtilities.getWindowAncestor(parent);

        JDialog dialog =
                new JDialog(
                        owner,
                        title,
                        JDialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setUndecorated(false);

        dialog.setResizable(false);

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        return dialog;
    }

    // =====================================================
    // CONTENT PANEL
    // =====================================================

    private static JPanel createContentPanel() {

        JPanel content =
                new JPanel(
                        new BorderLayout(
                                0,
                                20
                        )
                );

        content.setBackground(Color.WHITE);

        content.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_BLUE
                        ),
                        BorderFactory.createEmptyBorder(
                                25,
                                28,
                                22,
                                28
                        )
                )
        );

        return content;
    }

    // =====================================================
    // ICON
    // =====================================================

    private static JLabel createIconLabel(
            String type) {

        String symbol;
        Color iconColor;
        Color iconBackground;

        if (type.equals("success")) {

            symbol = "\u2713";
            iconColor = new Color(34, 180, 100);
            iconBackground = new Color(232, 248, 238);

        } else if (type.equals("error")) {

            symbol = "\u2715";
            iconColor = new Color(220, 60, 70);
            iconBackground = new Color(255, 237, 239);

        } else if (type.equals("warning")
                || type.equals("confirm")) {

            symbol = type.equals("confirm")
                    ? "?"
                    : "\u26A0";

            iconColor = new Color(245, 158, 11);
            iconBackground = new Color(255, 248, 225);

        } else {

            symbol = "!";
            iconColor = RENTIT_BLUE;
            iconBackground = LIGHT_BLUE;
        }

        JLabel icon =
                new JLabel(
                        symbol,
                        SwingConstants.CENTER
                );

        icon.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        22
                )
        );

        icon.setForeground(iconColor);
        icon.setBackground(iconBackground);
        icon.setOpaque(true);

        icon.setPreferredSize(
                new Dimension(
                        48,
                        48
                )
        );

        return icon;
    }

    private static String getDialogType(
            String title) {

        String text =
                title == null
                        ? ""
                        : title.toLowerCase();

        if (text.contains("confirmed")
                || text.contains("success")
                || text.contains("cancelled")
                || text.contains("credits added")
                || text.contains("completed")) {

            return "success";

        } else if (text.contains("failed")
                || text.contains("error")
                || text.contains("exception")
                || text.contains("unavailable")
                || text.contains("insufficient")) {

            return "error";

        } else if (text.contains("invalid")
                || text.contains("required")
                || text.contains("missing")
                || text.contains("warning")) {

            return "warning";
        }

        return "info";
    }

    // =====================================================
    // MESSAGE
    // =====================================================

    private static JLabel createMessageLabel(
            String message) {

        JLabel label =
                new JLabel(
                        "<html><div style='width:330px;'>"
                                + message.replace(
                                        "\n",
                                        "<br>"
                                )
                                + "</div></html>"
                );

        label.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        label.setForeground(RENTIT_NAVY);

        return label;
    }

    // =====================================================
    // BUTTON PANEL
    // =====================================================

    private static JPanel createButtonPanel() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        panel.setOpaque(false);

        return panel;
    }

    // =====================================================
    // PRIMARY BUTTON
    // =====================================================

    private static JButton createPrimaryButton(
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

        button.setForeground(Color.WHITE);

        button.setBackground(RENTIT_BLUE);

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        22,
                        10,
                        22
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    // =====================================================
    // SECONDARY BUTTON
    // =====================================================

    private static JButton createSecondaryButton(
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

        button.setForeground(RENTIT_NAVY);

        button.setBackground(Color.WHITE);

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_BLUE
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                21,
                                9,
                                21
                        )
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }
}