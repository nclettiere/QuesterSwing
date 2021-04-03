package com.valhalla.application.gui;

import com.valhalla.core.Node.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.util.UUID;

public class PropertyPanel extends JPanel {

    protected INodeProperty prop;
    protected EventListenerList listenerList;

    protected NodePanel node;

    protected JPanel inputPanel;
    protected JPanel controlPanel;
    protected JPanel outputPanel;

    public PropertyPanel(INodeProperty prop, NodePanel node) {
        this.prop = prop;
        this.node = node;

        setLayout(new MigLayout("fillx","2[14][grow][14]0", ""));
        setBorder(new MatteBorder(0,0,1,0, new Color(255,255,255,30)));
        setOpaque(false);

        inputPanel = new JPanel(new MigLayout("", "0[grow]0"));
        inputPanel.setBorder(new EmptyBorder(0,0,0,0));
        inputPanel.setOpaque(false);

        controlPanel = new JPanel(new MigLayout("", "[max]", ""));
        controlPanel.setOpaque(false);

        outputPanel = new JPanel(new MigLayout("fillx", "0[grow]0"));
        outputPanel.setOpaque(false);

        add(inputPanel);
        add(controlPanel);
        add(outputPanel);

        AddProperties();

    }

    private void AddProperties() {
        for (INodeData nData : prop.GetInputs()) {
            NodeConnector nodeConnector = new NodeConnector(nData, node);
            nodeConnector.AddOnControlUpdateListener(new ConnectorEventListener() {
                @Override
                public void OnConnectorClick(UUID uuid) {
                    node.NotifyConnectorClick(nData);
                }

                @Override
                public void OnConnectorDrag(UUID uuid) {

                }
            });
            inputPanel.add(nodeConnector, "grow, w 14!, h 14!, wrap");
        }
        if(prop.GetControl() != null) {
            prop.AddOnControlUpdateListener(new PropertyEventListener() {
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
            controlPanel.add(prop.GetControl().get(), "grow, wrap");
        }

        for (INodeData nData : prop.GetOutputs()) {
            NodeConnector nodeConnector = new NodeConnector(nData, node);
            nodeConnector.AddOnControlUpdateListener(new ConnectorEventListener() {
                @Override
                public void OnConnectorClick(UUID uuid) {
                    node.NotifyConnectorClick(nData);
                    // GET EDITOR
                    // EDITOR CHECKS IN INPUT/OUTPUT LIST UUID
                    // EDITOR VALIDATES CONNECTION
                    // CONNECT AND BIND VALUES
                    // UPDATE UI
                }

                @Override
                public void OnConnectorDrag(UUID uuid) {

                }
            });
            outputPanel.add(nodeConnector, "grow, w 14!, h 14!, wrap");
        }

        // Ensure a 'white space' on the input/output lane
        // Preventing control to get in
        if(prop.GetOutputCount() == 0)
            outputPanel.add(new JLabel(""), "grow, w 14!, h 14!, wrap");
        if(prop.GetInputCount() == 0)
            inputPanel.add(new JLabel(""), "grow, w 14!, h 14!, wrap");
    }

    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
}
