package com.valhalla.NodeEditor;

import com.valhalla.NodeEditor.Sockets.NodeSocket;

import java.util.EventListener;
import java.util.UUID;

public interface PropertyEventListener extends EventListener {
    void OnControlUpdate();
    void ConnectorAdded(UUID nodeUUID, Integer propIndex, NodeSocket connectorData);
    void ConnectorRemoved(UUID nodeUUID, Integer propIndex, NodeSocket connectorData);
    void OnConnect();
    void OnDisconnect();
}
