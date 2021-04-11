package com.valhalla.core.Node;

public class DisplayImageNode extends NodeBase {

    public DisplayImageNode() {
        super();
        groupName = "Images";
        SetName("Display Image");
        SetDescription("Displays an InputStream as Image");
        AddProperty(new DisplayImageProperty());
    }
}
