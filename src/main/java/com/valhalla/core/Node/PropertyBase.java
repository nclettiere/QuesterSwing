package com.valhalla.core.Node;

import com.valhalla.core.Ref;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PropertyBase implements INodeProperty {
    protected Ref<JComponent>   control;
    protected Set<INodeData>   inputs;
    protected Set<INodeData>   outputs;
    protected EventListenerList listenerList;

    PropertyBase() {
        inputs = new HashSet<>();
        outputs = new HashSet<>();
        listenerList = new EventListenerList();
    }

    @Override
    public void FireControlUpdateEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == PropertyEventListener.class) {
                ((PropertyEventListener) listeners[i+1]).OnControlUpdate();
            }
        }
    }

    @Override
    public Ref<JComponent> GetControl() {
        return control;
    }

    @Override
    public void SetControl(Ref<JComponent> control) {
        this.control = control;
    }

    @Override
    public Set<INodeData> GetInputs() {
        return inputs;
    }

    @Override
    public Set<INodeData> GetOutputs() {
        return outputs;
    }

    @Override
    public Set<INodeData> GetIO() {
        Set<INodeData> IO = new HashSet<>();
        IO.addAll(inputs);
        IO.addAll(outputs);
        return IO;
    }

    @Override
    public void AddInput(INodeData input) {
        input.SetMode(ConnectorMode.INPUT);
        inputs.add(input);
    }

    @Override
    public void RemoveInput(int index) {
        inputs.remove(index);
    }

    @Override
    public void AddOutput(INodeData output) {
        output.SetMode(ConnectorMode.OUTPUT);
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
    public void AddOnControlUpdateListener(PropertyEventListener listener) {
        listenerList.add(PropertyEventListener.class, listener);
    }

    @Override
    public void RemoveOnControlUpdateListener(PropertyEventListener listener) {
        listenerList.remove(PropertyEventListener.class, listener);
    }

    public void UpdateBindings() { }
}
