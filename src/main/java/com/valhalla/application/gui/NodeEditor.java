package com.valhalla.application.gui;

import com.valhalla.core.Node.*;
import com.valhalla.core.Node.DisplayImageNodePanel;
import com.valhalla.core.Node.NodePanel;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Array;
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
    extends JLayeredPane
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

    private ArrayList<NodePanel>   nodePanels;
    private ArrayList<INodeData>   dataTypes;
    private ArrayList<ColorPair>   dataColors;
    private ArrayList<DataBinding> bindings;

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
        this.nodePanels = new ArrayList<>();
        this.bindings = new ArrayList<>();


        SelectImageNodePanel nodeSelect = new SelectImageNodePanel();
        DisplayImageNodePanel nodeDisplay = new DisplayImageNodePanel();

        nodePanels.add(nodeSelect);
        nodePanels.add(nodeDisplay);

        //node.AddOnBoundsListener(this);
        nodeSelect.SetParentEditor(this);
        nodeSelect.setLocation(70,70);
        nodeSelect.setSize(nodeSelect.getPreferredSize());

        nodeDisplay.SetParentEditor(this);
        nodeDisplay.setLocation(300,70);
        nodeDisplay.setSize(nodeDisplay.getPreferredSize());

        setPosition(nodeSelect, 0);
        setPosition(nodeDisplay, 0);

        this.add(nodeSelect);
        this.add(nodeDisplay);

        nodeSelect.AddOnNodeEventListener(new NodeEventListener() {
            @Override
            public void OnNodePanelDrag(NodePanel nodePanel) {
                if(nodePanel != null) {
                    //for (NodePanel nPanel : nodePanels)
                    //    get().moveToBack(nPanel);
                    get().moveToFront(nodePanel);
                }
            }

            @Override
            public void OnNodePanelDragStop(NodePanel nodePanel) {

            }
        });

        nodeDisplay.AddOnNodeEventListener(new NodeEventListener() {
            @Override
            public void OnNodePanelDrag(NodePanel nodePanel) {
                if(nodePanel != null) {
                    get().moveToFront(nodePanel);
                }
            }

            @Override
            public void OnNodePanelDragStop(NodePanel nodePanel) {

            }
        });
    }

    public NodeEditor(Component c) {
        this();
        c.addMouseWheelListener(this);
        //this.setBackground(Color.GREEN);
        add(c);
    }

    private NodeEditor get() {return this;};

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

    public Point RequestMousePosition() {
        return mousePt;
    }

    INodeData nodeData;
    public void OnConnectorClick(INodeData nData) {
        if(nodeData == null) {
            nodeData = nData;
        }else {
            if(nodeData.GetMode() == ConnectorMode.OUTPUT)
                nData.SetBinding(nodeData);
            else
                nodeData.SetBinding(nData);
            UpdateNodes();

            // Reset for new bindings
            nodeData = null;
        }
    }

    public void UpdateNodes() {
        for (NodePanel nPanel : nodePanels) {
            nPanel.UpdateData();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        //graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.drawLine(0, origin.y, getWidth(), origin.y);
        graphics.drawLine(origin.x, 0, origin.x, getHeight());
        // set up grid
        int x = 0;
        int y = 0;
        graphics.setColor(new Color(100, 100, 100));
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
        this.mousePt = e.getPoint();
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
