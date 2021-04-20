package com.valhalla.core.Node;

import java.util.EventListener;

public interface EditorPropertyListener extends EventListener {
    void OnPropertyChanged(String propertyName, Object value);
}
