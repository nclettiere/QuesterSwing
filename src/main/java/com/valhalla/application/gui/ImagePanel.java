package com.valhalla.application.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import static com.valhalla.application.gui.Utils.*;

public class ImagePanel extends JPanel{

    private BufferedImage image;
    private Vector2D size;

    public ImagePanel(String pathname) {
        try {
            image = ImageIO.read(new File(pathname));
        } catch (IOException ex) {
            image = null;
        }
    }

    public ImagePanel(String pathname, Vector2D size) {
        try {
            image = ImageIO.read(new File(pathname));
            this.size = size;
        } catch (IOException ex) {
            image = null;
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(image != null)
            g.drawImage(image, 0, 0, size.x, size.y, this); // see javadoc for more info on the parameters
    }
}