package com.valhalla.application.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.util.concurrent.Flow;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SelectorButton
        extends JPanel
{
    JButton btn;
    boolean btnFocused;
    boolean btnHover;
    boolean btnPressed;
    Color color;

    public SelectorButton(String text, Color... tint) {
        btnFocused = false;

        GridLayout ly = new GridLayout(0, 1);
        this.setLayout(ly);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn = new JButton(text);
        btn.setLayout(new FlowLayout());
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);

        add(btn);

        color = (tint.length >= 1) ? tint[0] : new Color(19,159,253);
    }

    public SelectorButton(String text, Utils.Icon icon, Color... tint) {
        GridLayout ly = new GridLayout(0, 1);
        this.setLayout(ly);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon ico = new Utils().getIcon(icon);
        btn = new JButton(ico);
        btn.setLayout(new FlowLayout());
        btn.setText(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(5,5,5,5));
        //btn.setFocusPainted(false);
        //btn.setOpaque(true);
        color = (tint.length >= 1) ? tint[0] : new Color(19,159,253);

        btn.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
        SelectorButton sel = this;
        Dimension arcs = new Dimension(8, 8);
        btn.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                btnFocused = true;
            }
            @Override
            public void focusLost(FocusEvent e) {
                btnFocused = false;
            }
        });
        btn.getModel().addChangeListener(e -> {
            ButtonModel model = (ButtonModel) e.getSource();
                btnHover = model.isRollover();
                btnPressed = model.isPressed();
        });
        add(btn);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (btn != null) {
            if (btnFocused || btnHover) {
                Dimension arcs = new Dimension(10, 10); //Border corners arcs {width,height}, change this to whatever you want
                int width = btn.getWidth();
                int height = btn.getHeight();

                Graphics2D graphics = (Graphics2D) g;
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                //Draws the rounded panel with borders.

                Color generatedColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 30);
                if(btnPressed)
                    generatedColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 70);
                graphics.setColor(generatedColor);
                //btn.setBackground(generatedColor);
                graphics.fillRoundRect(btn.getBounds().x, btn.getBounds().y, width, height, arcs.width, arcs.height);//paint background
            }
        }
    }
}
