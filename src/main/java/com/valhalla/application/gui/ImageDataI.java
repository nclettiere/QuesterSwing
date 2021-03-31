package com.valhalla.application.gui;

import com.valhalla.core.Node.INodeData;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.UUID;

import java.util.Random;

public class ImageDataI implements INodeData {

    private UUID uuid;
    private String name;
    private String displayName;
    private Object data;
    private Color dataColor;

    public ImageDataI() {
        uuid = UUID.randomUUID();
        SetName("Image");
        SetDisplayName("Image Object");
        SetData(new ImageIcon());

        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        SetDataColor(new Color(r, g, b));
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
    public void SetDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String GetDisplayName() {
        return displayName;
    }

    @Override
    public void SetData(Object data) {
        this.data = data;
    }

    @Override
    public Object GetData() {
        return data;
    }

    @Override
    public Type GetDataType() {
        return data.getClass();
    }

    @Override
    public Color GetDataColor() {
        return dataColor;
    }

    @Override
    public void SetDataColor(Color color) {
        dataColor = color;
    }
}
