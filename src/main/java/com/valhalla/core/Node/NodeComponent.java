package com.valhalla.core.Node;

import com.valhalla.application.TestJLayerZoom;
import com.valhalla.application.gui.*;
import com.valhalla.application.gui.NodeEditor;
import net.miginfocom.swing.MigLayout;
import org.piccolo2d.PNode;
import org.piccolo2d.extras.pswing.PSwing;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.UUID;

public class NodeComponent extends JComponent implements MouseInputListener {
    protected NodeBase                 Node;
    protected String                   NodeName;
    protected String                   NodeSubtitle;
    protected Color                    NodeColor;
    protected JPanel                   Content;
    protected ArrayList<PropertyPanel> propPanelList;
    protected ArrayList<NodeConnector> connectors;
    protected TestJLayerZoom           editorParent;
    protected boolean                  selected;

    protected double zoomFactor = 1.0f;

    // Mouse vars
    protected boolean isMouseHeaderPressed;
    protected Point   mouseOriginPoint;

    NodeComponent() {
        this.NodeName = "Default";
        this.NodeSubtitle = "Default";

        propPanelList = new ArrayList<>();
        this.setLayout(new MigLayout("", "0[grow]0", "0[top]0"));
        this.setBorder(BorderFactory.createEmptyBorder(51 + 22, 10, 2, 12));
        this.setBackground(new Color(0,255,0,0));
        //this.setOpaque(false);


        Content = new JPanel(new MigLayout("", "[grow]", "[top]"));
        Content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        Content.setOpaque(false);

        add(Content, "w 200!");


        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        repaint();
    }

    public String GetGroup() {
        return Node.groupName;
    }

    // Delete later
    public ArrayList<PropertyPanel> GetPropertiesPanel() {
        return propPanelList;
    }

    public void CreateNodeStructure() {
        for (PropertyBase prop : Node.GetProperties()) {
            PropertyPanel panel = new PropertyPanel(prop, this);
            Content.add(panel, "grow, wrap");
            propPanelList.add(panel);
        }
    }

    public void SetParentEditor(TestJLayerZoom editor) {
        this.editorParent = editor;
    }

