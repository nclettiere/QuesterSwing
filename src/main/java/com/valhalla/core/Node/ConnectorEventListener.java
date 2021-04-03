package com.valhalla.core.Node;

import java.util.EventListener;
import java.util.UUID;

public interface ConnectorEventListener extends EventListener {
    void OnConnectorClick(UUID uuid);
    void OnConnectorDrag(UUID uuid);
}
