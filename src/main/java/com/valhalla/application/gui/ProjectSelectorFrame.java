package com.valhalla.application.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
        //BoxLayout boxlayout = new BoxLayout(appHeaderPanel, BoxLayout.X_AXIS);
        //appHeaderPanel.setLayout(boxlayout);
//
        //appHeaderPanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
//
        //logoImage = new ImagePanel("C:\\Users\\Percebe32\\Pictures\\D6jEXm_XkAATUuF.jpg", new Utils.Vector2D(200,200));
        //JLabel headerTitle = new JLabel();
        //headerTitle.setText("Allo");
        //appHeaderPanel.add(headerTitle);
        //appHeaderPanel.add(logoImage);
        //this.setPreferredSize(logoImage.getPreferredSize());

        JPanel panel = new JPanel();
        BoxLayout lyt = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(lyt);

        panel.setBorder(BorderFactory.createEmptyBorder(40, 0,0,0));

        ImagePanel img = new ImagePanel("img/dribbbleLogo.png");
        img.setAlignmentX(Component.CENTER_ALIGNMENT);
        img.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.add(img);

        JLabel lbl = new JLabel("WELCOME TO QUESTER");
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setAlignmentY(Component.TOP_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 0,0,0));
        Font ibmPlex = new Utils().getFontFromResource("fonts/Rubik-Bold.ttf");
        if(ibmPlex != null) {
            lbl.setFont(ibmPlex.deriveFont(14f));
        }
        panel.add(lbl);

        JPanel panelButtons = new JPanel();
        BoxLayout lytbtn = new BoxLayout(panelButtons, BoxLayout.X_AXIS);
        panelButtons.setLayout(lytbtn);

        JButton btn = new JButton();
        btn.setSize(100, 100);
        panelButtons.add(btn);
        panel.add(panelButtons);

        contentPane.add(panel, BorderLayout.NORTH);
    }
}