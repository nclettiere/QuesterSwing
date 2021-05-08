package com.valhalla.NodeEditor.Primitive;

import com.valhalla.NodeEditor.Sockets.NodeSocket;
import com.valhalla.NodeEditor.Sockets.SocketDirection;

import java.awt.*;
import java.util.HashMap;

public class ImageSocket extends NodeSocket {

    public ImageSocket(SocketDirection direction, Integer propertyIndex) {
        super(direction, propertyIndex, String.class);
    }

    @Override
    public boolean evaluate() {
        return super.evaluate();
    }

    @Override
    public Color getSocketColor() {
        return new Color(177, 24, 24);
    }

    @Override
    public void resetDataDefaults() {
        setData("");
    }

    @Override
    public boolean isDataBindAvailable() {
        if (socketEventListeners == null)
            socketEventListeners = new HashMap<>();
        return !(socketEventListeners.size() > 0);
    }
}
