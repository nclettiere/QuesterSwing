package com.valhalla.application.gui;

import com.valhalla.core.Ref;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class ImageProperty implements NodeProperty {

    private ArrayList<NodeData> inputs;
    private ArrayList<NodeData> outputs;

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

    ImageProperty() {
        component = new Ref<>(new ImagePanel());
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();

        AddInput(new ImageData());
        AddInput(new ImageData());
        AddOutput(new ImageData());

        component.get().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                FireControlUpdateEvent();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }
    @Override
    public Ref<JComponent> Control() {
        return component;
    }

    @Override
    public ArrayList<NodeData> GetInputs() {
        return inputs;
    }

    @Override
    public ArrayList<NodeData> GetOutputs() {
        return outputs;
    }

    @Override
    public void AddInput(NodeData nodeData) {
        inputs.add(nodeData);
    }

    @Override
    public void RemoveInput(int index) {
        inputs.remove(index);
    }

    @Override
    public void AddOutput(NodeData nodeData) {
        outputs.add(nodeData);
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
