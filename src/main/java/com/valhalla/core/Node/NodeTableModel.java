package com.valhalla.core.Node;

import javax.swing.table.AbstractTableModel;

public class NodeTableModel extends AbstractTableModel {
    protected NodeEditor nodeEditor;
    protected NodeBase node;
    protected NodeActionListener nodeActionListener;
    protected Object[][] dataList;
    protected String[] columnNames = new String[] {
            "Property", "Value"
    };
    protected Class[] columnClasses = new Class[] {
            String.class, Object.class
    };

    public NodeTableModel(NodeEditor nodeEditor) {
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
                {"ID", null},
                {"Name", null},
                {"Description", null},
                {"PropertyCount", null},
                {"ConnectorCount", null},
                {"CurrentAction", null}
        };

        nodeEditor.addEditorPropertyListener(new EditorPropertyListener() {
            @Override
            public void OnPropertyChanged(String propertyName, Object value) {
                setProperty(propertyName, value);
            }

            @Override
            public void OnNodeSelectionChanged(NodeBase nodeBase) {
                node = nodeBase;
                node.AddNodeActionListener(new NodeActionListener() {
                    @Override
                    public void OnNodeActionChanged(NodeBase.NodeAction nodeAction) {
                        System.out.println("asdas");
                    }
                });
                dataList = new Object[][] {
                        {"ID", node.GetUUID()},
                        {"Name", node.GetName()},
                        {"Description", node.GetDescription()},
                        {"PropertyCount", node.getPropertyCount()},
                        {"ConnectorCount", node.getIOCount()},
                        {"CurrentAction", node.GetCurrentAction()}
                };
                fireTableCellUpdated(0, 1);
                fireTableRowsUpdated(0, dataList.length - 1);
            }
        });
    }

    public void setProperty(String property, Object value) {
        //int row = -1;
        //switch (property) {
        //    case "ID" ->             row = 0;
        //    case "Name" ->           row = 1;
        //    case "Description" ->    row = 2;
        //    case "PropertyCount" ->  row = 3;
        //    case "ConnectorCount" -> row = 4;
        //    case "CurrentAction" ->  row = 5;
        //}
        //if (row != -1) {
        //    dataList[row][1] = value;
        //    fireTableCellUpdated(row, 1);
        //}
    }
}