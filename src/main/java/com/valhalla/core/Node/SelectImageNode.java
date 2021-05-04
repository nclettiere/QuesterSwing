package com.valhalla.core.Node;

import java.util.UUID;

public class SelectImageNode extends NodeBase {
    public SelectImageNode() {
        super();
        groupName = "Images";
        SetName("Select Image");
        SetDescription("Select an image file as InputStream");
        AddProperty(new SelectImageProperty(0, uuid));
    }

    public SelectImageNode(UUID uuid) {
        super(uuid);
        groupName = "Images";
        SetName("Select Image");
        SetDescription("Select an image file as InputStream");
        AddProperty(new SelectImageProperty(0, uuid));
    }
}
