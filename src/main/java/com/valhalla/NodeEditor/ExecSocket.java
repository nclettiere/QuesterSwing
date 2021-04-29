package com.valhalla.NodeEditor;

import java.awt.*;

public class ExecSocket extends NodeSocket {

    public ExecSocket(SocketDirection direction) {
        super(direction, Exec.class);
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
        setData(new Exec(direction));
    }

    @Override
    public boolean isDataBindAvailable() {
        return !(socketEventListeners.size() > 0);
    }
}