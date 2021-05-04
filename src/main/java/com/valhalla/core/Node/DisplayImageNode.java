package com.valhalla.core.Node;

import java.util.UUID;

public class DisplayImageNode extends NodeBase {

    public DisplayImageNode() {
        super();
        groupName = "Images";
        SetName("Display Image");
        SetDescription("Displays an InputStream as Image");
        AddProperty(new DisplayImageProperty(0, uuid));
    }

    public DisplayImageNode(UUID uuid) {
        super(uuid);
        groupName = "Images";
        SetName("Display Image");
        SetDescription("Displays an InputStream as Image");
        AddProperty(new DisplayImageProperty(0, uuid));
    }
}
