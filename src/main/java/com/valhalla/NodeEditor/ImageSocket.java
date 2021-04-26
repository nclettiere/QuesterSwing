package com.valhalla.NodeEditor;

import java.awt.*;

public class ImageSocket extends NodeSocket {
    public ImageSocket(SocketDirection direction) {
        super(direction, String.class);
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
        return !(socketEventListeners.size() > 0);
    }
}
