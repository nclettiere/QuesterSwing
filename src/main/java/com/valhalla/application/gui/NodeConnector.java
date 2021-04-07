package com.valhalla.application.gui;

import com.valhalla.core.Node.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;


public class NodeConnector
    extends
    JComponent
    implements
    ActionListener,
    FocusListener,
    MouseListener,
    MouseMotionListener {

    protected NodeComponent node;
    protected boolean mouseEntered;
    protected boolean dragNotified;
    protected INodeData nData;

    protected boolean disabled;

    protected EventListenerList listenerList;

    protected INodeData lastConnection;
    protected NodeConnector lastConnectionComp;

    NodeConnector(INodeData nData, NodeComponent node) {
        this.nData = nData;
        this.node = node;

        listenerList = new EventListenerList();

        this.addFocusListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        setToolTipText(nData.GetDisplayName());
        //setBorder(new EmptyBorder(0,10,0,0));
        setPreferredSize(new Dimension(15,15));
    }

    public void AddOnControlUpdateListener(ConnectorEventListener listener) {
        listenerList.add(ConnectorEventListener.class, listener);
    }

    public void RemoveOnControlUpdateListener(ConnectorEventListener listener) {
        listenerList.remove(ConnectorEventListener.class, listener);
    }

    void FireOnConnectorClickEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == ConnectorEventListener.class) {
                ((ConnectorEventListener) listeners[i+1]).OnConnectorClick(this.nData.GetUUID());
            }
        }
    }

    void FireOnConnectorDragEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == ConnectorEventListener.class) {
                ((ConnectorEventListener) listeners[i+1]).OnConnectorDrag(this.nData.GetUUID(), this);
            }
        }
    }

    void FireOnConnectorDragStopEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == ConnectorEventListener.class) {
                ((ConnectorEventListener) listeners[i+1]).OnConnectorDragStop(this.nData.GetUUID());
            }
        }
    }

    void FireOnConnectionCreated() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == ConnectorEventListener.class) {
                ((ConnectorEventListener) listeners[i+1]).OnConnectionCreated(this, lastConnectionComp, this.nData.GetUUID(), lastConnection.GetUUID());
            }
        }
    }

    void MatchType(Class<? extends INodeData> dataClass) {
        // If not from the same class disable the connector
        if(!this.nData.getClass().isAssignableFrom(dataClass)) {
            SetDisabled(true);
            return;
        }
        SetDisabled(false);
    }

    void SetDisabled(boolean disabled) {
        this.disabled = disabled;
        repaint();
    }

    public INodeData GetNodeData() {return this.nData;};
    public NodeComponent GetNode() {return this.node;};

    boolean GetDisabled() {
        return this.disabled;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int connectorWidth = 15;

        if(!GetDisabled()) {
            if (mouseEntered) {
                graphics.setColor(new Color(
                        nData.GetDataColor().getRed() + 20,
                        nData.GetDataColor().getGreen() + 20,
                        nData.GetDataColor().getBlue() + 20, 70));
                graphics.fillOval(0, 0, connectorWidth, connectorWidth);

                graphics.setColor(new Color(
                        nData.GetDataColor().getRed() + 20,
                        nData.GetDataColor().getGreen() + 20,
                        nData.GetDataColor().getBlue() + 20, 255));
                graphics.fillOval(1, 1, connectorWidth - 2, connectorWidth - 2);
            } else {
                Color cDarker = nData.GetDataColor().darker().darker();
                graphics.setColor(new Color(
                        cDarker.getRed(),
                        cDarker.getGreen(),
                        cDarker.getBlue()));
                graphics.fillOval(0, 0, connectorWidth, connectorWidth);

                graphics.setColor(new Color(
                        nData.GetDataColor().getRed(),
                        nData.GetDataColor().getGreen(),
                        nData.GetDataColor().getBlue(), 200));
                graphics.fillOval(1, 1, connectorWidth - 2, connectorWidth - 2);
            }
        }else {
            graphics.setColor(new Color(
                    nData.GetDataColor().getRed(),
                    nData.GetDataColor().getGreen(),
                    nData.GetDataColor().getBlue(), 10));
            graphics.fillOval(0, 0, connectorWidth, connectorWidth);

            graphics.setColor(new Color(
                    nData.GetDataColor().getRed(),
                    nData.GetDataColor().getGreen(),
                    nData.GetDataColor().getBlue(), 80));
            graphics.fillOval(1, 1, connectorWidth - 2, connectorWidth - 2);
        }
    }

    public Point GetRelativePosition() {
        //Get "absolute" coordinates of root pane
        Point editorAbsPos = node.GetEditor().getRootPane().getContentPane().getLocationOnScreen();
        //Get "absolute" coordinates of myComp2
        Point connectorAbsPos = this.getLocationOnScreen();
        //Subtract coordinates to get relative coordinates
        Point connectorRelPos = new Point(
                (int) (connectorAbsPos.getX() - editorAbsPos.getX()),
                (int) (connectorAbsPos.getY() - editorAbsPos.getY()));

        return connectorRelPos;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!GetDisabled())
            FireOnConnectorClickEvent();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragNotified = false;
        FireOnConnectorDragStopEvent();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(!GetDisabled()) {
            this.mouseEntered = true;
            repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(!GetDisabled()) {
            this.mouseEntered = false;
            repaint();
        }
    }

    public void ResetMatch() {
        this.SetDisabled(false);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(!GetDisabled()) {
            FireOnConnectorDragEvent();
            if(!dragNotified) {
                dragNotified = true;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void ConnectorDropped(NodeConnector draggingConnector, INodeData nodeData) {
        if(!GetDisabled()) {
            if(mouseEntered) {
                if(nData.getClass().isAssignableFrom(nodeData.getClass())) {
                    nData.SetBinding(nodeData);
                    lastConnection = nodeData;
                    lastConnectionComp = draggingConnector;
                    FireOnConnectionCreated();
                }
            }
        }
    }
}
