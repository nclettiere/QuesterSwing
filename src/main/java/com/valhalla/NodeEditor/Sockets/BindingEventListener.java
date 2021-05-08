package com.valhalla.NodeEditor.Sockets;

import com.valhalla.NodeEditor.Sockets.ISocketData;

import java.util.EventListener;
import java.util.Map;

public interface BindingEventListener<T> extends EventListener {
    void OnBindingDataChanged(T data);
    void onBindingBreak();
    void onDataEvaluationChanged(ISocketData data, Map.Entry<Boolean, String> evaluationState);
}
