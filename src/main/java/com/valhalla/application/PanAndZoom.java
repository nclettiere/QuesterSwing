package com.valhalla.application;

import com.valhalla.application.gui.NodeConnector;
import com.valhalla.application.gui.ProjectSelectorFrame;
import com.valhalla.application.gui.PropertyPanel;
import com.valhalla.core.Node.*;
import net.miginfocom.swing.MigLayout;
import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.PRoot;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PMouseWheelZoomEventHandler;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;
import org.piccolo2d.util.PPaintContext;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class PanAndZoom extends JFrame {
    public static void main(String[] args) {
        new PanAndZoom();
    }

    public PanAndZoom() {
        ZoomerPanel canvas = new ZoomerPanel();
        add(canvas);

        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500,500);
        setVisible(true);
    }
}

class ZoomerPanel extends PSwingCanvas {
    boolean dragging = false;
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

    public ZoomerPanel() {
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

        camera.addInputEventListener(new PBasicInputEventHandler() {
            public void mouseMoved(final PInputEvent event) {
                updateToolTip(event);
            }

            public void mouseDragged(final PInputEvent event) {
                updateToolTip(event);
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

        //JLabel lbl = new JLabel("asdasd");
        //lbl.setLayout(new MigLayout("debug"));
        SelectImageComponent sic = new SelectImageComponent();
        PNode pSwing = new PSwing(dic);
        PNode asd = new PSwing(sic);

        PNode ps1 = null;
        NodeConnector connector = null;
        for (PropertyPanel prop :
                propertyPanels) {
            for (NodeConnector conn :
                    prop.getConnectors()) {
                connector = conn;
                ps1 = conn.GetPNode();
            }
        }

        ps1.addAttribute("tooltip", "node 1");

        PNode finalPs = ps1;
        NodeConnector finalConnector = connector;
        ps1.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(final PInputEvent aEvent) {
                System.out.println("IIIIIIIIII");
                draggingConnector = finalConnector;
                draggingConnectorOrigin = aEvent.getPosition();
                draggingNodeConnector = finalPs;
                aEvent.setHandled(true);
            }

            public void mouseDragged(final PInputEvent aEvent) {
                System.out.println("IIIIIIIIII");
                draggingConnectorFinalPoint = aEvent.getPosition();
                gridLayer.repaint();
                aEvent.setHandled(true);
            }

            public void mouseReleased(final PInputEvent aEvent) {
                System.out.println("IIIIIIIIII");
                draggingNodeConnector = null;
                draggingConnector = null;
                gridLayer.repaint();
                aEvent.setHandled(true);
            }
        });

        pSwing.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(final PInputEvent aEvent) {
                System.out.println("YOOOO1");
                aEvent.setHandled(true);
            }

            public void mouseDragged(final PInputEvent aEvent) {
                System.out.println("YOOOO2");
                final Dimension2D delta = aEvent.getDeltaRelativeTo(pSwing);
                pSwing.translate(delta.getWidth(), delta.getHeight());
                aEvent.setHandled(true);
            }

            public void mouseReleased(final PInputEvent aEvent) {
                System.out.println("YOOOO3");
                aEvent.setHandled(true);
            }
        });


        asd.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(final PInputEvent aEvent) {
                System.out.println("YOOOO1");
                aEvent.setHandled(true);
            }

            public void mouseDragged(final PInputEvent aEvent) {
                System.out.println("YOOOO2");
                final Dimension2D delta = aEvent.getDeltaRelativeTo(asd);
                asd.translate(delta.getWidth(), delta.getHeight());
                aEvent.setHandled(true);
            }

            public void mouseReleased(final PInputEvent aEvent) {
                System.out.println("YOOOO3");
                aEvent.setHandled(true);
            }
        });

        getLayer().addChild(0, pSwing);
        getLayer().addChild(0, asd);
        ps1.translate(19, 140);
        pSwing.addChild(ps1);
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

        //if(GetDebugPaint()) {
        //    graphics.setColor(Color.RED);
        //    //ctrlOrigin
        //    graphics.drawOval(curveOriginCtrl.x, curveOriginCtrl.y, 10, 10);
        //    //ctrlEND
        //    graphics.drawOval(curveEndCtrl.x, curveEndCtrl.y, 10, 10);
        //}
    }
}