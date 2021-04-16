package com.valhalla.core.Node;

import com.valhalla.application.gui.NodeEditor;

import javax.swing.event.EventListenerList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.PublicKey;
import java.util.*;

public class NodeBase implements INode {
    public enum NodeAction {
        NONE,
        CLICKED,
        PRESSED,
        CTRL_CLICKED,
        DRAGGING,
        DELETE,
        CONNECTION_DRAGGING,
        CONNECTION_CLICKED,
        CONNECTION_CTRL_CLICKED
    };

    protected UUID uuid;
    protected String name;
    protected String groupName;
    protected String description;
    protected List<PropertyBase> properties;

    protected NodeAction nodeAction;
    protected EventListenerList listenerList;

    NodeBase() {
        this.listenerList = new EventListenerList();
        this.properties = new ArrayList<>();
        this.nodeAction = NodeAction.NONE;
        SetUUID(UUID.randomUUID());
    }

    public NodeAction GetCurrentAction() {
        return this.nodeAction;
    }

    public void SetCurrentAction(NodeAction nodeAction) {
        this.nodeAction = nodeAction;
        FireOnNodeActionChanged();
    }

    public void ResetNodeAction() {
        this.nodeAction = NodeAction.NONE;
    }

    public void AddNodeActionListener(NodeActionListener listener) {
        listenerList.add(NodeActionListener.class, listener);
    }

    public void RemoveNodeActionListener(NodeActionListener listener) {
        listenerList.remove(NodeActionListener.class, listener);
    }

    /**
     * Loops through all properties connectors and assigns them an index
     * @return HashMap with an Integer representing the property index and the list of connectors data of each property.
     */
    public HashMap<Integer, List<INodeData>> getAllConnectorsData() {
        HashMap<Integer, List<INodeData>> connectorsData = new HashMap<>();
        int i = 0;
        for (PropertyBase prop : properties) {
            connectorsData.put(i, prop.GetIO());
            i++;
        }
        return connectorsData;
    }

    void FireOnNodeActionChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == NodeActionListener.class) {
                ((NodeActionListener) listeners[i+1]).OnNodeActionChanged(GetCurrentAction());
            }
        }
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
