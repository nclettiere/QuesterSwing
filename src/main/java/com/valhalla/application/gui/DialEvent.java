package com.valhalla.application.gui;

public class DialEvent extends java.util.EventObject {
    int value;

    DialEvent(Node source, int value) {
        super(source);
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
