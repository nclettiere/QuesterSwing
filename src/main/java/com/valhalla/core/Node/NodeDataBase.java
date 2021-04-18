package com.valhalla.core.Node;

import javax.swing.event.EventListenerList;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NodeDataBase implements INodeData {

    protected UUID                     uuid;
    protected HashMap<UUID, INodeData> bindingMap;
    protected ConnectorMode            mode;
    protected String                   name;
    protected String                   displayName;
    protected boolean                  multipleAllowed;
    protected Object                   data;
    protected Color                    color;
    protected EventListenerList        listenerList;

    public NodeDataBase() {
        this.bindingMap = new HashMap<>();
        this.listenerList = new EventListenerList();
        this.uuid = UUID.randomUUID();
    }

    @Override
    public void SetUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID GetUUID() {
        return uuid;
    }

    @Override
    public ConnectorMode GetMode() {
        return mode;
    }

    @Override
    public void SetMode(ConnectorMode mode) {
        this.mode = mode;
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
    public String GetDisplayName() {
        return displayName;
    }

    @Override
    public void SetDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public void SetData(Object data) {
        this.data = data;
        FireOnBindingDataChanged();
    }

    @Override
    public Object GetData() {
        return this.data;
    }

    @Override
    public Type GetDataType() {
        return data.getClass();
    }

    @Override
    public Color GetDataColor() {
        return color;
    }

    @Override
    public void SetDataColor(Color color) {
        this.color = color;
    }

    @Override
    public boolean GetMultipleBindingAllowed() {
        return multipleAllowed;
    }

    @Override
    public void SetMultipleBindingAllowed(boolean allowed) {
        this.multipleAllowed = multipleAllowed;
    }

    @Override
    public boolean evaluate() {
        return true;
    }

    @Override
    public boolean isDataBindAvailable() {
        return true;
    }

    @Override
    public boolean IsBinded() {
        return (bindingMap.size() > 0);
    }

    @Override
    public INodeData GetBinding(UUID uuid) {
        if(!bindingMap.containsKey(uuid)) return null;
        return bindingMap.get(uuid);
    }

    void FireOnBindingDataChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == BindingEventListener.class) {
                ((BindingEventListener) listeners[i+1]).OnBindingDataChanged(this.GetData());
            }
        }
    }

    @Override
    public void SetBinding(INodeData nData) {
        FireOnBindingDataChanged();
    }

    @Override
    public void AddOnBindingEventListener(BindingEventListener listener) {
        listenerList.add(BindingEventListener.class, listener);
    }
    @Override
    public void RemoveOnBindingEventListener(BindingEventListener listener) {
        listenerList.remove(BindingEventListener.class, listener);
    }
}
