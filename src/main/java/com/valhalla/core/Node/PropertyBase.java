package com.valhalla.core.Node;

import com.valhalla.core.Ref;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;

public class PropertyBase implements INodeProperty {

    protected Ref<JComponent>   control;
    protected List<INodeData>   inputs;
    protected List<INodeData>   outputs;
    protected EventListenerList listenerList;

    PropertyBase() {
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        listenerList = new EventListenerList();
    }

    void FireControlUpdateEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == NodeEventListener.class) {
                ((NodeEventListener) listeners[i+1]).OnControlUpdate();
            }
        }
    }

    @Override
    public Ref<JComponent> Control() {
        return control;
    }

    @Override
    public List<INodeData> GetInputs() {
        return inputs;
    }

    @Override
    public List<INodeData> GetOutputs() {
        return outputs;
    }

    @Override
    public void AddInput(INodeData input) {
        inputs.add(input);
    }

    @Override
    public void RemoveInput(int index) {
        inputs.remove(index);
    }

    @Override
    public void AddOutput(INodeData output) {
        outputs.add(output);
    }

    @Override
    public void RemoveOutput(int index) {
        outputs.remove(index);
    }

    @Override
    public int GetInputCount() {
        return inputs.size();
    }

    @Override
    public int GetOutputCount() {
        return outputs.size();
    }

    @Override
    public void AddOnControlUpdateListener(NodeEventListener listener) {
        listenerList.add(NodeEventListener.class, listener);
    }

    @Override
    public void RemoveOnControlUpdateListener(NodeEventListener listener) {
        listenerList.remove(NodeEventListener.class, listener);
    }
}
