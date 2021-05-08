package com.valhalla.NodeEditor;

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
