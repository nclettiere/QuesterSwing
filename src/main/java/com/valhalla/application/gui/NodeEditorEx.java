package com.valhalla.application.gui;

import com.valhalla.core.Node.*;
import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.event.*;
import org.piccolo2d.extras.event.PNotificationCenter;
import org.piccolo2d.extras.event.PSelectionEventHandler;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;
import org.piccolo2d.util.PBounds;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class NodeEditorEx
        extends PSwingCanvas
        implements
        MouseInputListener {

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

    PSwing ps;

    protected NodeEditorSelectionHandler neSelectionHandler;

    public NodeEditorEx() {
        this.nodeComponents = new ArrayList<>();
        this.bindings = new ArrayList<>();
        this.connectionPoints = new ArrayList<>();
        this.debugPaint = false;

        // uninstall default zoom event handler
        removeInputEventListener(getZoomEventHandler());

        // install mouse wheel zoom event handler
        final PMouseWheelZoomEventHandler mouseWheelZoomEventHandler = new PMouseWheelZoomEventHandler();
        addInputEventListener(mouseWheelZoomEventHandler);
        mouseWheelZoomEventHandler.zoomAboutMouse();

        // Create a new event handler the creates new rectangles on
        // mouse pressed, dragged, release.
        //final PBasicInputEventHandler rectEventHandler = createRectangleEventHandler(this);
//
        //// Make the event handler only work with BUTTON1 events, so that it does
        //// not conflict with the zoom event handler that is installed by
        //// default.
        //rectEventHandler.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
//
        //// Remove the pan event handler that is installed by default so that it
        //// does not conflict with our new rectangle creation event handler.
        //removeInputEventListener(getPanEventHandler());
//
        //// Register our new event handler.
        //addInputEventListener(rectEventHandler);

        // Create a selection event handler
        neSelectionHandler = new NodeEditorSelectionHandler(getLayer(), getLayer(), this);
        addInputEventListener(neSelectionHandler);
        getRoot().getDefaultInputManager().setKeyboardFocus(neSelectionHandler);

        PNotificationCenter.defaultCenter().addListener(this, "selectionChanged",
                PSelectionEventHandler.SELECTION_CHANGED_NOTIFICATION, neSelectionHandler);

        DisplayImageComponent igp = new DisplayImageComponent();
        SelectImageComponent igp2 = new SelectImageComponent();
        //igp.SetParentEditor(this);
        //igp2.SetParentEditor(this);
        igp.AddOnNodeEventListener(new NodeEventListener() {
            @Override
            public void OnNodeComponentDrag(NodeComponent nodeComponent) {
                if(nodeComponent != null) {
                    //get().moveToFront(nodeComponent);
                    nodeDragging = nodeComponent;
                    //repaint();
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
                    //get().moveToFront(nodeComponent);
                    selectedNode = nodeComponent;
                }
                repaint();
            }
        });

        ps = new PSwing(igp);
        ps.setBounds(300,100, 200, 61);
        getLayer().addChild(ps);
        PSwing ps1 = new PSwing(igp2);
        ps1.setBounds(180,100, 200, 61);
        getLayer().addChild(ps1);
    }

    public NodeEditorEx get() {return this;}
    public void SetDebugPaint(boolean debugPaint) {
        this.debugPaint = debugPaint;
    }
    public boolean GetDebugPaint() {
        return this.debugPaint;
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
            neSelectionHandler.SetConnectorDragging(true);
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
        neSelectionHandler.SetConnectorDragging(false);
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
    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        super.paintComponent(graphics);
    }

    public void paintChildren(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

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
                graphics.setColor(new Color(0, 0, 0, 180));
        }else {
            graphics.setColor(new Color(0, 0, 0, 180));
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
        graphics.setColor(new Color(0,0,0, 180));
        graphics.setStroke(new BasicStroke(1.3f));
        //graphics.drawLine(dragOrigin.x, dragOrigin.y, getMousePosition().x, getMousePosition().y);

        CubicCurve2D c = new CubicCurve2D.Double();

        Point curveOrigin = draggingConnector.GetRelativePosition();
        Point curveEnd = getMousePosition();

        if(curveEnd == null)
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

    //public PSelectionEventHandler createRectangleEventHandler(PSwingCanvas canvas) {
//
    //    // Create a new subclass of PBasicEventHandler that creates new PPath
    //    // nodes on mouse pressed, dragged, and released sequences. Not that
    //    // subclassing PDragSequenceEventHandler would make this class easier to
    //    // implement, but here you can see how to do it from scratch.
    //    return new PSelectionEventHandler() {
//
    //        // The rectangle that is currently getting created.
    //        protected PPath rectangle;
//
    //        // The mouse press location for the current pressed, drag, release
    //        // sequence.
    //        protected Point2D pressPoint;
//
    //        // The current drag location.
    //        protected Point2D dragPoint;
//
    //        public void mousePressed(final PInputEvent e) {
    //            super.mousePressed(e);
    //            final PLayer layer = canvas.getLayer();
//
    //            // Initialize the locations.
    //            pressPoint = e.getPosition();
    //            dragPoint = pressPoint;
//
    //            // create a new rectangle and add it to the canvas layer so that
    //            // we can see it.
    //            rectangle = new PPath.Float();
    //            rectangle.setStroke(new BasicStroke((float) (1 / e.getCamera().getViewScale())));
    //            layer.addChild(rectangle);
//
    //            // update the rectangle shape.
    //            updateRectangle();
    //        }
//
    //        public void mouseDragged(final PInputEvent e) {
//
    //            // update the drag point location.
    //            dragPoint = e.getPosition();
//
    //            //if(nodeDragging != null) {
    //            //    double x = ps.getOffset().getX() + (dragPoint.getX());
    //            //    double y = ps.getOffset().getY() + (dragPoint.getY());
    //            //    ps.setOffset(x, y);
    //            //}
//
    //            // update the rectangle shape.
    //            //updateRectangle();
    //            //super.mouseDragged(e);
    //        }
//
    //        public void mouseReleased(final PInputEvent e) {
    //            super.mouseReleased(e);
    //            // update the rectangle shape.
    //            updateRectangle();
    //            rectangle = null;
    //        }
//
    //        public void updateRectangle() {
    //            // create a new bounds that contains both the press and current
    //            // drag point.
    //            final PBounds b = new PBounds();
    //            b.add(pressPoint);
    //            b.add(dragPoint);
//
    //            // Set the rectangles bounds.
    //            rectangle.reset();
    //            rectangle.append(b, false);
    //            rectangle.closePath();
    //        }
    //    };
    //}
}
