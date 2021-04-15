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
    public List<INodeData> GetInputs() {
        return inputs;
    }

    @Override
    public List<INodeData> GetOutputs() {
        return outputs;
    }

    @Override
    public List<INodeData> GetIO() {
        List<INodeData> IO = new ArrayList<>();
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
