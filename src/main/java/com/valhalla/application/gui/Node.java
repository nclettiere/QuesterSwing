package com.valhalla.application.gui;

import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import javax.swing.border.MatteBorder;

public class  Node extends JPanel {

    String                   NodeName;
    String                   NodeSubtitle;
    Color                    NodeColor;
    NodeProperty[]           properties;
    JPanel                   content;
    ArrayList<PropertyPanel> panelList;

    public Node(NodeProperty[] properties) {
        this.NodeName = "Default";
        this.NodeSubtitle = "Default";
        this.properties = properties;
        this.panelList = new ArrayList<>();
        this.NodeColor = new Color(255, 100, 70);

        this.setLayout(new MigLayout("", "0[grow]0", "0[grow]0"));
        this.setBorder(BorderFactory.createEmptyBorder(51, 10, 2, 12));
        this.setBackground(new Color(0,0,0,0));
        this.setOpaque(false);

        content = new JPanel(new MigLayout("", "grow"));
        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        content.setOpaque(false);

        CreateNodeStructure();

        add(content, "grow");

        repaint();
    }

    public Node() {
        this.NodeName = "Default";
        this.NodeSubtitle = "Default";
        panelList = new ArrayList<>();
        this.setLayout(new MigLayout("", "0[grow]0", "0[grow]0"));
        this.setBorder(BorderFactory.createEmptyBorder(51, 10, 2, 12));
        this.setBackground(new Color(0,0,0,0));
        this.setOpaque(false);

        content = new JPanel(new MigLayout("", "grow"));
        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        content.setOpaque(false);

        add(content, "grow");

        repaint();
    }

    public void CreateNodeStructure() {
        for (NodeProperty prop : properties) {

            PropertyPanel panel = new PropertyPanel(prop, this);
            content.add(panel, "grow, wrap");
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
        // header size + panels size + additional paddings
        int width = this.getWidth() + 2;

        /* -- Node Base -- */
        graphics.setColor(new Color(255,255,255, 30));
        graphics.fillRoundRect(
                15,
                0,
                width-32,
                height,
                arcs.width,
                arcs.height);
        graphics.setColor(new Color(30,30,30));
        graphics.fillRoundRect(
                16,
                1,
                width-34,
                height-2,
                arcs.width,
                arcs.height);
        /* -- Node Header -- */
        graphics.setColor(NodeColor);
        graphics.fillRoundRect(
                16,
                1,
                width-34,
                50,
                arcs.width,
                arcs.height);
        graphics.setColor(NodeColor);
        graphics.fillRect(
                16,
                41,
                width-34,
                10);
        graphics.setColor(new Color(255,255,255, 30));
        graphics.drawLine(
                16,
                49,
                width-19,
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
                width-38,
                51,
                20,
                height-61);
        graphics.fillRoundRect(
                width-38,
                height-21,
                20,
                20,
                arcs.width,
                arcs.height);
        graphics.fillRect(
                width-38,
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

    public void setValue(int value) {
        //firePropertyChange( "value", this.value, value );
        //this.value = value;
        //repaint( );
        //fireEvent( );
    }

    //public int getValue( )  { return value; }
    //public void setMinimum(int minValue)  { this.minValue = minValue; }
    //public int getMinimum( )  { return minValue; }
    //public void setMaximum(int maxValue)  { this.maxValue = maxValue; }
    //public int getMaximum( )  { return maxValue; }

    public void addDialListener(DialListener listener) {
        //listenerList.add( DialListener.class, listener );
    }
    public void removeDialListener(DialListener listener) {
        //listenerList.remove( DialListener.class, listener );
    }

    void fireEvent( ) {
        //Object[] listeners = listenerList.getListenerList( );
        //for ( int i = 0; i < listeners.length; i += 2 )
        //    if ( listeners[i] == DialListener.class )
        //        ((DialListener)listeners[i + 1]).dialAdjusted(
        //                new DialEvent(this, value) );
    }
}
