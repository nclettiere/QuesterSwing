package com.valhalla.NodeEditor.Primitive;

import com.valhalla.NodeEditor.Sockets.NodeSocket;
import com.valhalla.NodeEditor.Editor.NodeComponent;

import java.awt.*;
import java.util.UUID;

public class SelectImageComponent extends NodeComponent {

    public SelectImageComponent() {
        super();
        this.Node = new SelectImageNode();
        this.NodeName = "Select Image";
        this.NodeSubtitle = "Selects an image file";
        this.NodeColor = new Color(0, 80, 50);

        CreateNodeStructure();
    }

    public SelectImageComponent(UUID uuid, Iterable<NodeSocket> sockets) {
        super(uuid, sockets);
        this.Node = new SelectImageNode(uuid, sockets);
        this.NodeName = "Select Image";
        this.NodeSubtitle = "Selects an image file";
        this.NodeColor = new Color(0, 80, 50);

        CreateNodeStructure();
    }
}
