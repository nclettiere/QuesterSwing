package com.valhalla.core.Node;

import com.valhalla.application.gui.ButtonPropertyI;
import com.valhalla.application.gui.ImagePropertyI;

import java.awt.*;

public class ImageNodePanel extends NodePanel {

    public ImageNodePanel() {
        super();
        //this.properties = new INodeProperty[]{new DisplayImageProperty()};
        this.Node = new DisplayImageNode();
        this.NodeName = "Display Image";
        this.NodeSubtitle = "Displays Custom Image";
        this.NodeColor = new Color(10, 100, 70);

        CreateNodeStructure();
    }
}
