package com.valhalla.NodeEditor.Sockets;

import com.valhalla.core.Node.ConnectorMode;
import com.valhalla.NodeEditor.PropertyBase;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.UUID;

public interface ISocketData {
    void          SetUUID(UUID uuid);
    UUID          GetUUID();
    ConnectorMode GetMode();
    void          SetMode(ConnectorMode mode);
    String        GetName();
    void          SetName(String name);
    String        GetDisplayName();
    void          SetDisplayName(String displayName);
    void          SetData(Object data);
    Object        GetData();
    Type          GetDataType();
    Color         GetDataColor();
    void          SetDataColor(Color color);
    boolean       GetMultipleBindingAllowed();
    void          SetMultipleBindingAllowed(boolean allowed);
    PropertyBase getParentProperty();
    int           getDataPropertyIndex();
    boolean       evaluate();
    boolean       isDataBindAvailable(ISocketData nodeData);
    boolean       IsBinded();
    ISocketData GetBinding(UUID uuid);
    boolean       SetBinding(ISocketData nData);
    void          breakBindings();
    void          AddOnBindingEventListener(BindingEventListener listener);
    void          RemoveOnBindingEventListener(BindingEventListener listener);
}
