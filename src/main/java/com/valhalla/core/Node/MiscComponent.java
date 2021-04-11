package com.valhalla.core.Node;

import java.awt.*;

public class MiscComponent extends NodeComponent {

    public MiscComponent() {
        super();
        this.Node = new MiscNode();
        this.NodeName = "Misc Node Comp";
        this.NodeSubtitle = "Does Nothing";
        this.NodeColor = new Color(100, 80, 0);

        CreateNodeStructure();
    }
}
