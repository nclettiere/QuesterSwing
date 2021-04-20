package com.valhalla.application.gui;

import com.valhalla.core.Node.NodeEditor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class NodeEditorInspector
    extends JFrame {

    protected NodeEditor nodeEditor;

    public NodeEditorInspector(NodeEditor nodeEditor) {
        this.nodeEditor = nodeEditor;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    protected void initComponents() {
        JPanel content = new JPanel(
                new MigLayout("", "[grow]", "[grow]"));
        JTabbedPane navigationTab = new JTabbedPane();

        //======== this ========
        setName("this");
        setLayout(new MigLayout(
                "insets dialog,hidemode 3",
                "[grow,fill]",
                "[grow,fill]"));

        initTabPlacementTabs(navigationTab);
        content.add(navigationTab);

        add(content);
    }

    protected void initTabPlacementTabs( JTabbedPane tabbedPane ) {
        addTab( tabbedPane, "Tab 1", "tab content 1" );

        JComponent tab2 = createTab( "tab content 2" );
        tab2.setBorder( new LineBorder( Color.magenta ) );
        tabbedPane.addTab( "Second Tab", tab2 );

        addTab( tabbedPane, "Disabled", "tab content 3" );
        tabbedPane.setEnabledAt( 2, false );
    }

    protected void addTab( JTabbedPane tabbedPane, String title, String text ) {
        tabbedPane.addTab( title, createTab( text ) );
    }

    protected JComponent createTab( String text ) {
        JLabel label = new JLabel( text );
        label.setHorizontalAlignment( SwingConstants.CENTER );

        JPanel tab = new JPanel( new BorderLayout() );
        tab.add( label, BorderLayout.CENTER );
        return tab;
    }
}
