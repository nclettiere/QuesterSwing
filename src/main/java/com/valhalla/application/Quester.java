package com.valhalla.application;

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.util.SystemInfo;
import com.valhalla.application.gui.*;
import com.valhalla.core.Node.NodeEditor;

public class Quester {
    NodeEditorInspector nodeEditorInspector;

    public Quester() {
        if( SystemInfo.isMacOS && System.getProperty( "apple.laf.useScreenMenuBar" ) == null )
            System.setProperty( "apple.laf.useScreenMenuBar", "true" );

        SwingUtilities.invokeLater( () -> {
            FlatLaf.registerCustomDefaultsSource( "com.valhalla.application" );
        });

        FlatDarculaLaf.install();

        // default frame
        ProjectSelectorFrame pSelect = new ProjectSelectorFrame(Quester.this);

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
        nodeEditorInspector.setSize(256,512);
        nodeEditorInspector.setLocationRelativeTo( null );
        nodeEditorInspector.setVisible( true );
    }
}
