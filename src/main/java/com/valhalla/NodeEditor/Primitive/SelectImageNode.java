package com.valhalla.NodeEditor.Primitive;

import com.valhalla.NodeEditor.Sockets.NodeSocket;
import com.valhalla.NodeEditor.NodeBase;

import java.util.UUID;

public class SelectImageNode extends NodeBase {
    public SelectImageNode() {
        super();
        groupName = "Images";
        SetName("Select Image");
        SetDescription("Select an image file as InputStream");
        AddProperty(new SelectImageProperty(0, uuid));
    }

    public SelectImageNode(UUID uuid, Iterable<NodeSocket> sockets) {
        super(uuid, sockets);
        groupName = "Images";
        SetName("Select Image");
        SetDescription("Select an image file as InputStream");

        AddProperty(new SelectImageProperty(0, uuid, sockets));
    }
}
