package com.valhalla.core.Node;

import com.valhalla.application.gui.ImagePanel;
import com.valhalla.core.Ref;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DisplayImageProperty extends PropertyBase {
    DisplayImageProperty() {
        super();

        Ref<JComponent> ref = new Ref<>(new ImagePanel());
        ref.get().setPreferredSize(new Dimension(200,200));

        ref.get().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                FireControlUpdateEvent();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        SetControl(ref);
        AddInput(new ImageData());
        AddOutput(new ImageData());
    }
}
