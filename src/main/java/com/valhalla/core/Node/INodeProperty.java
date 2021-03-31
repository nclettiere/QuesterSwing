package com.valhalla.core.Node;

import com.valhalla.core.Ref;

import javax.swing.*;
import java.util.ArrayList;
import java.util.EventListener;

public interface INodeProperty extends EventListener {
    Ref<JComponent> Control();

    ArrayList<INodeData> GetInputs();
    ArrayList<INodeData> GetOutputs();

    void AddInput(INodeData INodeData);
    void RemoveInput(int index);
    void AddOutput(INodeData INodeData);
    void RemoveOutput(int index);

    int GetInputCount();
    int GetOutputCount();

    void AddOnControlUpdateListener(NodeEventListener listener);
    void RemoveOnControlUpdateListener(NodeEventListener listener);
}
