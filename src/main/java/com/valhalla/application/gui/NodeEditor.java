package com.valhalla.application.gui;

import com.valhalla.core.Node.*;
import com.valhalla.core.Node.DisplayImageComponent;
import com.valhalla.core.Node.NodeComponent;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

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

class NodeConnectionPoints {
    private UUID uuid1;
    private UUID uuid2;
    private Point p1;
    private Point p2;

    public NodeConnectionPoints(UUID uuid1, Point p1, UUID uuid2, Point p2) {
        this.p1 = p1;
        this.uuid1 = uuid1;
        this.p2 = p2;
        this.uuid2 = uuid2;
    }

    public Point GetPoint1() {
        return p1;
    }

    public Point GetPoint2() {
        return p1;
    }

    public Point GetPoint(UUID uuid) {
        if(uuid == this.uuid1)
            return p1;
        else if(uuid == this.uuid2)
            return p2;
        else
            return null;
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

    private ArrayList<NodeComponent> nodeComponents;
    private ArrayList<INodeData>   dataTypes;
    private ArrayList<ColorPair>   dataColors;
    private ArrayList<DataBinding> bindings;
    private ArrayList<NodeConnectionPoints> connectionPoints;

    double zoom = 1.0;
    private int spacing;
    private Point origin = new Point(0,0);
    private Point mousePt;
    private boolean wheelPressed;
    private boolean debugPaint;

    protected boolean draggingConnector;
    protected Point dragOrigin;

    public NodeEditor() {
        this.setLayout(null);
        this.addMouseWheelListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.setOpaque(true);
        this.nodeComponents = new ArrayList<>();
        this.bindings = new ArrayList<>();
        this.connectionPoints = new ArrayList<>();
        this.debugPaint = false;

        SelectImageComponent nodeSelect = new SelectImageComponent();
        DisplayImageComponent nodeDisplay = new DisplayImageComponent();

        nodeComponents.add(nodeSelect);
        nodeComponents.add(nodeDisplay);

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
            public void OnNodePanelDrag(NodeComponent nodeComponent) {
                if(nodeComponent != null) {
                    get().moveToFront(nodeComponent);
                }
            }

            @Override
            public void OnNodePanelDragStop(NodeComponent nodeComponent) {

            }
        });

