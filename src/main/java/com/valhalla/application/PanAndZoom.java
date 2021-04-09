package com.valhalla.application;

import com.valhalla.application.gui.NodeConnector;
import com.valhalla.application.gui.ProjectSelectorFrame;
import com.valhalla.application.gui.PropertyPanel;
import com.valhalla.core.Node.ConnectorEventListener;
import com.valhalla.core.Node.DisplayImageComponent;
import com.valhalla.core.Node.NodeComponent;
import com.valhalla.core.Node.NodeEventListener;
import net.miginfocom.swing.MigLayout;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PMouseWheelZoomEventHandler;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.nodes.PPath;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
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
    public ZoomerPanel() {
        // uninstall default zoom event handler
        removeInputEventListener(getZoomEventHandler());

        // install mouse wheel zoom event handler
        final PMouseWheelZoomEventHandler mouseWheelZoomEventHandler = new PMouseWheelZoomEventHandler();
        addInputEventListener(mouseWheelZoomEventHandler);

        //PSwing ps1 = new PSwing(new DisplayImageComponent());
        DisplayImageComponent dic = new DisplayImageComponent();
        ArrayList<PropertyPanel> propertyPanels = dic.GetPropertiesPanel();


        //JLabel lbl = new JLabel("asdasd");
        //lbl.setLayout(new MigLayout("debug"));
        PNode pSwing = new PSwing(dic);

        PNode ps1 = null;

        for (PropertyPanel prop :
                propertyPanels) {
            for (NodeConnector conn :
                    prop.getConnectors()) {
                ps1 = conn.GetPSwing();
            }
        }

        ps1.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(final PInputEvent aEvent) {
                System.out.println("IIIIIIIIII");
                aEvent.setHandled(true);
            }

            public void mouseDragged(final PInputEvent aEvent) {
                System.out.println("IIIIIIIIII");
                aEvent.setHandled(true);
            }

            public void mouseReleased(final PInputEvent aEvent) {
                System.out.println("IIIIIIIIII");
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

        getLayer().addChild(0, pSwing);
        ps1.translate(19, 140);
        pSwing.addChild(ps1);
    }
}