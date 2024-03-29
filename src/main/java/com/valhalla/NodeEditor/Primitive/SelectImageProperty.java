package com.valhalla.NodeEditor.Primitive;

import com.valhalla.NodeEditor.*;
import com.valhalla.NodeEditor.Sockets.NodeSocket;
import com.valhalla.NodeEditor.Sockets.SocketDirection;
import com.valhalla.NodeEditor.Sockets.SocketEventListener;
import com.valhalla.NodeEditor.Sockets.SocketState;
import com.valhalla.NodeEditor.PropertyBase;
import com.valhalla.NodeEditor.Ref;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.util.UUID;

public class SelectImageProperty extends PropertyBase {
    Object selectedData;

    SelectImageProperty(Integer propertyIndex, UUID nodeUUID) {
        super(propertyIndex, nodeUUID);

        Ref<JComponent> ref = new Ref<>(new ImageAngleControl());
        ImageAngleControl iac = ((ImageAngleControl)ref.get());

        iac.selectButton.addActionListener(e -> {
            selectImageActionPerformed(ref.get());
            FireControlUpdateEvent();
        });

        iac.angleSelector.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double val = (double) ((JSpinner)e.getSource()).getValue();
                angleChanged(val);
            }
        });

        SetControl(ref);

        ImageSocket imageIn =  new ImageSocket(SocketDirection.IN, this.propertyIndex);
        ImageSocket imageOut = new ImageSocket(SocketDirection.OUT, this.propertyIndex);
        DoubleSocket imageAngleSocket = new DoubleSocket(SocketDirection.OUT, this.propertyIndex);

        imageIn.addOnBindingEventListener(new SocketEventListener() {
            protected boolean isOutBinded = false;
            @Override
            public void onBindingDataChanged(Object data) {
                if (!isOutBinded) {
                    imageOut.setBind(imageIn);
                    isOutBinded = true;
                }
                ref.get().setVisible(imageIn.getBindingCount() == 0);
            }

            @Override
            public void onBindingBreak() {
                if (isOutBinded) {
                    imageOut.removeBind(imageIn);
                    isOutBinded = false;
                }
                ref.get().setVisible(imageIn.getBindingCount() == 0);
                ref.get().setVisible(true);
            }

            @Override
            public void onDataEvaluationChanged(NodeSocket socket, SocketState socketState) {
                FireControlUpdateEvent();
            }
        });

        AddInput(imageIn);
        AddOutput(imageOut);
        AddOutput(imageAngleSocket);
    }

    SelectImageProperty(Integer propertyIndex, UUID nodeUUID, Iterable<NodeSocket> sockets) {
        super(propertyIndex, nodeUUID, sockets);

        Ref<JComponent> ref = new Ref<>(new ImageAngleControl());
        ImageAngleControl iac = ((ImageAngleControl)ref.get());

        iac.selectButton.addActionListener(e -> {
            selectImageActionPerformed(ref.get());
            FireControlUpdateEvent();
        });

        iac.angleSelector.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double val = (double) ((JSpinner)e.getSource()).getValue();
                angleChanged(val);
            }
        });

        SetControl(ref);
    }

    private void addAction() {
        ImageSocket imageIn = new ImageSocket(SocketDirection.OUT, this.propertyIndex);
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
            this.GetOutputs().get(0).setData(selectedData);

            // Notify for DataBinding
            //this.GetOutputs().get(0).fireOnBindingDataChanged();
            // Notify Panel of change
            FireControlUpdateEvent();
        }
    }

    private void angleChanged(double value) {
        this.GetOutputs().get(1).setData(value);
        FireControlUpdateEvent();
    }
}
