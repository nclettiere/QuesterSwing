package com.valhalla.NodeEditor.Sockets;

import com.valhalla.NodeEditor.Editor.NodeConnector;

import java.util.EventListener;
import java.util.UUID;

public interface ConnectorEventListener extends EventListener {
    void OnConnectionCreated(NodeConnector dropped, NodeConnector initialConnector, UUID uuid1, UUID uuid2);
}