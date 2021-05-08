package com.valhalla.NodeEditor.Primitive;

import com.valhalla.NodeEditor.Sockets.NodeSocket;
import com.valhalla.NodeEditor.NodeBase;

import java.util.UUID;

public class DisplayImageNode extends NodeBase {

    public DisplayImageNode() {
        super();
        groupName = "Images";
        SetName("Display Image");
        SetDescription("Displays an InputStream as Image");
        AddProperty(new DisplayImageProperty(0, uuid));
    }

    public DisplayImageNode(UUID uuid, Iterable<NodeSocket> sockets) {
        super(uuid, sockets);
        groupName = "Images";
        SetName("Display Image");
        SetDescription("Displays an InputStream as Image");
        AddProperty(new DisplayImageProperty(0, uuid, sockets));
    }
}
