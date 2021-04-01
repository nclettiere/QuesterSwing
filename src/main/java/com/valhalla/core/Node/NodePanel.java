package com.valhalla.core.Node;

import com.valhalla.application.gui.PropertyPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class NodePanel extends JComponent implements MouseInputListener {

    NodeBase                 Node;
    String                   NodeName;
    String                   NodeSubtitle;
    Color                    NodeColor;
    JPanel                   Content;
    ArrayList<PropertyPanel> panelList;

    // Dd
    boolean isMousePressed;
    Point mouseOriginPoint;

    NodePanel() {
        this.NodeName = "Default";
        this.NodeSubtitle = "Default";

        this.addMouseListener(this);
        this.addMouseMotionListener(this);


        panelList = new ArrayList<>();
        this.setLayout(new MigLayout("debug", "0[grow]0", "0[grow]0"));
        this.setBorder(BorderFactory.createEmptyBorder(51, 10, 2, 12));
        this.setBackground(new Color(0,0,0,0));
        this.setOpaque(false);

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
            panelList.add(panel);
        }
    }

    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension arcs = new Dimension(10, 10);

        int accumulatedHeight = 0;
        for(PropertyPanel panel : panelList) {
            accumulatedHeight += panel.getHeight();
        }

        // header size + panels size + additional paddings
        int height = 51 + accumulatedHeight + 20 + 2;

        this.setSize(this.getWidth(), height);

        /* -- Node Base -- */
        graphics.setColor(new Color(255,255,255, 30));
        graphics.fillRoundRect(
                15,
                0,
                this.getWidth()-32,
                height,
                arcs.width,
                arcs.height);
        graphics.setColor(new Color(30,30,30));
        graphics.fillRoundRect(
                16,
                1,
                this.getWidth()-34,
                height-2,
                arcs.width,
                arcs.height);
        /* -- Node Header -- */
        graphics.setColor(NodeColor);
        graphics.fillRoundRect(
                16,
                1,
                this.getWidth()-34,
                50,
                arcs.width,
                arcs.height);
        graphics.setColor(NodeColor);
        graphics.fillRect(
                16,
                41,
                this.getWidth()-34,
                10);
        graphics.setColor(new Color(255,255,255, 30));
        graphics.drawLine(
                16,
                49,
                this.getWidth()-19,
                49);
        /* -- Node Connectors Sections -- */
        /* -- Input Section -- */
        graphics.setColor(new Color(50,50,50));
        graphics.fillRect(
                16,
                51,
                20,
                height-61);
        graphics.fillRoundRect(
                16,
                height-21,
                20,
                20,
                arcs.width,
                arcs.height);
        graphics.fillRect(
                26,
                height-21,
                10,
                20);
        /* -- Output Section -- */
        graphics.fillRect(
                this.getWidth()-38,
                51,
                20,
                height-61);
        graphics.fillRoundRect(
                this.getWidth()-38,
                height-21,
                20,
                20,
                arcs.width,
                arcs.height);
        graphics.fillRect(
                this.getWidth()-38,
                height-21,
                10,
                20);
        /* -- Node Title -- */
        graphics.setColor(new Color(255,255,255, 200));
        graphics.drawString(this.NodeName, 25, 20);
        graphics.setColor(new Color(255,255,255, 150));
        graphics.drawString(this.NodeSubtitle, 25, 40);
    }

    public Dimension getPreferredSize( ) {
        return new Dimension(200, 350);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.mouseOriginPoint = e.getPoint();

        // If dragging node from header (51px height)
        if(mouseOriginPoint.y > 0 && mouseOriginPoint.y < 52)
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
        if(isMousePressed) {
            this.setLocation(getMousePosition(true));
            System.out.println(e.getPoint().toString());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
