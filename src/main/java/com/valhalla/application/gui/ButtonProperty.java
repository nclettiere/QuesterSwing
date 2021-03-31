package com.valhalla.application.gui;

import javax.swing.*;

public class ButtonProperty implements NodeProperty{

    int AreaHeight;

    public ButtonProperty() {
        AreaHeight = 0;
    }

    @Override
    public JComponent GetControl() {
        return new JButton("Meep");
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
        return 1;
    }

    @Override
    public int GetOutputCount() {
        return 0;
    }

    @Override
    public void OnControlUpdate() {

    }

    @Override
    public void OnConnect() {

    }

    @Override
    public void OnDisconnect() {

    }
}
