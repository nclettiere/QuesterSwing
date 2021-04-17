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
        ((ImagePanel)ref.get()).SetCustomSize(new Dimension(100,100));

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

        ImageData id = new ImageData();
        id.SetMode(ConnectorMode.INPUT);
        id.AddOnBindingEventListener(new BindingEventListener() {
            @Override
            public void OnBindingDataChanged(Object data) {
                UpdateBindings();
            }

            @Override
            public void OnBindingReleased() {

            }
        });
        AddInput(id);
        FireControlUpdateEvent();
    }

    @Override
    public void UpdateBindings() {
        super.UpdateBindings();

        ((ImagePanel)GetControl().get()).addImage(
                (String)((INodeData)GetInputs().toArray()[0]).GetData());
        FireControlUpdateEvent();
    }
}
