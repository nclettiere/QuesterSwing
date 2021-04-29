package com.valhalla.core.Node;

import com.valhalla.NodeEditor.NodeSocket;
import com.valhalla.NodeEditor.SocketDirection;
import com.valhalla.NodeEditor.SocketState;
import com.valhalla.NodeEditor.StateErrorLevel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class NodeMessage extends JPanel {

    public boolean isError;
    public String message;

    public NodeMessage(SocketState socketState) {
        this.isError = socketState.errorLevel == StateErrorLevel.ERROR;
        this.message = socketState.stateMessage;

        this.setLayout(new MigLayout("", "0[grow]0", "0[grow]0"));
        this.setBorder(new EmptyBorder(10,10,10,10));
        this.setOpaque(false);

        int dataIndex = 0;//data.getDataPropertyIndex() + 1;
        String mode = (socketState.direction == SocketDirection.IN) ? "input" : "output";
        String connectorRef = mode + " " + dataIndex;

        String lblText = "<html><i style='color:#d4a26e'>Warning</i><p style='word-wrap: break-word;'>"+ message +" <i style='color:#d4a26e'><a href='#'> ("+connectorRef+")</a></i></p></html>";
        if(isError)
            lblText = "<html><i style='color:#dbb3b3'>Error</i><p style='word-wrap: break-word;'>"+ message +" <i style='color:#dbb3b3'><a href='#'> ("+connectorRef+")</a></i></p></html>";

        JLabel lbl = new JLabel(lblText);
        this.add(lbl, "wmax 200, grow");
    }

    public boolean isError() {
        return isError;
    }
    public String getMessage() { return message; }
}
