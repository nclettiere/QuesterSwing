package com.valhalla.core.Node;

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

        ImageData id = new ImageData(this);
        id.SetMode(ConnectorMode.INPUT);
        id.AddOnBindingEventListener(new BindingEventListener() {
            @Override
            public void OnBindingDataChanged(Object data) {
                if(id.evaluate())
                    ((ImagePanel) ref.get()).addImage((String) id.GetData());
                FireControlUpdateEvent();
            }

            @Override
            public void OnBindingReleased() {

            }

            @Override
            public void onDataEvaluationChanged(INodeData data, Map.Entry<Boolean, String> evaluationState) {

            }
        });
        AddInput(id);
    }
}
