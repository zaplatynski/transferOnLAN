/*
 Copyright (C) 2011 Arnaud FRANÇOIS

 This file is part of Transfer on LAN (http://code.google.com/p/transfer-on-lan).

 Transfer on LAN is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Transfer on LAN is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Transfer on LAN.  If not, see <http://www.gnu.org/licenses/>. 
 */
package transfer_on_lan.gui;

import debug.D;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;
import transfer_on_lan.Constants;
import transfer_on_lan.Main;
import transfer_on_lan.User;
import static transfer_on_lan.Constants.ICON_48_48;
import static transfer_on_lan.Constants.ICON_256_256;
import static transfer_on_lan.Constants.NAME;
import static transfer_on_lan.Constants.VERSION;
import transfer_on_lan.ThreadTransferSending;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class Window extends JFrame {
    
    private Main main;
    private ResourceBundle strings;
    private int width;
    private int height;
    private JTabbedPane tabs;
    private JList lstUsers;
    private JList lstSendings;
    private JList lstReceptions;
    private PanPreferences panPreferences;
    private JEditorPane editor;
    private JButton btnQuit;
    private JPanel tabUsers;
    private JPanel tabSendings;
    private JPanel tabReceptions;
    private JPanel tabPreferences;
    private JPanel tabHelp;
    private JPanel tabAbout;
    private JPopupMenu ppmSend;
    private JPopupMenu ppmCancel;
    private JPopupMenu ppmRemoveSendings;
    private JPopupMenu ppmRemoveReceptions;
    private JMenuItem miSend;
    private JMenuItem miCancel;
    private JMenuItem miRemoveSendings;
    private JMenuItem miRemoveReceptions;
    private JMenuItem miCancelReceptions;
    private JMenuItem miOpenDirectory;
    private JTextField txtFilter;
    
    public Window(Main main, int width, int height) {
        super();
        this.main = main;
        this.strings = main.getStrings();
        this.width = width;
        this.height = height;
        initComponents();
        manageFilter();
        manageTabs();

        // mise en place de la gestion du "drop" sur la liste des utilisateurs
        this.lstUsers.setDropMode(DropMode.ON);
        this.lstUsers.setTransferHandler(new TransferHandlerDnD(main, this));

        // mise en place de l'icône de l'application
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image im = tk.getImage(getClass().getResource("/images/" + ICON_48_48));
        ImageIcon icon = new ImageIcon(im);
        setIconImage(icon.getImage());
    }
    
    private void initComponents() {
        this.btnQuit = new JButton();
        this.btnQuit.setAction(new ActionQuit(this.main));
        
        this.ppmSend = new JPopupMenu();
        this.miSend = new JMenuItem();
        this.miSend.setAction(new ActionSend(this.main));
        this.ppmSend.add(this.miSend);
        
        this.ppmCancel = new JPopupMenu();
        this.miCancel = new JMenuItem();
        this.miCancel.setAction(new ActionCancel(this.main));
        this.ppmCancel.add(this.miCancel);
        
        this.ppmRemoveSendings = new JPopupMenu();
        this.miRemoveSendings = new JMenuItem();
        this.miRemoveSendings.setAction(new ActionRemoveSendings(this.main));
        this.ppmRemoveSendings.add(this.miRemoveSendings);
        
        this.ppmRemoveReceptions = new JPopupMenu();
        this.miRemoveReceptions = new JMenuItem();
        this.miRemoveReceptions.setAction(new ActionRemoveReceptions(this.main));
        this.ppmRemoveReceptions.add(this.miRemoveReceptions);
        this.miOpenDirectory = new JMenuItem();
        this.miOpenDirectory.setAction(new ActionOpenDirectory(this.main));
        this.ppmRemoveReceptions.add(this.miOpenDirectory);
        this.miCancelReceptions = new JMenuItem();
        this.miCancelReceptions.setAction(new ActionCancelReceptions(this.main));
        this.ppmRemoveReceptions.add(this.miCancelReceptions);

        // mise en place de l'opération à effectuer en cas de fermeture
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (main.isSystemTray()) {
                    Window.this.setVisible(false);
                } else {
                    ActionQuit actionQuit = new ActionQuit(main);
                    actionQuit.actionPerformed(null);
                }
            }
        });

        // mise en page
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        
        this.tabs = new JTabbedPane();

        // *********************** tab Users ***********************        
        if(this.main.isShowFilter()) {
            this.lstUsers = new JList(main.getModelUserFiltered());
        }else {
            this.lstUsers = new JList(main.getModelUser());
        }
        lstUsers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    // double click on left button
                    // choix des fichiers à envoyer
                    JFileChooser.setDefaultLocale(main.getLocale());
                    JFileChooser choice = new JFileChooser(main.getSourceDirectoryPath());
                    
                    choice.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // permet de choisir des fichiers et des répertoires
                    choice.setMultiSelectionEnabled(true);
                    choice.setFileHidingEnabled(!main.isShowHiddenFiles());
                    
                    int response = choice.showOpenDialog(main.getWindow());
                    if (response == JFileChooser.APPROVE_OPTION) {
                        User user = (User) lstUsers.getSelectedValue();
                        ThreadTransferSending tts = new ThreadTransferSending(main, user, choice.getSelectedFiles());
                        main.getExecutor().execute(tts);
                        try {
                            main.setSourceDirectoryPath(choice.getCurrentDirectory().getCanonicalPath());
                        } catch (IOException ex) {
                            Logger.getLogger(NAME).logp(Level.WARNING, "ActionExpedier", "actionPerformed", "erreur entrée/sortie", ex);
                        }
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // click on right button
                    if (lstUsers.isSelectionEmpty()) {
                        // no selection
                        boolean end = false;
                        int x = 0;
                        while (x <= lstUsers.getModel().getSize() && !end) {
                            Rectangle r = lstUsers.getCellBounds(x, x);
                            if (r.contains(e.getPoint())) {
                                lstUsers.setSelectedIndex(x);
                                end = true;
                            }
                            x++;
                        }
                    }
                }
            }
        });
        
        CellRendererUser cellRendererUser = new CellRendererUser();
        cellRendererUser.setPreferredSize(new Dimension(180, 60));
        this.lstUsers.setCellRenderer(cellRendererUser);
        JScrollPane panScroll = new JScrollPane(this.lstUsers, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        /**
         * **************************************************************************
         */
        /*        
         tabUsers = new JPanel(false); // false => pas de double buffer        
         //tabUsers.setLayout(new BoxLayout(tabUsers, BoxLayout.PAGE_AXIS));
         tabUsers.setLayout(new GridBagLayout());
         /* ********** line 1 ********** */
        /*      this.txtFilter = new JTextField();
         this.txtFilter.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
         Window.this.main.getModelUser().setFilter(Window.this.txtFilter.getText());
         }
         });
         this.txtFilter.setVisible(!this.main.isShowFilter());
         this.txtFilter.setMinimumSize(new Dimension(50, 25));
         this.txtFilter.setPreferredSize(new Dimension(50, 25));

         //tabUsers.add(this.txtFilter);
         GridBagConstraints constraints = new GridBagConstraints();
         constraints.gridx = 0;
         constraints.gridy = 0;
         constraints.gridwidth = GridBagConstraints.REMAINDER;
         constraints.fill = GridBagConstraints.HORIZONTAL;
         constraints.weightx = 0;
         constraints.weighty = 1;
         //constraints.anchor = GridBagConstraints.LINE_START;
         tabUsers.add(this.txtFilter, constraints);
        
        
        
        
         /* ********** line 2 ********** */
        //tabUsers.add(panScroll);
        //this.tabs.add(this.strings.getString("Window.users"), panScroll);
/*constraints = new GridBagConstraints();
         constraints.gridx = 0;
         constraints.gridy = 1;
         constraints.gridwidth = GridBagConstraints.REMAINDER;
         constraints.gridheight = GridBagConstraints.REMAINDER;
         constraints.fill = GridBagConstraints.BOTH;
         constraints.weightx = 1;
         constraints.weighty = 1;
         //constraints.anchor = GridBagConstraints.LINE_START;
         tabUsers.add(panScroll, constraints);

        
        
         /*****************************************************************************/
        tabUsers = new JPanel(false); // false => pas de double buffer        
        tabUsers.setLayout(new BoxLayout(tabUsers, BoxLayout.PAGE_AXIS));

        /* ********** line 1 ********** */
        this.txtFilter = new JTextField(this.main.getFilter());
        this.txtFilter.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                //Window.this.main.getModelUser().setFilter(Window.this.txtFilter.getText());
                //Window.this.main.getModelUser().setFilter(Window.this.txtFilter.getText());
                Window.this.main.setFilter(Window.this.txtFilter.getText());
                Window.this.main.setFilter(Window.this.txtFilter.getText());
                manageFilter();
            }
        });
        this.txtFilter.setVisible(this.main.isShowFilter());
        //D.p(this.txtFilter.getPreferredSize());
        this.txtFilter.setMaximumSize(new Dimension(9999, this.txtFilter.getPreferredSize().height));
        tabUsers.add(this.txtFilter);
