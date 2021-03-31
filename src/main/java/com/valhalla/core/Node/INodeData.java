package com.valhalla.core.Node;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.UUID;

public interface INodeData {
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
