package com.valhalla.application.gui;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.UUID;

public class NodeConnectionPoints {
    private UUID connector1UUID;
    private UUID connector2UUID;
    private Point2D connectorPoint1;
    private Point2D connectorPoint2;

    public NodeConnectionPoints(UUID connector1UUID,
                                UUID connector2UUID,
                                Point2D connectorPoint1,
                                Point2D connectorPoint2) {
        this.connector1UUID  = connector1UUID;
        this.connector2UUID  = connector2UUID;
        this.connectorPoint1 = connectorPoint1;
        this.connectorPoint2 = connectorPoint2;
    }

    public UUID getConnector1UUID() {
        return connector1UUID;
    }

    public UUID getConnector2UUID() {
        return connector2UUID;
    }

    public Point2D getConnectorPoint1() {
        return connectorPoint1;
    }

    public Point2D getConnectorPoint2() {
        return connectorPoint2;
    }

    public void setConnectorPoint1(Point2D connectorPoint1) {
        this.connectorPoint1 = connectorPoint1;
    }

    public void setConnectorPoint2(Point2D connectorPoint2) {
        this.connectorPoint2 = connectorPoint2;
    }
}
