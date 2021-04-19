package com.valhalla.core.Node;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NodeMessage extends JPanel {

    protected boolean isError;

    public NodeMessage(boolean isError) {
        this.isError = isError;
        this.setLayout(new MigLayout("", "0[grow]0", "0[grow]0"));
        this.setBorder(new EmptyBorder(10,10,10,10));
        this.setOpaque(false);

        String lblText = "<html><i style='color:#d4a26e'>Warning</i><p style='word-wrap: break-word;'>The file does not exist or cannot be read. <i style='color:#d4a26e'><a href='#'>(output 2)</a></i></p></html>";
        if(isError)
            lblText = "<html><i style='color:#A25C5C'>Error</i><p style='word-wrap: break-word;'>The file does not exist or cannot be read. <i style='color:#A25C5C'><a href='#'>(output 2)</a></i></p></html>";

        JLabel lbl = new JLabel(lblText);
        this.add(lbl, "wmax 200, grow");
    }

    public boolean isError() {
        return isError;
    }
}
