package com.valhalla.NodeEditor.Editor;

import com.valhalla.NodeEditor.NodeBase;

import java.util.EventListener;

public interface EditorPropertyListener extends EventListener {
    void OnPropertyChanged(String propertyName, Object value);
    void OnNodeSelectionChanged(NodeBase nodeBase);
}
