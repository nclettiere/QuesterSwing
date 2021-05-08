package com.valhalla.NodeEditor.Editor;

import com.valhalla.NodeEditor.INodeProperty;
import com.valhalla.NodeEditor.NodeBase;
import com.valhalla.NodeEditor.Sockets.NodeSocket;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PropertyPanel
        extends JPanel
        implements MouseInputListener {

    protected INodeProperty prop;
    protected EventListenerList listenerList;

    protected NodeComponent node;
    protected ArrayList<NodeConnector> connectorList;

    protected JPanel inputPanel;
    protected JPanel controlPanel;
    protected JPanel outputPanel;

    public PropertyPanel(INodeProperty prop, NodeComponent node) {
        this.prop = prop;
        this.node = node;
        this.connectorList = new ArrayList<>();

        setLayout(new MigLayout("debug, wmin 200","[left]0[shrink 0]0[right]"));
        //setBorder(new MatteBorder(0,0,1,0, new Color(255,255,255,30)));
        setOpaque(false);

        inputPanel = new JPanel(new MigLayout("debug", "0[grow]0", "2[top]2"));
        inputPanel.setBorder(new EmptyBorder(10,0,10,0));
        inputPanel.setOpaque(false);

        controlPanel = new JPanel(new MigLayout("", "0[grow]0", "0[grow]0"));
        controlPanel.setBorder(new EmptyBorder(10,10,10,10));
        controlPanel.setOpaque(false);

        outputPanel = new JPanel(new MigLayout("debug", "0[20]0", "2[top]2"));
        outputPanel.setBorder(new EmptyBorder(10,0,10,0));
        outputPanel.setOpaque(false);

        add(inputPanel,"dock west");
        add(controlPanel,"dock center, grow");
        add(outputPanel,"dock east");

        AddProperties();

    }


    // delete later
    public ArrayList<NodeConnector> getConnectors() {
        return connectorList;
    }

    private void AddProperties() {

        UpdateIOLayout();

        if(prop.GetControl() != null)
            controlPanel.add(prop.GetControl().get(), "dock north");
    }

    public void UpdateIOLayout() {
        inputPanel.removeAll();
        outputPanel.removeAll();

        for (NodeSocket sock : prop.GetInputs()) {
            inputPanel.add(new JLabel(""), "grow, w 20!, h 20!, wrap");
        }

        for (NodeSocket sock : prop.GetOutputs()) {
            outputPanel.add(new JLabel(""), "grow, w 20!, h 20!, wrap");
        }

        // Ensure a 'white space' on the input/output lane
        // Preventing control to get in
        if(prop.GetOutputCount() == 0)
            outputPanel.add(new JLabel(""), "grow, w 20!, h 20!, wrap");
        if(prop.GetInputCount() == 0)
            inputPanel.add(new JLabel(""), "grow, w 20!, h 20!, wrap");

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        node.GetNode().SetCurrentAction(NodeBase.NodeAction.PRESSED);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
