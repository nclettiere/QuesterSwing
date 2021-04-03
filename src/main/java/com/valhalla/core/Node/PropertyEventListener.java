package com.valhalla.core.Node;

import java.util.EventListener;

public interface PropertyEventListener extends EventListener {
    void OnControlUpdate();
    void OnConnect();
    void OnDisconnect();
}
