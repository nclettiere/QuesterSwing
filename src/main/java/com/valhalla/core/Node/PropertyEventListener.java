package com.valhalla.core.Node;

import java.util.EventListener;
import java.util.UUID;

public interface PropertyEventListener extends EventListener {
    void OnControlUpdate();
    void ConnectorAdded(UUID nodeUUID, Integer propIndex, INodeData connectorData);
    void ConnectorRemoved(UUID nodeUUID, Integer propIndex, INodeData connectorData);
    void OnConnect();
    void OnDisconnect();
}
