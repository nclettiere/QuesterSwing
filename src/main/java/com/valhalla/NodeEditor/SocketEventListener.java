package com.valhalla.NodeEditor;

import com.valhalla.core.Node.INodeData;

import java.util.EventListener;
import java.util.Map;

public interface SocketEventListener extends EventListener {
    void onBindingDataChanged(Object data);
    void onBindingBreak();
    void onDataEvaluationChanged(NodeSocket socket, NodeSocket.SocketState socketState);
}