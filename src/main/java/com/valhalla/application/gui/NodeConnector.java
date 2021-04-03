package com.valhalla.application.gui;

import com.valhalla.core.Node.*;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;


public class NodeConnector
    extends
    JComponent
    implements
    ActionListener,
    FocusListener,
    MouseListener {

    protected NodePanel node;
    protected boolean mouseEntered;
    protected INodeData nData;

    protected EventListenerList listenerList;

    NodeConnector(INodeData nData, NodePanel node) {
        this.nData = nData;
        this.node = node;

        listenerList = new EventListenerList();

        this.addFocusListener(this);
        this.addMouseListener(this);

        setToolTipText(nData.GetDisplayName());
        setPreferredSize(new Dimension(14,14));
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

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int connectorWidth = 15;

        if(mouseEntered) {
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
        }else {
            graphics.setColor(new Color(
                    nData.GetDataColor().getRed(),
                    nData.GetDataColor().getGreen(),
                    nData.GetDataColor().getBlue(), 40));
            graphics.fillOval(0, 0, connectorWidth, connectorWidth);

            graphics.setColor(new Color(
                    nData.GetDataColor().getRed(),
                    nData.GetDataColor().getGreen(),
                    nData.GetDataColor().getBlue(), 200));
            graphics.fillOval(1, 1, connectorWidth - 2, connectorWidth - 2);
        }
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
        FireOnConnectorClickEvent();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.mouseEntered = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.mouseEntered = false;
        repaint();
    }
}
