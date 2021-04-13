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

public class SelectImageProperty extends PropertyBase {
    SelectImageProperty() {
        super();

        Ref<JComponent> ref = new Ref<>(new JButton("Select"));

        ((JButton)ref.get()).addActionListener(e -> {
            openActionPerformed(ref.get());
        });

        SetControl(ref);

        ImageData imageOut = new ImageData();
        IntegerData integerData = new IntegerData();

        ImageData imageIn = new ImageData();

        AddInput(imageIn);
        AddOutput(imageOut);
        AddOutput(integerData);
    }

    private void openActionPerformed(Component parent) {
        //JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
        //int result = chooser.showOpenDialog(parent);
//
        //// if the user selects a file
        //if (result == JFileChooser.APPROVE_OPTION) {
        //    // set the output data
        //    // update the control if needed
        //    this.GetOutputs().get(0)
        //            .SetData(chooser.getSelectedFile().getAbsolutePath());
//
        //    // Notify for DataBinding
        //    ((ImageData)this.GetOutputs().get(0)).FireOnBindingDataChanged();
        //    // Notify Panel of change
        //    FireControlUpdateEvent();
        //}

        ImageData imageIn = new ImageData();
        AddOutput(imageIn);
        FireControlUpdateEvent();
    }
}
