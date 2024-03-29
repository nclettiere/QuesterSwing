package com.valhalla.application.gui;

import com.valhalla.NodeEditor.Editor.EditorData;
import com.valhalla.application.Quester;
import com.valhalla.NodeEditor.Primitive.DisplayImageComponent;
import com.valhalla.NodeEditor.Primitive.MiscComponent;
import com.valhalla.NodeEditor.Editor.NodeEditor;
import com.valhalla.NodeEditor.Primitive.SelectImageComponent;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ProjectSelectorFrame
    extends JFrame {

    protected Quester parent;
    protected NodeEditor nEditor;

    public ProjectSelectorFrame(Quester parent) {
        this.parent = parent;
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
        JMenu editMenu = new JMenu();
        JMenuItem newMenuItem = new JMenuItem();
        JMenuItem openMenuItem = new JMenuItem();
        JMenuItem saveAsMenuItem = new JMenuItem();
        JMenuItem closeMenuItem = new JMenuItem();
        JMenuItem exitMenuItem = new JMenuItem();

        JMenuItem openInspectorItem = new JMenuItem();

        JPanel appHeaderPanel = new JPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        ImagePanel logoImage;

        EditorData editorData = new EditorData("DialogueEditor");

        nEditor = new NodeEditor(new Class[]{
                SelectImageComponent.class,
                DisplayImageComponent.class,
                MiscComponent.class},
                editorData);

        //======== this ========
        setTitle("Quester");
        Container contentPane = getContentPane();
        //contentPane.setLayout(new BorderLayout());
        contentPane.setLayout(new MigLayout("", "0[grow]0", "0[grow]0"));

        {
            //======== fileMenu ========
            {
                fileMenu.setText("File");
                fileMenu.setMnemonic('F');

                editMenu.setText("Edit");
                editMenu.setMnemonic('E');

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
                openMenuItem.addActionListener(e -> nEditor.loadState());
                fileMenu.add(openMenuItem);

                //---- saveMenuItem ----
                JMenuItem saveMenuItem = new JMenuItem();
                saveMenuItem.setText("Save");
                saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                saveMenuItem.setMnemonic('S');
                saveMenuItem.addActionListener(e -> nEditor.saveState());
                fileMenu.add(saveMenuItem);
                fileMenu.addSeparator();

                //---- saveAsMenuItem ----
                saveAsMenuItem.setText("Save As...");
                saveAsMenuItem.setMnemonic('S');
                //saveAsMenuItem.addActionListener(e -> saveAsActionPerformed());
                fileMenu.add(saveAsMenuItem);
                fileMenu.addSeparator();

                //---- saveAsMenuItem ----
                openInspectorItem.setText("Open Inspector");
                openInspectorItem.setMnemonic('I');
                openInspectorItem.addActionListener(e -> openInspector());
                fileMenu.add(openInspectorItem);
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
            {
                JMenuItem setDebugMenuItem = new JMenuItem();
                setDebugMenuItem.setText("Toggle Editor Debug Mode");
                setDebugMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                setDebugMenuItem.setMnemonic('D');
                setDebugMenuItem.addActionListener(e -> setEditorDebug());
                editMenu.add(setDebugMenuItem);
                editMenu.addSeparator();
            }
            menuBar1.add(fileMenu);
            menuBar1.add(editMenu);
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

        //JPanel panel = new JPanel();
        ////BoxLayout lyt = new BoxLayout(panel, BoxLayout.Y_AXIS);
        //MigLayout lyt = new MigLayout("", "[grow][20][grow]", "[grow]");
        //panel.setLayout(lyt);
        //JPanel headerPanel = new JPanel(new MigLayout("","[shrink]", "[grow]"));
//
        //headerPanel.setBackground(new Color(0,0,0,30));
        //headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5,5,0));
//
        //ImagePanel img = new ImagePanel("img/dribbbleLogo.png");
        //img.setAlignmentX(Component.LEFT_ALIGNMENT);
        //img.setAlignmentY(Component.TOP_ALIGNMENT);
        //headerPanel.add(img, "west, shrink");
//
        //JLabel titleLabel = new JLabel("WELCOME TO QUESTER");
        //titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        //titleLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        //titleLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        //titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10,0,0));
        //Font ibmPlex = new Utils().getFontFromResource("fonts/Rubik-Bold.ttf");
        //if(ibmPlex != null) {
        //    titleLabel.setFont(ibmPlex.deriveFont(14f));
        //}
        //JLabel subtitleLabel = new JLabel("Select or Create Project");
        //subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10,0,0));
//
        //headerPanel.add(titleLabel, "growx, wrap");
        //headerPanel.add(subtitleLabel);
        //panel.add(headerPanel, "dock north, shrink 0, wrap 1");
//
        //JPanel panelButtons = new JPanel();
        //panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.Y_AXIS));
        //panelButtons.setBorder(new EmptyBorder(0,0,0,0));
//
        //SelectorButton btn = new SelectorButton("New Project", Utils.Icon.plus);
        //SelectorButton btn2 = new SelectorButton("Open Project", Utils.Icon.folder);
        //SelectorButton btn3 = new SelectorButton("Settings", Utils.Icon.settings);
//
        //btn.AddSelectorClickListener(() -> {
        //    NewProjectFrame newProject = new NewProjectFrame();
        //    //final JDialog frame = new JDialog(newProject, "New Project", true);
        //    //newProject.getContentPane().add(panel);
        //    this.setVisible(false);
        //    newProject.pack();
        //    newProject.setVisible(true);
        //});
//
        //panelButtons.add(btn);
        //panelButtons.add(btn2);
        //panelButtons.add(btn3);
//
        //panel.add(new JLabel(""));
        //panel.add(panelButtons, "w 200!, gaptop 20");
        //NodeProperty[] props = {new ImageProperty()};
        //panel.add(new ImageNode());
        //panel.add(new JTextPane());

        //contentPane.add(panel, BorderLayout.NORTH);

        //NodeEditorEx editor = new NodeEditorEx();
        //editor.SetDebugPaint(true);
        //contentPane.add(editor, "grow");

       //SelectImageComponent editor = new SelectImageComponent();
       //contentPane.add(editor);

        contentPane.add(nEditor, "grow");
    }

    public void openInspector() {
        parent.openNodeEditorInspector(nEditor);
    }

    public void setEditorDebug() {
        nEditor.setDebugging(!nEditor.isDebugging());
    }
}