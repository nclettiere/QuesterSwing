package com.valhalla.NodeEditor.Primitive;

import com.valhalla.NodeEditor.Sockets.NodeSocket;
import com.valhalla.NodeEditor.Sockets.SocketDirection;

import java.awt.*;

public class ExecSocket extends NodeSocket {

    public ExecSocket(SocketDirection direction) {
        super(direction, 0, Exec.class);
    }

    @Override
    public boolean evaluate() {
        return super.evaluate();
    }

    @Override
    public Color getSocketColor() {
        return new Color(177, 177, 177);
    }

    @Override
    public void resetDataDefaults() {
        setData(new Exec(uuid, direction));
    }

    @Override
    public boolean isDataBindAvailable() {
        return true;
    }
}