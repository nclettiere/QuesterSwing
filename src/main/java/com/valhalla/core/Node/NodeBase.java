package com.valhalla.core.Node;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NodeBase implements INode {
    private UUID uuid;
    private String name;
    private String description;
    private List<PropertyBase> properties;

    NodeBase() {
        this.properties = new ArrayList<>();
    }

    @Override
    public UUID GetUUID() {
        return uuid;
    }

    @Override
    public void SetUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String GetName() {
        return name;
    }

    @Override
    public void SetName(String name) {
        this.name = name;
    }

    @Override
    public String GetDescription() {
        return description;
    }

    @Override
    public void SetDescription(String description) {
        this.description = description;
    }

    @Override
    public List<PropertyBase> GetProperties() {
        return properties;
    }

    @Override
    public void SetProperties(List<PropertyBase> properties) {
        this.properties = properties;
    }

    @Override
    public void AddProperty(PropertyBase propertyClass) {
        properties.add(propertyClass);
    }
}
