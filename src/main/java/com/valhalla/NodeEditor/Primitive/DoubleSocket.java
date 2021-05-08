package com.valhalla.NodeEditor;

import com.valhalla.NodeEditor.Sockets.NodeSocket;
import com.valhalla.NodeEditor.Sockets.SocketDirection;

import java.awt.*;

public class DoubleSocket extends NodeSocket {
    public DoubleSocket(SocketDirection direction, Integer propertyIndex) {
        super(direction, propertyIndex, Double.class);
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
