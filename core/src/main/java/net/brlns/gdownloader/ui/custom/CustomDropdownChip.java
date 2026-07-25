/*
 * Copyright (C) 2026 hstr0100
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.brlns.gdownloader.ui.custom;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static net.brlns.gdownloader.ui.UIUtils.loadIcon;
import static net.brlns.gdownloader.ui.themes.ThemeProvider.*;
import static net.brlns.gdownloader.ui.themes.UIColors.*;

/**
 * @author Gabriel / hstr0100 / vertx010
 */
public class CustomDropdownChip extends JPanel {

    private static final int ARC = 14;

    private final JLabel textLabel;
    private final JLabel clearLabel;

    private final ImageIcon clearIcon;
    private final ImageIcon clearIconHover;

    private boolean hovered = false;
    private boolean active = false;

    @SuppressWarnings("this-escape")
    public CustomDropdownChip(String iconAsset, String defaultText, String tooltipText,
        String clearTooltipText, Consumer<CustomDropdownChip> onOpen, Runnable onClear) {
        super(new FlowLayout(FlowLayout.LEFT, 6, 0));

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 12));
        setToolTipText(tooltipText);

        JLabel iconLabel = new JLabel(loadIcon(iconAsset, ICON, 16));
        iconLabel.setOpaque(false);
        add(iconLabel);

        textLabel = new JLabel(defaultText);
        textLabel.setOpaque(false);
        textLabel.setForeground(color(FOREGROUND));
        textLabel.setFont(textLabel.getFont().deriveFont(textLabel.getFont().getSize2D() + 1.4f));
        add(textLabel);

        clearIcon = loadIcon("/assets/x-mark.png", ICON, 11);
        clearIconHover = loadIcon("/assets/x-mark.png", ICON_CLOSE, 11);

        clearLabel = new JLabel(clearIcon);
        clearLabel.setOpaque(false);
        clearLabel.setVisible(false);
        clearLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        clearLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearLabel.setToolTipText(clearTooltipText);
        clearLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClear.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                clearLabel.setIcon(clearIconHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                clearLabel.setIcon(clearIcon);
            }
        });

        add(clearLabel);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onOpen.accept(CustomDropdownChip.this);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;

                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;

                repaint();
            }
        });
    }

    public void setState(String text, boolean activeIn) {
        if (text.equals(textLabel.getText()) && active == activeIn) {
            return;
        }

        textLabel.setText(text);
        active = activeIn;
        clearLabel.setVisible(activeIn);

        revalidate();
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g.create();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color background;
            if (hovered) {
                background = color(MEDIA_CARD_HOVER);
            } else if (active) {
                background = color(SIDE_PANEL_SELECTED);
            } else {
                background = color(MEDIA_CARD);
            }

            g2d.setColor(background);
            g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        } finally {
            g2d.dispose();
        }

        super.paintComponent(g);
    }
}
