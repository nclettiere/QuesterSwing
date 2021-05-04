package com.valhalla.core.Node;

import com.valhalla.NodeEditor.NodeSocket;

import javax.swing.event.EventListenerList;
import java.util.*;

public class NodeBase implements INode, java.io.Serializable {
    public enum NodeAction {
        NONE,
        CLICKED,
        PRESSED,
        CTRL_CLICKED,
        DRAGGING,
        DELETE,
        CONNECTION_DRAGGING,
        CONNECTION_CLICKED,
        CONNECTION_CTRL_CLICKED
    };

    protected UUID uuid;
    protected String name;
    protected String groupName;
    protected String description;
    protected List<PropertyBase> properties;

    protected NodeAction nodeAction;
    protected EventListenerList listenerList;

    protected boolean allowSelfBinding;
    protected boolean isPureNode;

    NodeBase() {
        this.listenerList = new EventListenerList();
        this.properties = new ArrayList<>();
        this.nodeAction = NodeAction.NONE;
        this.allowSelfBinding = true;
        this.isPureNode = false;
        this.uuid = UUID.randomUUID();
    }

    NodeBase(UUID uuid, Iterable<NodeSocket> sockets) {
        this();
        this.uuid = uuid;
    }

    public NodeAction GetCurrentAction() {
        return this.nodeAction;
    }

    public void SetCurrentAction(NodeAction nodeAction) {
        this.nodeAction = nodeAction;
        FireOnNodeActionChanged();
    }

    public int getInputCount() {
        int iCount = 0;
        for (PropertyBase propertyBase : properties)
            iCount += propertyBase.inputs.size();
        return iCount;
    }

    public int getOutputCount() {
        int oCount = 0;
        for (PropertyBase propertyBase : properties)
            oCount += propertyBase.outputs.size();
        return oCount;
    }

    public int getIOCount() {
        int iCount = 0;
        for (PropertyBase propertyBase : properties)
            iCount += propertyBase.inputs.size();
        int oCount = 0;
        for (PropertyBase propertyBase : properties)
            oCount += propertyBase.outputs.size();
        return iCount + oCount;
    }

    public int getPropertyCount() {
        return properties.size();
    }

    public void ResetNodeAction() {
        this.nodeAction = NodeAction.NONE;
    }

    public void AddNodeActionListener(NodeActionListener listener) {
        listenerList.add(NodeActionListener.class, listener);
    }

    public void RemoveNodeActionListener(NodeActionListener listener) {
        listenerList.remove(NodeActionListener.class, listener);
    }

    /**
     * Loops through all properties connectors and assigns them an index
     * @return HashMap with an Integer representing the property index and the list of connectors data of each property.
     */
    public HashMap<Integer, List<NodeSocket>> getAllConnectorsData() {
        HashMap<Integer, List<NodeSocket>> connectorsData = new HashMap<>();
        int i = 0;
        for (PropertyBase prop : properties) {
            connectorsData.put(i, prop.GetIO());
            i++;
        }
        return connectorsData;
    }

    void FireOnNodeActionChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
            if (listeners[i] == NodeActionListener.class) {
                ((NodeActionListener) listeners[i+1]).OnNodeActionChanged(GetCurrentAction());
            }
        }
    }

    public Iterable<PropertyBase> getProperties() {
        return properties;
    }

    public boolean isPureNode() {
        return isPureNode;
    }

    public void setPureNode(boolean pureNode) {
        isPureNode = pureNode;
    }

    @Override
    public boolean isAllowSelfBinding() {
        return allowSelfBinding;
    }

    @Override
    public void setAllowSelfBinding(boolean allowSelfBinding) {
        this.allowSelfBinding = allowSelfBinding;
    }

    @Override
    public UUID GetUUID() {
        return uuid;
    }

    @Override
    public void SetUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String GetName() {
        return name;
    }

    @Override
    public void SetName(String name) {
        this.name = name;
    }

    @Override
    public String GetDescription() {
        return description;
    }

    @Override
    public void SetDescription(String description) {
        this.description = description;
    }

    @Override
    public List<PropertyBase> GetProperties() {
        return properties;
    }

    @Override
    public void SetProperties(List<PropertyBase> properties) {
        this.properties = properties;
    }

    @Override
    public void AddProperty(PropertyBase property) {
        property.setAllowSelfBinding(allowSelfBinding);
        properties.add(property);
    }
}
