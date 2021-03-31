package com.valhalla.core.Node;

import com.valhalla.core.Ref;

import javax.swing.*;
import java.util.ArrayList;

public class DisplayImageProperty implements INodeProperty {
    @Override
    public Ref<JComponent> Control() {
        return null;
    }

    @Override
    public ArrayList<INodeData> GetInputs() {
        return null;
    }

    @Override
    public ArrayList<INodeData> GetOutputs() {
        return null;
    }

    @Override
    public void AddInput(INodeData INodeData) {

    }

    @Override
    public void RemoveInput(int index) {

    }

    @Override
    public void AddOutput(INodeData INodeData) {

    }

    @Override
    public void RemoveOutput(int index) {

    }

    @Override
    public int GetInputCount() {
        return 0;
    }

    @Override
    public int GetOutputCount() {
        return 0;
    }

    @Override
    public void AddOnControlUpdateListener(NodeEventListener listener) {

    }

    @Override
    public void RemoveOnControlUpdateListener(NodeEventListener listener) {

    }
}
