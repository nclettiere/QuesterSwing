package com.valhalla.core.Node;

import java.util.EventListener;

public interface NodeEventListener extends EventListener {
    void OnNodePanelDrag(NodeComponent nodeComponent);
    void OnNodePanelDragStop(NodeComponent nodeComponent);
}
