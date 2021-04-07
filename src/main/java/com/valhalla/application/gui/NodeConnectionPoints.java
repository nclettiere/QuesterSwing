package com.valhalla.application.gui;

import java.awt.*;
import java.util.UUID;

public class NodeConnectionPoints {
    private UUID nodeUUID1;
    private UUID nodeUUID2;
    private UUID dataUUID1;
    private UUID dataUUID2;
    private Point p1;
    private Point p2;

    public NodeConnectionPoints(UUID nodeUUID1, UUID nodeUUID2, UUID uuid1, Point p1, UUID uuid2, Point p2) {
        this.nodeUUID1 = nodeUUID1;
        this.nodeUUID2 = nodeUUID2;
        this.p1 = p1;
        this.dataUUID1 = uuid1;
        this.p2 = p2;
        this.dataUUID2 = uuid2;
    }

    public UUID GetNodeUUID1() {
        return nodeUUID1;
    }
    public UUID GetNodeUUID2() { return nodeUUID2; }

    public Point GetPoint1() {
        return p1;
    }

    public Point GetPoint2() {
        return p2;
    }

    public Point GetPointOfNode(UUID uuid) {
        if (uuid == this.nodeUUID1)
            return p1;
        else if (uuid == this.nodeUUID2)
            return p2;
        else
            return null;
    }

    public Point GetPointOfData(UUID uuid) {
        if (uuid == this.dataUUID1)
            return p1;
        else if (uuid == this.dataUUID2)
            return p2;
        else
            return null;
    }

    public UUID GetDataUUID1() {
        return this.dataUUID1;
    }

    public UUID GetDataUUID2() {
        return this.dataUUID2;
    }

    public void SetPoint1(Point p1) {
        this.p1 = p1;
    }
    public void SetPoint2(Point p2) { this.p2 = p2; }
}
