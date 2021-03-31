package com.valhalla.application.gui;

import com.valhalla.core.Ref;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;

interface NodeEventListener extends EventListener {
    void OnControlUpdate();
    void OnConnect();
    void OnDisconnect();
}

public interface NodeProperty extends EventListener {
    Ref<JComponent> Control();

    ArrayList<NodeData> GetInputs();
    ArrayList<NodeData> GetOutputs();

    void AddInput(NodeData nodeData);
    void RemoveInput(int index);
    void AddOutput(NodeData nodeData);
    void RemoveOutput(int index);

    int GetInputCount();
    int GetOutputCount();

    void AddOnControlUpdateListener(NodeEventListener listener);
    void RemoveOnControlUpdateListener(NodeEventListener listener);
}
