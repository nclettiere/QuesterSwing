package com.valhalla.core.Node;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class DataBinding {
    private UUID from;
    private UUID to;

    public DataBinding() { }

    public DataBinding(UUID from, UUID to) {
        this.from  = from;
        this.to  = to;
    }

    public UUID GetFrom() {return from;}
    public UUID GetTo() {return to;}
    public void SetFrom(UUID uuid) {this.from = uuid;}
    public void SetTo(UUID uuid) {this.to = uuid;}
}