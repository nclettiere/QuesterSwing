/*
 * Created by JFormDesigner on Sat Mar 27 12:33:31 UYT 2021
 */

package com.valhalla.application.gui;

import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author unknown
 */
public class ProjectSelector extends JFrame {
    public ProjectSelector() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Nicolas
        panel1 = new JPanel();
        button1 = new JButton();
        label1 = new JLabel();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "fillx,hidemode 3,alignx center",
            // columns
            "[fill]" +
            "[fill]",
            // rows
            "[]" +
            "[]"));

        //======== panel1 ========
        {
            panel1.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(
            new javax.swing.border.EmptyBorder(0,0,0,0), "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn"
            ,javax.swing.border.TitledBorder.CENTER,javax.swing.border.TitledBorder.BOTTOM
            ,new java.awt.Font("Dia\u006cog",java.awt.Font.BOLD,12)
            ,java.awt.Color.red),panel1. getBorder()));panel1. addPropertyChangeListener(
            new java.beans.PropertyChangeListener(){@Override public void propertyChange(java.beans.PropertyChangeEvent e
            ){if("\u0062ord\u0065r".equals(e.getPropertyName()))throw new RuntimeException()
            ;}});
            panel1.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[]" +
                "[fill]",
                // rows
                "[]"));

            //---- button1 ----
            button1.setText("text");
            panel1.add(button1, "cell 0 0");

            //---- label1 ----
            label1.setText("text");
            panel1.add(label1, "cell 1 0");
        }
        contentPane.add(panel1, "cell 0 0 2 1");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Nicolas
    private JPanel panel1;
    private JButton button1;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
