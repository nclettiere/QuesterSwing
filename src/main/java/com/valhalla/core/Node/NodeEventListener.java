package com.valhalla.core.Node;

import java.util.EventListener;

public interface NodeEventListener extends EventListener {
    void OnNodeComponentDrag(NodeComponent nodeComponent);
    void OnNodeComponentDragStop(NodeComponent nodeComponent);

    void OnNodeComponentClick(NodeComponent nodeComponent);
}