    void FireNodeOnDraggedEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == NodeEventListener.class) {
                ((NodeEventListener) listeners[i+1]).OnNodeComponentDrag(this);
            }
        }
    }

    void FireNodeOnDragStopEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == NodeEventListener.class) {
                ((NodeEventListener) listeners[i+1]).OnNodeComponentDragStop(this);
            }
        }
    }

    void FireNodeOnClickEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == NodeEventListener.class) {
                ((NodeEventListener) listeners[i+1]).OnNodeComponentClick(this);
            }
        }
    }

    private AffineTransform m_zoom;
    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension arcs = new Dimension(10, 10);

        int accumulatedHeight = 0;
        for(PropertyPanel panel : propPanelList) {
            accumulatedHeight += panel.getHeight();
        }

        // header size + panels size + additional paddings
        //int height = (51 + 24 + accumulatedHeight + 20 + 2);
        int height = getHeight();
        //int height = getPreferredSize().height + 51;

        //this.setSize(getWidth() - 50, height);

        /* -- Node Base -- */
        if(selected) {
            graphics.setColor(new Color(242,176,18));
            graphics.drawRoundRect(
                    15,
                    12,
                    this.getWidth()-32,
                    height - 23,
                    arcs.width,
                    arcs.height);
            graphics.setColor(new Color(242,176,18, 50));
        }else {
            graphics.setColor(new Color(255,255,255, 30));
        }
        graphics.fillRoundRect(
                15,
                12,
                this.getWidth()-32,
                height - 24,
                arcs.width,
                arcs.height);
        /* -- Node Connectors Section -- */
        graphics.setColor(new Color(50,50,50, 190));
        graphics.fillRoundRect(
                15+1,
                12 + 1,
                this.getWidth()-32-2,
                height - 24 - 2,
                arcs.width,
                arcs.height);
        /* -- Node Control Section -- */
        graphics.setColor(new Color(35,35,35));
        graphics.fillRoundRect(
                16+20,
                12 + 1,
                this.getWidth()-34-40,
                height - 2 - 24,
                arcs.width,
                arcs.height);
        graphics.fillRect(
                16+20,
                height-17,
                4,
                4);
        graphics.fillRect(
                this.getWidth()-34-8,
                height-17,
                4,
                4);
        /* -- Node Header -- */
         Color lightColor = new Color(
                 NodeColor.getRed() + 80,
                 NodeColor.getGreen() + 80,
                 NodeColor.getBlue() + 80);
         GradientPaint headerColor = new GradientPaint(
                 0,0, NodeColor,
                 400,400, lightColor);
         graphics.setPaint(headerColor);
         graphics.fillRoundRect(
                 16,
                 12 + 1,
                 this.getWidth()-34,
                 50,
                 arcs.width,
                 arcs.height);
         graphics.fillRect(
                 16,
                 12+41,
                 this.getWidth()-34,
                 10);
         graphics.setColor(new Color(255,255,255, 30));
         graphics.drawLine(
                 16,
                 12+49,
                 this.getWidth()-19,
                 12+49);
         /* -- Node Kind Indicator (Top-Left) -- */
         Color cKindIndicator = new Color(176,0,159);
         Color cKindIndicatorDarker = cKindIndicator.darker();
         Color cKindIndicatorLighter = cKindIndicator.brighter().brighter();
         graphics.setColor(new Color(
                 cKindIndicatorDarker.getRed(),
                 cKindIndicatorDarker.getGreen(),
                 cKindIndicatorDarker.getBlue()));
         graphics.fillOval(this.getWidth() - 19 - 16, 5, 24, 24);

         Point center = new Point(this.getWidth() - 19, 5);
         float radius = 24;
         float[] dist = { 0f, 1f};
         Color[] colors = { cKindIndicatorLighter, cKindIndicator};
         RadialGradientPaint p =
                 new RadialGradientPaint(center, radius, dist, colors);
         graphics.setPaintMode();
         graphics.setPaint(p);
         graphics.fillOval(this.getWidth() - 19 - 16 + 1, 5 + 1, 24 - 2, 24 - 2);
         /* -- Node Title -- */
         graphics.setColor(new Color(255,255,255, 200));
         graphics.drawString(this.NodeName, 25, 20+12);
         graphics.setColor(new Color(255,255,255, 150));
         graphics.drawString(this.NodeSubtitle, 25, 40+12);
    }

    public NodeBase GetNode() {return Node;}

    //public Dimension getPreferredSize( ) {
    //    return new Dimension(200, 350);
    //}

    @Override
    public void mouseClicked(MouseEvent e) {
        GetNode().SetCurrentAction(NodeBase.NodeAction.CLICKED);
        //FireNodeOnClickEvent();
        Select();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        GetNode().SetCurrentAction(NodeBase.NodeAction.PRESSED);
        // If dragging node from header (51px height)
        // Making available for mouse dragging
        if(e.getPoint().y > 12 && e.getPoint().y < 52 + 12 && e.getPoint().x > 15 && e.getPoint().x < getWidth() - 15)
            this.isMouseHeaderPressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.isMouseHeaderPressed = false;
        GetNode().SetCurrentAction(NodeBase.NodeAction.NONE);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(this.isMouseHeaderPressed) {
            GetNode().SetCurrentAction(NodeBase.NodeAction.DRAGGING);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseOriginPoint = e.getPoint();
    }

    public void AddOnNodeEventListener(NodeEventListener listener) {
        listenerList.add(NodeEventListener.class, listener);
    }

    public void RemoveOnNodeEventListener(NodeEventListener listener) {
        listenerList.remove(NodeEventListener.class, listener);
    }

    public INodeData GetConnectorData(UUID uuid) {
        for (PropertyBase prop :  Node.GetProperties()) {
            for (INodeData nData : prop.GetInputs()) {
                if(nData.GetUUID() == uuid)
                    return nData;
            }
        }
        return null;
    }

    public void NotifyConnectorClick(INodeData nData) {
        //editorParent.OnConnectorClick(nData);
    }

    public void NotifyConnectorDrag(INodeData nData, NodeConnector connector) {
        //editorParent.OnConnectorDrag(this, nData, connector);
    }

    public void NotifyConnectorDragStop(INodeData nData) {
        //editorParent.OnConnectorDragStop(nData);
    }

    public void UpdateData() {
        for (PropertyBase prop :  Node.GetProperties()) {
            prop.UpdateBindings();
        }
        repaint();
    }

    public void MatchConnectorType(Class<? extends INodeData> dataType) {
        for(PropertyPanel prop : propPanelList)
            prop.UpdateConnectorsMatch(dataType);
    }

    public void ResetDataTypesState() {
        for(PropertyPanel prop : propPanelList)
            prop.ResetConnectorMatch();
    }

    public void ConnectorDropped(NodeConnector draggingConnector, INodeData nodeData) {
        for(PropertyPanel prop : propPanelList)
            prop.ConnectorDropped(draggingConnector, nodeData);
    }

    public void NotifyConnectionCreated(NodeConnector connectorDropped, NodeConnector initialConnector, UUID uuid1, UUID uuid2) {
        //editorParent.CreateConnection(initialConnector, connectorDropped, this.Node.GetUUID(), connectorDropped.GetNode().GetNode().GetUUID(), uuid1, uuid2);
    }

    public NodeConnector GetConnectorComponent(UUID uuid) {
        for (PropertyPanel propPanel : propPanelList) {
            NodeConnector connector =  propPanel.GetConnectorLocation(uuid);
            if(connector != null)
                return connector;
        }
        return null;
    }

    public TestJLayerZoom GetEditor() {
        return editorParent;
    }

    public void Select() {
        this.selected = true;
        repaint();
    }

    public void Unselect() {
        this.selected = false;
        repaint();
    }

    public void SetZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
        //setLocation(
        //        (int)(getLocation().x * zoomFactor),
        //        (int)(getLocation().y * zoomFactor)
        //);
        //setSize(new Dimension(
        //        (int)(getSize().width * zoomFactor - 10),
        //        (int)(getSize().height * zoomFactor)));
        //setPreferredSize(new Dimension(
        //        (int)(getSize().width * zoomFactor),
        //        (int)(getSize().height * zoomFactor)));
    }

    //public void NotifyControlUpdated(PropertyPanel pPanel) {
    //    editorParent.UpdateNode(this, pPanel);
    //}
}
