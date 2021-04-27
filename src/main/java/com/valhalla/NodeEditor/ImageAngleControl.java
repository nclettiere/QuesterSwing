package com.valhalla.NodeEditor;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ImageAngleControl extends JPanel {
    public String src;
    public double angle;

    public JButton selectButton;
    public JSpinner angleSelector;

    public ImageAngleControl() {
        setupLayout();
    }

    private void setupLayout() {
        this.setLayout(new MigLayout("", "[grow]", "2[grow]2"));
        setOpaque(false);
        setBorder(new EmptyBorder(0,0,0,0));

        SpinnerModel model =
                new SpinnerNumberModel(
                        0.0d,
                        0.0d,
                        360.0d,
                        10.0d);

        selectButton = new JButton("Select Image File");
        angleSelector = new JSpinner(model);

        add(new JLabel("Image Source: "), "grow, wrap");
        add(selectButton, "grow, wrap");
        add(new JLabel("Image Angle: "), "grow, wrap");
        add(angleSelector, "grow");
    }
}
