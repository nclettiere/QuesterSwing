package com.valhalla.NodeEditor.Sockets;

import com.valhalla.NodeEditor.PropertyBase;

import java.awt.*;

public class IntegerData extends SocketData {

    public IntegerData(PropertyBase parentProperty) {
        super(parentProperty);
        SetName("Integer");
        SetDisplayName("Integer");
        SetData(0);
        SetDataColor(new Color(90, 155, 30));
    }

    @Override
    public boolean evaluate() {
        if(data == null) return false;
        return data instanceof Integer;
    }
}
