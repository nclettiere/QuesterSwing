package com.valhalla.application;

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.util.SystemInfo;
import com.valhalla.application.gui.*;

public class Quester {
    public static void main(String[] args) {
        if( SystemInfo.isMacOS && System.getProperty( "apple.laf.useScreenMenuBar" ) == null )
            System.setProperty( "apple.laf.useScreenMenuBar", "true" );

        SwingUtilities.invokeLater( () -> {
            FlatLaf.registerCustomDefaultsSource( "com.valhalla.application" );
        });

        FlatDarculaLaf.install();

        // default frame
        ProjectSelectorFrame pSelect = new ProjectSelectorFrame();

        // show frame
        pSelect.pack();
        pSelect.setSize(580,460);
        pSelect.setLocationRelativeTo( null );
        pSelect.setVisible( true );
    }
}
