package com.valhalla.application.gui;

import javax.swing.*;
import java.awt.*;

public class NewProjectFrame
        extends JDialog
{
    public NewProjectFrame() {
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(0,2);
        JTabbedPane tabs = new JTabbedPane();

        //contentPanel.setLayout(gridLayout);
        //tabs.add("main", new ProjectSelectorFrame(this));

        contentPanel.add(tabs);

        add(contentPanel);
        this.setVisible(true);
    }
}
