package com.valhalla.application.gui;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.UUID;

public interface NodeData {
    void SetUUID(UUID uuid);
    UUID GetUUID();
    String GetName();
    void SetName(String name);
    String GetDisplayName();
    void SetDisplayName(String displayName);
    void SetData(Object data);
    Object GetData();
    Type GetDataType();
    Color GetDataColor();
    void SetDataColor(Color color);
}
