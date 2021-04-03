package com.valhalla.core.Node;

import java.util.EventListener;

public interface NodeEventListener extends EventListener {
    void OnNodePanelDrag(NodePanel nodePanel);
    void OnNodePanelDragStop(NodePanel nodePanel);
}
