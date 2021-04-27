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

    protected Object img;
    protected boolean useCustomSize;
    private Dimension customSize;

    public ImagePanel() { img = null; }

    public ImagePanel(String filename) {
        addImage(filename);
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

    public void addImage(String filename) {
        File file = new File(filename);
        System.out.println("filename = "+filename);
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(img != null)
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

    //@Override
    //public Dimension getPreferredSize() {
    //    if(img != null) {
    //        if(useCustomSize)
    //            return GetCustomSize();
    //        else
    //            return new Dimension(((Image) img).getWidth(this), ((Image) img).getHeight(this));
    //    }else {
    //        if(useCustomSize)
    //            return GetCustomSize();
    //        else
    //            return new Dimension(0, 0);
    //    }
    //}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(img != null) {
            if(!useCustomSize) {
                g.drawImage(
                        (Image) img,
                        0, 0,
                        ((Image) img).getWidth(this),
                        ((Image) img).getHeight(this),
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
}