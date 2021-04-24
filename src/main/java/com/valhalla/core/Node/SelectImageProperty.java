package com.valhalla.core.Node;

import com.valhalla.application.gui.ImagePanel;
import com.valhalla.core.Ref;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SelectImageProperty extends PropertyBase {
    Object selectedData;

    SelectImageProperty(Integer propertyIndex, UUID nodeUUID) {
        super(propertyIndex, nodeUUID);

        Ref<JComponent> ref = new Ref<>(new JButton("Select Image"));

        /*Ref<JComponent> ref = new Ref<>(new DynamicNodeJPanel());

        ((DynamicNodeJPanel)ref.get()).buttonAdd.addActionListener(e -> {
            addAction();
            FireControlUpdateEvent();
        });
        ((DynamicNodeJPanel)ref.get()).buttonRemove.addActionListener(e -> {
            removeAction();
            FireControlUpdateEvent();
        });*/

        ((JButton)ref.get()).addActionListener(e -> {
            selectImageActionPerformed(ref.get());
            addAction();
            FireControlUpdateEvent();
        });

        SetControl(ref);

        ImageData imageOut = new ImageData(this);
        IntegerData integerData = new IntegerData(this);

        ImageData imageIn = new ImageData(this);

        imageIn.AddOnBindingEventListener(new BindingEventListener() {
            @Override
            public void OnBindingDataChanged(Object data) {
                imageOut.SetData(data);
                // Notify for DataBinding
                imageOut.FireOnBindingDataChanged();
                // Hide control as it is not necessary
                ref.get().setVisible(false);
                // Notify Panel of change
                FireControlUpdateEvent();
            }

            @Override
            public void OnBindingReleased() {
                imageOut.SetData(selectedData);
                // Notify for DataBinding
                imageOut.FireOnBindingDataChanged();
                ref.get().setVisible(true);
            }

            @Override
            public void onDataEvaluationChanged(INodeData data, Map.Entry<Boolean, String> evaluationState) {

            }
        });

        AddInput(imageIn);
        AddOutput(imageOut);
        AddOutput(integerData);
    }

    private void addAction() {
        ImageData imageIn = new ImageData(this);
        AddOutput(imageIn);
        FireConnectorAddedEvent(imageIn);
    }

    private void removeAction() {
        if(outputs.size() > 0) {
            FireConnectorRemovedEvent((INodeData) outputs.toArray()[outputs.size() - 1]);
            RemoveOutput(outputs.size() - 1);
        }
    }

    private void selectImageActionPerformed(Component parent) {
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
        int result = chooser.showOpenDialog(parent);

        // if the user selects a file
        if (result == JFileChooser.APPROVE_OPTION) {
            // set the output data
            // update the control if needed
            selectedData = chooser.getSelectedFile().getAbsolutePath();
            this.GetOutputs().get(0)
                    .SetData(selectedData);

            // Notify for DataBinding
            ((ImageData)this.GetOutputs().get(0)).FireOnBindingDataChanged();
            // Notify Panel of change
            FireControlUpdateEvent();
        }
    }
}
