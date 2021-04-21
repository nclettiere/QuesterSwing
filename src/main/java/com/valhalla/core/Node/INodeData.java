package com.valhalla.core.Node;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.UUID;

public interface INodeData {
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

    boolean       evaluate();
    boolean       isDataBindAvailable(INodeData nodeData);
    boolean       IsBinded();
    INodeData     GetBinding(UUID uuid);
    boolean       SetBinding(INodeData nData);
    void          breakBindings();
    void          AddOnBindingEventListener(BindingEventListener listener);
    void          RemoveOnBindingEventListener(BindingEventListener listener);
}
