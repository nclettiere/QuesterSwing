package com.valhalla.NodeEditor;

import java.awt.*;

public class DoubleSocket extends NodeSocket {
    public DoubleSocket(SocketDirection direction) {
        super(direction, Double.class);
    }

    @Override
    public boolean evaluate() {
        return super.evaluate();
    }

    @Override
    public Color getSocketColor() {
        return new Color(177, 177, 0);
    }

    @Override
    public void resetDataDefaults() {
        setData(0.0d);
    }

}