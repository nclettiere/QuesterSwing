package com.valhalla.application;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.util.SystemInfo;

public class Quester {
    public static void main(String[] args) {
        if( SystemInfo.isMacOS && System.getProperty( "apple.laf.useScreenMenuBar" ) == null )
            System.setProperty( "apple.laf.useScreenMenuBar", "true" );

        FlatDarculaLaf.install();

        JFrame f=new JFrame();//creating instance of JFrame

        JButton b=new JButton("click");//creating instance of JButton
        b.setBounds(130,100,100, 40);//x axis, y axis, width, height

        f.add(b);//adding button in JFrame

        f.setSize(400,500);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
    }
}
