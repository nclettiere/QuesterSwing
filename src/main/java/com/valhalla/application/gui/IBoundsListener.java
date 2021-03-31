package com.valhalla.application.gui;

import javax.swing.*;
import java.awt.*;
import java.util.EventListener;
import java.util.EventObject;

class BoundsEvent extends EventObject {
    public BoundsEvent(Object source) {
        super(source);
    }
}

public interface IBoundsListener extends EventListener {
    Dimension OnSizeChanged(JComponent component);
    Point OnPositionChanged(JComponent component);
}
