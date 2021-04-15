package com.valhalla.application.gui;

import com.valhalla.core.Node.*;
import org.piccolo2d.*;
import org.piccolo2d.event.*;
import org.piccolo2d.extras.event.PNotificationCenter;
import org.piccolo2d.extras.event.PSelectionEventHandler;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.extras.pswing.PSwingMouseEvent;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;
import org.w3c.dom.Node;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

import static java.awt.event.MouseEvent.BUTTON3;

public class NodeEditorEx
    extends PSwingCanvas {

    boolean debugPaint = false;
    protected Line2D gridLine = new Line2D.Double();
    protected Stroke gridStroke = new BasicStroke(1);
    protected Color gridPaint = Color.BLACK;
    protected double gridSpacing = 15;
    protected double gridSpacingThick = 150;

    protected NodeConnector draggingConnector;
    protected PNode draggingNodeConnector;
    protected Point2D draggingConnectorOrigin;
    protected Point2D draggingConnectorFinalPoint;
    protected Point dragOrigin;

    protected ArrayList<NodeConnectionPoints> connectionPoints;
    //protected ArrayList<NodeComponent> nodeComponents;
    protected HashMap<PNode, NodeComponent> nodeComponents;
    protected NodeComponent nodeDragging;
    protected NodeComponent selectedNode;
    protected INodeData nodeData;

    protected Point2D selectorPoint;
    boolean isNodeSelectorOpened = false;
    boolean isMouseOnNodeSelector = false;

    // TEST ZONE
    private volatile double screenX = 0;
    private volatile double screenY = 0;
    private volatile double myX = 0;
    private volatile double myY = 0;

    public NodeEditorEx() {
        final PRoot root = getRoot();
        final PCamera camera = getCamera();


        this.connectionPoints = new ArrayList<>();
        this.nodeComponents = new HashMap<>();

        setBackground(new Color(50,50,50));

        // uninstall default zoom event handler
        removeInputEventListener(getZoomEventHandler());

        // install mouse wheel zoom event handler
        final PMouseWheelZoomEventHandler mouseWheelZoomEventHandler = new PMouseWheelZoomEventHandler();
        addInputEventListener(mouseWheelZoomEventHandler);

        removeInputEventListener(getPanEventHandler());
        addInputEventListener(new PPanEventHandler() {
            @Override
            public void mousePressed(PInputEvent event) {
                super.mousePressed(event);
                screenX = event.getCanvasPosition().getX();
                screenY = event.getCanvasPosition().getY();

                myX = getX();
                myY = getY();
            }

            @Override
            protected void drag(PInputEvent event) {
                if(draggingConnector == null || nodeDragging == null)
                    super.drag(event);
            }

            @Override
            public void setAutopan(boolean autopan) {
                super.setAutopan(false);
            }
        });

        //PSwing ps1 = new PSwing(new DisplayImageComponent());
        DisplayImageComponent dic = new DisplayImageComponent();
        ArrayList<PropertyPanel> propertyPanels = dic.GetPropertiesPanel();

        final PLayer gridLayer = new PLayer() {
            protected void paint(final PPaintContext paintContext) {
                // make sure grid gets drawn on snap to grid boundaries. And
                // expand a little to make sure that entire view is filled.
                final double bx = getX() - getX() % gridSpacing - gridSpacing;
                final double by = getY() - getY() % gridSpacing - gridSpacing;
                final double rightBorder = getX() + getWidth() + gridSpacing;
                final double bottomBorder = getY() + getHeight() + gridSpacing;

                final double bxT = getX() - getX() % gridSpacingThick - gridSpacingThick;
                final double byT = getY() - getY() % gridSpacingThick - gridSpacingThick;
                final double rightBorderT = getX() + getWidth() + gridSpacingThick;
                final double bottomBorderT = getY() + getHeight() + gridSpacingThick;

                final Graphics2D g2 = paintContext.getGraphics();
                final Rectangle2D clip = paintContext.getLocalClip();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

                g2.setStroke(gridStroke);
                g2.setColor(new Color(70,70,70));

                for (double x = bx; x < rightBorder; x += gridSpacing) {
                    gridLine.setLine(x, by, x, bottomBorder);
                    if (clip.intersectsLine(gridLine)) {
                        g2.draw(gridLine);
                    }
                }

                for (double y = by; y < bottomBorder; y += gridSpacing) {
                    gridLine.setLine(bx, y, rightBorder, y);
                    if (clip.intersectsLine(gridLine)) {
                        g2.draw(gridLine);
                    }
                }

                g2.setColor(new Color(30,30,30));

                for (double x = bxT; x < rightBorderT; x += gridSpacingThick) {
                    gridLine.setLine(x, byT, x, bottomBorderT);
                    if (clip.intersectsLine(gridLine)) {
                        g2.draw(gridLine);
                    }
                }

                for (double y = byT; y < bottomBorderT; y += gridSpacingThick) {
                    gridLine.setLine(bxT, y, rightBorderT, y);
                    if (clip.intersectsLine(gridLine)) {
                        g2.draw(gridLine);
                    }
                }

                if(draggingConnector != null)
                    DrawConnection(g2);

                for (NodeConnectionPoints connectionPoints : connectionPoints) {
                    if(nodeDragging != null) {
                        // Input and ouput in the same node!
                        if(connectionPoints.GetNodeUUID1() == nodeDragging.GetNode().GetUUID()) {
                            Point2D connectorPoint1 = nodeDragging
                                    .GetConnectorComponent(connectionPoints.GetDataUUID1())
                                    .GetPNode()
                                    .getGlobalBounds()
                                    .getOrigin();

                            connectorPoint1 = new Point2D.Double(connectorPoint1.getX() + 5, connectorPoint1.getY() + 5);
                            connectionPoints.SetPoint1(connectorPoint1);
                        }
                        if(connectionPoints.GetNodeUUID2() == nodeDragging.GetNode().GetUUID()) {
                            Point2D connectorPoint2 = nodeDragging
                                    .GetConnectorComponent(connectionPoints.GetDataUUID2())
                                    .GetPNode()
                                    .getGlobalBounds()
                                    .getOrigin();
                            connectorPoint2 = new Point2D.Double(connectorPoint2.getX() + 5, connectorPoint2.getY() + 5);
                            connectionPoints.SetPoint2(connectorPoint2);
                        }
                        // Input and ouput in the same node!
                        //if(connectionPoints.GetNodeUUID1() == nodeDragging.GetNode().GetUUID() &&
                        //        connectionPoints.GetNodeUUID2() == nodeDragging.GetNode().GetUUID()) {
                        //    System.out.println("YUP");
                        //}
                    }
                    DrawConnection(g2, connectionPoints);
                }
            }
        };


        // replace standar layer with grid layer.
        root.removeChild(camera.getLayer(0));
        camera.removeLayer(0);
        root.addChild(gridLayer);
        camera.addLayer(gridLayer);

        // add constrains so that grid layers bounds always match cameras view
        // bounds. This makes it look like an infinite grid.
        camera.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                gridLayer.setBounds(camera.getViewBounds());
            }
        });

        camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                gridLayer.setBounds(camera.getViewBounds());
            }
        });

        final PText tooltipNode = new PText();

        tooltipNode.setPickable(false);
        camera.addChild(tooltipNode);

        ArrayList<Class<? extends NodeComponent>> nodes = new ArrayList<>();
        nodes.add(SelectImageComponent.class);
        nodes.add(DisplayImageComponent.class);
        nodes.add(MiscComponent.class);
        NodeSelectorPanel nsp = new NodeSelectorPanel(nodes);
        PNode pSelector = new PSwing(nsp);

        nsp.addNodeSelectorEventListener(new NodeSelectorListener() {
            @Override
            public void OnNodeSelected(Class<? extends NodeComponent> nodeClass) {
                nsp.reset();
                pSelector.setVisible(false);
                isNodeSelectorOpened = false;
                addNode(nodeClass, selectorPoint);
            }
        });

        pSelector.setVisible(true);

        pSelector.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseEntered(PInputEvent event) {
                isMouseOnNodeSelector = true;
                super.mouseEntered(event);
            }

            @Override
            public void mouseExited(PInputEvent event) {
                isMouseOnNodeSelector = false;
                super.mouseExited(event);
            }
        });

        camera.addInputEventListener(new PBasicInputEventHandler() {
            public void mouseMoved(final PInputEvent event) {
                updateToolTip(event);
            }

            public void mouseDragged(final PInputEvent event) {
                if(!isMouseOnNodeSelector) {
                    nsp.reset();
                    pSelector.setVisible(false);
                    isNodeSelectorOpened = false;
                }
                updateToolTip(event);
            }

            @Override
            public void mouseClicked(PInputEvent event) {
                if(event.getButton() == BUTTON3) {
                    if(!isMouseOnNodeSelector && !isNodeSelectorOpened) {
                        isNodeSelectorOpened = true;
                        getLayer().addChild(0, pSelector);
                        selectorPoint = event.getPosition();
                        pSelector.setOffset(selectorPoint);
                        pSelector.setVisible(true);
                        nsp.setFocusField();
                        pSelector.raiseToTop();
                    }else
                        nsp.setFocusField();
                }else {
                    if(!isMouseOnNodeSelector) {
                        nsp.reset();
                        pSelector.setVisible(false);
                        isNodeSelectorOpened = false;
                    }
                }
                super.mouseClicked(event);
            }

            public void updateToolTip(final PInputEvent event) {
                final PNode n = event.getPickedNode();
                final String tooltipString = (String) n.getAttribute("tooltip");
                final Point2D p = event.getCanvasPosition();

                event.getPath().canvasToLocal(p, camera);

                tooltipNode.setText(tooltipString);
                tooltipNode.setOffset(p.getX() + 8, p.getY() - 8);
            }
        });

        this.getRoot().addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseClicked(PInputEvent event) {
                super.mouseClicked(event);
            }
        });
    }


    public void SetDebugPaint(boolean debugPaint) {
        this.debugPaint = debugPaint;
    }

    public boolean GetDebugPaint() {
        return debugPaint;
    }

    private void addNode(Class<? extends NodeComponent> nodeClass, Point2D offset) {
        NodeComponent nodeComp = null;
        try {
            nodeComp = nodeClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        if(nodeComp != null) {
            //nodeComponents.
            nodeComp.SetParentEditor(this);
            PNode pNodeComp = new PSwing(nodeComp);

            AddToNodeList(pNodeComp, nodeComp);

            NodeComponent finalNodeComp = nodeComp;
            //pNodeComp.addInputEventListener(new PBasicInputEventHandler() {
            //    @Override
            //    public void mouseClicked(PInputEvent event) {
            //        super.mouseClicked(event);
//
            //    }
//
            //    public void mouseDragged(final PInputEvent aEvent) {
            //        aEvent.setHandled(true);
//
            //        nodeDragging = finalNodeComp;
//
            //        if(draggingConnector == null) {
            //            pNodeComp.raiseToTop();
            //            final Dimension2D delta = aEvent.getDeltaRelativeTo(pNodeComp);
            //            pNodeComp.translate(delta.getWidth(), delta.getHeight());
            //        }
//
            //        getLayer().repaint();
            //    }
//
            //    @Override
            //    public void mouseReleased(PInputEvent event) {
            //        nodeDragging = null;
            //        getLayer().repaint();
            //        super.mouseReleased(event);
            //    }
            //});

            nodeComp.GetNode().AddNodeActionListener(nodeAction -> {
                switch (nodeAction) {
                    case DRAGGING -> {
                        pNodeComp.raiseToTop();
                        getMousePosition(true);
                        double deltaX = getMousePosition(true).x - screenX;
                        double deltaY = getMousePosition(true).y - screenY;
                        pNodeComp.translate(myX + deltaX, myY + deltaY);
                    }
                }
            });

            //nodeComp.AddOnNodeEventListener(new NodeEventListener() {
            //    @Override
            //    public void OnNodeComponentDrag(NodeComponent nodeComponent) {
//
            //    }
//
            //    @Override
            //    public void OnNodeComponentDragStop(NodeComponent nodeComponent) {
//
            //    }
//
            //    @Override
            //    public void OnNodeComponentClick(NodeComponent nodeComponent) {
            //        Iterator<Map.Entry<PNode, NodeComponent>> it = nodeComponents.entrySet().iterator();
            //        while (it.hasNext()) {
            //            Map.Entry<PNode, NodeComponent> pair = it.next();
            //            pair.getValue().Unselect();
            //            it.remove();
            //        }
//
            //        if(nodeComponent != null) {
            //            //get().moveToFront(nodeComponent);
            //            selectedNode = nodeComponent;
            //        }
            //        repaint();
            //    }
            //});

            getLayer().addChild(0, pNodeComp);
            pNodeComp.setOffset(offset);

            CreateNodeConnectors(nodeComp);

            pNodeComp.raiseToTop();
        }
    }

    // Dragging
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

        Point2D curveOrigin = points.GetPoint1();
        Point2D curveEnd = points.GetPoint2();

        if(curveOrigin == null || curveEnd == null)
            return;

        Point curveOriginCtrl = new Point();
        Point curveEndCtrl = new Point();

        float delta = ((float)(curveEnd.getX()) / (float)(curveOrigin.getX())) - 1.0f;

        if(delta < 0) {
            if(curveEnd.getY() < curveOrigin.getY()) {

                curveOriginCtrl.x = (int) curveOrigin.getX() + 400;
                curveOriginCtrl.y = (int) curveOrigin.getY() - 200;
                curveEndCtrl.x    = (int) curveEnd.getX() - 300;
                curveEndCtrl.y    = (int) curveEnd.getY() - 300;
            }else {
                curveOriginCtrl.x = (int) curveOrigin.getX() + 400;
                curveOriginCtrl.y = (int) curveOrigin.getY() + 200;
                curveEndCtrl.x    = (int) curveEnd.getX() - 300;
                curveEndCtrl.y    = (int) curveEnd.getY() + 300;
            }
        }else {
            curveOriginCtrl.x = (int)((curveOrigin.getX() + 30) + (50 * delta));
            curveOriginCtrl.y = (int)(curveOrigin.getY() + (50 * delta));
            curveEndCtrl.x    = (int)((curveEnd.getX() - 30) - (50 * delta));
            curveEndCtrl.y    = (int)(curveEnd.getY() - (50 * delta));
        }

        c.setCurve(
                curveOrigin.getX(),
                curveOrigin.getY(),
                curveOriginCtrl.x,
                curveOriginCtrl.y,
                curveEndCtrl.x,
                curveEndCtrl.y,
                curveEnd.getX(),
                curveEnd.getY());

        graphics.draw(c);

        if(GetDebugPaint()) {
            graphics.setColor(Color.GREEN);
            //OriginPoint
            graphics.fillOval((int)curveOrigin.getX(), (int)curveOrigin.getY(), 10, 10);
            //EndPoint
            graphics.setColor(Color.YELLOW);
            graphics.fillOval((int)curveEnd.getX(), (int)curveEnd.getY(), 10, 10);

            graphics.setColor(Color.RED);
            //ctrlOrigin
            graphics.drawOval(curveOriginCtrl.x, curveOriginCtrl.y, 10, 10);
            //ctrlEND
            graphics.drawOval(curveEndCtrl.x, curveEndCtrl.y, 10, 10);

        }
    }

    private void DrawConnection(Graphics2D graphics) {
        graphics.setColor(new Color(255,255,255, 180));
        graphics.setStroke(new BasicStroke(1.3f));
        //graphics.drawLine(dragOrigin.x, dragOrigin.y, getMousePosition().x, getMousePosition().y);

        CubicCurve2D c = new CubicCurve2D.Double();

        //Point2D curveOrigin = draggingConnectorOrigin;
        Point2D curveOrigin = draggingConnectorOrigin;
        Point2D curveEnd = draggingConnectorFinalPoint;
        // Add the connector size
        curveOrigin = new Point2D.Double(curveOrigin.getX() + 5, curveOrigin.getY() + 5);
        // curveEnd = new Point2D.Double(curveEnd.getX() - 10, curveEnd.getY() - 10);

        Point curveOriginCtrl = new Point();
        Point curveEndCtrl = new Point();

        float delta = ((float)(curveEnd.getX()) / (float)(curveOrigin.getX())) - 1.0f;

        if(draggingConnector.GetNodeData().GetMode() == ConnectorMode.OUTPUT) {
            if (delta < 0) {
                if (curveEnd.getY() < curveOrigin.getY()) {

                    curveOriginCtrl.x = (int) curveOrigin.getX() + 400;
                    curveOriginCtrl.y = (int) curveOrigin.getY() - 200;
                    curveEndCtrl.x = (int) curveEnd.getX() - 300;
                    curveEndCtrl.y = (int) curveEnd.getY() - 300;
                }else {
                    curveOriginCtrl.x = (int) curveOrigin.getX() + 400;
                    curveOriginCtrl.y = (int) curveOrigin.getY() + 200;
                    curveEndCtrl.x = (int) curveEnd.getX() - 300;
                    curveEndCtrl.y = (int) curveEnd.getY() + 300;
                }
            }else {
                curveOriginCtrl.x = (int) ((curveOrigin.getX() + 30) + (50 * delta));
                curveOriginCtrl.y = (int) (curveOrigin.getY() + (50 * delta));
                curveEndCtrl.x = (int) ((curveEnd.getX() - 30) - (50 * delta));
                curveEndCtrl.y = (int) (curveEnd.getY() - (50 * delta));
            }
        }else {
            if (delta < 0) {
                curveOriginCtrl.x = (int) ((curveOrigin.getX() + 30) + (50 * delta));
                curveOriginCtrl.y = (int) (curveOrigin.getY() + (50 * delta));
                curveEndCtrl.x = (int) ((curveEnd.getX() - 30) - (50 * delta));
                curveEndCtrl.y = (int) (curveEnd.getY() - (50 * delta));
            } else {
                if (curveEnd.getY() < curveOrigin.getY()) {
                    curveOriginCtrl.x = (int) curveOrigin.getX() + 400;
                    curveOriginCtrl.y = (int) curveOrigin.getY() + 200;
                    curveEndCtrl.x = (int) curveEnd.getX() - 300;
                    curveEndCtrl.y = (int) curveEnd.getY() + 300;
                } else {
                    curveOriginCtrl.x = (int) curveOrigin.getX() + 400;
                    curveOriginCtrl.y = (int) curveOrigin.getY() - 200;
                    curveEndCtrl.x = (int) curveEnd.getX() - 300;
                    curveEndCtrl.y = (int) curveEnd.getY() - 300;
                }
            }
        }

        c.setCurve(
                curveOrigin.getX(),
                curveOrigin.getY(),
                curveOriginCtrl.x,
                curveOriginCtrl.y,
                curveEndCtrl.x,
                curveEndCtrl.y,
                curveEnd.getX(),
                curveEnd.getY());

        graphics.draw(c);

        if(GetDebugPaint()) {
            graphics.setColor(Color.GREEN);
            //OriginPoint
            graphics.fillOval((int)curveOrigin.getX(), (int)curveOrigin.getY(), 10, 10);
            //EndPoint
            graphics.setColor(Color.YELLOW);
            graphics.fillOval((int)curveEnd.getX(), (int)curveEnd.getY(), 10, 10);

            graphics.setColor(Color.RED);
            //ctrlOrigin
            graphics.drawOval(curveOriginCtrl.x, curveOriginCtrl.y, 10, 10);
            //ctrlEND
            graphics.drawOval(curveEndCtrl.x, curveEndCtrl.y, 10, 10);
        }
    }

    public void CreateConnection(NodeConnector initialConnector, NodeConnector connectorDropped, UUID nodeUUID1, UUID nodeUUID2, UUID dataUUID1, UUID dataUUID2) {

        Point2D connectorPoint2 = connectorDropped.GetPNode().getGlobalBounds().getOrigin();
        connectorPoint2 = new Point2D.Double(connectorPoint2.getX() + 5, connectorPoint2.getY() + 5);
        connectionPoints.add(new NodeConnectionPoints(
                initialConnector.node.GetNode().GetUUID(),
                connectorDropped.node.GetNode().GetUUID(),
                dataUUID2,
                draggingConnectorOrigin,
                dataUUID1,
                connectorPoint2));
        repaint();
    }

    public void UpdateNodes() {
        Iterator<Map.Entry<PNode, NodeComponent>> it = nodeComponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<PNode, NodeComponent> pair = it.next();
            pair.getValue().UpdateData();
            it.remove();
        }
    }

    public void NotifyConnectorsDrop() {
        Iterator<Map.Entry<PNode, NodeComponent>> it = nodeComponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<PNode, NodeComponent> pair = it.next();
            pair.getValue().ConnectorDropped(draggingConnector, nodeData);
            it.remove();
        }
    }

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
        needsReset = false;
        this.nodeData = nData;
        this.draggingConnector = connector;
        this.dragOrigin = getMousePosition(true);
        System.out.println("ORIGIN: ");

        ShowDataType(nData.getClass());
        repaint();
    }

    Point dropPoint;
    public void OnConnectorDragStop(INodeData nData) {
        NotifyConnectorsDrop();

        // Reset for new bindings
        ResetDataTypesState();
        needsReset = true;
        this.draggingConnector = null;
        dropPoint = getMousePosition();

        UpdateNodes();
        repaint();
    }

    // OnConnector Click/Drag disables all
    // connector with different data type
    public void ShowDataType(Class<? extends INodeData> dataType) {
        Iterator<Map.Entry<PNode, NodeComponent>> it = nodeComponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<PNode, NodeComponent> pair = it.next();
            pair.getValue().MatchConnectorType(dataType);
            it.remove();
        }
    }
    // Resets All connectors and return to
    // enabled state
    public void ResetDataTypesState() {
        this.nodeData = null;
        Iterator<Map.Entry<PNode, NodeComponent>> it = nodeComponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<PNode, NodeComponent> pair = it.next();
            pair.getValue().ResetDataTypesState();
            it.remove();
        }
    }

    private void AddToNodeList(PNode pNode, NodeComponent nComp) {
        UUID uuidToFind = nComp.GetNode().GetUUID();
        boolean safeToAdd = true;

        // Checks if the node is already added
        Iterator<Map.Entry<PNode, NodeComponent>> it = nodeComponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<PNode, NodeComponent> pair = it.next();

            if(pair.getValue().GetNode().GetUUID().equals(uuidToFind))
                safeToAdd = false;

            it.remove();
        }

        if(safeToAdd)
            nodeComponents.put(pNode, nComp);
    }

    private PNode GetPNodeWithID(UUID uuid) {
        Iterator<Map.Entry<PNode, NodeComponent>> it = nodeComponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<PNode, NodeComponent> pair = it.next();

            if(pair.getValue().GetNode().GetUUID().equals(uuid))
                return pair.getKey();

            it.remove();
        }
        return null;
    }

    public void UpdateNode(NodeComponent nodeComponent, PropertyPanel pPanel) {
        PNode pNode = GetPNodeWithID(nodeComponent.GetNode().GetUUID());
        CreateNodeConnectors(nodeComponent);
        nodeComponent.repaint();;
    }

    private void CreateNodeConnectors(NodeComponent nodeComponent) {
        PNode pNode = GetPNodeWithID(nodeComponent.GetNode().GetUUID());
        if (pNode == null) return;

        double lastOffsetY = 70;
        for (int i = 0; i < pNode.getChildrenCount(); i++) {
            PNode childPNode = pNode.getChild(i);
            lastOffsetY = childPNode.getYOffset();
        }

        lastOffsetY += 18;
        for (PropertyPanel prop : nodeComponent.GetPropertiesPanel()) {
            for (NodeConnector conn : prop.getConnectors()) {
                boolean canContinue = true;
                for (int i = 0; i < pNode.getChildrenCount(); i++) {
                    PSwing childPNode = (PSwing) pNode.getChild(i);
                    NodeConnector childNodeConnector = (NodeConnector) childPNode.getComponent();
                    if(childNodeConnector.GetNodeData().GetUUID().equals(conn.GetNodeData().GetUUID())) {
                        canContinue = false;
                        break;
                    }
                }
                if(!canContinue)
                    continue;

                PNode connNode = new PSwing(conn);
                conn.SetPNode(connNode);

                if (conn.GetNodeData().GetMode() == ConnectorMode.INPUT) {
                    connNode.setOffset(18, lastOffsetY);
                } else {
                    connNode.setOffset(187, lastOffsetY);
                }

                connNode.addInputEventListener(new PBasicInputEventHandler() {
                    public void mousePressed(final PInputEvent aEvent) {
                        draggingConnector = conn;
                        draggingNodeConnector = connNode;
                        draggingConnectorOrigin = connNode.getGlobalTranslation();
                        draggingNodeConnector = pNode;
                        aEvent.setHandled(true);
                    }

                    public void mouseDragged(final PInputEvent aEvent) {
                        aEvent.setHandled(false);
                        conn.FireOnConnectorDragEvent();
                        draggingConnectorFinalPoint = aEvent.getPosition();
                        getLayer().repaint();
                    }

                    public void mouseReleased(final PInputEvent aEvent) {
                        aEvent.setHandled(true);
                        conn.FireOnConnectorDragStopEvent();
                        System.out.println("YES");
                        draggingNodeConnector = null;
                        draggingConnector = null;
                        getLayer().repaint();
                    }
                });

                connNode.addAttribute("tooltip", conn.GetNodeData().GetDisplayName());
                pNode.addChild(connNode);
                lastOffsetY += 18;
            }
            lastOffsetY += 50;
        }
    }

}
