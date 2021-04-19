package com.valhalla.application.gui;

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
    protected INodeData nData;
    protected boolean disabled;
    protected boolean evaluationPassing;
    protected EventListenerList listenerList;
    protected INodeData lastConnection;
    protected NodeConnector lastConnectionComp;

    public NodeConnector(INodeData nData) {
        this.nData = nData;
        listenerList = new EventListenerList();

        nData.AddOnBindingEventListener(new BindingEventListener() {
            @Override
            public void OnBindingDataChanged(Object data) {

            }

            @Override
            public void OnBindingReleased() {

            }

            @Override
            public void onDataEvaluationChanged(UUID dataUUID, Map.Entry<Boolean, String> evaluationState) {
                if(evaluationState == null)
                    evaluationPassing = true;
                else
                    evaluationPassing = evaluationState.getKey();
            }
        });

        nData.evaluate();

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
                ((ConnectorEventListener) listeners[i+1]).OnConnectionCreated(this, lastConnectionComp, this.nData.GetUUID(), lastConnection.GetUUID());
            }
        }
    }

    public void MatchType(Class<? extends INodeData> dataClass) {
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

    public INodeData GetNodeData() {return this.nData;}

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
                        nData.GetDataColor().getRed() + 20,
                        nData.GetDataColor().getGreen() + 20,
                        nData.GetDataColor().getBlue() + 20, 70));
                graphics.fillOval(0, 0, connectorSize, connectorSize);

                graphics.setColor(new Color(
                        nData.GetDataColor().getRed() + 20,
                        nData.GetDataColor().getGreen() + 20,
                        nData.GetDataColor().getBlue() + 20, 255));
                graphics.fillOval(1, 1, connectorSize - 2, connectorSize - 2);
            } else {
                Color cDarker = nData.GetDataColor().darker().darker();
                graphics.setColor(new Color(
                        cDarker.getRed(),
                        cDarker.getGreen(),
                        cDarker.getBlue()));
                graphics.fillOval(0, 0, connectorSize, connectorSize);

                graphics.setColor(new Color(
                        nData.GetDataColor().getRed(),
                        nData.GetDataColor().getGreen(),
                        nData.GetDataColor().getBlue(), 200));
                graphics.fillOval(1, 1, connectorSize - 2, connectorSize - 2);
            }
        }else {
            graphics.setColor(new Color(
                    nData.GetDataColor().getRed(),
                    nData.GetDataColor().getGreen(),
                    nData.GetDataColor().getBlue(), 10));
            graphics.fillOval(0, 0, connectorSize, connectorSize);

            graphics.setColor(new Color(
                    nData.GetDataColor().getRed(),
                    nData.GetDataColor().getGreen(),
                    nData.GetDataColor().getBlue(), 80));
            graphics.fillOval(1, 1, connectorSize - 2, connectorSize - 2);
        }

        if (!evaluationPassing) {
            graphics.setColor(Color.RED);
            graphics.setStroke(new BasicStroke(1));
            graphics.drawOval(-1, -1, connectorSize+2, connectorSize+2);
        }
    }

    public void ResetMatch() {
        this.SetDisabled(false);
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

    public void Hover(boolean b) {
        if(!GetDisabled()) {
            this.mouseEntered = b;
            repaint();
        }
    }
}
