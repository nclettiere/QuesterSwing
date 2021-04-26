package com.valhalla.core.Node;

import com.valhalla.NodeEditor.ImageSocket;
import com.valhalla.NodeEditor.IntegerSocket;
import com.valhalla.NodeEditor.NodeSocket;
import com.valhalla.NodeEditor.SocketEventListener;
import com.valhalla.application.gui.ImagePanel;
import com.valhalla.core.Ref;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.UUID;

public class DisplayImageProperty extends PropertyBase {

    public DisplayImageProperty(Integer propertyIndex, UUID nodeUUID) {
        super(propertyIndex, nodeUUID);

        Ref<JComponent> ref = new Ref<>(new ImagePanel());
        ((ImagePanel)ref.get()).SetCustomSize(new Dimension(100,100));

        SetControl(ref);

        ImageSocket id = new ImageSocket(NodeSocket.SocketDirection.IN);
        id.addOnBindingEventListener(new SocketEventListener() {
            @Override
            public void onBindingDataChanged(Object data) {
                ((ImagePanel) ref.get()).addImage((String) id.getData());
                FireControlUpdateEvent();
            }

            @Override
            public void onBindingBreak() {
            }

            @Override
            public void onDataEvaluationChanged(NodeSocket socket, NodeSocket.SocketState socketState) {

            }
        });
        AddInput(id);
        AddInput(new IntegerSocket(NodeSocket.SocketDirection.IN));
    }
}
