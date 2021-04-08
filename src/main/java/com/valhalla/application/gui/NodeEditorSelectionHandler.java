package com.valhalla.application.gui;

import org.piccolo2d.PNode;
import org.piccolo2d.activities.PActivity;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.event.PSelectionEventHandler;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

import static java.awt.desktop.UserSessionEvent.Reason.LOCK;

public class NodeEditorSelectionHandler extends PSelectionEventHandler {

    protected NodeEditorEx nodeEditorEx;
    protected boolean connectorDragging;

    public NodeEditorSelectionHandler(PNode marqueeParent, PNode selectableParent) {
        super(marqueeParent, selectableParent);
    }

    public NodeEditorSelectionHandler(PNode marqueeParent, List selectableParents) {
        super(marqueeParent, selectableParents);
    }

    public NodeEditorSelectionHandler(PNode marqueeParent, PNode selectableParents, NodeEditorEx nodeEditorEx) {
        super(marqueeParent, selectableParents);
        this.nodeEditorEx = nodeEditorEx;
    }

    public void SetConnectorDragging(boolean connectorDragging) {
        this.connectorDragging = connectorDragging;
    }

    @Override
    public void select(PNode node) {
        super.select(node);
    }

    @Override
    public void decorateSelectedNode(PNode node) {
        //super.decorateSelectedNode(node);
    }

    @Override
    public void mouseClicked(PInputEvent event) {
        super.mouseClicked(event);
    }

    @Override
    protected void drag(PInputEvent event) {
        if(nodeEditorEx.draggingConnector == null)
            super.drag(event);
    }

    @Override
    public void setIsDragging(boolean isDragging) {
        if(nodeEditorEx.draggingConnector == null)
            super.setIsDragging(isDragging);
    }

    @Override
    protected void startDrag(PInputEvent e) {
        if(nodeEditorEx.draggingConnector == null)
            super.startDrag(e);
    }

    int count = 0;
    @Override
    protected void endDrag(PInputEvent event) {
        if(nodeEditorEx.draggingConnector == null)
            super.endDrag(event);

        count = 0;
    }

    @Override
    protected PActivity getDragActivity() {
        if(nodeEditorEx.draggingConnector != null)
            return null;
        return super.getDragActivity();
    }

    @Override
    protected void dragStandardSelection(PInputEvent e) {
        if(nodeEditorEx.draggingConnector == null)
            super.dragStandardSelection(e);
    }

    @Override
    protected void dragActivityStep(PInputEvent aEvent) {
        if(nodeEditorEx.draggingConnector == null)
            super.dragActivityStep(aEvent);
    }


    @Override
    protected boolean shouldStartDragInteraction(PInputEvent event) {
        synchronized (LOCK) {
            try {
                LOCK.wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Object '" + LOCK + "' is woken after" +
                    " waiting for 1 second");
        }

        if (nodeEditorEx.draggingConnector == null)
            return true;
        else
            return false;
    }
}
