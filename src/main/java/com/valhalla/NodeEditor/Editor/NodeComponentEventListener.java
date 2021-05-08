package com.valhalla.core.Node;

import com.valhalla.NodeEditor.Editor.NodeComponent;

import java.util.EventListener;

public interface NodeComponentEventListener extends EventListener {
    void OnNodeComponentLayoutChanged(NodeComponent nodeComponent);
}
