package com.valhalla.NodeEditor;

import com.valhalla.NodeEditor.Sockets.NodeSocket;
import com.valhalla.NodeEditor.Sockets.SocketDirection;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.util.*;

public class PropertyBase implements INodeProperty {
    protected Ref<JComponent>   control;
    protected Stack<NodeSocket>  inputs;
    protected Stack<NodeSocket>  outputs;
    protected EventListenerList listenerList;

    protected Integer propertyIndex;
    protected UUID    nodeUUID;

    protected boolean allowSelfBinding;

    public PropertyBase(Integer propertyIndex, UUID nodeUUID) {
        this.propertyIndex = propertyIndex;
        this.nodeUUID = nodeUUID;
        inputs = new Stack<>();
        outputs = new Stack<>();
        listenerList = new EventListenerList();
    }

    public PropertyBase(Integer propertyIndex, UUID nodeUUID, Iterable<NodeSocket> sockets) {
        this(propertyIndex, nodeUUID);

        for (NodeSocket socket : sockets) {
            if (socket.propertyIndex.equals(propertyIndex)) {
                if (socket.getDirection().equals(SocketDirection.IN))
                    inputs.push(socket);
                else
                    outputs.push(socket);
            }
        }

        System.out.println("UPDATED INPUT STACK: "+ inputs.size());
        System.out.println("UPDATED OUTPUT STACK: "+ outputs.size());
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
    public Stack<NodeSocket> GetInputs() {
        return inputs;
    }

    @Override
    public Stack<NodeSocket> GetOutputs() {
        return outputs;
    }

    @Override
    public Stack<NodeSocket> GetIO() {
        Stack<NodeSocket> IO = new Stack<>();
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
