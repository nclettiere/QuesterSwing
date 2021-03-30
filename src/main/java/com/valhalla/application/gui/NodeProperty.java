package com.valhalla.application.gui;

import javax.swing.*;
import java.awt.*;

public interface NodeProperty {
    JComponent GetControl();
    int GetPaintedAreaHeight();
    void SetPaintedAreaHeight(int height);

    int GetInputCount();
    int GetOutputCount();

    void OnControlUpdate();
    void OnConnect();
    void OnDisconnect();
}
