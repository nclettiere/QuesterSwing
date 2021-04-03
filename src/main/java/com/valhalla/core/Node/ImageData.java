package com.valhalla.core.Node;

import javax.swing.event.EventListenerList;
import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

public class ImageData implements INodeData {

    protected UUID                 uuid;
    protected ArrayList<INodeData> bindingList;
    protected ConnectorMode        mode;
    protected String               name;
    protected String               displayName;
    protected boolean              multipleAllowed;
    protected Object               data;
    protected Color                color;
    protected EventListenerList    listenerList;

    public ImageData() {
        this.bindingList = new ArrayList<>();
        this.listenerList = new EventListenerList();

        SetUUID(UUID.randomUUID());
        SetName("Image");
        SetDisplayName("Image");
        SetData("");
        SetDataColor(new Color(200, 75, 30));
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
    public INodeData GetBinding(UUID uuid) {
        for(INodeData nData : bindingList) {
            if(nData.GetUUID() == uuid)
                return nData;
        }
        return null;
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
        if(nData.GetUUID() != this.GetUUID()) {
            ImageData iThis = this;
            nData.AddOnBindingEventListener(new BindingEventListener() {
                @Override
                public void OnBindingDataChanged(Object data) {
                    iThis.SetData(nData.GetData());
                    //FireOnBindingDataChanged();
                }

                @Override
                public void OnBindingReleased() {

                }
            });
        }
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
