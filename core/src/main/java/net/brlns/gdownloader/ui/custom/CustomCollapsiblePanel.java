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

import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import lombok.Getter;

/**
 * @author Gabriel / hstr0100 / vertx010
 */
public class CustomCollapsiblePanel extends JPanel {

    @Getter
    private boolean collapsed = true;

    @SuppressWarnings("this-escape")
    public CustomCollapsiblePanel(LayoutManager layout) {
        super(layout);

        setOpaque(false);
    }

    public void setCollapsed(boolean collapsedIn) {
        if (collapsed == collapsedIn) {
            return;
        }

        collapsed = collapsedIn;

        revalidate();
        repaint();
    }

    public Dimension getExpandedPreferredSize() {
        return super.getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return collapsed ? new Dimension(0, 0) : super.getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return collapsed ? new Dimension(0, 0) : super.getMinimumSize();
    }
}
