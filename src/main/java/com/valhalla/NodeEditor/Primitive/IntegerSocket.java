package com.valhalla.NodeEditor;

import java.awt.*;

public class IntegerSocket extends NodeSocket {
    public IntegerSocket(SocketDirection direction, Integer propertyIndex) {
        super(direction, propertyIndex, Integer.class);
    }

    @Override
    public boolean evaluate() {
        return super.evaluate();
    }

    @Override
    public Color getSocketColor() {
        return new Color(10, 177, 24);
    }

    @Override
    public void resetDataDefaults() {
        setData(0);
    }
}
