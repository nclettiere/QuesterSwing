package com.valhalla.NodeEditor;

import com.valhalla.core.Node.PropertyBase;

import javax.swing.event.EventListenerList;
import java.awt.*;
import java.util.*;

public class NodeSocket<T> {
    public SocketProperties<T> props;
    protected EventListenerList listenerList;
    protected PropertyBase parentProperty;
    protected Color socketColor;

    NodeSocket(T initialData, SocketDirection direction) {
        this.listenerList = new EventListenerList();
        this.props = new SocketProperties<T>(this, direction);
        this.props.setData(initialData);
        this.socketColor = Color.WHITE;
    }

    public Color getSocketColor() {
        return socketColor;
    }

    public void setSocketColor(Color socketColor) {
        this.socketColor = socketColor;
    }

    public void setDirection(SocketDirection direction) {
        props.setDirection(direction);
    }

    public UUID getUUID() {
        return props.getUuid();
    }

    public void addNewBinding(NodeSocket nodeSocket, SocketDirection direction) {
        props.addBinding(nodeSocket, false);
    }

    public void removeBinding(NodeSocket nodeSocket) {
        props.removeBinding(nodeSocket);
    }

    public boolean evaluate() {
        fireOnBindingDataChanged();
        return true;
    }

    public boolean isDataBindAvailable() {
        return true;
    }

    public boolean isDataBindAvailable(NodeSocket nodeSocket) {
        if (!isDataBindAvailable()) return false;
        if (nodeSocket.props.getData() == null) return true;
        return !((nodeSocket.getUUID() == getUUID()) ||
                nodeSocket.getDirection() == this.getDirection() ||
                !(nodeSocket.props.getDataClass().isAssignableFrom(this.props.getDataClass())));
    }

    public SocketDirection getDirection() {
        return props.getDirection();
    }

    public void fireOnBindingDataChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == SocketEventListener.class) {
                ((SocketEventListener) listeners[i+1]).onBindingDataChanged(props.getData());
            }
        }
    }

    void fireOnEvaluationStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == SocketEventListener.class) {
                ((SocketEventListener) listeners[i+1]).onDataEvaluationChanged(this, props.getState());
            }
        }
    }

    void fireOnBindingBreak() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == SocketEventListener.class) {
                ((SocketEventListener) listeners[i+1]).onBindingBreak();
            }
        }
    }

    public void addOnBindingEventListener(SocketEventListener listener) {
        listenerList.add(SocketEventListener.class, listener);
    }

    public void removeOnBindingEventListener(SocketEventListener listener) {
        listenerList.remove(SocketEventListener.class, listener);
    }

    public void breakBindings() {
        SocketEventListener[] listenerArr = listenerList.getListeners(SocketEventListener.class);
        for (int i = listenerList.getListenerCount() - 1; i > 0; i--) {
            listenerList.remove(SocketEventListener.class, listenerArr[i]);
        }
        props.clearBindingMap();
        fireOnBindingBreak();
    }

    public class SocketProperties<T> {
        protected UUID uuid;
        protected Object data;
        protected SocketDirection direction;
        protected NodeSocket<T> socket;
        protected SocketState state;
        protected HashMap<NodeSocket<T>, SocketDirection> bindings;

        public SocketProperties(NodeSocket<T> socket) {
            this.socket = socket;
            this.uuid = UUID.randomUUID();
            this.bindings = new HashMap<>();
            data = null;
        }

        public SocketProperties(NodeSocket<T> socket, SocketDirection direction) {
            this.socket = socket;
            this.direction = direction;
            this.uuid = UUID.randomUUID();
            this.bindings = new HashMap<>();
            data = null;
        }

        public T getData() {
            return (T) data;
        }

        public void setData(Object data) {
            if (this.data != null && data != null) {
                if (!data.getClass().isAssignableFrom(getDataClass()))
                    return;
            }
            this.data = data;
            socket.fireOnBindingDataChanged();
        }

        public UUID getUuid() {
            return uuid;
        }

        public Class<?> getDataClass() {
            if (data == null) return null;
            return data.getClass();
        }

        public void setBindings(HashMap<NodeSocket<T>, SocketDirection> bindings) {
            this.bindings = bindings;
        }

        public boolean addBinding(NodeSocket<T> nodeSocket, boolean oneWay) {
            if (nodeSocket.getUUID() == socket.getUUID() ||
                    !nodeSocket.props.getDataClass().isAssignableFrom(getDataClass()))
                return false;

            this.bindings.put(nodeSocket, nodeSocket.getDirection());

            if (direction == SocketDirection.IN) {
                nodeSocket.addOnBindingEventListener(new SocketEventListener() {
                    @Override
                    public void onBindingDataChanged(Object data) {
                        setData((T) data);
                    }

                    @Override
                    public void onBindingBreak() {

                    }

                    @Override
                    public void onDataEvaluationChanged(NodeSocket data, NodeSocket.SocketState socketState) {
                        state = socketState;
                        socket.fireOnEvaluationStateChanged();
                    }
                });
                setData(nodeSocket.props.getData());
            }

            if (!oneWay) {
                nodeSocket.props.addBinding(socket, true);
            }

            socket.fireOnBindingDataChanged();

            return true;
        }

        public int getBindingCount() {
            return bindings.size();
        }

        public SocketState getState() {
            return state;
        }

        public void setState(SocketState state) {
            this.state = state;
            socket.fireOnEvaluationStateChanged();
        }

        public SocketDirection getDirection() {
            return direction;
        }

        public void removeBinding(NodeSocket nodeSocket) {
            if (bindings.containsKey(nodeSocket)) {
                bindings.remove(nodeSocket);
            }
        }

        public void setDirection(SocketDirection direction) {
            this.direction = direction;
        }

        public void clearBindingMap() {
            bindings.clear();
        }
    }

    public class SocketState {
        public StateErrorLevel errorLevel;
        public String stateMessage;
        public SocketDirection direction;

        public SocketState(SocketDirection direction) {
            this.errorLevel = StateErrorLevel.PASSING;
            this.stateMessage = "";
            this.direction = direction;
        }

        public SocketState(StateErrorLevel errorLevel, String stateMessage, SocketDirection direction) {
            this.errorLevel = errorLevel;
            this.stateMessage = stateMessage;
            this.direction = direction;
        }

        public StateErrorLevel getErrorLevel() {
            return errorLevel;
        }

        public void setErrorLevel(StateErrorLevel errorLevel) {
            this.errorLevel = errorLevel;
        }

        public String getStateMessage() {
            return stateMessage;
        }

        public void setStateMessage(String stateMessage) {
            this.stateMessage = stateMessage;
        }

        public SocketDirection getDirection() {
            return direction;
        }

        public void setDirection(SocketDirection direction) {
            this.direction = direction;
        }
    }
    public enum SocketDirection {
        IN,
        OUT
    }
    public enum StateErrorLevel {
        PASSING,
        WARNING,
        ERROR
    }
}

