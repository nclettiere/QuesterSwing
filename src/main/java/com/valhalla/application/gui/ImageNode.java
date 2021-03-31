package com.valhalla.application.gui;

import java.awt.*;

public class ImageNode extends Node {
    public ImageNode(NodeProperty[] properties) {
        super(properties);

    }

    public ImageNode() {
        super();
        this.NodeName = "Image Node";
        this.NodeSubtitle = "Creates Custom Image";
        this.properties = new NodeProperty[]{new ImageProperty(), new ButtonProperty()};
        this.NodeColor = new Color(10, 100, 70);
        CreateNodeStructure();
    }
}
