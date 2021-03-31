package com.valhalla.application.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class PropertyPanel extends JPanel {

    private NodeProperty prop;

    public PropertyPanel(NodeProperty prop) {
        this.prop = prop;
        setLayout(new MigLayout("debug, fillx","2[][max][]2", "[grow]"));
        setOpaque(false);

        JPanel inputPanel = new JPanel(new MigLayout("", "0[grow]0"));
        inputPanel.setBorder(new EmptyBorder(0,0,0,0));
        inputPanel.setOpaque(false);

        JPanel controlPanel = new JPanel(new MigLayout("", "[max]"));
        //controlPanel.setBorder(new EmptyBorder(0,0,0,0));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(new MatteBorder(0,0,1,0, new Color(255,255,255,30)));

        JPanel outputPanel = new JPanel(new MigLayout("fillx", "0[grow]0"));
        outputPanel.setBorder(new EmptyBorder(0,0,0,0));
        outputPanel.setOpaque(false);

        add(inputPanel);
        add(controlPanel);
        add(outputPanel);

        for (int i = 0; i < prop.GetInputCount(); i++) {
            inputPanel.add(new NodeConnector(), "grow, w 14!, h 14!, wrap");
        }

        if(prop.GetControl() != null) {
            JComponent control = prop.GetControl();
            //control.setMaximumSize(new Dimension(controlPanel.getWidth(), 99999));
            //control.setPreferredSize(new Dimension(1000, control.getPreferredSize().height));
            controlPanel.add(control, "grow, wrap");
        }

        for (int i = 0; i < prop.GetOutputCount(); i++) {
            outputPanel.add(new NodeConnector(), "grow, w 14!, h 14!, wrap");
        }

    }

    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int connectorWidth = 13;

        //graphics.setColor(new Color(255, 255, 255, 40));
        //graphics.fillOval(5, 5, connectorWidth, connectorWidth);

        //graphics.setColor(new Color(255, 255, 255, 200));
        //graphics.fillOval(10, nextY + 6, connectorWidth - 2, connectorWidth - 2);
        //nextY += connectorWidth + 5;
    }
}
