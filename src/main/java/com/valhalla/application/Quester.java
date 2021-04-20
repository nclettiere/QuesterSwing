package com.valhalla.application;

import javax.swing.*;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.util.SystemInfo;
import com.valhalla.application.gui.*;
import com.valhalla.core.Node.NodeEditor;

import java.awt.*;

public class Quester {
    ProjectSelectorFrame pSelect;
    NodeEditorInspector nodeEditorInspector;

    public Quester() {
        if( SystemInfo.isMacOS && System.getProperty( "apple.laf.useScreenMenuBar" ) == null )
            System.setProperty( "apple.laf.useScreenMenuBar", "true" );

        SwingUtilities.invokeLater( () -> {
            FlatLaf.registerCustomDefaultsSource( "com.valhalla.application" );
        });

        FlatDarculaLaf.install();

        // default frame
        pSelect = new ProjectSelectorFrame(Quester.this);

        // show frame
        pSelect.pack();
        pSelect.setSize(1024,720);
        pSelect.setLocationRelativeTo( null );
        pSelect.setVisible( true );
    }

    public static void main(String[] args) { new Quester(); }

    public void openNodeEditorInspector(NodeEditor nodeEditor) {
        if(nodeEditorInspector == null)
            nodeEditorInspector = new NodeEditorInspector(nodeEditor);

        nodeEditorInspector.pack();
        nodeEditorInspector.setSize(360,512);
        nodeEditorInspector.setLocation(pSelect.getX() + pSelect.getWidth(), pSelect.getY());
        nodeEditorInspector.setVisible( true );
    }

    static void setLocationToTopRight(JFrame frame) {
        GraphicsConfiguration config = frame.getGraphicsConfiguration();
        Rectangle bounds = config.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);

        int x = bounds.x + bounds.width - insets.right - frame.getWidth();
        int y = bounds.y + insets.top;
        frame.setLocation(x, y);
    }
}