//tabUsers.add(Box.createHorizontalGlue());
//tabUsers.add(Box.createRigidArea(new Dimension(5,0)));
        /* ********** line 2 ********** */
        tabUsers.add(panScroll);

        // *********************** tab Sendings ***********************
        this.lstSendings = new JList(this.main.getModelSending());
        CellRendererSending cellRendererSending = new CellRendererSending(this.main.getLocale());
        cellRendererSending.setPreferredSize(new Dimension(180, 60));
        this.lstSendings.setCellRenderer(cellRendererSending);
        JScrollPane panScroll2 = new JScrollPane(this.lstSendings);
        
        MouseListener mouseListenerTabSendings = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    // affichage du popup 
                    //ppmCancel.show(me.getComponent(), me.getX(), me.getY());
                    ppmRemoveSendings.show(me.getComponent(), me.getX(), me.getY());
                    if (lstSendings.isSelectionEmpty()) {
                        boolean end = false;
                        int x = 0;
                        while (x <= lstSendings.getModel().getSize() && !end) {
                            if (lstSendings.isSelectionEmpty()) {
                                // no selection
                                Rectangle r = lstSendings.getCellBounds(x, x);
                                if (r.contains(me.getPoint())) {
                                    lstSendings.setSelectedIndex(x);
                                    end = true;
                                }
                                x++;
                            }
                        }
                    }
                }
            }
        };
        this.lstSendings.addMouseListener(mouseListenerTabSendings);
        
        this.tabSendings = new JPanel(false); // false => pas de double buffer
        this.tabSendings.setLayout(new BoxLayout(this.tabSendings, BoxLayout.PAGE_AXIS));
        this.tabSendings.add(panScroll2);
        //this.tabs.add(this.strings.getString("Window.sendings"), this.tabSendings);

        // *********************** tab Receptions ***********************
        this.lstReceptions = new JList(main.getModelReception());
        CellRendererReception cellRendererRecepetion = new CellRendererReception(this.main.getLocale());
        cellRendererRecepetion.setPreferredSize(new Dimension(180, 60));
        this.lstReceptions.setCellRenderer(cellRendererRecepetion);
        JScrollPane panScrollReception = new JScrollPane(this.lstReceptions);
        
        MouseListener mouseListenerTabRecepetions = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    // affichage du popup 

                    ppmRemoveReceptions.show(me.getComponent(), me.getX(), me.getY());
                    if (lstReceptions.isSelectionEmpty()) {
                        boolean end = false;
                        int x = 0;
                        while (x <= lstReceptions.getModel().getSize() && !end) {

                            // no selection
                            Rectangle r = lstReceptions.getCellBounds(x, x);
                            if (r.contains(me.getPoint())) {
                                lstReceptions.setSelectedIndex(x);
                                end = true;
                            }
                            x++;
                        }
                    }
                }
            }
        };
        this.lstReceptions.addMouseListener(mouseListenerTabRecepetions);
        
        this.tabReceptions = new JPanel(false); // false => pas de double buffer
        this.tabReceptions.setLayout(new BoxLayout(this.tabReceptions, BoxLayout.PAGE_AXIS));
        this.tabReceptions.add(panScrollReception);
        //this.tabs.add(this.strings.getString("Window.receptions"), this.tabReceptions);

        // *********************** tab Preferences ***********************
        this.panPreferences = new PanPreferences(this.main);
        //this.tabs.add(this.strings.getString("Window.preferences"), this.panPreferences);

        // *********************** tab Help ***********************
        tabHelp = new JPanel(false); // false => pas de double buffer
        tabHelp.setLayout(new BoxLayout(tabHelp, BoxLayout.PAGE_AXIS));
        
        this.editor = new JEditorPane();
        JScrollPane sp = new JScrollPane(this.editor);
        tabHelp.add(sp);
        
        try {
            this.editor.setEditorKit(new HTMLEditorKit());
            this.editor.setPage(getClass().getResource("/lang/help_" + this.main.getLocale().getLanguage() + ".html"));
            this.editor.setEditable(false);
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Fenetre", "initComposants", "erreur entrée/sortie", ex);
        }

        //this.tabs.add(this.strings.getString("Window.help"), tabHelp);
        // *********************** tab About ***********************
        tabAbout = new JPanel(false); // false => pas de double buffer
        tabAbout.setLayout(new GridBagLayout());

        /* ---------- lige 1 ---------- */
        JLabel labApplicationName = new JLabel(NAME + " " + VERSION);
        labApplicationName.setFont(labApplicationName.getFont().deriveFont(40.0f));
        labApplicationName.setFont(new Font("Serif", Font.BOLD, 26));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        tabAbout.add(labApplicationName, constraints);

        /* ---------- line 2 ---------- */
        JLabel labDescription = new JLabel(this.strings.getString("description"));
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        tabAbout.add(labDescription, constraints);

        /* ---------- line 3 ---------- */
        JLabel labBlank = new JLabel(" ");
        constraints.gridx = 0;
        constraints.gridy = 2;
        tabAbout.add(labBlank, constraints);

        /* ---------- line 4 ---------- */
        JLabel labAuthor = new JLabel("Copyright © 2011-2013 Arnaud FRANÇOIS");
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        tabAbout.add(labAuthor, constraints);

        /* ---------- line 5 ---------- */
        Link lkSite = new Link(this.strings.getString("hosted") + " Google Code", Constants.SITE);
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 4;
        tabAbout.add(lkSite, constraints);

        /* ---------- line 6 ---------- */
        JLabel labIcon = new JLabel();
        
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResource("/images/" + ICON_256_256));
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Fenetre", "initComposants", "problème avec l'icône de l'application", ex);
        }
        ImageIcon icon = new ImageIcon(image);
        labIcon.setIcon(icon);
        
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 5;
        tabAbout.add(labIcon, constraints);

        /* ----------  ---------- */
        /*int freeMemory = (new Float(Runtime.getRuntime().freeMemory() / 1024.0)).intValue();
         int totalMemory = (new Float(Runtime.getRuntime().totalMemory() / 1024.0)).intValue();
         Format format = new Format(this.main.getLocale(), this.chaines);
        
         JLabel labMemory = new JLabel("mémoire utilisée : " + format.formaterVolume(totalMemory - freeMemory));
         contraintes = new GridBagConstraints();
         contraintes.gridx = 0;
         contraintes.gridy = 4;
         ongletAPropos.add(labMemory, contraintes);
         */

        /* ---------- line 7 ---------- */
        Link lkLicense = new Link(this.strings.getString("license") + " : GNU GPL version 3", "http://gnu.org/licenses/gpl.html");
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 6;
        tabAbout.add(lkLicense, constraints);

        /* ---------- line 8 ---------- */
        JLabel labBlank2 = new JLabel(" ");
        constraints.gridx = 0;
        constraints.gridy = 7;
        tabAbout.add(labBlank2, constraints);

        /* ---------- line 9 ---------- */
        JLabel labThanks = new JLabel(this.strings.getString("thanksTo") + " :");
        constraints.gridx = 0;
        constraints.gridy = 8;
        tabAbout.add(labThanks, constraints);

        /* ---------- line 10 ---------- */
        JLabel labThanks1 = new JLabel("Eirik HAUSTVEIT (" + this.strings.getString("norwegianTranslation") + ")");
        constraints.gridx = 0;
        constraints.gridy = 9;
        tabAbout.add(labThanks1, constraints);
        
        JLabel labThanks2 = new JLabel("Matías HERRERA (" + this.strings.getString("spanishTranslation") + ")");
        constraints.gridx = 0;
        constraints.gridy = 10;
        tabAbout.add(labThanks2, constraints);

        /* ---------- line 11 ---------- */
        JLabel labThanks3 = new JLabel("Daniel KESSEL (" + this.strings.getString("germanTranslation") + ")");
        constraints.gridx = 0;
        constraints.gridy = 11;
        tabAbout.add(labThanks3, constraints);

        /* ---------- line 12 ---------- */
        JLabel labThanks4 = new JLabel("Marc VALLET (" + this.strings.getString("iconDesigner") + ")");
        constraints.gridx = 0;
        constraints.gridy = 12;
        tabAbout.add(labThanks4, constraints);

        //this.tabs.add(this.strings.getString("about"), tabAbout);
        getContentPane().add(this.tabs);
        
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    // affichage du popup s'il y a au moins un utilisateur sélectionné
                    if (!lstUsers.isSelectionEmpty()) {
                        ppmSend.show(me.getComponent(), me.getX(), me.getY());
                    }
                }
            }
        };
        
        this.lstUsers.addMouseListener(mouseListener);
        
        setTexts();
        setPreferredSize(new Dimension(this.width, this.height));
        pack();
    }
    
    public void setTexts() {
        this.strings = main.getStrings();

        /*this.tabs.setTitleAt(0, this.strings.getString("Window.users"));
         this.tabs.setTitleAt(1, this.strings.getString("Window.sendings"));
         this.tabs.setTitleAt(2, this.strings.getString("Window.receptions"));
         this.tabs.setTitleAt(3, this.strings.getString("Window.preferences"));
         this.tabs.setTitleAt(4, this.strings.getString("Window.help"));
         this.tabs.setTitleAt(5, this.strings.getString("about"));
         */
        this.txtFilter.setToolTipText(this.strings.getString("Window.filter"));
        this.miSend.setMnemonic(this.strings.getString("send.mnemonic").charAt(0));
        this.miSend.setText(this.strings.getString("send"));
        
        this.miCancel.setMnemonic(this.strings.getString("cancel.mnemonic").charAt(0));
        this.miCancel.setText(this.strings.getString("cancel"));
        
        this.miRemoveSendings.setMnemonic(this.strings.getString("removeSendings.mnemonic").charAt(0));
        this.miRemoveSendings.setText(this.strings.getString("removeSendings"));
        this.miOpenDirectory.setMnemonic(this.strings.getString("openFolder.mnemonic").charAt(0));
        this.miOpenDirectory.setText(this.strings.getString("openFolder"));
        
        this.miRemoveReceptions.setMnemonic(this.strings.getString("removeReceptions.mnemonic").charAt(0));
        this.miRemoveReceptions.setText(this.strings.getString("removeReceptions"));
        
        this.miCancelReceptions.setText(this.strings.getString("cancel"));
        this.miCancelReceptions.setMnemonic(this.strings.getString("cancel.mnemonic").charAt(0));
        
        this.btnQuit.setText(this.strings.getString("quit"));
        this.btnQuit.setMnemonic(this.strings.getString("quit.mnemonic").charAt(0));
        
        this.panPreferences.setTexts();
        
        try {
            this.editor.setPage(getClass().getResource("/lang/help_" + main.getLocale().getLanguage() + ".html"));
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Fenetre", "setTextes", "erreur entrée/sortie", ex);
        }
    }
    
    public void manageTabs() {
        this.tabs.removeAll();
        
        this.tabs.add(this.strings.getString("Window.users"), this.tabUsers);
        if (this.main.isShowTabSendings()) {
            this.tabs.add(this.strings.getString("Window.sendings"), this.tabSendings);
        }
        if (this.main.isShowTabReceptions()) {
            this.tabs.add(this.strings.getString("Window.receptions"), this.tabReceptions);
        }
        this.tabs.add(this.strings.getString("Window.preferences"), this.panPreferences);
        if (this.main.isShowTabHelp()) {
            this.tabs.add(this.strings.getString("Window.help"), tabHelp);
        }
        if (this.main.isShowTabAbout()) {
            this.tabs.add(this.strings.getString("about"), tabAbout);
        }
    }
    
    public void manageFilter() {
        if (this.main.isShowFilter()) {
            this.lstUsers.setModel(new ModelUserFiltered(this.txtFilter.getText(), this.main.getModelUser().getUsers()));
            this.txtFilter.setVisible(true);
        } else {
            this.lstUsers.setModel(this.main.getModelUser());
            this.txtFilter.setVisible(false);
        }
    }
    
    public JList getLstUsers() {
        return this.lstUsers;
    }
    
    public JList getLstSendings() {
        return this.lstSendings;
    }
    
    public JList getLstReceptions() {
        return this.lstReceptions;
    }
    
    public JPanel getTabPreferences() {
        return this.tabPreferences;
    }
    
    public JTextField getTxtFilter() {
        return this.txtFilter;
    }
}
