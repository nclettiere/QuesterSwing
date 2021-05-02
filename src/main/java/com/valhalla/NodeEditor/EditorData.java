package com.valhalla.NodeEditor;

import com.valhalla.core.Node.NodeBase;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EditorData {
    public String editorName;
    public Long editorVersion;

    private Iterable<? extends NodeBase> nodeList;
    private Iterable<? extends NodeSocket> socketList;
    private Iterable<Point> nodeCompsLocations;

    public EditorData() {
        this.editorVersion = 1L;
        this.nodeList = new ArrayList<>();
        this.socketList = new ArrayList<>();
    }

    public EditorData(String editorName) {
        this();
        this.editorName = editorName;
    }

    public void setNodeList(Iterable<? extends NodeBase> nodeList) {
        this.nodeList = nodeList;
    }

    public void setSocketList(Iterable<? extends NodeSocket> socketList) {
        this.socketList = socketList;
    }

    public Iterable<? extends NodeBase> getNodeList() {
        return nodeList;
    }

    public Iterable<? extends NodeSocket> getSocketList() {
        return socketList;
    }

    public Iterable<Point> getNodeCompsLocations() {
        return nodeCompsLocations;
    }

    public void setNodeCompsLocations(Iterable<Point> nodeCompsLocations) {
        this.nodeCompsLocations = nodeCompsLocations;
    }
}