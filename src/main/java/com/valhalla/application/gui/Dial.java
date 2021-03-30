package com.valhalla.application.gui;

import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import javax.swing.border.MatteBorder;

public class  Dial extends JPanel {
    NodeProperty[] properties;
    JPanel panel;
    int[] connectors = {1,2,3};
    int[] input = {10, 20, 30};
    int[] output = {10};
    boolean repainted = false;

    public Dial(NodeProperty[] properties) {
        this.properties = properties;
        this.setLayout(new MigLayout("", "[grow]", "0[grow]0"));
        this.setBorder(BorderFactory.createEmptyBorder(51, 9, 0, 11));

        boolean strippedBg = true;
        for(NodeProperty prop : properties) {
            JPanel panel = new JPanel(new MigLayout("fill","[grow]", "[grow]"));
            panel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(255,255,255,30)));
            boolean finalStrippedBg = strippedBg;
            EventQueue.invokeLater(() -> {
                panel.add(prop.GetControl());
                if(finalStrippedBg)
                    panel.setBackground(new Color(255,255,255, 10));
                else
                    panel.setBackground(new Color(0,0,0, 0));
                add(panel, "grow, wrap");
                prop.SetPaintedAreaHeight(panel.getPreferredSize().height);
            });
            strippedBg = !strippedBg;
        }
    }

    public Dial(int minValue, int maxValue, int value) {
        setForeground(Color.lightGray);

        addMouseListener(new MouseAdapter( ) {
            public void mousePressed(MouseEvent e) { spin(e); }
        });
        addMouseMotionListener(new MouseMotionAdapter( ) {
            public void mouseDragged(MouseEvent e) { spin(e); }
        });
    }

    protected void spin(MouseEvent e) {
        //int y = e.getY( );
        //int x = e.getX( );
        //double th = Math.atan((1.0 * y - radius) / (x - radius));
        //int value=((int)(th / (2 * Math.PI) * (maxValue - minValue)));
        //if (x < radius)
        //    setValue(value + maxValue / 2);
        //else if (y < radius)
        //    setValue(value + maxValue);
        //else
        //    setValue(value);
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

    private void drawIO(Graphics2D graphics) {
        boolean strippedBg = true;

        int lastY = 51;

        for (NodeProperty prop : properties) {
            int connectorWidth = 13;
            System.out.println(prop.GetPaintedAreaHeight());
            int nextY = lastY;
            for (int i : input) {
                graphics.setColor(new Color(255, 255, 255, 40));
                graphics.fillOval(9, nextY + 5, connectorWidth, connectorWidth);

                graphics.setColor(new Color(255, 255, 255, 200));
                graphics.fillOval(10, nextY + 6, connectorWidth - 2, connectorWidth - 2);
                nextY += connectorWidth + 5;
            }

            lastY += prop.GetPaintedAreaHeight();
        }
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
        for (NodeProperty prop : properties) {
            int height =
                (input.length > output.length) ?
                    (25 * input.length) : (25 * output.length);

            accumulatedHeight += height;
        }
        
        return new Dimension(200, accumulatedHeight);
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
