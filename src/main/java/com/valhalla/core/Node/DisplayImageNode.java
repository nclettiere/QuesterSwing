package com.valhalla.core.Node;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DisplayImageNode implements INode {
    private UUID uuid;
    private String name;
    private String description;
    private List<Class<?>> properties;

    DisplayImageNode() {
        this.properties = new ArrayList<>();
        this.properties.add(DisplayImageProperty.class);
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
    public List<Class<?>> GetProperties() {
        return properties;
    }

    @Override
    public void SetProperties(List<Class<?>> properties) {
        this.properties = properties;
    }

    @Override
    public void AddProperty(Class<?> propertyClass) {
        Constructor<?> constructor = propertyClass.getConstructors()[0];
        try {
            Object data = constructor.newInstance();
            if(!(data instanceof INodeProperty))
                throw new NullPointerException();
            properties.add(propertyClass);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new NullPointerException();
        }
    }
}
