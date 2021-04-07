package com.valhalla.application.gui;

import com.valhalla.core.Node.NodeBase;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NodeSelectorPanel extends JPanel {
    protected List<Class<? extends NodeBase>> nodeList;

    NodeSelectorPanel() {
        this.nodeList = new ArrayList<>();

        this.setLayout(new MigLayout("debug"));
        //this.setOpaque(false);
        setBackground(Color.GREEN);
        add(new JLabel("asdasd"), "grow");
        setPreferredSize(new Dimension(200,200));
    }

    public void AddNodeClass(Class<? extends NodeBase> nClass) {
        if(!nodeList.contains(nClass))
            nodeList.add(nClass);
    }

}
