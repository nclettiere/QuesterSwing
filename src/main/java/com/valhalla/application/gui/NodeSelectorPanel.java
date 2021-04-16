package com.valhalla.application.gui;

import com.valhalla.core.Node.NodeComponent;
import com.valhalla.core.Node.NodeEventListener;
import com.valhalla.core.Node.NodeSelectorItem;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

class NodeDisplayData {
    private Class<? extends NodeComponent> nodeClass;
    private String nodeName;
    private String nodeDescription;

    NodeDisplayData() {}
    NodeDisplayData(Class<? extends NodeComponent> nodeClass, String nodeName, String nodeDescription) {
        this.nodeClass = nodeClass;
        this.nodeName = nodeName;
        this.nodeDescription = nodeDescription;
    }

    public Class<? extends NodeComponent> getNodeClass() {
        return nodeClass;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeDescription() {
        return nodeDescription;
    }

    public void setNodeClass(Class<? extends NodeComponent> nodeClass) {
        this.nodeClass = nodeClass;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setNodeDescription(String nodeDescription) {
        this.nodeDescription = nodeDescription;
    }
}

public class NodeSelectorPanel extends JXPanel {
    protected List<Class<? extends NodeComponent>> nodeList;
    protected List<NodeDisplayData> miscList;
    protected HashMap<String, List<NodeDisplayData>> GroupList;

    protected JTextField searchField;
    protected JPanel groupPanel;

    public NodeSelectorPanel(List<Class<? extends NodeComponent>> nodeList) {
        this.GroupList = new HashMap<>();
        this.nodeList = nodeList;
        this.miscList = new ArrayList<>();
        this.listenerList = new EventListenerList();
        this.setLayout(new MigLayout("debug", "0[250]0", "0[35]11[grow]0"));
        searchField = new JTextField();
        searchField.putClientProperty("JComponent.roundRect", "roundRect");
        searchField.putClientProperty("JTextField.placeholderText", "Search Node...");
        add(searchField, "growX, gapleft 10, gapright 10, gaptop 10, wrap");
        groupPanel = new JPanel(new MigLayout("", "0[grow]0"));
        //groupPanel.setBorder(new EmptyBorder(0,0,0,0));
        groupPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(groupPanel);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new EmptyBorder(0,0,0,0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, "grow, hmax 500, gapleft 1, gapright 1, gapbottom 2");

        setVisible(true);
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

    private void DrawNodeSelections() {
        GenerateGroupList();

        // Empty Panels
        groupPanel.removeAll();

        Icon icon = new Utils().getIcon(Utils.Icon.plus);
        // Create Groups Panels
        Iterator<Map.Entry<String, List<NodeDisplayData>>> it = GroupList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<NodeDisplayData>> pair = it.next();
            JPanel subGroupPanel = new JPanel(new MigLayout("", "0[grow]0", "0[grow]0"));
            subGroupPanel.setOpaque(false);
            subGroupPanel.add(new JLabel(pair.getKey()), "growx, gapleft 10, gapbottom 5, wrap");
            subGroupPanel.setBorder(new MatteBorder(0,0,1,0, Color.GRAY));
            for (NodeDisplayData ndd : pair.getValue()) {
                NodeSelectorItem menuItem = new NodeSelectorItem(ndd.getNodeName(), icon);
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FireSelectionEvent(ndd.getNodeClass());
                    }
                });
                subGroupPanel.add(menuItem, "grow, wrap");
            }
            groupPanel.add(subGroupPanel, "grow, wrap");
            it.remove();
        }

        if(miscList.size() > 0) {
            JPanel miscPanel = new JPanel(new MigLayout("", "0[grow]0", "0[grow]0"));
            miscPanel.setOpaque(false);
            miscPanel.add(new JLabel("Miscellaneous"), "growx, gapleft 10, gapbottom 5, wrap");
            for (NodeDisplayData ndd : miscList) {
                NodeSelectorItem menuItem = new NodeSelectorItem(ndd.getNodeName(), icon);
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FireSelectionEvent(ndd.getNodeClass());
                    }
                });
                miscPanel.add(menuItem, "grow, wrap");
            }
            groupPanel.add(miscPanel, "grow, wrap");
        }

        groupPanel.revalidate();
    }

    private void GenerateGroupList() {
        GroupList.clear();
        miscList.clear();

        for (Class<? extends NodeComponent> cls : nodeList) {
            String group = null;
            NodeComponent nodeComp = null;
            try {
                nodeComp = cls.getConstructor().newInstance();
                group = nodeComp.GetGroup();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            if(group == null) group = "Miscellaneous";

            if(!GroupList.containsKey(group)) {
                List<NodeDisplayData> newGroupList = new ArrayList<>();
                NodeDisplayData ndd;
                if(nodeComp != null)
                    ndd = new NodeDisplayData(cls, nodeComp.GetNode().GetName(), nodeComp.GetNode().GetDescription());
                else
                    ndd = new NodeDisplayData(cls, "Undefined", "Undefined");

                newGroupList.add(ndd);

                if(group.equals("Miscellaneous"))
                    miscList.add(ndd);
                else
                    GroupList.put(group, newGroupList);
            }else {
                if(!GroupList.get(group).contains(cls)) {
                    NodeDisplayData ndd;
                    if(nodeComp != null)
                        ndd = new NodeDisplayData(cls, nodeComp.GetNode().GetName(), nodeComp.GetNode().GetDescription());
                    else
                        ndd = new NodeDisplayData(cls, "Undefined", "Undefined");

                    if(!group.equals("Miscellaneous"))
                        GroupList.get(group).add(ndd);
                    else
                        miscList.add(ndd);
                }
            }
        }
    }

    void FireSelectionEvent(Class<? extends NodeComponent> nodeCompClass) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == NodeSelectorListener.class) {
                ((NodeSelectorListener) listeners[i+1]).OnNodeSelected(nodeCompClass);
            }
        }
    }

    public void addNodeSelectorEventListener(NodeSelectorListener listener) {
        listenerList.add(NodeSelectorListener.class, listener);
    }
    public void removeNodeSelectorEventListener(NodeSelectorListener listener) {
        listenerList.remove(NodeSelectorListener.class, listener);
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

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        DrawNodeSelections();
    }
}
