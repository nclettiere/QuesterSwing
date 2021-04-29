package com.valhalla.core.Node;

import com.valhalla.NodeEditor.*;
import com.valhalla.application.gui.*;
import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.PRoot;
import org.piccolo2d.event.*;
import org.piccolo2d.extras.event.PNotificationCenter;
import org.piccolo2d.extras.event.PSelectionEventHandler;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PNodeFilter;
import org.piccolo2d.util.PPaintContext;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

import static java.awt.event.MouseEvent.*;

public class NodeEditor extends PSwingCanvas {

    protected UUID editorUUID;
    protected NodeEditorProperties props;
    protected NodeSelectorPanel nsp;
    protected PNode pSelector;
    protected boolean isDebugging;
    protected PCamera camera;

    public NodeEditor(Class<? extends NodeComponent>[] nodeClasses) {
        this.editorUUID = UUID.randomUUID();
        this.props = new NodeEditorProperties();
        this.props.registerNodeClasses(nodeClasses);

        final PRoot root = getRoot();
        camera = getCamera();

        SetupCanvas(root, camera);
        SetupListeners();

       props.setEditorCurrentAction(EditorAction.NONE);
    }

    public void CreateNewNode(Class<? extends NodeComponent> nCompClass) {
        NodeComponent nodeComp;
        try {
            nodeComp = nCompClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            return;
        }

        final UUID uuid = nodeComp.GetNode().GetUUID();
        // Create a new PNode for the NodeComponent
        final PNode pNode = new PSwing(nodeComp);
        // Add nodes to the props list
        props.addNode(uuid, pNode);
        props.addComponent(nodeComp);
        props.addToSelectableList(pNode);
        // Set editor parent
        nodeComp.SetParentEditor(this);
        // Setup node connectors
        SetupNodeConnectors(uuid);
        // Add node listeners
        NodeComponent finalNodeComp = nodeComp;

        for (PropertyBase nodeProperty : nodeComp.getAllProperties()) {
            nodeProperty.AddOnControlUpdateListener(new PropertyEventListener() {
                @Override
                public void OnControlUpdate() {
                    UpdateNode(finalNodeComp);
                }

                @Override
                public void ConnectorAdded(UUID nodeUUID, Integer propIndex, NodeSocket connectorData) {
                    AddConnector(nodeUUID, propIndex, connectorData);
                }

                @Override
                public void ConnectorRemoved(UUID nodeUUID, Integer propIndex, NodeSocket connectorData) {
                    RemoveConnector(nodeUUID, propIndex, connectorData);
                }

                @Override
                public void OnConnect() {

                }

                @Override
                public void OnDisconnect() {

                }
            });
        }

        nodeComp.AddNodeComponentUpdateEvent(nodeComponent -> {
            PNode outputLayout = props.getNodeCompOutputLayout(nodeComponent.GetNode().GetUUID());
            Point2D layOffset = outputLayout.getOffset();
            int nComponentWidth = nodeComponent.getPreferredSize().width;
            outputLayout.setOffset(nComponentWidth - 35, layOffset.getY());
        });

        pNode.addInputEventListener(new PDragSequenceEventHandler() {
            protected Point2D nodeStartPosition;

            protected void startDrag(final PInputEvent event) {
                super.startDrag(event);
                pNode.raiseToTop();
                nodeStartPosition = pNode.getOffset();
            }

            @Override
            public void mouseDragged(PInputEvent event) {
                if (props.isNodeDragging() && !props.isConnectorDragging()) {

                    final Point2D start = getCamera().localToView(
                            (Point2D) getMousePressedCanvasPoint().clone());
                    final Point2D current = event.getPositionRelativeTo(getLayer());
                    final Point2D dest = new Point2D.Double();

                    dest.setLocation(nodeStartPosition.getX() + current.getX() - start.getX(), nodeStartPosition.getY()
                            + current.getY() - start.getY());

                    dest.setLocation(dest.getX() - dest.getX() % props.getGridSpacing(), dest.getY() - dest.getY() % props.getGridSpacing());

                    pNode.setOffset(dest.getX(), dest.getY());
                    UpdateConnectorPosition(finalNodeComp);
                }
            }

            @Override
            public void setMinDragStartDistance(double minDistance) {
                super.setMinDragStartDistance(100d);
            }
        });

        nodeComp.GetNode().AddNodeActionListener(nodeAction -> {
            switch (nodeAction) {
                case NONE -> {
                    props.resetNodeState();
                    getLayer().repaint();
                }
                case CLICKED -> {
                    SelectNode(finalNodeComp, true);
                    props.setEditorCurrentAction(EditorAction.NODE_SELECTED);
                }
                case DRAGGING -> {
                    props.setActionedNode(uuid);
                    props.setNodeDragging(true);
                    SelectNode(finalNodeComp, true);
                    props.setEditorCurrentAction(EditorAction.DRAGGING_NODE);
                    getLayer().repaint();
                }
                case PRESSED -> {
                    props.setActionedNode(uuid);
                    props.setNodePressed(true);
                }
                case CONNECTION_DRAGGING -> {
                    props.setActionedNode(uuid);
                    props.setConnectorDragging(true);
                    props.setTriggerPointCatch(true);
                    props.setEditorCurrentAction(EditorAction.DRAGGING_CONNECTOR);
                    getLayer().repaint();
                }
            }
        });
        // Add node to the canvas
        pNode.setOffset(props.getLastInputEvent().getPosition());
        getLayer().addChild(pNode);
        // ...
        pSelector.setVisible(false);
        nsp.reset();

        fireOnEditorPropertyChanged("NodeCount", props.getNodeCount());
    }

