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
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;
import org.w3c.dom.Node;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static java.awt.event.MouseEvent.BUTTON3;

public class NodeEditorEx
    extends PSwingCanvas {

    boolean debugPaint = false;
    protected Line2D gridLine = new Line2D.Double();
    protected Stroke gridStroke = new BasicStroke(1);
    protected Stroke gridStrokeThick = new BasicStroke(2);
    protected Color gridPaint = Color.BLACK;
    protected double gridSpacing = 20;
    protected double gridSpacingThick = 100;

    protected NodeConnector draggingConnector;
    protected PNode draggingNodeConnector;
    protected Point2D draggingConnectorOrigin;
    protected Point2D draggingConnectorFinalPoint;


    protected Point2D selectorPoint;
    boolean isNodeSelectorOpened = false;
    boolean isMouseOnNodeSelector = false;

    public NodeEditorEx() {
        final PRoot root = getRoot();
        final PCamera camera = getCamera();

        setBackground(new Color(50,50,50));

        // uninstall default zoom event handler
        removeInputEventListener(getZoomEventHandler());

        // install mouse wheel zoom event handler
        final PMouseWheelZoomEventHandler mouseWheelZoomEventHandler = new PMouseWheelZoomEventHandler();
        addInputEventListener(mouseWheelZoomEventHandler);

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

                g2.setColor(new Color(100,100,100));
                g2.setStroke(gridStrokeThick);

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
            PNode pSwing = new PSwing(nodeComp);
            pSwing.addInputEventListener(new PBasicInputEventHandler() {
                public void mouseDragged(final PInputEvent aEvent) {
                    System.out.println("YOOOO2");
                    pSwing.raiseToTop();
                    final Dimension2D delta = aEvent.getDeltaRelativeTo(pSwing);
                    pSwing.translate(delta.getWidth(), delta.getHeight());
                    aEvent.setHandled(true);
                }
            });


            getLayer().addChild(0, pSwing);
            pSwing.setOffset(offset);
            double connYOffset = 75;
            for (PropertyPanel prop : nodeComp.GetPropertiesPanel()) {
                for (NodeConnector conn : prop.getConnectors()) {
                    PNode connNode = new PSwing(conn);
                    if (conn.GetNodeData().GetMode() == ConnectorMode.INPUT) {
                        connNode.setOffset(18, connYOffset);
                    } else {
                        connNode.setOffset(165, connYOffset);
                    }

                    connNode.addInputEventListener(new PBasicInputEventHandler() {
                        public void mousePressed(final PInputEvent aEvent) {
                            draggingConnector = conn;
                            draggingConnectorOrigin = aEvent.getPosition();
                            draggingNodeConnector = pSwing;
                            aEvent.setHandled(true);
                        }

                        public void mouseDragged(final PInputEvent aEvent) {
                            draggingConnectorFinalPoint = aEvent.getPosition();
                            getLayer().repaint();
                            aEvent.setHandled(true);
                        }

                        public void mouseReleased(final PInputEvent aEvent) {
                            draggingNodeConnector = null;
                            draggingConnector = null;
                            getLayer().repaint();
                            aEvent.setHandled(true);
                        }
                    });

                    connNode.addAttribute("tooltip", conn.GetNodeData().GetDisplayName());
                    pSwing.addChild(connNode);
                    connYOffset += 18;
                }
            }
            pSwing.raiseToTop();
        }
    }

    // Dragging
    private void DrawConnection(Graphics2D graphics) {
        graphics.setColor(new Color(255,255,255, 180));
        graphics.setStroke(new BasicStroke(1.3f));
        //graphics.drawLine(dragOrigin.x, dragOrigin.y, getMousePosition().x, getMousePosition().y);

        CubicCurve2D c = new CubicCurve2D.Double();

        Point2D curveOrigin = draggingConnectorOrigin;
        Point2D curveEnd = draggingConnectorFinalPoint;

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
                } else {
                    curveOriginCtrl.x = (int) curveOrigin.getX() + 400;
                    curveOriginCtrl.y = (int) curveOrigin.getY() + 200;
                    curveEndCtrl.x = (int) curveEnd.getX() - 300;
                    curveEndCtrl.y = (int) curveEnd.getY() + 300;
                }
            } else {
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
            graphics.setColor(Color.RED);
            //ctrlOrigin
            graphics.drawOval(curveOriginCtrl.x, curveOriginCtrl.y, 10, 10);
            //ctrlEND
            graphics.drawOval(curveEndCtrl.x, curveEndCtrl.y, 10, 10);
        }
    }
}
