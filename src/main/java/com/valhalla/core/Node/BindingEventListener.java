package com.valhalla.core.Node;

import java.util.EventListener;
import java.util.Map;
import java.util.UUID;

public interface BindingEventListener<T> extends EventListener {
    void OnBindingDataChanged(T data);
    void onBindingBreak();
    void onDataEvaluationChanged(INodeData data, Map.Entry<Boolean, String> evaluationState);
}
