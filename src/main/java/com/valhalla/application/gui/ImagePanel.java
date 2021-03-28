package com.valhalla.application.gui;

import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImagePanel extends JPanel{

    Image img;

    public ImagePanel(String filename) {
        try  {
            java.io.File f = new File(filename);
            URL url = getClass().getClassLoader().getResource(filename);
            InputStream is = new Utils().getFileFromResourceAsStream(filename);
            img  = ImageIO.read(is);
        } catch (Exception e)  {
            e.printStackTrace();
        }

        setLayout(new BorderLayout (5,5));
        JLabel lbl = new JLabel(new ImageIcon(img));
        add(lbl);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(img.getWidth(this), img.getHeight(this));
    }
}