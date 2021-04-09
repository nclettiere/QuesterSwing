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
import java.awt.geom.AffineTransform;
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

    private double zoomFactor = 1;
    private double prevZoomFactor = 1;
    private boolean zoomer;

    private int spacing;
    private Point origin = new Point(0,0);
    private Point mousePt;
    private boolean wheelPressed;
    private boolean debugPaint;

    protected NodeComponent nodeDragging;
    protected NodeConnector draggingConnector;
    protected NodeComponent draggingConnectorNode;
    protected NodeComponent selectedNode;
    protected Point dragOrigin;

    public NodeEditor() {
        this.setLayout(null);
        this.addMouseWheelListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.setOpaque(true);
        setBackground(new Color(60, 60, 60));
        this.nodeComponents = new ArrayList<>();
        this.bindings = new ArrayList<>();
        this.connectionPoints = new ArrayList<>();
        this.debugPaint = false;

        this.setSize(99999, 8888);

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
            public void OnNodeComponentDrag(NodeComponent nodeComponent) {
                if(nodeComponent != null) {
                    get().moveToFront(nodeComponent);
                    nodeDragging = nodeComponent;
                    repaint();
                }
            }

            @Override
            public void OnNodeComponentDragStop(NodeComponent nodeComponent) {
                nodeDragging = null;
            }

            @Override
            public void OnNodeComponentClick(NodeComponent nodeComponent) {
                for (NodeComponent nComp: nodeComponents)
                    nComp.Unselect();
                if(nodeComponent != null) {
                    get().moveToFront(nodeComponent);
                    selectedNode = nodeComponent;
                }
                repaint();
            }
        });

        nodeDisplay.AddOnNodeEventListener(new NodeEventListener() {
            @Override
            public void OnNodeComponentDrag(NodeComponent nodeComponent) {
                if(nodeComponent != null) {
                    get().moveToFront(nodeComponent);
                    nodeDragging = nodeComponent;
                    repaint();
                }
            }

            @Override
            public void OnNodeComponentDragStop(NodeComponent nodeComponent) {
                nodeDragging = null;
            }

            @Override
            public void OnNodeComponentClick(NodeComponent nodeComponent) {
                for (NodeComponent nComp: nodeComponents)
                    nComp.Unselect();
                if(nodeComponent != null) {
                    get().moveToFront(nodeComponent);
                    selectedNode = nodeComponent;
                }
                repaint();
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
    public void OnConnectorDrag(NodeComponent nodeComponent, INodeData nData, NodeConnector connector) {
        if(draggingConnector == null) {
            needsReset = false;
            this.nodeData = nData;
            this.draggingConnectorNode = nodeComponent;
            this.draggingConnector = connector;
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
        this.draggingConnectorNode = null;
        this.draggingConnector = null;
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
            nPanel.ConnectorDropped(draggingConnector, nodeData);
        }
    }

    public void CreateConnection(NodeConnector initialConnector, NodeConnector connectorDropped, UUID nodeUUID1, UUID nodeUUID2, UUID dataUUID1, UUID dataUUID2) {
        connectionPoints.add(new NodeConnectionPoints(
                initialConnector.node.GetNode().GetUUID(),
                connectorDropped.node.GetNode().GetUUID(),
                dataUUID2,
                initialConnector.GetRelativePosition(),
                dataUUID1,
                connectorDropped.GetRelativePosition()));
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
    }

    double m_scale = 1.0d;
    public void setScale(double p_newScale) {
        m_scale = p_newScale;
        int width = (int) (getWidth() * m_scale);
        int height = (int) (getHeight() * m_scale);
        setPreferredSize(new Dimension(width, height));
        repaint();
        revalidate();
    }

    private AffineTransform m_zoom;
    public void paintChildren(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        //graphics.translate(getWidth()/2, getHeight()/2);
        //graphics.scale(zoomFactor, zoomFactor);
        //graphics.translate(-getWidth()/2, -getHeight()/2);

        if(draggingConnector != null) {
            DrawConnection(graphics);
        }

        for (NodeConnectionPoints connectionPoints : connectionPoints) {
            if(nodeDragging != null) {
                if(connectionPoints.GetNodeUUID1() == nodeDragging.GetNode().GetUUID()) {
                    Point connectorPoint1 = nodeDragging.GetConnectorLocation(connectionPoints.GetDataUUID1());
                    connectionPoints.SetPoint1(connectorPoint1);
                }else if(connectionPoints.GetNodeUUID2() == nodeDragging.GetNode().GetUUID()) {
                    Point connectorPoint2 = nodeDragging.GetConnectorLocation(connectionPoints.GetDataUUID2());
                    connectionPoints.SetPoint2(connectorPoint2);
                }
            }
            DrawConnection(graphics, connectionPoints);
        }

        super.paintChildren(graphics);
    }


    private void DrawConnection(Graphics2D graphics, NodeConnectionPoints points) {
        if(selectedNode != null) {
            if (points.GetNodeUUID1() == selectedNode.GetNode().GetUUID() ||
                    points.GetNodeUUID2() == selectedNode.GetNode().GetUUID())
                graphics.setColor(new Color(240, 175, 50));
            else
                graphics.setColor(new Color(255, 255, 255, 180));
        }else {
            graphics.setColor(new Color(255, 255, 255, 180));
        }

        graphics.setStroke(new BasicStroke(1.3f));

        CubicCurve2D c = new CubicCurve2D.Double();

        Point curveOrigin = points.GetPoint1();
        Point curveEnd = points.GetPoint2();

        if(curveOrigin == null || curveEnd == null)
            return;

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
        graphics.setColor(new Color(255,255,255, 180));
        graphics.setStroke(new BasicStroke(1.3f));
        //graphics.drawLine(dragOrigin.x, dragOrigin.y, getMousePosition().x, getMousePosition().y);

        CubicCurve2D c = new CubicCurve2D.Double();

        Point curveOrigin = draggingConnector.GetRelativePosition();
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

    private static final int SIZE = 14;
    private static final String FONT = "Dialog";

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //Zoom in
        if (e.getWheelRotation() < 0) {
            zoomFactor += 0.1;
        }
        //Zoom out
        if (e.getWheelRotation() > 0) {
            zoomFactor -= 0.1;
        }

        if(zoomFactor < 1.0) {
            zoomFactor = 1.0;
            return;
        }
        if(zoomFactor > 1.5) {
            zoomFactor = 1.5;
            return;
        }

        for (NodeComponent nComp : nodeComponents) {
            nComp.setScale(zoomFactor);
        }

        repaint();
        revalidate();
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
        if(e.getButton() == MouseEvent.BUTTON3) {
            ImagePanel nsp = new ImagePanel();
            nsp.addImage("C:\\Users\\Percebe64\\Pictures\\5f7b5e35cd151.jpg");
            //nsp.setPreferredSize(new Dimension(300, 300));
            add(nsp);
        }else {
            for (NodeComponent nComp: nodeComponents)
                nComp.Unselect();
            selectedNode = null;
            ResetDataTypesState();
        }
        repaint();
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
