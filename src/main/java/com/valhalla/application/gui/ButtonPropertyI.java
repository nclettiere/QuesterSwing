package com.valhalla.application.gui;

import com.valhalla.core.Node.INodeData;
import com.valhalla.core.Node.NodeEventListener;
import com.valhalla.core.Node.INodeProperty;
import com.valhalla.core.Ref;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.util.ArrayList;

public class ButtonPropertyI implements INodeProperty {

    private ArrayList<INodeData> inputs;
    private ArrayList<INodeData> outputs;

    private Ref<JComponent> component;

    protected EventListenerList listenerList = new EventListenerList();

    void FireControlUpdateEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == NodeEventListener.class) {
                ((NodeEventListener) listeners[i+1]).OnControlUpdate();
            }
        }
    }

    ButtonPropertyI() {
        component = new Ref<>(new JButton("Button Property"));
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();

        AddInput(new ImageDataI());
    }
    @Override
    public Ref<JComponent> GetControl() {
        return component;
    }

    @Override
    public void SetControl(Ref<JComponent> control) {
        this.component = control;
    }

    @Override
    public ArrayList<INodeData> GetInputs() {
        return inputs;
    }

    @Override
    public ArrayList<INodeData> GetOutputs() {
        return outputs;
    }

    @Override
    public void AddInput(INodeData INodeData) {
        inputs.add(INodeData);
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
    public int GetInputCount() { return inputs.size(); }

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
