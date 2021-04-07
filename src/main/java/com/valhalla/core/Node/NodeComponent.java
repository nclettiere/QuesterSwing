package com.valhalla.core.Node;

import com.valhalla.application.gui.NodeEditor;
import com.valhalla.application.gui.PropertyPanel;
import com.valhalla.core.NodeIcons;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;

public class NodeComponent extends JComponent implements MouseInputListener {
    protected NodeBase                 Node;
    protected String                   NodeName;
    protected String                   NodeSubtitle;
    protected Color                    NodeColor;
    protected JPanel                   Content;
    protected ArrayList<PropertyPanel> propPanelList;
    protected NodeEditor               editorParent;

    // Mouse vars
    protected boolean isMousePressed;
    protected Point   mouseOriginPoint;

    NodeComponent() {
        this.NodeName = "Default";
        this.NodeSubtitle = "Default";

        this.listenerList = new EventListenerList();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        propPanelList = new ArrayList<>();
        this.setLayout(new MigLayout("", "0[grow]0", "0[grow]0"));
        this.setBorder(BorderFactory.createEmptyBorder(51 + 22, 10, 2, 12));
        this.setBackground(new Color(0,255,0,0));
        //this.setOpaque(false);

        Content = new JPanel(new MigLayout("", "grow"));
        Content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        Content.setOpaque(false);

        add(Content, "grow");

        repaint();
    }

    public void CreateNodeStructure() {
        for (PropertyBase prop : Node.GetProperties()) {
            PropertyPanel panel = new PropertyPanel(prop, this);
            Content.add(panel, "grow, wrap");
            propPanelList.add(panel);
        }
    }

    public void SetParentEditor(NodeEditor editor) {
        this.editorParent = editor;
    }

    void FireNodeOnDraggedEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == NodeEventListener.class) {
                ((NodeEventListener) listeners[i+1]).OnNodePanelDrag(this);
            }
        }
    }

    void FireNodeOnDragStopEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == NodeEventListener.class) {
                ((NodeEventListener) listeners[i+1]).OnNodePanelDragStop(this);
            }
        }
    }

    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension arcs = new Dimension(10, 10);

        int accumulatedHeight = 0;
        int width = 0;
        for(PropertyPanel panel : propPanelList) {
            accumulatedHeight += panel.getHeight();
            if(panel.getWidth() > width)
                width = panel.getWidth() + 34;
        }

        // header size + panels size + additional paddings
        int height = 51 + 24 + accumulatedHeight + 20 + 2;

        this.setSize(getWidth(), height);

        /* -- Node Base -- */
        graphics.setColor(new Color(255,255,255, 30));
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
        graphics.setColor(new Color(30,30,30));
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

         //BufferedImage icon = null;
         //try {
         //    icon = NodeIcons.GetIcon(new File("C:\\Users\\Percebe64\\Downloads\\formula.svg"));
         //} catch (IOException e) {
         //    e.printStackTrace();
         //    System.out.println(e.getMessage());
         //}

        //graphics.setClip(null);
        //graphics.clipRect(100, 10, 50, 100);
        //graphics.draw(new Rectangle2D.Double(100, 10, 95, 30));

       //String s = "f";
       //Font font = new Font("Serif", Font.PLAIN, 50);
       //FontRenderContext frc = graphics.getFontRenderContext();

        //GlyphVector gv = font.createGlyphVector(frc, s);
        //graphics.drawGlyphVector(gv, 40, 60);
    }

    public NodeBase GetNode() {return Node;}

    public Dimension getPreferredSize( ) {
        return new Dimension(200, 350);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        FireNodeOnDraggedEvent();
        // If dragging node from header (51px height)
        if(e.getPoint().y > 12 && e.getPoint().y < 52 + 12 && e.getPoint().x > 15 && e.getPoint().x < getWidth() - 15)
            this.isMousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.isMousePressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(this.isMousePressed) {
            int x = (int) this.getLocation().getX() + (e.getX()) - (this.getSize().width / 2);
            int y = (int) this.getLocation().getY() + (e.getY()) - (51 / 2);
            setLocation(x,y);
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

    public INodeData GetConnector(UUID uuid) {
        for (PropertyBase prop :  Node.GetProperties()) {
            for (INodeData nData : prop.GetInputs()) {
                if(nData.GetUUID() == uuid)
                    return nData;
            }
        }
        return null;
    }

    public void NotifyConnectorClick(INodeData nData) {
        editorParent.OnConnectorClick(nData);
    }

    public void NotifyConnectorDrag(INodeData nData, Component connector) {
        editorParent.OnConnectorDrag(nData, connector);
    }

    public void NotifyConnectorDragStop(INodeData nData) {
        editorParent.OnConnectorDragStop(nData);
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

    public void ConnectorDropped(INodeData nodeData) {
        for(PropertyPanel prop : propPanelList)
            prop.ConnectorDropped(nodeData);
    }

    public void NotifyConnectionCreated(UUID uuid1, UUID uuid2) {
        editorParent.CreateConnection(this.Node.GetUUID(), uuid1, uuid2);
    }

    public Point GetConnectorLocation(UUID uuid) {
        for (PropertyPanel propPanel : propPanelList) {
            Point p =  propPanel.GetConnectorLocation(uuid);
            if(p != null)
                return p;
        }
        return null;
    }
}
