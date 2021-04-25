package com.valhalla.application.gui;

import com.valhalla.NodeEditor.NodeSocket;
import com.valhalla.core.Node.*;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PInputEventListener;
import org.piccolo2d.extras.pswing.PSwing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.UUID;


public class NodeConnector
    extends
    JComponent {

    protected boolean mouseEntered;
    protected boolean dragNotified;
    protected NodeSocket socket;
    protected boolean disabled;
    protected boolean evaluationPassing;
    protected EventListenerList listenerList;
    protected NodeSocket lastConnection;
    protected NodeConnector lastConnectionComp;

    public NodeConnector(NodeSocket socket) {
        this.socket = socket;
        listenerList = new EventListenerList();

        socket.evaluate();

        //setToolTipText(nData.GetDisplayName());
        //setBorder(new EmptyBorder(0,10,0,0));
        setPreferredSize(new Dimension(15,15));
    }

    public void AddOnControlUpdateListener(ConnectorEventListener listener) {
        listenerList.add(ConnectorEventListener.class, listener);
    }

    public void AddInputEve(PInputEventListener listener) {
        listenerList.add(PInputEventListener.class, listener);
    }


    public void RemoveOnControlUpdateListener(ConnectorEventListener listener) {
        listenerList.remove(ConnectorEventListener.class, listener);
    }

    void FireOnConnectionCreated() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == ConnectorEventListener.class) {
                ((ConnectorEventListener) listeners[i+1]).OnConnectionCreated(this, lastConnectionComp, this.socket.getUUID(), lastConnection.getUUID());
            }
        }
    }

    public void MatchType(NodeSocket socket) {
        // If not from the same class disable the connector
        if(!this.socket.isDataBindAvailable(socket)) {
            SetDisabled(true);
            return;
        }
        if(this.socket.getDirection() == socket.getDirection()) {
            SetDisabled(true);
            return;
        }
        if(!this.socket.props.getDataClass().isAssignableFrom(
                socket.props.getDataClass())) {
            SetDisabled(true);
            return;
        }
        SetDisabled(false);
    }

    public void ResetMatch() {
        this.SetDisabled(false);
    }

    void SetDisabled(boolean disabled) {
        this.disabled = disabled;
        repaint();
    }

    public NodeSocket GetNodeSocket() {return this.socket;}

    boolean GetDisabled() {
        return this.disabled;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int connectorSize = 15;

        if(!GetDisabled()) {
            if (mouseEntered) {
                graphics.setColor(new Color(
                        socket.getSocketColor().getRed() + 20,
                        socket.getSocketColor().getGreen() + 20,
                        socket.getSocketColor().getBlue() + 20, 70));
                graphics.fillOval(0, 0, connectorSize, connectorSize);

                graphics.setColor(new Color(
                        socket.getSocketColor().getRed() + 20,
                        socket.getSocketColor().getGreen() + 20,
                        socket.getSocketColor().getBlue() + 20, 255));
                graphics.fillOval(1, 1, connectorSize - 2, connectorSize - 2);
            } else {
                Color cDarker = socket.getSocketColor().darker().darker();
                graphics.setColor(new Color(
                        cDarker.getRed(),
                        cDarker.getGreen(),
                        cDarker.getBlue()));
                graphics.fillOval(0, 0, connectorSize, connectorSize);

                graphics.setColor(new Color(
                        socket.getSocketColor().getRed(),
                        socket.getSocketColor().getGreen(),
                        socket.getSocketColor().getBlue(), 200));
                graphics.fillOval(1, 1, connectorSize - 2, connectorSize - 2);
            }
        }else {
            graphics.setColor(new Color(
                    socket.getSocketColor().getRed(),
                    socket.getSocketColor().getGreen(),
                    socket.getSocketColor().getBlue(), 10));
            graphics.fillOval(0, 0, connectorSize, connectorSize);

            graphics.setColor(new Color(
                    socket.getSocketColor().getRed(),
                    socket.getSocketColor().getGreen(),
                    socket.getSocketColor().getBlue(), 80));
            graphics.fillOval(1, 1, connectorSize - 2, connectorSize - 2);
        }
    }

    public void ConnectorDropped(NodeConnector draggingConnector, NodeSocket socket) {
        if(!GetDisabled()) {
            if(mouseEntered) {
                if (socket.props.addBinding(socket, false)) {
                    lastConnection = socket;
                    lastConnectionComp = draggingConnector;
                    FireOnConnectionCreated();
                    socket.evaluate();
                }
            }
        }
    }

    public void Hover(boolean b) {
        this.mouseEntered = b;
        repaint();
    }
}
