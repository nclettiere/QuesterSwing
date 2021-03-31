package com.valhalla.application.gui;

import com.valhalla.core.Ref;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

public class PropertyPanel extends JPanel {

    private NodeProperty prop;

    public PropertyPanel(NodeProperty prop, Node node) {
        this.prop = prop;
        setLayout(new MigLayout("fillx","2[14][grow][14]0", ""));
        setBorder(new MatteBorder(0,0,1,0, new Color(255,255,255,30)));
        setOpaque(false);

        JPanel inputPanel = new JPanel(new MigLayout("", "0[grow]0"));
        inputPanel.setBorder(new EmptyBorder(0,0,0,0));
        inputPanel.setOpaque(false);

        JPanel controlPanel = new JPanel(new MigLayout("", "[max]"));
        controlPanel.setOpaque(false);

        JPanel outputPanel = new JPanel(new MigLayout("fillx", "0[grow]0"));

        outputPanel.setOpaque(false);

        add(inputPanel);
        add(controlPanel);
        add(outputPanel);

        for (NodeData nData : prop.GetInputs()) {
            inputPanel.add(new NodeConnector(nData.GetDisplayName(), nData.GetDataColor()), "grow, w 14!, h 14!, wrap");
        }

        // Ensure a 'white space' on the input lane
        // Preventing control to get in
        if(prop.GetOutputCount() == 0)
            outputPanel.add(new JLabel(""), "grow, w 14!, h 14!, wrap");

        if(prop.Control() != null) {
            prop.AddOnControlUpdateListener(new NodeEventListener() {
                @Override
                public void OnControlUpdate() {
                    node.repaint();
                }

                @Override
                public void OnConnect() {
                    node.repaint();
                }

                @Override
                public void OnDisconnect() {
                    node.repaint();
                }
            });
            controlPanel.add(prop.Control().get(), "grow, wrap");
        }

        for (NodeData nData : prop.GetOutputs()) {
            outputPanel.add(new NodeConnector(nData.GetDisplayName(), nData.GetDataColor()), "grow, w 14!, h 14!, wrap");
        }

        // Ensure a 'white space' on the output lane
        // Preventing control to get in
        if(prop.GetOutputCount() == 0)
            outputPanel.add(new JLabel(""), "grow, w 14!, h 14!, wrap");
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
