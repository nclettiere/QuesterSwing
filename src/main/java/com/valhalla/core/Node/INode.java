package com.valhalla.core.Node;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface INode extends Serializable {
    UUID   GetUUID();
    void   SetUUID(UUID uuid);
    String GetName();
    void   SetName(String name);
    String GetDescription();
    void   SetDescription(String description);

    List<Class<?>> GetProperties();
    void   SetProperties(List<Class<?>> properties);
    void AddProperty(Class<?> propertyClass);

}
