package com.valhalla.application;

import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

class Box extends JPanel {
    ZoomPane zoomp;
    double scale = 1.0d;
    public Box(ZoomPane zoomp) {
        this.zoomp = zoomp;
        this.setLayout(new MigLayout("debug"));
        this.setSize(new Dimension(100,100));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        AffineTransform at = new AffineTransform();
        graphics.scale(scale, scale);
        graphics.transform(at);

        graphics.setColor(Color.BLUE);
        graphics.fillRect(0,0,100,100);

        super.paintComponent(g);
    }

    public void setScale(double p_newScale) {
        scale = p_newScale;
        int width = (int) (getWidth() * scale);
        int height = (int) (getHeight() * scale);
        setSize(new Dimension(width, height));
        repaint();
        revalidate();
    }

    public double getScale() {return scale;};
}

public class ZoomPane extends JPanel implements MouseWheelListener {

    Box box;

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();

        jFrame.add(new ZoomPane());

        jFrame.pack();
        jFrame.setSize(580,460);
        jFrame.setLocationRelativeTo( null );
        jFrame.setVisible( true );
    }

    public ZoomPane() {
        setLayout(null);
        setBackground(Color.GREEN);
        this.addMouseWheelListener(this);

        this.setPreferredSize(new Dimension(200,200));
        box = new Box(this);
        add(box);
    }

    @Override
    protected void paintChildren(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        super.paintChildren(g);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.getWheelRotation() == 1) {
            double sc = box.getScale() + 1.0;
            box.setScale(sc);

        }else {
            double sc = box.getScale() - 1.0;
            box.setScale(sc);
        }
    }
}

