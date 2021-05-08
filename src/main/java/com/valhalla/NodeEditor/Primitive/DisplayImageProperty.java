package com.valhalla.core.Node;

import com.valhalla.NodeEditor.*;
import com.valhalla.application.gui.ImagePanel;
import com.valhalla.core.Ref;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.UUID;

public class DisplayImageProperty extends PropertyBase {

    String imageSrc = "";
    double imageAngle = 0d;

    public DisplayImageProperty(Integer propertyIndex, UUID nodeUUID) {
        super(propertyIndex, nodeUUID);

        Ref<JComponent> ref = new Ref<>(new ImagePanel());
        ((ImagePanel)ref.get()).SetCustomSize(new Dimension(100,100));

        SetControl(ref);

        ImageSocket id = new ImageSocket(SocketDirection.IN, this.propertyIndex);
        DoubleSocket integerIn = new DoubleSocket(SocketDirection.IN, this.propertyIndex);
        id.addOnBindingEventListener(new SocketEventListener() {
            @Override
            public void onBindingDataChanged(Object data) {
                imageSrc = (String) data;
                ((ImagePanel) ref.get()).addImage(imageSrc, imageAngle);
                FireControlUpdateEvent();
            }

            @Override
            public void onBindingBreak() {
            }

            @Override
            public void onDataEvaluationChanged(NodeSocket socket, SocketState socketState) {

            }
        });
        integerIn.addOnBindingEventListener(new SocketEventListener() {
            @Override
            public void onBindingDataChanged(Object data) {
                imageAngle = (double) data;
                ((ImagePanel) ref.get()).addImage(imageSrc, (double)data);
                FireControlUpdateEvent();
            }

            @Override
            public void onBindingBreak() {
            }

            @Override
            public void onDataEvaluationChanged(NodeSocket socket, SocketState socketState) {

            }
        });
        AddInput(id);
        AddInput(integerIn);
    }

    public DisplayImageProperty(Integer propertyIndex, UUID nodeUUID, Iterable<NodeSocket> sockets) {
        super(propertyIndex, nodeUUID, sockets);

        Ref<JComponent> ref = new Ref<>(new ImagePanel());
        ((ImagePanel)ref.get()).SetCustomSize(new Dimension(100,100));

        SetControl(ref);
    }
}
