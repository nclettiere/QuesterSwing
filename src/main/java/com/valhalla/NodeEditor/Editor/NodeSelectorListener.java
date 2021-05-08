package com.valhalla.NodeEditor.Editor;

import com.valhalla.NodeEditor.Editor.NodeComponent;

import java.util.EventListener;

public interface NodeSelectorListener extends EventListener {
    void OnNodeSelected(Class<? extends NodeComponent> nodeClass);
}
