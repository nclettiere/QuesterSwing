package com.valhalla.core.Node;

import java.util.EventListener;
import java.util.Map;
import java.util.UUID;

public interface BindingEventListener extends EventListener {
    void OnBindingDataChanged(Object data);
    void OnBindingReleased();
    void onDataEvaluationChanged(UUID dataUUID, Map.Entry<Boolean, String> evaluationState);
}
