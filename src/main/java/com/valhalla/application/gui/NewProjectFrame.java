package com.valhalla.application.gui;

import javax.swing.*;
import java.awt.*;

public class NewProjectFrame
        extends JFrame
{
    public NewProjectFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(0,2);
        JTabbedPane tabs = new JTabbedPane();

        contentPanel.setLayout(gridLayout);
        tabs.add("main", new ProjectSelectorFrame());

        contentPanel.add(tabs);

        add(contentPanel);
        this.setVisible(true);
    }
}
