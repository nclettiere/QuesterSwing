package com.valhalla.application.gui;

import com.valhalla.core.Node.NodeBase;
import com.valhalla.core.Node.NodeComponent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NodeSelectorPanel extends JComponent {
    protected List<Class<? extends NodeComponent>> nodeList;
    // Create a HashMap object called capitalCities
    protected HashMap<String, List<Class<? extends  NodeComponent>>> GroupList;

    protected JTextField searchField;
    protected JPanel groupPanel;

    NodeSelectorPanel(List<Class<? extends NodeComponent>> nodeList) {
        this.nodeList = nodeList;
        this.setLayout(new MigLayout("debug", "[200]", "[35][grow]"));
        searchField = new JTextField();
        searchField.putClientProperty("JComponent.roundRect", "roundRect");
        searchField.putClientProperty("JTextField.placeholderText", "Search Node...");
        add(searchField, "growX, wrap");
        groupPanel = new JPanel();
        groupPanel.setOpaque(false);
        groupPanel.setLayout(new MigLayout("debug"));
        add(groupPanel, "growX, wrap");
        setVisible(true);

        GenerateGroupList();
    }

    public void reset() {
        searchField.setText("");
    }

    public void setFocusField() {
        searchField.requestFocusInWindow();
    }

    public void AddNodeClass(Class<? extends NodeComponent> nClass) {
        if(!nodeList.contains(nClass))
            nodeList.add(nClass);
    }

    private void GenerateGroupList() {
        for (Class<? extends NodeComponent> cls : nodeList) {
            String group = null;
            try {
                NodeComponent node = cls.getConstructor().newInstance();
                group = node.GetGroup();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            if(group == null) group = "Miscellaneous";

            if(!GroupList.containsKey(group)) {
                List<Class<? extends  NodeComponent>> newGroupList = new ArrayList<>();
                newGroupList.add(cls);
            }else {
                GroupList.get(group).add(cls);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension arcs = new Dimension(10, 10);

        graphics.setColor(new Color(150,150,150));
        graphics.fillRoundRect(0, 0, getWidth(), getHeight(), arcs.width, arcs.height);
        graphics.setColor(new Color(90,90,90));
        graphics.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, arcs.width, arcs.height);
        graphics.setColor(new Color(60,60,60));
        graphics.fillRoundRect(1, 1, getWidth()-2, 45, arcs.width, arcs.height);
    }
}
