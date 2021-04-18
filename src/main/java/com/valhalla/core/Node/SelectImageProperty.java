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
import java.util.Random;
import java.util.UUID;

public class SelectImageProperty extends PropertyBase {
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
            FireControlUpdateEvent();
        });

        SetControl(ref);

        ImageData imageOut = new ImageData();
        IntegerData integerData = new IntegerData();

        ImageData imageIn = new ImageData();

        AddInput(imageIn);
        AddOutput(imageOut);
        AddOutput(integerData);
    }

    private void addAction() {
        ImageData imageIn = new ImageData();
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
            this.GetOutputs().get(0)
                    .SetData(chooser.getSelectedFile().getAbsolutePath());

            // Notify for DataBinding
            ((ImageData)this.GetOutputs().get(0)).FireOnBindingDataChanged();
            // Notify Panel of change
            FireControlUpdateEvent();
        }
    }
}
