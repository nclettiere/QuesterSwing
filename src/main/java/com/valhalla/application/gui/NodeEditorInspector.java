package com.valhalla.application.gui;

import com.valhalla.core.Node.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class NodeEditorInspector
    extends JFrame {

    protected NodeEditor nodeEditor;
    protected EventListenerList listenerList = new EventListenerList();

    protected JTable editorTable;
    protected JTable nodesTable;

    public NodeEditorInspector(NodeEditor nodeEditor) {
        this.nodeEditor = nodeEditor;
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    protected void initComponents() {
        JPanel content = new JPanel(
                new MigLayout("", "0[fill]0", "0[fill]0"));
        content.setBorder(new EmptyBorder(0,0,0,0));
        JTabbedPane navigationTab = new JTabbedPane();

        // Initializing the JTable
        editorTable = new JTable();
        editorTable.setModel(new EditorTableModel(nodeEditor));
        editorTable.setShowGrid(true);
        editorTable.setDragEnabled(false);
        JScrollPane sp1 = new JScrollPane(editorTable);
        sp1.getViewport().setOpaque(false);
        sp1.setBorder(new EmptyBorder(0,0,0,0));
        sp1.setBorder(BorderFactory.createEmptyBorder());
        sp1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JTable nodesTable = new JTable();
        nodesTable.setModel(new NodeTableModel(nodeEditor));
        nodesTable.setShowGrid(true);
        nodesTable.setDragEnabled(false);
        JScrollPane sp2 = new JScrollPane(nodesTable);
        sp2.getViewport().setOpaque(false);
        sp2.setBorder(new EmptyBorder(0,0,0,0));
        sp2.setBorder(BorderFactory.createEmptyBorder());
        sp2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        navigationTab.addTab("Editor", sp1);
        navigationTab.add("Nodes", sp2);

        //======== this ========
        setTitle("Node Editor Inspector");
        setName("this");
        setLayout(new MigLayout(
                "",
                "0[grow]0",
                "0[grow]0"));
        content.add(navigationTab, "grow");
        add(content, "grow");
    }
}
