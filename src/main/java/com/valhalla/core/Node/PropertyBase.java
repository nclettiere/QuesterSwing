package com.valhalla.core.Node;

import com.valhalla.NodeEditor.NodeSocket;
import com.valhalla.NodeEditor.SocketDirection;
import com.valhalla.core.Ref;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.util.*;

public class PropertyBase implements INodeProperty {
    protected Ref<JComponent>   control;
    protected List<NodeSocket>  inputs;
    protected List<NodeSocket>  outputs;
    protected EventListenerList listenerList;

    protected Integer propertyIndex;
    protected UUID    nodeUUID;

    protected boolean allowSelfBinding;

    PropertyBase(Integer propertyIndex, UUID nodeUUID) {
        this.propertyIndex = propertyIndex;
        this.nodeUUID = nodeUUID;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        listenerList = new EventListenerList();
    }

    PropertyBase(Integer propertyIndex, UUID nodeUUID, Iterable<NodeSocket> sockets) {
        this(propertyIndex, nodeUUID);
        for (NodeSocket socket : sockets) {
            if (socket.propertyIndex == propertyIndex) {
                if (socket.getDirection() == SocketDirection.IN)
                    inputs.add(socket);
                else
                    outputs.add(socket);
            }
        }
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
    public void FireConnectorAddedEvent(NodeSocket socket) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == PropertyEventListener.class) {
                ((PropertyEventListener) listeners[i+1]).ConnectorAdded(nodeUUID, propertyIndex, socket);
            }
        }
    }

    @Override
    public void FireConnectorRemovedEvent(NodeSocket socket) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == PropertyEventListener.class) {
                ((PropertyEventListener) listeners[i+1]).ConnectorRemoved(nodeUUID, propertyIndex, socket);
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
    public List<NodeSocket> GetInputs() {
        return inputs;
    }

    @Override
    public List<NodeSocket> GetOutputs() {
        return outputs;
    }

    @Override
    public List<NodeSocket> GetIO() {
        List<NodeSocket> IO = new ArrayList<>();
        IO.addAll(inputs);
        IO.addAll(outputs);
        return IO;
    }

    @Override
    public void AddInput(NodeSocket input) {
        input.setDirection(SocketDirection.IN);
        inputs.add(input);
    }

    @Override
    public void RemoveInput(int index) {
        inputs.remove(index);
    }

    @Override
    public void AddOutput(NodeSocket output) {
        output.setDirection(SocketDirection.OUT);
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

    public void setAllowSelfBinding(boolean allowSelfBinding) {
        this.allowSelfBinding = allowSelfBinding;
    }

    public boolean isAllowSelfBinding() {
        return allowSelfBinding;
    }

    public int getIndexOf(NodeSocket data) {
        int i = 0;
        if (data.getDirection() == SocketDirection.IN) {
            for (NodeSocket nData : inputs) {
                if (data == nData)
                    break;
                i++;
            }
        }else {
            for (NodeSocket nData : outputs) {
                if (data == nData)
                    break;
                i++;
            }
        }

        return i;
    }
}
