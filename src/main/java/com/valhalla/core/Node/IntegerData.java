package com.valhalla.core.Node;

import javax.swing.event.EventListenerList;
import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

public class IntegerData extends NodeDataBase {

    public IntegerData(PropertyBase parentProperty) {
        super(parentProperty);
        SetName("Integer");
        SetDisplayName("Integer");
        SetData(0);
        SetDataColor(new Color(90, 155, 30));
    }

    @Override
    public boolean evaluate() {
        if(data == null) return false;
        return data instanceof Integer;
    }
}
