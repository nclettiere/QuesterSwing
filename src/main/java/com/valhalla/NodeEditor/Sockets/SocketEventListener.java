package com.valhalla.NodeEditor.Sockets;

import java.util.EventListener;

public interface SocketEventListener extends EventListener {
    void onBindingDataChanged(Object data);
    void onBindingBreak();
    void onDataEvaluationChanged(NodeSocket socket, SocketState socketState);
}