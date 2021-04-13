package com.valhalla.core.Node;

public class SelectImageNode extends NodeBase {
    public SelectImageNode() {
        super();
        groupName = "Images";
        SetName("Select Image");
        SetDescription("Select an image file as InputStream");
        AddProperty(new SelectImageProperty());
        AddProperty(new DisplayImageProperty());
    }
}
