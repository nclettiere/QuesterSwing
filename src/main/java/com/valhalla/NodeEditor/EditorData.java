package com.valhalla.NodeEditor;

import com.valhalla.core.Node.NodeBase;
import com.valhalla.core.Node.NodeComponent;
import org.piccolo2d.PNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class EditorData implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public String editorName;

    private Map<UUID, Class<? extends NodeComponent>> nodeList;
    private Map<NodeSocket, UUID> socketList;
    private Map<UUID, Point2D> nodePositions;

    public EditorData() {
        this.nodeList = new HashMap<>();
        this.socketList = new HashMap<>();
        this.nodePositions = new HashMap<>();
    }

    public EditorData(String editorName) {
        this();
        this.editorName = editorName;
    }

    public void setNodeList(Map<UUID, Class<? extends NodeComponent>> nodeList) {
        this.nodeList = nodeList;
    }

    public void setSocketList(Map<NodeSocket, UUID> socketList) {
        this.socketList = socketList;
    }

    public Map<UUID, Class<? extends NodeComponent>> getNodeList() {
        return nodeList;
    }

    public Map<? extends NodeSocket, UUID> getSocketList() {
        return socketList;
    }

    public Map<UUID, Point2D> getNodePositions() {
        return nodePositions;
    }

    public void setNodePositions(Map<UUID, Point2D> nodeCompsLocations) {
        this.nodePositions = nodeCompsLocations;
    }

    public void addNodePosition(UUID uuid, Point2D nodePosition) {
        nodePositions.put(uuid, nodePosition);
    }

    public void addNodeSocket(NodeSocket socket, UUID nodeUUID) {
        socketList.put(socket, nodeUUID);
    }

    public Iterator<Map.Entry<UUID, Class<? extends NodeComponent>>> getNodeListIterator() {
        return nodeList.entrySet().iterator();
    }

    public Iterator<? extends Map.Entry<NodeSocket, UUID>> getSocketListIterator() {
        return socketList.entrySet().iterator();
    }

    public Iterator<Map.Entry<UUID, Point2D>> getPositionsListIterator() {
        return nodePositions.entrySet().iterator();
    }

    public Iterable<NodeSocket> getSocketsOfNode(UUID nodeUUID) {
        List<NodeSocket> sockets = new ArrayList<>();
        for (Map.Entry<NodeSocket, UUID> socEntry : socketList.entrySet()) {
            if (socEntry.getValue().equals(nodeUUID))
                sockets.add(socEntry.getKey());
        }
        return sockets;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        Iterator<Map.Entry<UUID, Class<? extends NodeComponent>>> itNodes = getNodeListIterator();
        Iterator<? extends Map.Entry<? super NodeSocket, UUID>> itSockets = getSocketListIterator();
        Iterator<Map.Entry<UUID, Point2D>> itPositions = getPositionsListIterator();

        str.append("\n*****************************\n");
        str.append("data for editor: ");
        str.append(editorName).append("\n");
        str.append("version: ").append(serialVersionUID).append("\n\n");
        str.append("[List of Registered Nodes]").append("\n");

        str.append("i")
            .append("\t")
            .append("Node UUID")
            .append("\t\t\t\t\t\t\t\t")
            .append("Node Class")
            .append("\n");

        int index = 0;
        while (itNodes.hasNext()) {
            Map.Entry<UUID, Class<? extends NodeComponent>> pair = itNodes.next();
            str.append(index)
                .append("\t")
                .append(pair.getKey())
                .append("\t")
                .append(pair.getValue())
                .append("\n");
            index++;
            itNodes.remove();
        }

        str.append("\n\n");

        str.append("[List of sockets]").append("\n");

        str.append("i")
                .append("\t")
                .append("Socket UUID")
                .append("\t\t\t\t\t\t\t\t")
                .append("Node UUID")
                .append("\t\t\t\t\t\t\t\t")
                .append("Socket Class")
                .append("\n");

        index = 0;
        while (itSockets.hasNext()) {
            Map.Entry<? super NodeSocket, UUID> pair = itSockets.next();
            NodeSocket nodeSocket = (NodeSocket) pair.getKey();
            str.append(index)
                    .append("\t")
                    .append(nodeSocket.getUuid())
                    .append("\t")
                    .append(pair.getValue())
                    .append("\t")
                    .append(nodeSocket.getClass())
                    .append("\n");
            index++;
            itSockets.remove();
        }

        str.append("\n\n");

        str.append("[List of Node Positions]").append("\n");

        str.append("i")
                .append("\t")
                .append("Node UUID")
                .append("\t\t\t\t\t\t\t\t")
                .append("Node Position")
                .append("\n");

        index = 0;
        while (itPositions.hasNext()) {
            Map.Entry<UUID, Point2D> pair = itPositions.next();
            str.append(index)
                    .append("\t")
                    .append(pair.getKey())
                    .append("\t")
                    .append(pair.getValue())
                    .append("\n");
            index++;
            itPositions.remove();
        }

        str.append("\n*****************************\n");

        return str.toString();
    }


    public Point2D getPositionOfNode(UUID nodeUUID) {
        if (!nodePositions.containsKey(nodeUUID))
            return new Point2D.Double(0,0);
        return nodePositions.get(nodeUUID);
    }
}