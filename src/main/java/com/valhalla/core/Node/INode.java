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

    List<PropertyBase> GetProperties();
    void SetProperties(List<PropertyBase> properties);
    void AddProperty(PropertyBase propertyClass);
}