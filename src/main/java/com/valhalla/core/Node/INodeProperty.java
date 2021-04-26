package com.valhalla.core.Node;

import com.valhalla.NodeEditor.NodeSocket;
import com.valhalla.core.Ref;

import javax.swing.*;
import java.util.EventListener;
import java.util.List;
import java.util.Set;

public interface INodeProperty extends EventListener {
    Ref<JComponent> GetControl();
    void SetControl(Ref<JComponent> control);

    List<NodeSocket<?>> GetInputs();
    List<NodeSocket<?>> GetOutputs();
    List<NodeSocket<?>> GetIO();

    void AddInput(NodeSocket<?> input);
    void RemoveInput(int index);
    void AddOutput(NodeSocket<?> output);
    void RemoveOutput(int index);

    int GetInputCount();
    int GetOutputCount();

    void AddOnControlUpdateListener(PropertyEventListener listener);
    void RemoveOnControlUpdateListener(PropertyEventListener listener);

    void FireControlUpdateEvent();
    void FireConnectorAddedEvent(NodeSocket<?> socket);
    void FireConnectorRemovedEvent(NodeSocket<?> socket);
}
