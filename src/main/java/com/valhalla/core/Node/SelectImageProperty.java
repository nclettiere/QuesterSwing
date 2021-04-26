package com.valhalla.core.Node;

import com.valhalla.NodeEditor.ImageSocket;
import com.valhalla.NodeEditor.NodeSocket;
import com.valhalla.NodeEditor.SocketEventListener;
import com.valhalla.application.gui.ImagePanel;
import com.valhalla.core.Ref;
import org.w3c.dom.Node;

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

        ((JButton)ref.get()).addActionListener(e -> {
            selectImageActionPerformed(ref.get());
            //addAction();
            FireControlUpdateEvent();
        });

        SetControl(ref);

        ImageSocket imageOut = new ImageSocket(NodeSocket.SocketDirection.OUT);
        //IntegerData integerData = new IntegerData(this);
        ImageSocket imageIn =  new ImageSocket(NodeSocket.SocketDirection.IN);

        imageIn.addOnBindingEventListener(new SocketEventListener() {
            @Override
            public void onBindingDataChanged(Object data) {
                imageOut.props.setData((String) data);
                // Notify for DataBinding
                imageOut.fireOnBindingDataChanged();
                // Hide control as it is not necessary
                //if (imageOut.evaluate())
                    ref.get().setVisible(false);
                // Notify Panel of change
                FireControlUpdateEvent();
                imageOut.evaluate();
            }

            @Override
            public void onBindingBreak() {
                imageOut.props.setData((String) selectedData);
                ref.get().setVisible(true);
                imageOut.evaluate();
            }

            @Override
            public void onDataEvaluationChanged(NodeSocket socket, NodeSocket.SocketState socketState) {
                FireControlUpdateEvent();
            }
        });

        AddInput(imageIn);
        AddOutput(imageOut);
        //AddOutput(integerData);
    }

    private void addAction() {
        ImageSocket imageIn = new ImageSocket(NodeSocket.SocketDirection.OUT);
        AddOutput(imageIn);
        FireConnectorAddedEvent(imageIn);
    }

    private void removeAction() {
        if(outputs.size() > 0) {
            FireConnectorRemovedEvent((NodeSocket) outputs.toArray()[outputs.size() - 1]);
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
            this.GetOutputs().get(0).props.setData(selectedData);

            // Notify for DataBinding
            this.GetOutputs().get(0).fireOnBindingDataChanged();
            // Notify Panel of change
            FireControlUpdateEvent();
        }
    }
}
