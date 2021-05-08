package com.valhalla.NodeEditor.Editor;

import org.piccolo2d.PCamera;
import org.piccolo2d.PCanvas;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;
import org.piccolo2d.event.PMouseWheelZoomEventHandler;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class NEditorMouseWheelZoomHandler extends PBasicInputEventHandler {
    /** Default scale factor, <code>0.1d</code>. */
    static final double DEFAULT_SCALE_FACTOR = 0.1d;

    /** Scale factor. */
    private double scaleFactor = DEFAULT_SCALE_FACTOR;

    private int minTicks = 10;
    private int currentMinTicks = 0;
    private int maxTicks = 10;
    private int currentMaxTicks = 0;

    private double currentZoom = 1.0d;

    /** Zoom mode. */
    private ZoomMode zoomMode = ZoomMode.ZOOM_ABOUT_CANVAS_CENTER;


    /**
     * Create a new mouse wheel zoom event handler.
     */
    public NEditorMouseWheelZoomHandler() {
        PInputEventFilter eventFilter = new PInputEventFilter();
        eventFilter.rejectAllEventTypes();
        eventFilter.setAcceptsMouseWheelRotated(true);
        eventFilter.setAcceptsMouseClicked(true);
        setEventFilter(eventFilter);
        zoomAboutMouse();
    }


    /**
     * Return the scale factor for this mouse wheel zoom event handler.  Defaults to <code>DEFAULT_SCALE_FACTOR</code>.
     *
     * @see #DEFAULT_SCALE_FACTOR
     * @return the scale factor for this mouse wheel zoom event handler
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Set the scale factor for this mouse wheel zoom event handler to <code>scaleFactor</code>.
     *
     * @param scaleFactor scale factor for this mouse wheel zoom event handler
     */
    public void setScaleFactor(final double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * Switch to zoom about mouse mode.
     *
     * @see ZoomMode#ZOOM_ABOUT_MOUSE
     */
    public void zoomAboutMouse() {
        zoomMode = ZoomMode.ZOOM_ABOUT_MOUSE;
    }

    /**
     * Switch to zoom about canvas center mode.
     *
     * @see ZoomMode#ZOOM_ABOUT_CANVAS_CENTER
     */
    public void zoomAboutCanvasCenter() {
        zoomMode = ZoomMode.ZOOM_ABOUT_CANVAS_CENTER;
    }

    /**
     * Switch to zoom about view center mode.
     *
     * @see ZoomMode#ZOOM_ABOUT_VIEW_CENTER
     */
    public void zoomAboutViewCenter() {
        zoomMode = ZoomMode.ZOOM_ABOUT_VIEW_CENTER;
    }

    /**
     * Return the zoom mode for this mouse wheel zoom event handler.  Defaults to
     * <code>ZoomMode.ZOOM_ABOUT_CANVAS_CENTER</code>.
     *
     * @return the zoom mode for this mouse wheel zoom event handler
     */
    ZoomMode getZoomMode() {
        return zoomMode;
    }

    /** {@inheritDoc} */
    public void mouseWheelRotated(final PInputEvent event) {
        PCamera camera = event.getCamera();
        double scale = 1.0d + event.getWheelRotation() * scaleFactor;

        if(event.getWheelRotation() > 0) {
            if(currentMaxTicks < maxTicks) {
                currentMaxTicks += 1;
                if(currentMinTicks > 0)
                    currentMinTicks -= 1;
            }else
                return;
        }else {
            if(currentMinTicks < minTicks) {
                currentMinTicks += 1;
                if (currentMaxTicks > 0)
                    currentMaxTicks -= 1;
            }else
                return;
        }

        Point2D viewAboutPoint = getViewAboutPoint(event);
        camera.scaleViewAboutPoint(scale, viewAboutPoint.getX(), viewAboutPoint.getY());
    }

    @Override
    public void mouseClicked(PInputEvent event) {
        super.mouseClicked(event);

        if(event.getButton() == MouseEvent.BUTTON2) {
            PCamera camera = event.getCamera();
            currentMaxTicks = 0;
            currentMinTicks = 0;
            camera.setViewScale(1.0d);
        }
    }

    /**
     * Return the view about point for the specified event according to the current zoom mode.
     *
     * @param event input event
     * @return the view about point for the specified event according to the current zoom mode
     */
    private Point2D getViewAboutPoint(final PInputEvent event) {
        switch (zoomMode) {
            case ZOOM_ABOUT_MOUSE:
                return event.getPosition();
            case ZOOM_ABOUT_CANVAS_CENTER:
                Rectangle canvasBounds = ((PCanvas) event.getComponent()).getBounds();
                Point2D canvasCenter = new Point2D.Double(canvasBounds.getCenterX(), canvasBounds.getCenterY());
                event.getPath().canvasToLocal(canvasCenter, event.getCamera());
                return event.getCamera().localToView(canvasCenter);
            case ZOOM_ABOUT_VIEW_CENTER:
                return event.getCamera().getBoundsReference().getCenter2D();
        }
        throw new IllegalArgumentException("illegal zoom mode " + zoomMode);
    }

    /**
     * Zoom mode.
     */
    enum ZoomMode {
        /**
         * Zoom about mouse mode.
         */
        ZOOM_ABOUT_MOUSE,

        /**
         * Zoom about canvas center mode.
         */
        ZOOM_ABOUT_CANVAS_CENTER,

        /**
         * Zoom about view center mode.
         */
        ZOOM_ABOUT_VIEW_CENTER;
    }
}
