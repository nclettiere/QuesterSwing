package com.valhalla.core.Node;

import com.valhalla.application.gui.NodeEditor;

import javax.swing.event.EventListenerList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NodeBase implements INode {
    protected UUID uuid;
    protected String name;
    protected String description;
    protected List<PropertyBase> properties;

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
