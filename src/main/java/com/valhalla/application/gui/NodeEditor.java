package com.valhalla.application.gui;

import com.valhalla.core.Node.INodeData;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class ColorPair {
    private Class<?> key;
    private Color color;

    public ColorPair(Class<?> dataClass, Color color) {
        Constructor<?> constructor = dataClass.getConstructors()[0];
        try {
            Object data = constructor.newInstance();
            if(!(data instanceof INodeData)) {
                throw new NullPointerException();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new NullPointerException();
        }

        this.key = dataClass;
        this.color = color;
    }

    INodeData GetNodeData() {
        Constructor<?> constructor = key.getConstructors()[0];
        try {
            Object data = constructor.newInstance();
            if(!(data instanceof INodeData)) {
                throw new NullPointerException();
            }
            return (INodeData) data;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new NullPointerException();
        }
    }
}

public class NodeEditor
    extends JPanel
    implements
    MouseWheelListener,
    MouseMotionListener,
    MouseInputListener,
    IBoundsListener {

    /*
        -- Components
            - GridCanvas -> Where nodes are placed
            - NodeInfoOverlay -> Displays node information and editing features
            - NodeShowcase -> Displays the registered nodes to drag'n'drop
            - EditorBar -> Multiple tools (compile, simulate, etc)
     */

    private ArrayList<INodeData> dataTypes;
    private ArrayList<ColorPair> dataColors;

    double zoom = 1.0;
    private int spacing;
    private Point origin = new Point(0,0);
    private Point mousePt;
    private boolean wheelPressed;

    public NodeEditor() {
        this.setLayout(null);
        this.addMouseWheelListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.setOpaque(true);

        CompTest test = new CompTest();
        //test.setSize(test.getPreferredSize());

        ImageNode node = new ImageNode();

        node.AddOnBoundsListener(this);

        node.setLocation(70,70);
        node.setSize(node.getPreferredSize());
        this.add(test);
        this.add(node);
        node.repaint();

        SwingUtilities.invokeLater( () -> {
            node.repaint();
        });
    }

    public NodeEditor(Component c) {
        this();
        c.addMouseWheelListener(this);
        //this.setBackground(Color.GREEN);
        add(c);
    }

    void RegisterDataTypes(INodeData[] dataTypes) {
        this.dataTypes.addAll(Arrays.asList(dataTypes));

        for (INodeData nData : dataTypes) {
            Random rand = new Random();
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            System.out.println(nData.getClass());
            dataColors.add(new ColorPair(nData.getClass(), new Color(r, g, b)));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.drawLine(0, origin.y, getWidth(), origin.y);
        graphics.drawLine(origin.x, 0, origin.x, getHeight());
        // set up grid
        int x = 0;
        int y = 0;
        graphics.setColor(new Color(220, 220, 220));
        while (x < getWidth()) {
            graphics.drawLine(origin.x + x, 0, origin.x + x, getHeight());
            x += 20;
        }
        while (y < getHeight()) {
            graphics.drawLine(0, origin.y + y, getWidth(), origin.y + y);
            y += 20;
        }
    }

    public void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(zoom, zoom);
        super.paintChildren(g2);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //int ticks = e.getWheelRotation();
        //zoom *= Math.pow(1.2, ticks);

        //System.out.println("xd");

        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(wheelPressed)
            System.out.println("MEEEP");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON2) {
            wheelPressed = true;

            origin.x += e.getPoint().x;
            origin.y += e.getPoint().y;

            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON2) {
            wheelPressed = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public Dimension OnSizeChanged(JComponent component) {
        System.out.println("AAAAAAAAAAA");
        return null;
    }

    @Override
    public Point OnPositionChanged(JComponent component) {
        return null;
    }
}
