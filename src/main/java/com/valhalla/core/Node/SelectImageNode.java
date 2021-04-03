package com.valhalla.core.Node;

public class SelectImageNode extends NodeBase {
    public SelectImageNode() {
        super();
        SetName("Select Image");
        SetDescription("Select an image file as InputStream");
        AddProperty(new SelectImageProperty());
    }
}
