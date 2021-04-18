package com.valhalla.core.Node;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DynamicNodeJPanel extends JComponent {
    public JButton buttonAdd;
    public JButton buttonRemove;
    public DynamicNodeJPanel() {
        setLayout(new MigLayout("", "0[grow]0", "0[]0[]0"));
        setBorder(new EmptyBorder(0,0,0,0));
        setOpaque(false);
        buttonAdd = new JButton("Add Connector");
        buttonRemove = new JButton("Remove Connector");
        add(buttonAdd, "growx, wrap");
        add(buttonRemove, "growx, wrap");
    }
}
