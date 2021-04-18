package com.valhalla.core.Node;

import java.util.EventListener;
import java.util.Map;

public interface BindingEventListener extends EventListener {
    void OnBindingDataChanged(Object data);
    void OnBindingReleased();
    void onDataEvaluationChanged(Map.Entry<Boolean, String> evaluationState);
}
