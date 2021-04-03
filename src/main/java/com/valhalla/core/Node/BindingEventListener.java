package com.valhalla.core.Node;

import java.util.EventListener;

public interface BindingEventListener extends EventListener {
    void OnBindingDataChanged(Object data);
    void OnBindingReleased();
}
