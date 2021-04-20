package com.valhalla.core.Node;

import javax.swing.table.AbstractTableModel;

public class EditorTableModel extends AbstractTableModel {
    protected NodeEditor nodeEditor;
    protected Object[][] dataList;
    protected String[] columnNames = new String[] {
            "Property", "Value"
    };
    protected Class[] columnClasses = new Class[] {
            String.class, Object.class
    };

    public EditorTableModel(NodeEditor nodeEditor) {
        this.nodeEditor = nodeEditor;
        setupData();
    }

    public int getColumnCount() { return columnNames.length; }  // A constant for this model
    public int getRowCount() { return dataList.length; }  // # of files in dir

    // Information about each column
    public String getColumnName(int col) { return columnNames[col]; }
    public Class getColumnClass(int col) { return columnClasses[col]; }

    // The method that must actually return the value of each cell
    public Object getValueAt(int row, int col) {
        return dataList[row][col];
    }

    protected void setupData() {
        dataList = new Object[][] {
            {"ID", nodeEditor.getEditorUUID()},
            {"Name", nodeEditor.getName()},
            {"NodeCount", nodeEditor.getNodeCount()},
            {"Debugging", nodeEditor.isDebugging()},
            {"CurrentAction", nodeEditor.getCurrentAction()}
        };
        nodeEditor.addEditorPropertyListener(new EditorPropertyListener() {
            @Override
            public void OnPropertyChanged(String propertyName, Object value) {
                setProperty(propertyName, value);
            }

            @Override
            public void OnNodeSelectionChanged(NodeBase nodeBase) {

            }
        });
    }

    public void setProperty(String property, Object value) {
        int row = -1;
        switch (property) {
            case "ID" ->            row = 0;
            case "Name" ->          row = 1;
            case "NodeCount" ->     row = 2;
            case "Debugging" ->     row = 3;
            case "CurrentAction" -> row = 4;
        }
        if (row != -1) {
            dataList[row][1] = value;
            fireTableCellUpdated(row, 1);
        }
    }
}