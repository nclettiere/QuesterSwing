package com.valhalla.application.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImagePanel extends JPanel {

    protected BufferedImage img;
    protected boolean useCustomSize;
    private Dimension customSize;

    public ImagePanel() { img = null; }

    public ImagePanel(String filename) {
        addImage(filename, 0);
    }

    public void addImageFromResources(String filename) {
        try  {
            java.io.File f = new File(filename);
            URL url = getClass().getClassLoader().getResource(filename);
            InputStream is = new Utils().getFileFromResourceAsStream(filename);
            img  = ImageIO.read(is);
        } catch (Exception e)  {
            e.printStackTrace();
        }

        if(img != null)
            repaint();
    }

    public void addImage(String filename, double angle) {
        if (filename.isBlank() || filename.isEmpty()) {
            img = null;
            repaint();
            return;
        }

        File file = new File(filename);
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        img = rotate(img, angle);

        repaint();
    }

    public void SetCustomSize(Dimension customSize) {
        this.customSize = customSize;
        setSize(customSize);
        setPreferredSize(customSize);
        this.useCustomSize = true;
    }
    public Dimension GetCustomSize() {
        return this.customSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(img != null) {
            if(!useCustomSize) {
                g.drawImage(
                        img,
                        0, 0,
                        img.getWidth(this),
                        img.getHeight(this),
                        this);
            }else {
                setSize(GetCustomSize().width, GetCustomSize().height);
                g.drawImage(
                        (Image) img,
                        0, 0,
                        GetCustomSize().width,
                        GetCustomSize().height,
                        this);
            }
        }
    }

    public static BufferedImage rotate(BufferedImage image, double angle) {
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int)Math.floor(w*cos+h*sin), newh = (int) Math.floor(h * cos + w * sin);
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(neww, newh, Transparency.TRANSLUCENT);
        Graphics2D g = result.createGraphics();
        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(angle, w / 2, h / 2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return result;
    }

    private static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }
}