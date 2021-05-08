package com.valhalla.NodeEditor;

import com.valhalla.NodeEditor.Sockets.NodeSocket;

import javax.swing.*;
import java.util.EventListener;
import java.util.List;

public interface INodeProperty extends EventListener {
    Ref<JComponent> GetControl();
    void SetControl(Ref<JComponent> control);

    List<NodeSocket> GetInputs();
    List<NodeSocket> GetOutputs();
    List<NodeSocket> GetIO();

    void AddInput(NodeSocket input);
    void RemoveInput(int index);
    void AddOutput(NodeSocket output);
    void RemoveOutput(int index);

    int GetInputCount();
    int GetOutputCount();

    void AddOnControlUpdateListener(PropertyEventListener listener);
    void RemoveOnControlUpdateListener(PropertyEventListener listener);

    void FireControlUpdateEvent();
    void FireConnectorAddedEvent(NodeSocket socket);
    void FireConnectorRemovedEvent(NodeSocket socket);
}
