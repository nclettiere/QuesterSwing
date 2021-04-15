package com.valhalla.application;

import com.valhalla.NodeEditor.NEditorMouseWheelZoomHandler;
import com.valhalla.application.gui.NodeConnectionPoints;
import com.valhalla.application.gui.NodeConnector;
import com.valhalla.core.Node.NodeComponent;
import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.PRoot;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PMouseWheelZoomEventHandler;
import org.piccolo2d.event.PPanEventHandler;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.util.PPaintContext;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestJLayerZoom extends PSwingCanvas {

    protected NodeEditorProperties props;

    public TestJLayerZoom() {
        this.props = new NodeEditorProperties();

        final PRoot root = getRoot();
        final PCamera camera = getCamera();

        SetupListeners();
        SetupGridLayer(root, camera);
    }

    public void CreateNewNode(Class<? extends NodeComponent> nCompClass) {
        NodeComponent nodeComp = null;
        try {
            nodeComp = nCompClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            return;
        }

        UUID uuid = nodeComp.GetNode().GetUUID();
        // Create a new PNode for the NodeComponent
        PNode pNode = new PSwing(nodeComp);
        // Add nodes to the props list
        props.addNode(uuid, pNode);
        props.addComponent(nodeComp);
        // Setup node connectors
        UpdateNodeConnectors(uuid);
        // Add node to the canvas
        // ...
    }

    public void UpdateNodeConnectors(UUID nodeCompUUID) {
        PNode pNode = props.getPNode(nodeCompUUID);
        if(pNode == null) return;
        NodeComponent nodeComponent = props.getNodeComponent(nodeCompUUID);
        if(nodeComponent == null) return;

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
                super.drag(event);
            }
            @Override
            public void setAutopan(boolean autopan) {
                super.setAutopan(false);
            }
        });
    }

    protected void SetupGridLayer(PRoot root, PCamera camera) {
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
        private HashMap<UUID, PNode> pNodesMap;
        private HashMap<UUID, NodeComponent> nodeComponents;
        private HashMap<UUID, NodeConnector> nodeConnectors;

        NodeEditorProperties() {
            pNodesMap = new HashMap<>();
            nodeComponents = new HashMap<>();
            nodeConnectors = new HashMap<>();
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
    }
}