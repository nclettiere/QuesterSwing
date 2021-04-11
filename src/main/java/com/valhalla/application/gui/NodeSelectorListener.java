package com.valhalla.application.gui;

import com.valhalla.core.Node.NodeComponent;

import java.util.EventListener;

public interface NodeSelectorListener extends EventListener {
    void OnNodeSelected(Class<? extends NodeComponent> nodeClass);
}
