package com.valhalla.core.Node;

import java.awt.*;

public class SelectImageNodePanel extends NodePanel {

    public SelectImageNodePanel() {
        super();
        this.Node = new SelectImageNode();
        this.NodeName = "Select Image";
        this.NodeSubtitle = "Selects an image file";
        this.NodeColor = new Color(0, 80, 50);

        CreateNodeStructure();
    }
}
