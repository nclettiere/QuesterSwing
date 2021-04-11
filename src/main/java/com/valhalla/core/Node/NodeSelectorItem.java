package com.valhalla.core.Node;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

public class NodeSelectorItem extends JMenuItem implements MouseInputListener {
    private boolean hover;
    private boolean pressed;

    public NodeSelectorItem(String text, Icon icon) {
        super(text, icon);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setOpaque(false);
        this.setBorder(new EmptyBorder(5,15,5,0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if(hover) {
            if(pressed) {
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }else {
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        hover = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hover = false;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
