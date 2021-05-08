package com.valhalla.NodeEditor;

import com.valhalla.NodeEditor.Sockets.NodeSocket;
import com.valhalla.application.gui.*;
import com.valhalla.core.Node.NodeComponentEventListener;
import com.valhalla.NodeEditor.Editor.NodeEditor;
import com.valhalla.core.Node.NodeMessage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;

public class NodeComponent extends JComponent implements MouseInputListener {
    protected NodeBase                 Node;
    protected String                   NodeName;
    protected String                   NodeSubtitle;
    protected Color                    NodeColor;
    protected JPanel                   Content;
    protected ArrayList<PropertyPanel> propPanelList;
    protected ArrayList<NodeConnector> connectors;
    protected NodeEditor editorParent;
    protected boolean                  selected;

    protected double zoomFactor = 1.0f;

    // Mouse vars
    protected boolean isMouseHeaderPressed;
    protected Point   mouseOriginPoint;

    protected boolean showMessage;
    protected Map.Entry<UUID, NodeMessage> nodeMessage;

    protected JPanel messagePanel;

    public NodeComponent() {
        this.NodeName = "Default";
        this.NodeSubtitle = "Default";

        propPanelList = new ArrayList<>();
        this.setLayout(new MigLayout("", "0[shrink 0]0", "0[grow]0"));
        this.setBorder(BorderFactory.createEmptyBorder(51 + 22, 10, 2, 12));
        this.setBackground(new Color(0,255,0,0));

        Content = new JPanel(new MigLayout("", "0[grow]0", "0[grow]0"));
        Content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        Content.setOpaque(false);

        add(Content, "grow, wrap");

        messagePanel = new JPanel(new MigLayout("", "0[grow]0", "0[grow]0"));
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(0,0,0,0));

        add(messagePanel, "grow, wrap");

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        repaint();
    }

    public NodeComponent(UUID uuid, Iterable<NodeSocket> sockets) {
        this();
    }

    public String GetGroup() {
        return Node.groupName;
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


    public Iterable<PropertyBase> getAllProperties() {
        return Node.getProperties();
    }


    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension arcs = new Dimension(10, 10);

        int height = getHeight();

        /* -- Node Base -- */
        if(selected) {
            graphics.setColor(new Color(242,176,18));
            graphics.drawRoundRect(
                    15,
                    12,
                    this.getWidth()-32,
                    height-13,
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
                height-13,
                arcs.width,
                arcs.height);
        /* -- Node Connectors Section -- */
        graphics.setColor(new Color(50,50,50, 190));
        graphics.fillRoundRect(
                15+1,
                12 + 1,
                this.getWidth()-32-2,
                height - 13 - 2,
                arcs.width,
                arcs.height);
        /* -- Node Control Section -- */
        graphics.setColor(new Color(35,35,35));
        graphics.fillRoundRect(
                16+20,
                12 + 1,
                this.getWidth()-34-40,
                height - 13 - 2,
                arcs.width,
                arcs.height);
        graphics.fillRect(
                16+20,
                height - 6,
                4,
                4);
        graphics.fillRect(
                this.getWidth()-34-8,
                height - 6,
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

         // Draw Message Zone
        if(showMessage && nodeMessage != null) {
            NodeMessage nMsg = nodeMessage.getValue();
            int baseY = getHeight() - (nMsg.getHeight()) - 2;
            Color color1 = new Color(85, 64, 0);
            Color color2 = new Color(114, 85, 0);
            if(nMsg.isError) {
                color1 = new Color(127,30,30);
                color2 = new Color(139,52,52);
            }

            graphics.setColor(color1);
            graphics.fillRoundRect(16, baseY, getWidth() - 34, nMsg.getHeight(), arcs.width, arcs.height);
            graphics.fillRect(16, baseY, 10, 10);
            graphics.fillRect(getWidth() - 28, baseY, 10, 10);
            graphics.setColor(color2);
            graphics.drawLine(16, baseY, getWidth() - 18, baseY);
        }

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

    @Override
    public void mouseClicked(MouseEvent e) {
        GetNode().SetCurrentAction(NodeBase.NodeAction.CLICKED);
        Select();
        FireOnNodeComponentLayoutChanged();
        repaint();
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


    public void Select() {
        this.selected = true;
        repaint();
    }

    public void Unselect() {
        this.selected = false;
        repaint();
    }

    public void Update() {
        for(PropertyPanel propertyPanel : propPanelList) {
            propertyPanel.UpdateIOLayout();
        }
    }

    void FireOnNodeComponentLayoutChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == NodeComponentEventListener.class) {
                ((NodeComponentEventListener) listeners[i+1]).OnNodeComponentLayoutChanged(this);
            }
        }
    }

    public void AddNodeComponentUpdateEvent(NodeComponentEventListener listener) {
        listenerList.add(NodeComponentEventListener.class, listener);
    }
    public void RemoveNodeComponentUpdateEvent(NodeComponentEventListener listener) {
        listenerList.remove(NodeComponentEventListener.class, listener);
    }

    public void addMessage(UUID dataUUID, NodeMessage nMsg) {
        removeMessage();
        nodeMessage = new AbstractMap.SimpleEntry<>(dataUUID, nMsg);
        messagePanel.add(nMsg, "grow, bottom, wrap");
        showMessage = true;
        repaint();
    }

    public void removeMessage() {
        messagePanel.removeAll();
        nodeMessage = null;
        showMessage = false;
        repaint();
    }
}
