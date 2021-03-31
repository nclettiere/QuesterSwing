package com.valhalla.application.gui;

import javax.swing.*;
import java.awt.*;

public class IntegerProperty implements NodeProperty {
    private JComponent component;
    private int inputCount, outputCount;

    private int AreaHeight;

    IntegerProperty() {
        AreaHeight = 0;
        component = new JTextArea();
        //component.setPreferredSize(new Dimension(150,10));
        inputCount = 3;
        outputCount = 1;
    }
    @Override
    public JComponent GetControl() {
        return component;
    }

    @Override
    public int GetPaintedAreaHeight() {
        return AreaHeight;
    }

    @Override
    public void SetPaintedAreaHeight(int height) {
        AreaHeight = height;
    }

    @Override
    public int GetInputCount() {
        return inputCount;
    }

    @Override
    public int GetOutputCount() {
        return outputCount;
    }

    @Override
    public void OnControlUpdate() {

    }

    @Override
    public void OnConnect() {
        System.out.println("XD - Connect");
    }

    @Override
    public void OnDisconnect() {
        System.out.println("XD - Disconnect");
    }
}
