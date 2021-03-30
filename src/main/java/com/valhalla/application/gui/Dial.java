package com.valhalla.application.gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;

public class  Dial extends JComponent {
    int minValue, value, maxValue, radius;

    int[] properties = {1,2,3,4,5};
    int[] connectors = {1,2,3};
    int[] input = {10, 20};
    int[] output = {10};
    boolean repainted = false;

    public Dial( ) { this(0, 100, 0); }

    public Dial(int minValue, int maxValue, int value) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = value;
        setForeground(Color.lightGray);

        addMouseListener(new MouseAdapter( ) {
            public void mousePressed(MouseEvent e) { spin(e); }
        });
        addMouseMotionListener(new MouseMotionAdapter( ) {
            public void mouseDragged(MouseEvent e) { spin(e); }
        });
    }

    protected void spin(MouseEvent e) {
        int y = e.getY( );
        int x = e.getX( );
        double th = Math.atan((1.0 * y - radius) / (x - radius));
        int value=((int)(th / (2 * Math.PI) * (maxValue - minValue)));
        if (x < radius)
            setValue(value + maxValue / 2);
        else if (y < radius)
            setValue(value + maxValue);
        else
            setValue(value);
    }

    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension arcs = new Dimension(10, 10);

        /* -- Node Base -- */
        graphics.setColor(new Color(255,255,255, 30));
        graphics.fillRoundRect(
                15,
                0,
                getPreferredSize().width-32,
                getPreferredSize().height,
                arcs.width,
                arcs.height);
        //graphics.setColor(new Color(0,0,255, 100));
        //graphics.fillRoundRect(
        //        0,
        //        0,
        //        getPreferredSize().width,
        //        getPreferredSize().height,
        //        0,
        //        0);
        graphics.setColor(new Color(30,30,30));
        graphics.fillRoundRect(
                16,
                1,
                getPreferredSize().width-34,
                getPreferredSize().height-2,
                arcs.width,
                arcs.height);

        /* -- Node Header -- */
        graphics.setColor(new Color(150,0,0));
        graphics.fillRoundRect(
                16,
                1,
                getPreferredSize().width-34,
                50,
                arcs.width,
                arcs.height);
        graphics.setColor(new Color(150,0,0));
        graphics.fillRect(
                16,
                41,
                getPreferredSize().width-34,
                10);
        graphics.setColor(new Color(255,255,255, 30));
        graphics.drawLine(
                16,
                49,
                getPreferredSize().width-19,
                49);

        graphics.setColor(new Color(255,255,255, 200));
        graphics.drawString("Node Title", 35, 20);
        graphics.setColor(new Color(255,255,255, 150));
        graphics.drawString("Node subtitle", 35, 40);

        drawIO(graphics);
        //drawComponents(graphics);
        //drawConnections(graphics);
    }

    // Connector
        // -- inputs -> ARRAY
        // -- control
        // -- outputs -> ARRAY
    private void drawIO(Graphics2D graphics) {
        boolean strippedBg = true;

        int lastY = 70;

        for (int con : properties) {
            int connectorWidth = 13;
            
            int height =
                    (input.length > output.length) ?
                            (25 * input.length) : (25 * output.length);

            if(strippedBg) {

                graphics.setColor(new Color(255, 255, 255, 10));
                graphics.fillRect(
                        16,
                        lastY,
                        getPreferredSize().width - 34,
                        height);
            }

            for (int i : input) {
                graphics.setColor(new Color(255, 255, 255, 40));
                graphics.fillOval(9, lastY, connectorWidth, connectorWidth);

                graphics.setColor(new Color(255, 255, 255, 200));
                graphics.fillOval(10, lastY + 1, connectorWidth - 2, connectorWidth - 2);
                lastY += connectorWidth + 10;
            }

            lastY -= connectorWidth + 10;
            for (int i : output) {
                graphics.setColor(new Color(255, 255, 255, 40));
                graphics.fillOval(getPreferredSize().width - 24, lastY, connectorWidth, connectorWidth);

                graphics.setColor(new Color(255, 255, 255, 200));
                graphics.fillOval(getPreferredSize().width - 23, lastY + 1, connectorWidth - 2, connectorWidth - 2);
                lastY += connectorWidth + 10;
            }

            //graphics.setColor(new Color(255, 0, 0));
            //graphics.drawLine(
            //        16,
            //        lastY - connectorWidth,
            //        getPreferredSize().width-19,
            //        lastY - connectorWidth);
//
            //nextY += height;
            strippedBg = !strippedBg;
        }

        //if(!repainted) {
        //    repaint();
        //    repainted = true;
        //}
    }

    private void draw3DCircle( Graphics g, int x, int y,
                               int radius, boolean raised) {
        Color foreground = getForeground( );
        Color light = foreground.brighter( );
        Color dark = foreground.darker( );
        g.setColor(foreground);
        g.fillOval(x, y, radius * 2, radius * 2);
        g.setColor(raised ? light : dark);
        g.drawArc(x, y, radius * 2, radius * 2, 45, 180);
        g.setColor(raised ? dark : light);
        g.drawArc(x, y, radius * 2, radius * 2, 225, 180);
    }

    public Dimension getPreferredSize( ) {
        int accumulatedHeight = 51;
        for (int con : properties) {
            int height =
                (input.length > output.length) ?
                    (25 * input.length) : (25 * output.length);

            accumulatedHeight += height;
        }
        
        return new Dimension(200, accumulatedHeight);
    }

    public void setValue(int value) {
        firePropertyChange( "value", this.value, value );
        this.value = value;
        repaint( );
        fireEvent( );
    }
    public int getValue( )  { return value; }
    public void setMinimum(int minValue)  { this.minValue = minValue; }
    public int getMinimum( )  { return minValue; }
    public void setMaximum(int maxValue)  { this.maxValue = maxValue; }
    public int getMaximum( )  { return maxValue; }

    public void addDialListener(DialListener listener) {
        listenerList.add( DialListener.class, listener );
    }
    public void removeDialListener(DialListener listener) {
        listenerList.remove( DialListener.class, listener );
    }

    void fireEvent( ) {
        Object[] listeners = listenerList.getListenerList( );
        for ( int i = 0; i < listeners.length; i += 2 )
            if ( listeners[i] == DialListener.class )
                ((DialListener)listeners[i + 1]).dialAdjusted(
                        new DialEvent(this, value) );
    }
}
