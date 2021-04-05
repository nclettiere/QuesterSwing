package com.valhalla.core.Node;

import java.awt.*;

public class DisplayImageComponent extends NodeComponent {

    public DisplayImageComponent() {
        super();
        this.Node = new DisplayImageNode();
        this.NodeName = "Display Image";
        this.NodeSubtitle = "Displays Custom Image";
        this.NodeColor = new Color(10, 100, 70);

        CreateNodeStructure();
    }
}
