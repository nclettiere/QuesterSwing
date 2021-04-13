package com.valhalla.application.gui;

import com.valhalla.core.Node.*;
import net.miginfocom.swing.MigLayout;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class PropertyPanel extends JPanel {

    protected INodeProperty prop;
    protected EventListenerList listenerList;

    protected NodeComponent node;
    protected ArrayList<NodeConnector> connectorList;

    protected JPanel inputPanel;
    protected JPanel controlPanel;
    protected JPanel outputPanel;

    public PropertyPanel(INodeProperty prop, NodeComponent node) {
        this.prop = prop;
        this.node = node;
        this.connectorList = new ArrayList<>();

        setLayout(new MigLayout("","20[grow]20", ""));
        setBorder(new MatteBorder(0,0,1,0, new Color(255,255,255,30)));
        setOpaque(false);

        inputPanel = new JPanel(new MigLayout("", "0[grow]0"));
        inputPanel.setBorder(new EmptyBorder(0,0,0,0));
        inputPanel.setOpaque(false);

        controlPanel = new JPanel(new MigLayout("", "[max]", ""));
        controlPanel.setOpaque(false);

        outputPanel = new JPanel(new MigLayout("fillx", "0[grow]0"));
        outputPanel.setBorder(new EmptyBorder(0,0,0,0));
        outputPanel.setOpaque(false);

        //add(inputPanel);
        add(controlPanel);
        //add(outputPanel);

        AddProperties();

    }

    // delete later
    public ArrayList<NodeConnector> getConnectors() {
        return connectorList;
    }

    private void AddProperties() {
        for (INodeData nData : prop.GetInputs()) {
            NodeConnector nodeConnector = new NodeConnector(nData, node);
            connectorList.add(nodeConnector);
            nodeConnector.AddOnControlUpdateListener(new ConnectorEventListener() {
                @Override
                public void OnConnectorClick(UUID uuid) {
                    node.NotifyConnectorClick(nData);
                }
                @Override
                public void OnConnectorDrag(UUID uuid, NodeConnector connector) { node.NotifyConnectorDrag(nData, connector); }
                @Override
                public void OnConnectorDragStop(UUID uuid) {
                    node.NotifyConnectorDragStop(nData);
                }
                @Override
                public void OnConnectionCreated(NodeConnector dropped, NodeConnector initialConnector, UUID uuid1, UUID uuid2) { node.NotifyConnectionCreated(dropped, initialConnector, uuid1, uuid2); }
            });
            //inputPanel.add(nodeConnector, "grow, w 15!, h 15!, wrap");
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
            connectorList.add(nodeConnector);
            nodeConnector.AddOnControlUpdateListener(new ConnectorEventListener() {
                @Override
                public void OnConnectorClick(UUID uuid) {
                    node.NotifyConnectorClick(nData);
                }
                @Override
                public void OnConnectorDrag(UUID uuid, NodeConnector connector) { node.NotifyConnectorDrag(nData, connector); }
                @Override
                public void OnConnectorDragStop(UUID uuid) {
                    node.NotifyConnectorDragStop(nData);
                }
                @Override
                public void OnConnectionCreated(NodeConnector dropped, NodeConnector initialConnector, UUID uuid1, UUID uuid2) { node.NotifyConnectionCreated(dropped, initialConnector, uuid1, uuid2); }
            });
            //outputPanel.add(nodeConnector, "grow, w 15!, h 15!, wrap");
        }

        // Ensure a 'white space' on the input/output lane
        // Preventing control to get in
        if(prop.GetOutputCount() == 0)
            outputPanel.add(new JLabel(""), "grow, w 20!, h 20!, wrap");
        if(prop.GetInputCount() == 0)
            inputPanel.add(new JLabel(""), "grow, w 20!, h 20!, wrap");
    }

    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void UpdateConnectorsMatch(Class<? extends INodeData> dataType) {
        for (NodeConnector connector : connectorList)
            connector.MatchType(dataType);
    }

    public void ResetConnectorMatch() {
        for (NodeConnector connector : connectorList)
            connector.ResetMatch();
    }

    public void ConnectorDropped(NodeConnector draggingConnector, INodeData nodeData) {
        for (NodeConnector connector : connectorList)
            connector.ConnectorDropped(draggingConnector, nodeData);
    }

    public NodeConnector GetConnectorLocation(UUID uuid) {
        for (NodeConnector connector : connectorList) {
            if(connector.GetNodeData().GetUUID() == uuid) {
                return connector;
            }
        }
        return null;
    }
}
