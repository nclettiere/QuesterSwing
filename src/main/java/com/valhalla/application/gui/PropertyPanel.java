package com.valhalla.application.gui;

import com.valhalla.core.Node.*;
import net.miginfocom.swing.MigLayout;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.UUID;

public class PropertyPanel
        extends JPanel
        implements MouseInputListener {

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

        setLayout(new MigLayout("","[20][grow][20]", "[top][top][top]"));
        setBorder(new MatteBorder(0,0,1,0, new Color(255,255,255,30)));
        setOpaque(false);

        inputPanel = new JPanel(new MigLayout("debug", "0[grow]0", "2[top]2"));
        inputPanel.setBorder(new EmptyBorder(0,0,0,0));
        inputPanel.setOpaque(false);

        controlPanel = new JPanel(new MigLayout("", "[max]", "[top]"));
        controlPanel.setOpaque(false);

        outputPanel = new JPanel(new MigLayout("debug", "0[20]0", "2[top]2"));
        outputPanel.setBorder(new EmptyBorder(0,0,0,0));
        outputPanel.setOpaque(false);

        add(inputPanel);
        add(controlPanel);
        add(outputPanel);

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
            inputPanel.add(new JLabel(""), "grow, w 20!, h 20!, wrap");
        }

        if(prop.GetControl() != null) {
            prop.AddOnControlUpdateListener(new PropertyEventListener() {
                @Override
                public void OnControlUpdate() {
                    //AddProperties();
                    //node.NotifyControlUpdated(PropertyPanel.this);
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

        int i = 1;
        for (INodeData nData : prop.GetOutputs()) {
            NodeConnector nodeConnector = new NodeConnector(nData, node);
            connectorList.add(nodeConnector);
            nodeConnector.AddOnControlUpdateListener(new ConnectorEventListener() {
                @Override
                public void OnConnectorClick(UUID uuid) {
                    node.NotifyConnectorClick(nData);
                }
                @Override
                public void OnConnectorDrag(UUID uuid, NodeConnector connector) {
                    //node.NotifyConnectorDrag(nData, connector);
                    node.GetNode().SetCurrentAction(NodeBase.NodeAction.CONNECTION_DRAGGING);
                }
                @Override
                public void OnConnectorDragStop(UUID uuid) {
                    node.GetNode().SetCurrentAction(NodeBase.NodeAction.NONE);
                    //node.NotifyConnectorDragStop(nData);
                }
                @Override
                public void OnConnectionCreated(NodeConnector dropped, NodeConnector initialConnector, UUID uuid1, UUID uuid2) { node.NotifyConnectionCreated(dropped, initialConnector, uuid1, uuid2); }
            });
            outputPanel.add(new JLabel(""), "grow, w 20!, h 20!, wrap");
            System.out.println(i);
            i++;
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

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        node.GetNode().SetCurrentAction(NodeBase.NodeAction.PRESSED);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