    protected void UpdateNode(NodeComponent nodeComponent) {
        nodeComponent.Update();
        //UpdateNodeConnectors(nodeComponent.GetNode().GetUUID(), false);
    }

    protected void SetupNodeConnectors(UUID nodeCompUUID) {
        PNode pNode = props.getPNode(nodeCompUUID);
        NodeComponent nodeComponent = props.getNodeComponent(nodeCompUUID);
        if (pNode == null || nodeComponent == null) return;

        // Create IO layout PNodes
        // This code enables the PNode to stack the connectors PNodes.
        // Acting as a BoxLayout from top to bottom.
        final PNode inputLayoutNode = new PNode() {
            public void layoutChildren() {
                final double xOffset = 0;
                double yOffset = 0;

                final Iterator i = getChildrenIterator();
                while (i.hasNext()) {
                    final PNode each = (PNode) i.next();
                    each.setOffset(xOffset, yOffset - each.getY());
                    yOffset += each.getHeight() + 7;
                }
            }
        };

        final PNode outputLayoutNode = new PNode() {
            public void layoutChildren() {
                final double xOffset = 0;
                double yOffset = 0;

                final Iterator i = getChildrenIterator();
                while (i.hasNext()) {
                    final PNode each = (PNode) i.next();
                    each.setOffset(xOffset, yOffset - each.getY());
                    yOffset += each.getHeight() + 7;
                }
            }
        };

        // Add the IO layout as child of the NodeComponent's PNode.
        pNode.addChild(inputLayoutNode);
        pNode.addChild(outputLayoutNode);
        inputLayoutNode.setOffset(20, 90);
        outputLayoutNode.setOffset(nodeComponent.getPreferredSize().width - 35, 90);

        // Add and register all node connectors to the nComp
        HashMap<Integer, List<NodeSocket>> nodePropsData =
                nodeComponent.GetNode().getAllConnectorsData();

        // Create NodeConnectors
        Iterator<Map.Entry<Integer, List<NodeSocket>>> it = nodePropsData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<NodeSocket>> pair = it.next();
            List<NodeSocket> connectorDataList = pair.getValue();
            for (NodeSocket data : connectorDataList)
                AddConnector(nodeCompUUID, pair.getKey(), data);
            it.remove();
        }

        // Remove all handled input events from the IO layout to preventing
        // an override of connectors PNode's events.
        inputLayoutNode.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseDragged(PInputEvent event) {
                event.setHandled(false);
            }

            @Override
            public void mouseEntered(PInputEvent event) {
                event.setHandled(false);
            }

            @Override
            public void mouseExited(PInputEvent event) {
                event.setHandled(false);
            }

