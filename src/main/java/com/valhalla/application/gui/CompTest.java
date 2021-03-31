package com.valhalla.application.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class CompTest extends JComponent {

    public CompTest() {
        this.setLayout(new MigLayout());
        this.add(new JButton("XD Im Here"));
    }
}
