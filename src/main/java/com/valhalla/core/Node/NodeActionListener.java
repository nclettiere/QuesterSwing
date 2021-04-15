package com.valhalla.core.Node;

import java.util.EventListener;

public interface NodeActionListener extends EventListener {
    void OnNodeActionChanged(NodeBase.NodeAction nodeAction);
}
