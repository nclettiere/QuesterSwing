package com.valhalla.core.Node;

import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.UUID;

public class ImageData implements INodeData {

    protected UUID   uuid;
    protected String name;
    protected String displayName;
    protected Class<?> data;
    protected Color  color;

    public ImageData() {
        SetUUID(UUID.randomUUID());
        SetName("Image");
        SetDisplayName("Image");
        SetData(InputStream.class);
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
    public void SetData(Class<?> data) {
        this.data = data;
    }

    @Override
    public Class<?> GetData() {
        return data;
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
}
