package com.valhalla.application.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyleContext;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import net.miginfocom.swing.MigLayout;

public class ProjectSelectorFrame
        extends JFrame
{
    public ProjectSelectorFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    @Override
    public void dispose() {
        super.dispose();

    }

    private void exitActionPerformed() {
        dispose();
    }

    private void initComponents() {
        JMenuBar menuBar1 = new JMenuBar();
        JMenu fileMenu = new JMenu();
        JMenuItem newMenuItem = new JMenuItem();
        JMenuItem openMenuItem = new JMenuItem();
        JMenuItem saveAsMenuItem = new JMenuItem();
        JMenuItem closeMenuItem = new JMenuItem();
        JMenuItem exitMenuItem = new JMenuItem();

        JPanel appHeaderPanel = new JPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        ImagePanel logoImage;

        //======== this ========
        setTitle("Quester");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        {
            //======== fileMenu ========
            {
                fileMenu.setText("File");
                fileMenu.setMnemonic('F');

                //---- newMenuItem ----
                newMenuItem.setText("New");
                newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                newMenuItem.setMnemonic('N');
                //newMenuItem.addActionListener(e -> newActionPerformed());
                fileMenu.add(newMenuItem);

                //---- openMenuItem ----
                openMenuItem.setText("Open...");
                openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                openMenuItem.setMnemonic('O');
                //openMenuItem.addActionListener(e -> openActionPerformed());
                fileMenu.add(openMenuItem);

                //---- saveAsMenuItem ----
                saveAsMenuItem.setText("Save As...");
                saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                saveAsMenuItem.setMnemonic('S');
                //saveAsMenuItem.addActionListener(e -> saveAsActionPerformed());
                fileMenu.add(saveAsMenuItem);
                fileMenu.addSeparator();

                //---- closeMenuItem ----
                closeMenuItem.setText("Close");
                closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                closeMenuItem.setMnemonic('C');
                //closeMenuItem.addActionListener(e -> menuItemActionPerformed(e));
                fileMenu.add(closeMenuItem);
                fileMenu.addSeparator();

                //---- exitMenuItem ----
                exitMenuItem.setText("Exit");
                exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                exitMenuItem.setMnemonic('X');
                exitMenuItem.addActionListener(e -> exitActionPerformed());
                fileMenu.add(exitMenuItem);
            }
            menuBar1.add(fileMenu);
        }

        setJMenuBar(menuBar1);

        appHeaderPanel.setLayout(new MigLayout(
                "insets dialog,hidemode 3",
                "[grow,fill]",
                ""));

        logoImage = new ImagePanel("C:\\Users\\Percebe64\\Pictures\\Chico_Percebe.jpg", new Utils.Vector2D(75, 75));

        appHeaderPanel.add(logoImage, "cell 0 0");
        JLabel headerTitle = new JLabel();
        headerTitle.setText("Allo");
        appHeaderPanel.add(headerTitle, "cell 0 1");
        contentPane.add(appHeaderPanel, BorderLayout.CENTER);
    }
}