package com.valhalla.application.gui;

import javax.swing.*;
import java.awt.*;

public class NodeConnector extends JComponent {
    NodeConnector() {}

    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int connectorWidth = 15;

        graphics.setColor(new Color(255, 255, 255, 40));
        graphics.fillOval(0, 0, connectorWidth, connectorWidth);

        graphics.setColor(new Color(255, 255, 255, 200));
        graphics.fillOval(1, 1, connectorWidth - 2, connectorWidth - 2);
    }
}