            @Override
            public void mouseMoved(PInputEvent event) {
                event.setHandled(false);
            }
        });
        inputLayoutNode.setPickable(false);
        outputLayoutNode.setPickable(false);
    }

    protected void SetupConnectorListener(NodeComponent nodeComponent,
                                          NodeConnector connector,
                                          PNode pNodeConnector) {
        UUID connectorUUID = connector.GetNodeSocket().getUuid();

        connector.AddOnControlUpdateListener(
            (dropped,
             initialConnector,
             uuid1,
             uuid2) -> CreateConnection(uuid2, uuid1));

        pNodeConnector.addAttribute("tooltip", connectorUUID.toString() + "\ndata=> " + connector.GetNodeSocket().getData());

        pNodeConnector.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseClicked(PInputEvent event) {
                super.mouseClicked(event);
                // Ctrl pressed \\
                if (props.getPressedKeyCode() == 17) {

                    connector.GetNodeSocket().breakBindings();

                    for (NodeConnectionPoints nConnP : props.getConnectionPoints()) {
                        if (nConnP.getConnector1UUID() == connectorUUID ||
                                nConnP.getConnector2UUID() == connectorUUID) {
                            props.getConnectionPoints().remove(nConnP);
                            break;
                        }
                    }
                    nodeComponent
                            .GetNode()
                            .SetCurrentAction(NodeBase.NodeAction.CONNECTION_CTRL_CLICKED);
                    getLayer().repaint();
                }
            }

            @Override
            public void mouseEntered(PInputEvent event) {
                event.setHandled(false);
                connector.Hover(true);
            }
            @Override
            public void mouseExited(PInputEvent event) {
                event.setHandled(false);
                connector.Hover(false);
            }

            @Override
            public void mouseMoved(PInputEvent event) {
                event.setHandled(false);
                super.mouseMoved(event);
            }

            @Override
            public void mouseDragged(PInputEvent event) {
                event.setHandled(true);
                if (props.getConnectorDraggingUUID() != connector.GetNodeSocket().getUuid() && props.getConnectorDraggingUUID() != null) return;
                if(props.getConnectorDraggingUUID() == null)
                    MatchConnectorType(connector.GetNodeSocket());

                props.setConnectorDraggingUUID(connector.GetNodeSocket().getUuid());
                nodeComponent.GetNode().SetCurrentAction(NodeBase.NodeAction.CONNECTION_DRAGGING);
                props.setLastMousePosition(event.getPositionRelativeTo(getLayer()));
                super.mouseDragged(event);
            }

            @Override
            public void mouseReleased(PInputEvent event) {
                event.setHandled(false);
                NotifyConnectorsDrop(connector.GetNodeSocket().getUuid());
                nodeComponent.GetNode().SetCurrentAction(NodeBase.NodeAction.NONE);
                ResetDataTypeMatch();
                connector.Hover(false);
                super.mouseReleased(event);
            }
        });
    }

    protected void CreateConnection(UUID connector1, UUID connector2) {
        Point2D connector1Point = props
                .getPNodeConnector(connector1)
                .getGlobalBounds()
                .getCenter2D();
        Point2D connector2Point = props
                .getPNodeConnector(connector2)
                .getGlobalBounds()
                .getCenter2D();
        props.getConnectionPoints()
                .add(new NodeConnectionPoints(
                    connector1,
                    connector2,
                    connector1Point,
                    connector2Point));
        repaint();
    }

    protected void AddConnector(UUID nodeComponentUUID, Integer propertyIndex, NodeSocket socket) {
        if(props.getNodeComponent(nodeComponentUUID) != null) {
            final NodeComponent nComp = props.getNodeComponent(nodeComponentUUID);
            // Create Connector Comp and PNode
            final NodeConnector nConn = new NodeConnector(socket);
            final PNode nConnPNode = new PSwing(nConn);
            props.addConnector(nodeComponentUUID, nConn, nConnPNode);

            // Listeners
            socket.addOnBindingEventListener(new SocketEventListener() {
                @Override
                public void onBindingDataChanged(Object data) {

                }

                @Override
                public void onBindingBreak() {
                }

                @Override
                public void onDataEvaluationChanged(NodeSocket socket, SocketState socketState) {
                    if(socketState.errorLevel != StateErrorLevel.PASSING) {
                        nComp.addMessage(socket.getUuid(), new NodeMessage(socketState));
                    }else {
                        nComp.removeMessage();
                    }
                }
            });
            SetupConnectorListener(nComp, nConn, nConnPNode);

            if (socket.getDirection() == SocketDirection.IN) {
                PNode inputLayout = props.getNodeCompInputLayout(nodeComponentUUID);
                if (inputLayout != null)
                    inputLayout.addChild(nConnPNode);
            }else {
                PNode outputLayout = props.getNodeCompOutputLayout(nodeComponentUUID);
                if (outputLayout != null)
                    outputLayout.addChild(nConnPNode);
            }
        }
    }

    protected void RemoveConnector(UUID nodeComponentUUID, Integer propertyIndex, NodeSocket connectorData) {
        PNode nConnPNode = props.getPNodeConnector(connectorData.getUuid());
        nConnPNode.removeFromParent();
        nConnPNode.setVisible(false);
        getLayer().repaint();
    }

    protected void UpdateConnectorPosition(NodeComponent nodeComponent) {
        UUID nCompUUID = nodeComponent.GetNode().GetUUID();
        Set<ConnectorIdentifier> connectorsOfNode =
                props.getConnectorsOfNodeComp(nCompUUID);

        for (ConnectorIdentifier connId : connectorsOfNode) {
            UUID connectorUUID = connId.getNodeConnector().GetNodeSocket().getUuid();
            PNode connectorPNode = connId.getConnectorPNode();
            for (NodeConnectionPoints connPoint : props.getConnectionPoints()) {
                if(connPoint.getConnector1UUID().equals(connectorUUID)) {
                    connPoint.setConnectorPoint1(connectorPNode.getGlobalBounds().getCenter2D());
                }else if(connPoint.getConnector2UUID().equals(connectorUUID)) {
                    connPoint.setConnectorPoint2(connectorPNode.getGlobalBounds().getCenter2D());
                }
            }
        }
    }

    protected void SetupListeners() {
        // add custom mouse wheel zoom
        removeInputEventListener(getZoomEventHandler());
        // install mouse wheel zoom event handler
        addInputEventListener(new NEditorMouseWheelZoomHandler());
        // add custom pan event handler
        removeInputEventListener(getPanEventHandler());

        addInputEventListener(new PPanEventHandler() {
            protected PNode draggedNode;
            protected Point2D nodeStartPosition;

            @Override
            public void setEventFilter(PInputEventFilter newEventFilter) {
                super.setEventFilter(new PInputEventFilter(InputEvent.BUTTON3_MASK));
            }

            protected boolean shouldStartDragInteraction(final PInputEvent event) {
                if (super.shouldStartDragInteraction(event)) {
                    return (event.getPickedNode() == getLayer());
                }
                return false;
            }

            protected void startDrag(final PInputEvent event) {
                draggedNode = event.getPickedNode();
                if (draggedNode != null) {
                    draggedNode.raiseToTop();
                    nodeStartPosition = draggedNode.getOffset();
                }
                super.startDrag(event);
            }

            @Override
            protected void drag(PInputEvent event) {
                props.setEditorCurrentAction(EditorAction.MOVING);
                event.setHandled(false);
                super.drag(event);
            }

            @Override
            protected void endDrag(PInputEvent event) {
                super.endDrag(event);
                props.setEditorCurrentAction(EditorAction.NONE);
            }

            @Override
            public void setAutopan(boolean autopan) {
                super.setAutopan(false);
            }
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                props.setPressedKeyCode(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                props.resetPressedKeyCode();
            }
        });

        addInputEventListener(new PBasicInputEventHandler() {
            // The rectangle that is currently getting created.
            protected PPath rectangle;
            // The mouse press location for the current pressed, drag, release
            // sequence.
            protected Point2D pressPoint;
            // The current drag location.
            protected Point2D dragPoint;
            // The selected nodes array
            protected ArrayList<PNode> pNodeList = new ArrayList<>();

            @Override
            public void setEventFilter(PInputEventFilter newEventFilter) {
                super.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
            }

            public void mousePressed(final PInputEvent e) {
                if (props.isConnectorDragging() || props.isNodeDragging() ||
                        e.getPickedNode() != getLayer())
                {
                    e.setHandled(false);
                    return;
                }

                super.mousePressed(e);

                final PLayer layer = getLayer();

                // Initialize the locations.
                pressPoint = e.getPosition();
                dragPoint = pressPoint;

                // create a new rectangle and add it to the canvas layer so that
                // we can see it.
                rectangle = new PPath.Float();
                rectangle.setPaint(new Color(0,0,0,0));
                rectangle.setStrokePaint(new Color(140,140,140));
                rectangle.setStroke(new BasicStroke((float) (1 / e.getCamera().getViewScale())));
                layer.addChild(rectangle);

                // update the rectangle shape.
                updateRectangle(e);
            }

            public void mouseDragged(final PInputEvent e) {
                if (props.isConnectorDragging() || props.isNodeDragging() ||
                        e.getPickedNode() != getLayer())
                {
                    e.setHandled(false);
                    return;
                }

                super.mouseDragged(e);
                // update the drag point location.
                dragPoint = e.getPosition();
                // update the rectangle shape.
                updateRectangle(e);

            }

            public void mouseReleased(final PInputEvent e) {
                super.mouseReleased(e);
                if (rectangle == null) return;
                // update the rectangle shape.
                getLayer().removeChild(rectangle);
                rectangle = null;
            }

            public void updateRectangle(PInputEvent e) {
                // create a new bounds that contains both the press and current
                // drag point.
                final PBounds b = new PBounds();
                b.add(pressPoint);
                b.add(dragPoint);

                // Set the rectangles bounds.
                rectangle.reset();
                rectangle.append(b, false);
                rectangle.closePath();

                // Find if a NodeComponent is in the area of the rectangle
                // if so, select the node if not unselect it.
                getLayer().findIntersectingNodes(b, pNodeList);
                for (PNode pnode : props.getAllPNodes()) {
                    PSwing ps = (PSwing) pnode;
                    NodeComponent nComp = (NodeComponent) ps.getComponent();
                    if (pNodeList.contains(pnode))
                        nComp.Select();
                    else
                        nComp.Unselect();
                }

                getLayer().repaint();
                pNodeList.clear();
            }
            @Override
            public void mouseMoved(PInputEvent event) {
                event.setHandled(false);
                props.setLastMousePosition(event.getPosition());
                props.setLastInputEvent(event);

                if(props.isTriggerPointCatch()) {
                    props.setCatchedPoint(event.getPosition());
                    props.setTriggerPointCatch(false);
                }
                super.mouseMoved(event);
            }

            @Override
            public void mouseClicked(PInputEvent event) {
                super.mouseClicked(event);
                if (event.getButton() == BUTTON3) {
                    getLayer().addChild(0, pSelector);
                    pSelector.setOffset(event.getPosition());
                    pSelector.setVisible(true);
                    nsp.setFocusField();
                    pSelector.raiseToTop();
                }else if (event.getButton() == BUTTON1) {
                    UnselectNodes();
                    pSelector.setVisible(false);
                    nsp.reset();
                    props.setEditorCurrentAction(EditorAction.NONE);
                }
            }
        });

        // Add node selector panel listener
        nsp.addNodeSelectorEventListener(this::CreateNewNode);
    }

    protected void SetupCanvas(PRoot root, PCamera camera) {
        final PLayer gridLayer = CreateGridLayer();

        // replace current layer with the new grid layer
        root.removeChild(camera.getLayer(0));
        camera.removeLayer(0);
        root.addChild(gridLayer);
        camera.addLayer(gridLayer);

        // setup tooltip
        final PText tooltipNode = new PText();
        tooltipNode.setTextPaint(new Color(144,144,144));
        tooltipNode.setPickable(false);
        camera.addChild(tooltipNode);

        // add constrains so that grid layers bounds always match cameras view
        // bounds. This makes it look like an infinite grid.
        camera.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, evt -> {
            gridLayer.setBounds(camera.getViewBounds());
        });
        camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, evt -> {
            gridLayer.setBounds(camera.getViewBounds());
        });

        // update camra tooltip
        camera.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseDragged(PInputEvent event) {
                updateTooltip(event);
            }

            @Override
            public void mouseMoved(PInputEvent event) {
                updateTooltip(event);
            }

            public void updateTooltip(final PInputEvent event) {
                final PNode pNode = event.getPickedNode();
                String tooltipStr = (String) pNode.getAttribute("tooltip");
                if (pNode instanceof PSwing) {
                    PSwing ps = (PSwing) pNode;
                    if (ps.getComponent() instanceof NodeConnector) {
                        NodeConnector nConn = (NodeConnector) ps.getComponent();
                        NodeSocket socket = nConn.GetNodeSocket();
                        tooltipStr = tooltipStr + socket.getData();
                    }
                }
                final Point2D p = event.getCanvasPosition();

                event.getPath().canvasToLocal(p, camera);

                tooltipNode.setText(tooltipStr);
                tooltipNode.setOffset(p.getX() + 8, p.getY() - 8);
            }
        });

        nsp = new NodeSelectorPanel(props.getRegisteredNodeClasses());
        pSelector = new PSwing(nsp);
        pSelector.setVisible(false);
    }

    protected PLayer CreateGridLayer() {
        final Line2D gridLine = new Line2D.Double();
        final Stroke gridStroke = new BasicStroke(1);
        final Color gridPaint = Color.BLACK;
        final double gridSpacingThick = 150;

        return new PLayer() {
            protected void paint(final PPaintContext paintContext) {
                // make sure grid gets drawn on snap to grid boundaries. And
                // expand a little to make sure that entire view is filled.
                final double bx = getX() - getX() % props.getGridSpacing() - props.getGridSpacing();
                final double by = getY() - getY() % props.getGridSpacing() - props.getGridSpacing();
                final double rightBorder = getX() + getWidth() + props.getGridSpacing();
                final double bottomBorder = getY() + getHeight() + props.getGridSpacing();

                final double bxT = getX() - getX() % gridSpacingThick - gridSpacingThick;
                final double byT = getY() - getY() % gridSpacingThick - gridSpacingThick;
                final double rightBorderT = getX() + getWidth() + gridSpacingThick;
                final double bottomBorderT = getY() + getHeight() + gridSpacingThick;

                final Graphics2D g2 = paintContext.getGraphics();
                final Rectangle2D clip = paintContext.getLocalClip();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(50, 50, 50));

                g2.fillRect((int)clip.getX() - 10, (int)clip.getY() - 10, (int)clip.getWidth() + 100, (int)clip.getHeight() + 100);

                g2.setStroke(gridStroke);
                g2.setColor(new Color(70, 70, 70));

                for (double x = bx; x < rightBorder; x += props.getGridSpacing()) {
                    gridLine.setLine(x, by, x, bottomBorder);
                    if (clip.intersectsLine(gridLine)) {
                        g2.draw(gridLine);
                    }
                }

                for (double y = by; y < bottomBorder; y += props.getGridSpacing()) {
                    gridLine.setLine(bx, y, rightBorder, y);
                    if (clip.intersectsLine(gridLine)) {
                        g2.draw(gridLine);
                    }
                }

                g2.setColor(new Color(30, 30, 30));

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

                // CONNECTORS ZONE

                // On connector drag
                if(props.isConnectorDragging())
                    DrawConnection(g2);
                // Created Connections
                for (NodeConnectionPoints connPoints : props.getConnectionPoints())
                    DrawConnection(g2, connPoints);
            }
        };
    }

    public void NotifyConnectorsDrop(UUID socketUUID) {
        final NodeConnector nConn = props.getNodeConnector(socketUUID);
        if (nConn == null) return;
        final NodeSocket socket = nConn.GetNodeSocket();
        for (NodeConnector connector : props.getAllNodeConnectors())
            connector.ConnectorDropped(nConn, socket);
    }

    public void MatchConnectorType(NodeSocket nodeData) {
        for(NodeConnector connector : props.getAllNodeConnectors())
            connector.MatchType(nodeData);
    }

    public void ResetDataTypeMatch() {
        for(NodeConnector connector : props.getAllNodeConnectors())
            connector.ResetMatch();
    }

    public UUID getEditorUUID() {
        return editorUUID;
    }

    // Created Connections
    private void DrawConnection(Graphics2D graphics, NodeConnectionPoints points) {
        if(props.getActionedNode() != null) {
            NodeComponent selectedNode = props.getNodeComponent(props.getActionedNode());

            if (points.getConnector1UUID() == selectedNode.GetNode().GetUUID() ||
                    points.getConnector2UUID() == selectedNode.GetNode().GetUUID())
                graphics.setColor(new Color(240, 175, 50));
            else
                graphics.setColor(new Color(200,200,200));
        }else {
            graphics.setColor(new Color(200,200,200));
        }

        graphics.setStroke(new BasicStroke(1.3f));

        CubicCurve2D c = new CubicCurve2D.Double();

        Point2D curveOrigin = points.getConnectorPoint1();
        Point2D curveEnd = points.getConnectorPoint2();

        if(curveOrigin == null || curveEnd == null)
            return;

        Point curveOriginCtrl = new Point();
        Point curveEndCtrl = new Point();

        float delta = ((float)(curveEnd.getX()) / (float)(curveOrigin.getX())) - 1.0f;
        if(delta < 0f)
            delta = delta * -1;
        if(delta > 0.8f)
            delta = 0.8f;

        curveOriginCtrl.x = (int) curveOrigin.getX() + (int)(300 * delta);
        curveOriginCtrl.y = (int) curveOrigin.getY();
        curveEndCtrl.x = (int) curveEnd.getX() - (int)(300 * delta);
        curveEndCtrl.y = (int) curveEnd.getY();

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

        if(isDebugging) {
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
    // Dragging
    private void DrawConnection(Graphics2D graphics) {
        NodeConnector draggingConnector = props.getNodeConnector(props.getConnectorDraggingUUID());
        PNode pNodeConnector = props.getPNodeConnector(props.getConnectorDraggingUUID());

        if(draggingConnector == null || pNodeConnector == null) return;

        //final Point2D p = getMousePosition(true);
        //final double scale = camera.getViewTransform().getScale();
        //props.getLastInputEvent().getPath().canvasToLocal(p, camera);
        //final double translationX = camera.getViewTransform().getTranslateX() + scale;
        //final double translationY = camera.getViewTransform().getTranslateY() + scale;

        //Point2D curveEnd = new Point2D.Double(p.getX() - translationX, p.getY() - translationY);

        Point2D curveOrigin = pNodeConnector.getGlobalBounds().getOrigin();
        Point2D curveEnd = props.getLastMousePosition();

        if (curveOrigin == null || curveEnd == null) return;

        graphics.setColor(new Color(200,200,200));
        graphics.setStroke(new BasicStroke(1.3f));
        //graphics.drawLine(dragOrigin.x, dragOrigin.y, getMousePosition().x, getMousePosition().y);

        CubicCurve2D c = new CubicCurve2D.Double();

        // Add the connector size
        curveOrigin = new Point2D.Double(curveOrigin.getX() + 5, curveOrigin.getY() + 5);

        Point curveOriginCtrl = new Point();
        Point curveEndCtrl = new Point();

        float delta = ((float)(curveEnd.getX()) / (float)(curveOrigin.getX())) - 1.0f;
        if(delta < 0f)
            delta = delta * -1;
        if(delta > 0.8f)
            delta = 0.8f;

        curveOriginCtrl.x = (int) curveOrigin.getX() + (int)(300 * delta);
        curveOriginCtrl.y = (int) curveOrigin.getY();
        curveEndCtrl.x = (int) curveEnd.getX() - (int)(300 * delta);
        curveEndCtrl.y = (int) curveEnd.getY();

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

        if(isDebugging) {
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

    protected void UnselectNode(NodeComponent nodeComponent) {
        nodeComponent.Unselect();
    }
    protected void UnselectNodes(NodeComponent ... nodeComponent) {
        for (NodeComponent nComp : nodeComponent)
            nComp.Unselect();
    }
    protected void UnselectNodes() {
        for (NodeComponent nComp : props.getAllNodeComponents())
            nComp.Unselect();
    }
    protected void SelectNode(NodeComponent nodeComponent, boolean unselectFirst) {
        if(unselectFirst)
            for (NodeComponent nComp : props.getAllNodeComponents())
                nComp.Unselect();
        nodeComponent.Select();
        fireOnNodeSelectionChanged(nodeComponent.GetNode());
    }
    protected void SelectNodes(boolean unselectFirst, NodeComponent ... nodeComponents) {
        if(unselectFirst)
            for (NodeComponent nComp : props.getAllNodeComponents())
                nComp.Unselect();
        for (NodeComponent nComp : nodeComponents)
            nComp.Select();
    }

    void fireOnNodeSelectionChanged(NodeBase node) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == EditorPropertyListener.class) {
                ((EditorPropertyListener) listeners[i+1]).OnNodeSelectionChanged(node);
            }
        }
    }

    void fireOnEditorPropertyChanged(String propertyName, Object value) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == EditorPropertyListener.class) {
                ((EditorPropertyListener) listeners[i+1]).OnPropertyChanged(propertyName, value);
            }
        }
    }

    public void addEditorPropertyListener(EditorPropertyListener listener) {
        listenerList.add(EditorPropertyListener.class, listener);
    }
    public void removeEditorPropertyListener(EditorPropertyListener listener) {
        listenerList.remove(EditorPropertyListener.class, listener);
    }

    public int getNodeCount() {
        return props.getNodeCount();
    }

    public boolean isDebugging() {
        return isDebugging;
    }

    public void setDebugging(boolean isDebugging) {
        this.isDebugging = isDebugging;
        fireOnEditorPropertyChanged("Debugging", isDebugging);
        repaint();
    }

    public EditorAction getCurrentAction() {
        return props.getEditorCurrentAction();
    }

    public class NodeEditorProperties {
        protected HashMap<UUID, PNode>                      pNodesMap;
        protected HashMap<UUID, ConnectorIdentifier>        connectorsMap;
        protected HashMap<UUID, NodeComponent>              nodeComponents;
        protected List<Class<? extends NodeComponent>>      registeredNodes;
        protected List<NodeConnectionPoints>                connectionPoints;
        protected ArrayList                                 selectablePNodes;

        // Editor Canvas props
        protected double      gridSpacing;
        protected UUID        actionedNode;
        protected UUID        connectorDraggingUUID;
        protected Point2D     lastMousePosition;
        protected PInputEvent lastInputEvent;
        protected boolean     isNodeDragging;
        protected boolean     isConnectorDragging;
        protected boolean     mouseOnCanvas;
        protected boolean     isNodePressed;
        protected int         pressedKeyCode;

        protected volatile EditorAction editorCurrentAction;

        protected volatile boolean nodeReseted;
        protected volatile boolean triggerPointCatch;
        protected volatile Point2D catchedPoint;


        NodeEditorProperties() {
            pNodesMap = new HashMap<>();
            connectorsMap = new HashMap<>();
            nodeComponents = new HashMap<>();
            registeredNodes = new ArrayList<>();
            selectablePNodes = new ArrayList();
            connectionPoints = new ArrayList<>();
            lastMousePosition = new Point2D.Double(0,0);
            nodeReseted = true;
            gridSpacing = 15;
        }

        /**
         * Add a PNode related to the Node Component UUID.<br/>
         * If a PNode with the same UUID is added, this method will do nothing.
         * @param uuid
         * The UUID from the Node Component.<br/>
         * Note: Not the PNode.
         * @param pNode
         * The generated PNode of the Component.
        */
        public void addNode(UUID uuid, PNode pNode) {
            if(pNodesMap.containsKey(uuid))
                return;
            pNodesMap.put(uuid, pNode);
        }

        public void addConnector(UUID uuid, NodeConnector nodeConnector, PNode pNodeConnector) {
            UUID connectorUUID = nodeConnector.GetNodeSocket().getUuid();
            if(connectorsMap.containsKey(connectorUUID))
                return;
            ConnectorIdentifier connectorIdentifier = new ConnectorIdentifier(
                    uuid,
                    nodeConnector,
                    pNodeConnector);
            connectorsMap.put(connectorUUID, connectorIdentifier);
        }

        /**
         * Add to the list of NodeComponents of NEditor<br/>
         * <b>Rules to add:</b>
         * There must be a PNode added before adding a new Component.<br/>
         * @param nComp
         * The Node component to add.
         */
        public void addComponent(NodeComponent nComp) {
            UUID uuid = nComp.GetNode().GetUUID();

            if(!pNodesMap.containsKey(uuid))
                return;

            nodeComponents.put(uuid, nComp);
        }

        public HashMap<UUID, ConnectorIdentifier> getConnectorsMap() {
            return connectorsMap;
        }

        public PNode getPNode(UUID uuid) {
            if(!pNodesMap.containsKey(uuid))
                return null;
            return pNodesMap.get(uuid);
        }

        public PNode getPNodeConnector(UUID uuidConnector) {
            if(!connectorsMap.containsKey(uuidConnector)) return null;
            return connectorsMap.get(uuidConnector).getConnectorPNode();
        }

        public NodeComponent getNodeComponent(UUID uuid) {
            if(!nodeComponents.containsKey(uuid))
                return null;
            return nodeComponents.get(uuid);
        }

        public NodeConnector getNodeConnector(UUID uuidConnector) {
            if(!connectorsMap.containsKey(uuidConnector)) return null;
            return connectorsMap.get(uuidConnector).getNodeConnector();
        }

        public void registerNodeClasses(Class<? extends NodeComponent>[] nodeClasses) {
            this.registeredNodes.addAll(Arrays.asList(nodeClasses));
        }

        public List<Class<? extends NodeComponent>> getRegisteredNodeClasses() {
            return this.registeredNodes;
        }

        public PNode getNodeCompInputLayout(UUID uuid) {
            if(!nodeComponents.containsKey(uuid)) return null;
            PNode nodeCompPNode = pNodesMap.get(uuid);
            return nodeCompPNode.getChild(0);
        }

        public PNode getNodeCompOutputLayout(UUID uuid) {
            if(!nodeComponents.containsKey(uuid)) return null;
            PNode nodeCompPNode = pNodesMap.get(uuid);
            return nodeCompPNode.getChild(1);
        }


        public PInputEvent getLastInputEvent() {
            return lastInputEvent;
        }

        public void setLastInputEvent(PInputEvent lastInputEvent) {
            if(lastInputEvent != null)
                this.lastInputEvent = lastInputEvent;
        }

        public UUID getActionedNode() {
            return actionedNode;
        }

        public void setActionedNode(UUID actionedNode) {
            this.actionedNode = actionedNode;
        }

        public void setNodeDragging(boolean nodeDragging) {
            isNodeDragging = nodeDragging;
        }

        public boolean isNodeDragging() {
            return isNodeDragging;
        }

        public boolean isConnectorDragging() {
            return isConnectorDragging;
        }

        public void setConnectorDragging(boolean connectorDragging) {
                isConnectorDragging = connectorDragging;
        }

        public void resetNodeState() {
            actionedNode = null;
            isNodeDragging = false;
            isNodePressed = false;
            isConnectorDragging = false;
            connectorDraggingUUID = null;
            nodeReseted = true;
        }

        public double getGridSpacing() {
            return gridSpacing;
        }

        public void setGridSpacing(double gridSpacing) {
            this.gridSpacing = gridSpacing;
        }

        public boolean isMouseOnCanvas() {
            return mouseOnCanvas;
        }

        public void setMouseOnCanvas(boolean mouseOnCanvas) {
            this.mouseOnCanvas = mouseOnCanvas;
        }

        public boolean isNodePressed() {
            return isNodePressed;
        }

        public void setNodePressed(boolean nodePressed) {
                isNodePressed = nodePressed;
        }

        public Iterable<? extends NodeComponent> getAllNodeComponents() {
            return nodeComponents.values();
        }

        public Iterable<? extends PNode> getAllPNodes() {
            return pNodesMap.values();
        }

        public Iterable<? extends NodeConnector> getAllNodeConnectors() {
            List<NodeConnector> connectorList = new ArrayList<>();
            for (ConnectorIdentifier connId : connectorsMap.values())
                connectorList.add(connId.nodeConnector);
            return connectorList;
        }

        public boolean isTriggerPointCatch() {
            return triggerPointCatch;
        }

        public void setTriggerPointCatch(boolean triggerPointCatch) {
            if(nodeReseted)
                this.triggerPointCatch = triggerPointCatch;
        }

        public Point2D getCatchedPoint() {
            return catchedPoint;
        }

        public Point2D getLastMousePosition() {
            return lastMousePosition;
        }

        public void setLastMousePosition(Point2D lastMousePosition) {
            this.lastMousePosition = lastMousePosition;
        }

        public void setCatchedPoint(Point2D catchedPoint) {
            this.catchedPoint = catchedPoint;
        }

        public void setConnectorDraggingUUID(UUID connectorDraggingUUID) {
            this.connectorDraggingUUID = connectorDraggingUUID;
        }

        public UUID getConnectorDraggingUUID() {
            return this.connectorDraggingUUID;
        }

        public List<NodeConnectionPoints> getConnectionPoints() {
            return connectionPoints;
        }

        public Set<ConnectorIdentifier> getConnectorsOfNodeComp(UUID nodeCompUUID) {
            HashSet<ConnectorIdentifier> connectors = new HashSet<>();
            if(nodeComponents.containsKey(nodeCompUUID)) {
                for (ConnectorIdentifier connId : connectorsMap.values()) {
                    if(connId.nodeCompUUID.equals(nodeCompUUID))
                        connectors.add(connId);
                }
            }
            return connectors;
        }

        public void deleteConnector(NodeSocket socket) {
            if(connectorsMap.containsKey(socket.getUuid())) {
                connectorsMap.remove(connectorsMap.get(socket.getUuid()));
            }

            NodeConnectionPoints connPointsToDelete = null;
            for (NodeConnectionPoints connPoints : connectionPoints) {
                if(connPoints.getConnector1UUID() == socket.getUuid() ||
                        connPoints.getConnector2UUID() == socket.getUuid()) {
                    connPointsToDelete = connPoints;
                    break;
                }
            }

            if (connPointsToDelete != null) {
                connectionPoints.remove(connPointsToDelete);
            }
        }

        public ArrayList getSelectablePNodes() {
            return selectablePNodes;
        }

        public void addToSelectableList(PNode pNode) {
            selectablePNodes.add(pNode);
        }

        public void removeFromSelectableList(PNode pNode) {
            selectablePNodes.remove(pNode);
        }

        public int getPressedKeyCode() {
            return pressedKeyCode;
        }

        public void resetPressedKeyCode() {
            this.pressedKeyCode = -1;
        }

        public void setPressedKeyCode(int pressedKeyCode) {
            this.pressedKeyCode = pressedKeyCode;
        }

        public int getNodeCount() {
            return nodeComponents.size();
        }

        public EditorAction getEditorCurrentAction() {
            return editorCurrentAction;
        }

        public void setEditorCurrentAction(EditorAction editorCurrentAction) {
            this.editorCurrentAction = editorCurrentAction;
            fireOnEditorPropertyChanged("CurrentAction", editorCurrentAction);
        }
    }

    public class ConnectorIdentifier {
        private UUID          nodeCompUUID;
        private NodeConnector nodeConnector;
        private PNode         connectorPNode;

        public ConnectorIdentifier() {}

        public ConnectorIdentifier(UUID nodeCompUUID, NodeConnector nodeConnector, PNode connectorPNode) {
            this.nodeCompUUID = nodeCompUUID;
            this.nodeConnector = nodeConnector;
            this.connectorPNode = connectorPNode;
        }

        public UUID getNodeCompUUID() {
            return nodeCompUUID;
        }

        public void setNodeCompUUID(UUID nodeCompUUID) {
            this.nodeCompUUID = nodeCompUUID;
        }

        public NodeConnector getNodeConnector() {
            return nodeConnector;
        }

        public void setNodeConnector(NodeConnector nodeConnector) {
            this.nodeConnector = nodeConnector;
        }

        public PNode getConnectorPNode() {
            return connectorPNode;
        }

        public void setConnectorPNode(PNode connectorPNode) {
            this.connectorPNode = connectorPNode;
        }
    }

    public enum EditorAction {
        NONE,
        MOVING,
        ZOOM_IN,
        ZOOM_OUT,
        NODE_SELECTED,
        MOVING_NODE,
        DRAGGING_NODE,
        DRAGGING_CONNECTOR,
        SEARCHING_NODE,
        DELETE_NODE,
        CREATED_CONNECTION,
        BREAK_CONNECTION,
        SIMULATING,
        NODE_CONTROL_UPDATED
    }
}