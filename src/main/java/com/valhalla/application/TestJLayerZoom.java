package com.valhalla.application;

import com.valhalla.NodeEditor.NEditorMouseWheelZoomHandler;
import com.valhalla.application.gui.*;
import com.valhalla.core.Node.*;
import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.PRoot;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PPanEventHandler;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.util.PPaintContext;

import java.awt.*;
import java.awt.geom.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

import static java.awt.event.MouseEvent.*;

public class TestJLayerZoom extends PSwingCanvas {

    protected NodeEditorProperties props;
    protected NodeSelectorPanel nsp;
    protected PNode pSelector;

    public TestJLayerZoom(Class<? extends NodeComponent>[] nodeClasses) {
        this.props = new NodeEditorProperties();
        this.props.registerNodeClasses(nodeClasses);

        final PRoot root = getRoot();
        final PCamera camera = getCamera();

        SetupCanvas(root, camera);
        SetupListeners();
    }

    public void CreateNewNode(Class<? extends NodeComponent> nCompClass) {
        NodeComponent nodeComp = null;
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
        // Set editor parent
        nodeComp.SetParentEditor(this);
        // Setup node connectors
        UpdateNodeConnectors(uuid);
        // Add node listeners
        pNode.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseDragged(PInputEvent event) {
                if (props.isNodeDragging() && !props.isConnectorDragging()) {
                    event.setHandled(true);
                    super.mouseDragged(event);
                    Dimension2D delta = event.getDeltaRelativeTo(pNode);
                    pNode.translate(delta.getWidth(), delta.getHeight());
                    props.setLastInputEvent(event);
                }
            }
        });

        NodeComponent finalNodeComp = nodeComp;
        nodeComp.GetNode().AddNodeActionListener(nodeAction -> {
            switch (nodeAction) {
                case NONE -> {
                    props.resetNodeState();
                    getLayer().repaint();
                }
                case DRAGGING -> {
                    props.setActionedNode(uuid);
                    props.setNodeDragging(true);
                    SelectNode(finalNodeComp, true);
                }
                case PRESSED -> {
                    props.setActionedNode(uuid);
                    props.setNodePressed(true);
                }
                case CONNECTION_DRAGGING -> {
                    props.setActionedNode(uuid);
                    props.setConnectorDragging(true);
                    props.setTriggerPointCatch(true);
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
    }

    public void UpdateNodeConnectors(UUID nodeCompUUID) {
        PNode pNode = props.getPNode(nodeCompUUID);
        if(pNode == null) return;
        NodeComponent nodeComponent = props.getNodeComponent(nodeCompUUID);
        if(nodeComponent == null) return;
        // Get Connector Data
        HashMap<Integer, List<INodeData>> nodePropsData =
                nodeComponent.GetNode().getAllConnectorsData();
        // Create NodeConnectors
        Iterator<Map.Entry<Integer, List<INodeData>>> it = nodePropsData.entrySet().iterator();
        int inputOffsetY = 95;
        int outputOffsetY = 95;
        while (it.hasNext()) {
            Map.Entry<Integer, List<INodeData>> pair = it.next();
            List<INodeData> connectorDataList = pair.getValue();

            for (INodeData data : connectorDataList) {
                NodeConnector nConn = new NodeConnector(data, nodeComponent);
                PNode nConnPNode = new PSwing(nConn);
                SetupConnectorListener(nodeComponent, nConn, nConnPNode);
                props.addConnector(nodeCompUUID, nConn, nConnPNode);
                pNode.addChild(nConnPNode);

                if(data.GetMode() == ConnectorMode.INPUT) {
                    nConnPNode.setOffset(20, inputOffsetY);
                    inputOffsetY += 22;
                }else {
                    nConnPNode.setOffset(187, outputOffsetY);
                    outputOffsetY += 22;
                }
            }

            // Add a margin to delimit a new property
            if (inputOffsetY > outputOffsetY) {
                inputOffsetY += 38;
                outputOffsetY = inputOffsetY;
            }else {
                outputOffsetY += 38;
                inputOffsetY = outputOffsetY;
            }

            it.remove();
        }
    }

    protected void SetupConnectorListener(NodeComponent nodeComponent,
                                          NodeConnector connector,
                                          PNode pNodeConnector) {
        UUID connectorUUID =
                connector.GetNodeData().GetUUID();

        connector.AddOnControlUpdateListener(
                (dropped, initialConnector, uuid1, uuid2) ->
                    CreateConnection(uuid1, uuid2));

        pNodeConnector.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseEntered(PInputEvent event) {
                super.mouseEntered(event);

                if(props.isConnectorDragging()) {
                    if(!props.getConnectorDraggingUUID().equals(connectorUUID))
                        connector.Hover(true);
                }else {
                    connector.Hover(true);
                }
            }

            @Override
            public void mouseExited(PInputEvent event) {
                super.mouseExited(event);
                connector.Hover(false);
            }

            @Override
            public void mouseDragged(PInputEvent event) {
                event.setHandled(false);
                if(props.getConnectorDraggingUUID() == null)
                    MatchConnectorType(connector.GetNodeData().getClass());

                props.setConnectorDraggingUUID(connector.GetNodeData().GetUUID());
                nodeComponent.GetNode().SetCurrentAction(NodeBase.NodeAction.CONNECTION_DRAGGING);
                super.mouseDragged(event);
            }

            @Override
            public void mouseReleased(PInputEvent event) {
                event.setHandled(false);
                NotifyConnectorsDrop();
                nodeComponent.GetNode().SetCurrentAction(NodeBase.NodeAction.NONE);
                ResetDataTypeMatch();
                super.mouseReleased(event);
            }
        });
    }

    protected void CreateConnection(UUID connector1, UUID connector2) {
        Point2D connector1Point = props
                .getPNodeConnector(connector1)
                .getGlobalBounds()
                .getOrigin();
        Point2D connector2Point = props
                .getPNodeConnector(connector2)
                .getGlobalBounds()
                .getOrigin();
        props.getConnectionPoints()
                .add(new NodeConnectionPoints(
                    connector1,
                    connector2,
                    connector1Point,
                    connector2Point));
        repaint();
    }

    protected void SetupListeners() {
        // add custom mouse wheel zoom
        removeInputEventListener(getZoomEventHandler());
        // install mouse wheel zoom event handler
        addInputEventListener(new NEditorMouseWheelZoomHandler());
        // add custom pan event handler
        removeInputEventListener(getPanEventHandler());
        addInputEventListener(new PPanEventHandler() {
            @Override
            protected void drag(PInputEvent event) {
                //props.setLastInputEvent(event);
                PNode pS = event.getPickedNode();
                if(!pS.getPickable() || (pS instanceof PLayer) && !props.isConnectorDragging())
                    super.drag(event);
            }
            @Override
            public void setAutopan(boolean autopan) {
                super.setAutopan(false);
            }
        });

        // Add Mouse listeners
        addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseMoved(PInputEvent event) {
                super.mouseMoved(event);
                props.setLastInputEvent(event);
                if(props.isTriggerPointCatch()) {
                    props.setCatchedPoint(event.getPosition());
                    props.setTriggerPointCatch(false);
                }
            }

            @Override
            public void mouseClicked(PInputEvent event) {
                super.mouseClicked(event);
                props.setLastInputEvent(event);
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
                }
            }
        });
        // Add node selector panel listener
        nsp.addNodeSelectorEventListener(new NodeSelectorListener() {
            @Override
            public void OnNodeSelected(Class<? extends NodeComponent> nodeClass) {
                CreateNewNode(nodeClass);
            }
        });
    }

    protected void SetupCanvas(PRoot root, PCamera camera) {
        final PLayer gridLayer = CreateGridLayer();

        // replace current layer with the new grid layer
        root.removeChild(camera.getLayer(0));
        camera.removeLayer(0);
        root.addChild(gridLayer);
        camera.addLayer(gridLayer);

        // add constrains so that grid layers bounds always match cameras view
        // bounds. This makes it look like an infinite grid.
        camera.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, evt ->
                gridLayer.setBounds(camera.getViewBounds()));
        camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, evt ->
                gridLayer.setBounds(camera.getViewBounds()));

        nsp = new NodeSelectorPanel(props.getRegisteredNodeClasses());
        pSelector = new PSwing(nsp);
        pSelector.setVisible(false);
    }

    protected PLayer CreateGridLayer() {
        final Line2D gridLine = new Line2D.Double();
        final Stroke gridStroke = new BasicStroke(1);
        final Color gridPaint = Color.BLACK;
        final double gridSpacing = 15;
        final double gridSpacingThick = 150;

        return new PLayer() {
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

                g2.setColor(new Color(50, 50, 50));

                g2.fillRect((int)clip.getX() - 10, (int)clip.getY() - 10, (int)clip.getWidth() + 100, (int)clip.getHeight() + 100);

                g2.setStroke(gridStroke);
                g2.setColor(new Color(70, 70, 70));

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

    public void NotifyConnectorsDrop() {
        final NodeConnector nConn =
                props.getNodeConnector(props.getConnectorDraggingUUID());
        final INodeData nodeData = nConn.GetNodeData();
        for (NodeConnector connector : props.getAllNodeConnectors())
            connector.ConnectorDropped(nConn, nodeData);
    }

    public void MatchConnectorType(Class<? extends INodeData> dataType) {
        for(NodeConnector connector : props.getAllNodeConnectors())
            connector.MatchType(dataType);
    }

    public void ResetDataTypeMatch() {
        for(NodeConnector connector : props.getAllNodeConnectors())
            connector.ResetMatch();
    }

    // Created Connections
    private void DrawConnection(Graphics2D graphics, NodeConnectionPoints points) {
        if(props.getActionedNode() != null) {
            NodeComponent selectedNode = props.getNodeComponent(props.getActionedNode());

            if (points.getConnector1UUID() == selectedNode.GetNode().GetUUID() ||
                    points.getConnector2UUID() == selectedNode.GetNode().GetUUID())
                graphics.setColor(new Color(240, 175, 50));
            else
                graphics.setColor(new Color(255, 255, 255, 180));
        }else {
            graphics.setColor(new Color(255, 255, 255, 180));
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

        //if(GetDebugPaint()) {
        //    graphics.setColor(Color.GREEN);
        //    //OriginPoint
        //    graphics.fillOval((int)curveOrigin.getX(), (int)curveOrigin.getY(), 10, 10);
        //    //EndPoint
        //    graphics.setColor(Color.YELLOW);
        //    graphics.fillOval((int)curveEnd.getX(), (int)curveEnd.getY(), 10, 10);
//
        //    graphics.setColor(Color.RED);
        //    //ctrlOrigin
        //    graphics.drawOval(curveOriginCtrl.x, curveOriginCtrl.y, 10, 10);
        //    //ctrlEND
        //    graphics.drawOval(curveEndCtrl.x, curveEndCtrl.y, 10, 10);
//
        //}
    }
    // Dragging
    private void DrawConnection(Graphics2D graphics) {
        NodeConnector draggingConnector = props.getNodeConnector(props.getConnectorDraggingUUID());
        PNode pNodeConnector = props.getPNodeConnector(props.getConnectorDraggingUUID());
        if(draggingConnector == null) return;

        graphics.setColor(new Color(255,255,255, 180));
        graphics.setStroke(new BasicStroke(1.3f));
        //graphics.drawLine(dragOrigin.x, dragOrigin.y, getMousePosition().x, getMousePosition().y);

        CubicCurve2D c = new CubicCurve2D.Double();

        //Point2D curveOrigin = draggingConnectorOrigin;
        Point2D curveOrigin = pNodeConnector.getGlobalBounds().getOrigin();
        Point2D curveEnd = props.getLastInputEvent().getPosition();
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

        //if(GetDebugPaint()) {
        //    graphics.setColor(Color.GREEN);
        //    //OriginPoint
        //    graphics.fillOval((int)curveOrigin.getX(), (int)curveOrigin.getY(), 10, 10);
        //    //EndPoint
        //    graphics.setColor(Color.YELLOW);
        //    graphics.fillOval((int)curveEnd.getX(), (int)curveEnd.getY(), 10, 10);

        //    graphics.setColor(Color.RED);
        //    //ctrlOrigin
        //    graphics.drawOval(curveOriginCtrl.x, curveOriginCtrl.y, 10, 10);
        //    //ctrlEND
        //    graphics.drawOval(curveEndCtrl.x, curveEndCtrl.y, 10, 10);
        //}
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
    }
    protected void SelectNodes(boolean unselectFirst, NodeComponent ... nodeComponents) {
        if(unselectFirst)
            for (NodeComponent nComp : props.getAllNodeComponents())
                nComp.Unselect();
        for (NodeComponent nComp : nodeComponents)
            nComp.Select();
    }

    public class NodeEditorProperties {
        protected HashMap<UUID, PNode>                      pNodesMap;
        protected HashMap<UUID, ConnectorIdentifier>        connectorsMap;
        protected HashMap<UUID, NodeComponent>              nodeComponents;
        protected List<Class<? extends NodeComponent>>      registeredNodes;
        protected List<NodeConnectionPoints>                connectionPoints;

        // Editor Canvas props
        protected UUID        actionedNode;
        protected UUID        connectorDraggingUUID;
        protected Point2D     lastMousePosition;
        protected PInputEvent lastInputEvent;
        protected boolean     isNodeDragging;
        protected boolean     isConnectorDragging;
        protected boolean     mouseOnCanvas;
        protected boolean     isNodePressed;

        protected volatile boolean nodeReseted;
        protected volatile boolean triggerPointCatch;
        protected volatile Point2D catchedPoint;


        NodeEditorProperties() {
            pNodesMap = new HashMap<>();
            connectorsMap = new HashMap<>();
            nodeComponents = new HashMap<>();
            registeredNodes = new ArrayList<>();
            connectionPoints = new ArrayList<>();
            lastMousePosition = new Point2D.Double(0,0);
            nodeReseted = true;
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
            UUID connectorUUID = nodeConnector.GetNodeData().GetUUID();
            if(connectorsMap.containsKey(connectorUUID))
                return;
            ConnectorIdentifier connectorIdentifier = new ConnectorIdentifier(
                    uuid,
                    nodeConnector,
                    pNodeConnector);
            connectorsMap.put(nodeConnector.GetNodeData().GetUUID(), connectorIdentifier);
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
    }
}