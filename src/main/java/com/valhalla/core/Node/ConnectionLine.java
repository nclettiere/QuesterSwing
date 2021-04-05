package com.valhalla.core.Node;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.CubicCurve2D;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class ConnectionLine
    extends
    JComponent
    implements
    MouseMotionListener {

    protected Point p1Origin;
    protected Point p2Origin;

    private double sqrResult;

    public ConnectionLine(Point p1Origin, Point p2Origin) {
        this.p1Origin = p1Origin;
        this.p2Origin = p2Origin;

        this.addMouseMotionListener(this);

        this.setBackground(Color.CYAN);

        //this.setLayout(new MigLayout("debug"));
        //this.setLocation(p1Origin);
        this.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);

        //setSize();

       int op1 = p2Origin.x - p1Origin.x;
       double pw1 = pow(op1, 2);
       int op2 = p2Origin.y - p1Origin.y;
       double pw2 = pow(op2, 2);

       double sqrResult = sqrt(pw1 + pw2);
    }

    public void UpdatePoints(Point p1Origin, Point p2Origin) {
        this.p1Origin = p1Origin;
        this.p2Origin = p2Origin;

        repaint();
    }

    public void UpdatePoints(Point p2Origin) {
        this.p2Origin = p2Origin;

        //setLocation(p1Origin.x, p2Origin.y);

        //int op1 = p2Origin.x - p1Origin.x;
        //double pw1 = pow(op1, 2);
        //int op2 = p2Origin.y - p1Origin.y;
        //double pw2 = pow(op2, 2);
//
        //sqrResult = sqrt(pw1 + pw2);

        //int cat = pow(sqrResult, 2) - pow(op2, 2);

        //setSize((int)sqrResult, p2Origin.y);

        repaint();
    }

    public Point GetP1() {
        return p1Origin;
    }
    public Point GetP2() {
        return p2Origin;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setColor(Color.GREEN);
        graphics.setStroke(new BasicStroke(3.0f));
        // create new CubicCurve2D.Double
        //CubicCurve2D c = new CubicCurve2D.Double();
        ////// draw CubicCurve2D.Double with set coordinates
        //c.setCurve(
        //        0,
        //        0,
        //        p1Origin.x,
        //        10,
        //        p2Origin.x,
        //        10,
        //        p1Origin.x,
        //        p1Origin.y);
        //this.setSize(c.getBounds().width, c.getBounds().height);
        graphics.drawRect(0, 0, p2Origin.x, p1Origin.y);
        //graphics.draw(c);
        graphics.setColor(Color.RED);
        graphics.drawLine(0, 0, getMousePosition(true).x, getMousePosition(true).y);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("ASdsadasd");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println("1111111111111");
    }
}
