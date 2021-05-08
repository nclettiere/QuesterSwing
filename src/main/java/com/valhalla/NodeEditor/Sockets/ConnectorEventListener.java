package com.valhalla.core.Node;

import com.valhalla.application.gui.NodeConnector;

import java.util.EventListener;
import java.util.UUID;

public interface ConnectorEventListener extends EventListener {
    void OnConnectionCreated(NodeConnector dropped, NodeConnector initialConnector, UUID uuid1, UUID uuid2);
}