package com.valhalla.core.Node;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class NodeMessage extends JPanel {

    public boolean isError;
    public String message;

    public NodeMessage(Map.Entry<Boolean, String> evaluationState) {
        this.isError = evaluationState.getKey();
        this.message = evaluationState.getValue();
        this.setLayout(new MigLayout("", "0[grow]0", "0[grow]0"));
        this.setBorder(new EmptyBorder(10,10,10,10));
        this.setOpaque(false);

        String lblText = "<html><i style='color:#d4a26e'>Warning</i><p style='word-wrap: break-word;'>"+ message +" <i style='color:#d4a26e'><a href='#'> (output 2)</a></i></p></html>";
        if(isError)
            lblText = "<html><i style='color:#A25C5C'>Error</i><p style='word-wrap: break-word;'>"+ message +" <i style='color:#A25C5C'><a href='#'> (output 2)</a></i></p></html>";

        JLabel lbl = new JLabel(lblText);
        this.add(lbl, "wmax 200, grow");
    }

    public boolean isError() {
        return isError;
    }
    public String getMessage() { return message; }
}
