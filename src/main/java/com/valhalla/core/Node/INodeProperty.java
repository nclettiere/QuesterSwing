package com.valhalla.core.Node;

import com.valhalla.core.Ref;

import javax.swing.*;
import java.util.EventListener;
import java.util.List;
import java.util.Set;

public interface INodeProperty extends EventListener {
    Ref<JComponent> GetControl();
    void SetControl(Ref<JComponent> control);

    List<INodeData> GetInputs();
    List<INodeData> GetOutputs();
    List<INodeData> GetIO();

    void AddInput(INodeData input);
    void RemoveInput(int index);
    void AddOutput(INodeData output);
    void RemoveOutput(int index);

    int GetInputCount();
    int GetOutputCount();

    void AddOnControlUpdateListener(PropertyEventListener listener);
    void RemoveOnControlUpdateListener(PropertyEventListener listener);

    void FireControlUpdateEvent();
    void FireConnectorAddedEvent(INodeData connectorData);
    void FireConnectorRemovedEvent(INodeData connectorData);
}
