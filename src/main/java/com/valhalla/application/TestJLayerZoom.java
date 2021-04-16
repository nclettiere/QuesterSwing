package com.valhalla.application;

import com.valhalla.NodeEditor.NEditorMouseWheelZoomHandler;
import com.valhalla.application.gui.NodeConnectionPoints;
import com.valhalla.application.gui.NodeConnector;
import com.valhalla.application.gui.NodeSelectorListener;
import com.valhalla.application.gui.NodeSelectorPanel;
import com.valhalla.core.Node.*;
import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.PRoot;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PMouseWheelZoomEventHandler;
import org.piccolo2d.event.PPanEventHandler;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.util.PPaintContext;
import org.w3c.dom.Node;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.ColorModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

import static java.awt.event.MouseEvent.BUTTON2;
import static java.awt.event.MouseEvent.BUTTON3;

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
                if (props.isNodeDragging()) {
                    event.setHandled(true);
                    super.mouseDragged(event);
                    Dimension2D delta = event.getDeltaRelativeTo(pNode);
                    pNode.translate(delta.getWidth(), delta.getHeight());
                    props.setLastInputEvent(event);
                }
            }
        });
        nodeComp.GetNode().AddNodeActionListener(nodeAction -> {
            switch (nodeAction) {
                case NONE -> props.resetNodeState();
                case DRAGGING -> {
                    props.setNodeDragging(true);
                }
                case PRESSED -> {
                    System.out.println("YEEES");
                    props.setNodePressed(true);
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
                props.addConnector(nodeCompUUID, nConn);
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
                if(!props.isNodeDragging() && !props.isNodePressed())
                super.drag(event);
            }
            @Override
            public void setAutopan(boolean autopan) {
                super.setAutopan(false);
            }
        });

        // Add Mouse Click listener
        addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseMoved(PInputEvent event) {
                super.mouseMoved(event);
               // props.setLastInputEvent(event);
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
                }else{
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
            }
        };
    }

    public class NodeEditorProperties {
        protected HashMap<UUID, PNode> pNodesMap;
        protected HashMap<UUID, NodeComponent> nodeComponents;
        protected HashMap<UUID, NodeConnector> nodeConnectors;
        protected ArrayList<Class<? extends NodeComponent>> registeredNodes;

        // Editor Canvas props
        protected Point2D lastMousePosition;
        protected PInputEvent lastInputEvent;
        protected boolean isNodeDragging;
        protected boolean mouseOnCanvas;
        protected boolean isNodePressed;


        NodeEditorProperties() {
            pNodesMap = new HashMap<>();
            nodeComponents = new HashMap<>();
            nodeConnectors = new HashMap<>();
            registeredNodes = new ArrayList<>();
            lastMousePosition = new Point2D.Double(0,0);
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

        /**
         * Add to the list of NodeConnectors of NEditor<br/>
         * <b>Rules to add:</b>
         * There must be a PNode added before adding a new Component.<br/>
         * @param uuid
         * The UUID from the Node Component.
         * @param nCon
         * The Node's Connector component to add.
         */
        public void addConnector(UUID uuid, NodeConnector nCon) {
            if(!pNodesMap.containsKey(uuid))
                return;

            nodeConnectors.put(uuid, nCon);
        }

        public PNode getPNode(UUID uuid) {
            if(!pNodesMap.containsKey(uuid))
                return null;
            return pNodesMap.get(uuid);
        }

        public NodeComponent getNodeComponent(UUID uuid) {
            if(!nodeComponents.containsKey(uuid))
                return null;
            return nodeComponents.get(uuid);
        }

        public NodeConnector getNodeConnector(UUID uuid) {
            if(!nodeConnectors.containsKey(uuid))
                return null;
            return nodeConnectors.get(uuid);
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

        public void setNodeDragging(boolean nodeDragging) {
            isNodeDragging = nodeDragging;
        }

        public boolean isNodeDragging() {
            return isNodeDragging;
        }

        public void resetNodeState() {
            isNodeDragging = false;
            isNodePressed = false;
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
    }
}