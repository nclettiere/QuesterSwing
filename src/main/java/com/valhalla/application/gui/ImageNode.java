package com.valhalla.application.gui;

import com.valhalla.core.Node.INodeProperty;

import java.awt.*;

public class ImageNode extends Node {
    public ImageNode(INodeProperty[] properties) {
        super(properties);

    }

    public ImageNode() {
        super();
        this.NodeName = "Image Node";
        this.NodeSubtitle = "Creates Custom Image";
        this.properties = new INodeProperty[]{new ImagePropertyI(), new ButtonPropertyI()};
        this.NodeColor = new Color(10, 100, 70);
        CreateNodeStructure();
    }
}
