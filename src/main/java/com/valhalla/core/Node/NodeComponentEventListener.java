package com.valhalla.core.Node;

import java.util.EventListener;

public interface NodeComponentEventListener extends EventListener {
    void OnNodeComponentLayoutChanged(NodeComponent nodeComponent);
}
