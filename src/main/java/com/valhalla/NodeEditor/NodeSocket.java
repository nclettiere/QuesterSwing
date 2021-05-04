package com.valhalla.NodeEditor;

import javax.swing.event.EventListenerList;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.UUID;

public class NodeSocket implements java.io.Serializable {
    protected UUID uuid;
    public Object data;
    private final Class<?> dataClass;
    protected SocketDirection direction;
    public Integer propertyIndex;
    public Integer laneIndex;
    transient protected EventListenerList listenerList;
    transient protected HashMap<NodeSocket, SocketEventListener> socketEventListeners;
    transient protected Color socketColor;

    public NodeSocket(SocketDirection direction, Class<?> dataClass) {
        this.uuid = UUID.randomUUID();
        this.listenerList = new EventListenerList();
        this.direction = direction;
        this.dataClass = dataClass;
        this.socketColor = Color.WHITE;
        this.socketEventListeners = new HashMap<>();

        resetDataDefaults();
    }

    public void setBind(NodeSocket otherSocket) {
        SocketEventListener socEv = new SocketEventListener() {
            @Override
            public void onBindingDataChanged(Object data) {
                setData(data);
            }

            @Override
            public void onBindingBreak() {
                otherSocket.removeOnBindingEventListener(this);
            }

            @Override
            public void onDataEvaluationChanged(NodeSocket socket, SocketState socketState) {

            }
        };

        socketEventListeners.put(otherSocket, socEv);
        setData(otherSocket.data);
        otherSocket.addOnBindingEventListener(socEv);
    }

    public boolean addBinding(NodeSocket otherSocket) {
        if (otherSocket.uuid == this.uuid) return false;
        if (!otherSocket.dataClass.isAssignableFrom(dataClass)) return false;

        // if this socket is input, bind the data of otherSocket
        // if not, bind this socket data to otherSocket
        if (this.direction == SocketDirection.IN) {
            this.setBind(otherSocket);
        }else if(otherSocket.direction == SocketDirection.IN) {
            otherSocket.setBind(NodeSocket.this);
        }
        evaluate();

        return true;
    }

    public void breakBindings() {
        for (SocketEventListener socEv : socketEventListeners.values())
            removeOnBindingEventListener(socEv);
        socketEventListeners.clear();
        resetDataDefaults();
        fireOnBindingBreak();
        fireOnBindingDataChanged();
    }

    public boolean evaluate() {return true;}

    public void resetDataDefaults() { setData(null); }

    public void addOnBindingEventListener(SocketEventListener listener) {
        listenerList.add(SocketEventListener.class, listener);
    }

    public void removeOnBindingEventListener(SocketEventListener listener) {
        listenerList.remove(SocketEventListener.class, listener);
    }

    public void fireOnBindingDataChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == SocketEventListener.class) {
                ((SocketEventListener) listeners[i+1]).onBindingDataChanged(data);
            }
        }
    }

    void fireOnEvaluationStateChanged() {
        //Object[] listeners = listenerList.getListenerList();
        //for (int i = 0; i < listeners.length; i = i+2) {
        //    if (listeners[i] == SocketEventListener.class) {
        //        ((SocketEventListener) listeners[i+1]).onDataEvaluationChanged(this, getState());
        //    }
        //}
    }

    void fireOnBindingBreak() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == SocketEventListener.class) {
                ((SocketEventListener) listeners[i+1]).onBindingBreak();
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public Class<?> getDataClass() {
        return this.data.getClass();
    }

    public SocketDirection getDirection() {
        return direction;
    }

    public void setDirection(SocketDirection direction) {
        this.direction = direction;
    }

    public Color getSocketColor() {
        return socketColor;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
        fireOnBindingDataChanged();
    }

    public boolean isDataBindAvailable(NodeSocket socket) {
        if (!isDataBindAvailable()) return false;
        return socket.dataClass.isAssignableFrom(this.dataClass);
    }

    public boolean isDataBindAvailable() {
        return true;
    }

    public int getBindingCount() {
        return socketEventListeners.size();
    }

    public void removeBind(NodeSocket socket) {
        if (this.socketEventListeners.containsKey(socket)) {
            removeOnBindingEventListener(socketEventListeners.get(socket));
        }
    }
}

