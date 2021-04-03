package com.valhalla.core.Node;

import java.awt.*;

public class DisplayImageNodePanel extends NodePanel {

    public DisplayImageNodePanel() {
        super();
        this.Node = new DisplayImageNode();
        this.NodeName = "Display Image";
        this.NodeSubtitle = "Displays Custom Image";
        this.NodeColor = new Color(10, 100, 70);

        CreateNodeStructure();
    }
}
