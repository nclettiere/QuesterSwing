package com.valhalla.core.Node;

import java.awt.*;
import java.util.EventListener;
import java.util.UUID;

public interface ConnectorEventListener extends EventListener {
    void OnConnectorClick(UUID uuid);
    void OnConnectorDrag(UUID uuid, Component connector);
    void OnConnectorDragStop(UUID uuid);
    void OnConnectionCreated(UUID uuid1, UUID uuid2);
}