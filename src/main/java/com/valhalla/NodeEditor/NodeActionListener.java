package com.valhalla.NodeEditor;

import com.valhalla.NodeEditor.NodeBase;

import java.util.EventListener;

public interface NodeActionListener extends EventListener {
    void OnNodeActionChanged(NodeBase.NodeAction nodeAction);
}
