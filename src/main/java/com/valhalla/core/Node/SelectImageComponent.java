package com.valhalla.core.Node;

import java.awt.*;

public class SelectImageComponent extends NodeComponent {

    public SelectImageComponent() {
        super();
        this.Node = new SelectImageNode();
        this.NodeName = "Select Image";
        this.NodeSubtitle = "Selects an image file";
        this.NodeColor = new Color(0, 80, 50);

        CreateNodeStructure();
    }
}