        nodeDisplay.AddOnNodeEventListener(new NodeEventListener() {
            @Override
            public void OnNodePanelDrag(NodeComponent nodeComponent) {
                if(nodeComponent != null) {
                    get().moveToFront(nodeComponent);
                }
            }

            @Override
            public void OnNodePanelDragStop(NodeComponent nodeComponent) {

            }
        });
    }

    private NodeEditor get() {return this;}

    public void SetDebugPaint(boolean debugPaint) {
        this.debugPaint = debugPaint;
    }
    public boolean GetDebugPaint() {
        return this.debugPaint;
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

    public Point RequestMousePosition() {
        return mousePt;
    }

    INodeData nodeData;
    public void OnConnectorClick(INodeData nData) {
        if(nodeData == null) {
            nodeData = nData;
            ShowDataType(nData.getClass());
        }else {
            if(nData.getClass().isAssignableFrom(nodeData.getClass())) {
                if (nodeData.GetMode() == ConnectorMode.OUTPUT)
                    nData.SetBinding(nodeData);
                else
                    nodeData.SetBinding(nData);
            }

            // Reset for new bindings
            ResetDataTypesState();
            nodeData = null;

            UpdateNodes();
        }
    }

    boolean needsReset;
    ConnectionLine cLine;
    public void OnConnectorDrag(INodeData nData, Component connector) {
        if(!draggingConnector) {
            needsReset = false;
            this.nodeData = nData;
            this.draggingConnector = true;
            this.dragOrigin = getMousePosition(true);
            System.out.println("ORIGIN: "+ origin.toString());

            ShowDataType(nData.getClass());
        }
        repaint();
    }

    Point dropPoint;
    public void OnConnectorDragStop(INodeData nData) {
        NotifyConnectorsDrop();
        // Reset for new bindings
        ResetDataTypesState();
        needsReset = true;
        this.draggingConnector = false;
        dropPoint = getMousePosition();
        UpdateNodes();
        repaint();
    }

    public void UpdateNodes() {
        for (NodeComponent nPanel : nodeComponents) {
            nPanel.UpdateData();
        }
    }

    public void NotifyConnectorsDrop() {
        for (NodeComponent nPanel : nodeComponents) {
            nPanel.ConnectorDropped(nodeData);
        }
    }

    public void CreateConnection(UUID uuid1, UUID uuid2) {
        Point drop = getMousePosition();
        connectionPoints.add(new NodeConnectionPoints(uuid1, dragOrigin, uuid2, drop));
        repaint();
    }

    // OnConnector Click/Drag disables all
    // connector with different data type
    public void ShowDataType(Class<? extends INodeData> dataType) {
        for (NodeComponent nPanel : nodeComponents) {
            nPanel.MatchConnectorType(dataType);
        }
    }
    // Resets All connectors and return to
    // enabled state
    public void ResetDataTypesState() {
        this.nodeData = null;
        for (NodeComponent nPanel : nodeComponents) {
            nPanel.ResetDataTypesState();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //graphics.drawLine(0, origin.y, getWidth(), origin.y);
        //graphics.drawLine(origin.x, 0, origin.x, getHeight());

        // set up grid
        int x = -1;
        int y = -1;
        graphics.setColor(new Color(100, 100, 100));
        while (x < getWidth()) {
            graphics.drawLine(origin.x + x, 0, origin.x + x, getHeight());
            x += 20;
        }
        while (y < getHeight()) {
            graphics.drawLine(0, origin.y + y, getWidth(), origin.y + y);
            y += 20;
        }

        if(draggingConnector) {
            DrawConnection(graphics);
        }

        for (NodeConnectionPoints points : connectionPoints) {
            DrawConnection(graphics, points);
        }

    }

    private void DrawConnection(Graphics2D graphics, NodeConnectionPoints points) {
        graphics.setColor(Color.GREEN);
        graphics.setStroke(new BasicStroke(3.0f));
        //graphics.drawLine(dragOrigin.x, dragOrigin.y, getMousePosition().x, getMousePosition().y);

        CubicCurve2D c = new CubicCurve2D.Double();

        Point curveOrigin = points.GetPoint1();
        Point curveEnd = points.GetPoint2();

        Point curveOriginCtrl = new Point();
        Point curveEndCtrl = new Point();

        float delta = ((float)(curveEnd.x) / (float)(curveOrigin.x)) - 1.0f;

        if(delta < 0) {
            if(curveEnd.y < curveOrigin.y) {

                curveOriginCtrl.x = curveOrigin.x + 400;
                curveOriginCtrl.y = curveOrigin.y - 200;
                curveEndCtrl.x    = curveEnd.x - 300;
                curveEndCtrl.y    = curveEnd.y - 300;
            }else {
                curveOriginCtrl.x = curveOrigin.x + 400;
                curveOriginCtrl.y = curveOrigin.y + 200;
                curveEndCtrl.x    = curveEnd.x - 300;
                curveEndCtrl.y    = curveEnd.y + 300;
            }
        }else {
            curveOriginCtrl.x = (int)((curveOrigin.x + 30) + (50 * delta));
            curveOriginCtrl.y = (int)(curveOrigin.y + (50 * delta));
            curveEndCtrl.x    = (int)((curveEnd.x - 30) - (50 * delta));
            curveEndCtrl.y    = (int)(curveEnd.y - (50 * delta));
        }

        c.setCurve(
                curveOrigin.x,
                curveOrigin.y,
                curveOriginCtrl.x,
                curveOriginCtrl.y,
                curveEndCtrl.x,
                curveEndCtrl.y,
                curveEnd.x,
                curveEnd.y);

        graphics.draw(c);

        if(GetDebugPaint()) {
            graphics.setColor(Color.RED);
            //ctrlOrigin
            graphics.drawOval(curveOriginCtrl.x, curveOriginCtrl.y, 10, 10);
            //ctrlEND
            graphics.drawOval(curveEndCtrl.x, curveEndCtrl.y, 10, 10);
        }
    }

    // Dragging
    private void DrawConnection(Graphics2D graphics) {
        graphics.setColor(Color.GREEN);
        graphics.setStroke(new BasicStroke(3.0f));
        //graphics.drawLine(dragOrigin.x, dragOrigin.y, getMousePosition().x, getMousePosition().y);

        CubicCurve2D c = new CubicCurve2D.Double();

        Point curveOrigin = dragOrigin;
        Point curveEnd = getMousePosition();

        Point curveOriginCtrl = new Point();
        Point curveEndCtrl = new Point();

        float delta = ((float)(curveEnd.x) / (float)(curveOrigin.x)) - 1.0f;

        if(delta < 0) {
            if(curveEnd.y < curveOrigin.y) {

                curveOriginCtrl.x = curveOrigin.x + 400;
                curveOriginCtrl.y = curveOrigin.y - 200;
                curveEndCtrl.x    = curveEnd.x - 300;
                curveEndCtrl.y    = curveEnd.y - 300;
            }else {
                curveOriginCtrl.x = curveOrigin.x + 400;
                curveOriginCtrl.y = curveOrigin.y + 200;
                curveEndCtrl.x    = curveEnd.x - 300;
                curveEndCtrl.y    = curveEnd.y + 300;
            }
        }else {
            curveOriginCtrl.x = (int)((curveOrigin.x + 30) + (50 * delta));
            curveOriginCtrl.y = (int)(curveOrigin.y + (50 * delta));
            curveEndCtrl.x    = (int)((curveEnd.x - 30) - (50 * delta));
            curveEndCtrl.y    = (int)(curveEnd.y - (50 * delta));
        }

        c.setCurve(
                curveOrigin.x,
                curveOrigin.y,
                curveOriginCtrl.x,
                curveOriginCtrl.y,
                curveEndCtrl.x,
                curveEndCtrl.y,
                curveEnd.x,
                curveEnd.y);

        graphics.draw(c);

        if(GetDebugPaint()) {
            graphics.setColor(Color.RED);
            //ctrlOrigin
            graphics.drawOval(curveOriginCtrl.x, curveOriginCtrl.y, 10, 10);
            //ctrlEND
            graphics.drawOval(curveEndCtrl.x, curveEndCtrl.y, 10, 10);
        }
    }

    public void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(zoom, zoom);
        super.paintChildren(g2);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int ticks = e.getWheelRotation();
        zoom *= Math.pow(1.2, ticks);

        //System.out.println("xd");

        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON2) {

        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.mousePt = e.getPoint();

        if(cLine != null)
            cLine.UpdatePoints(mousePt);

        if(wheelPressed)
            System.out.println("NodeEditor: Wheel Moved");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ResetDataTypesState();
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

        return null;
    }

    @Override
    public Point OnPositionChanged(JComponent component) {
        return null;
    }
}
