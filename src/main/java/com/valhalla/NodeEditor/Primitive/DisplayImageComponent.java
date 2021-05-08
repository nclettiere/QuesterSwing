package com.valhalla.core.Node;

import com.valhalla.NodeEditor.NodeSocket;

import java.awt.*;
import java.util.UUID;

public class DisplayImageComponent extends NodeComponent {

    public DisplayImageComponent() {
        super();
        this.Node = new DisplayImageNode();
        this.NodeName = "Display Image";
        this.NodeSubtitle = "Displays Custom Image";
        this.NodeColor = new Color(10, 100, 70);

        CreateNodeStructure();
    }

    public DisplayImageComponent(UUID uuid, Iterable<NodeSocket> sockets) {
        super(uuid, sockets);
        this.Node = new DisplayImageNode(uuid, sockets);
        this.NodeName = "Display Image";
        this.NodeSubtitle = "Displays Custom Image";
        this.NodeColor = new Color(10, 100, 70);

        CreateNodeStructure();
    }
}
