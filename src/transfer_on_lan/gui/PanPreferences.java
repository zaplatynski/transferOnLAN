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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import transfer_on_lan.IPAddress;
import transfer_on_lan.Main;
import transfer_on_lan.Tools;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class PanPreferences extends JPanel {

    private Main main;
    private ResourceBundle strings;
    private JLabel labLanguage;
    private JLabel labLaF;
    private JLabel labIPAddress;
    private JLabel labUserName;
    private JLabel labDestinationDirectoryPath;
    private JLabel labPort;
    private JTextField txtUserName;
    private JTextField txtDestinationDirectoryPath;
    private JTextField txtAvatarFilePath;
    private JTextField txtSoundFilePath;
    private JTextField txtServerPort;
    private JComboBox cbLanguage;
    private JComboBox cbLaF;
    private JComboBox cbIPAddress;
    private JButton btnApply;
    private JButton btnDirectory;
    private JButton btnAvatar;
    private JButton btnSound;
    private JButton btnDefaultValues;
    private JButton btnRestoreValues;
    private JCheckBox chbPresence;
    private JCheckBox chbAvatarOn;
    private JCheckBox chbSoundOn;
    private JCheckBox chbAcceptanceAuto; // acceptation automatique des transferts
    private JCheckBox chbShowHiddenFiles;
    private JCheckBox chbInvisible;
    private JCheckBox chbSystemTray;
    private JCheckBox chbShowTabSendings;
    private JCheckBox chbShowTabReceptions;
    private JCheckBox chbShowTabHelp;
    private JCheckBox chbShowTabAbout;
    private JCheckBox chbShowFilter;
    private JSpinner spiServerPort;

    public PanPreferences(Main main) {
        super(false); // pas de double buffer

        this.main = main;
        initComponents();
    }

    private void initComponents() {
        this.strings = this.main.getStrings();
        setLayout(new GridBagLayout());

        /* ********** line 1 ********** */
        this.labLanguage = new JLabel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(this.labLanguage, constraints);

        String[] langues = {"Deutsch", "English", "Español", "Français", "Norsk"};
        this.cbLanguage = new JComboBox(langues);
        if (this.main.getLocale().getLanguage().equals("fr")) {
            this.cbLanguage.setSelectedItem("Français");
        } else if (this.main.getLocale().getLanguage().equals("en")) {
            this.cbLanguage.setSelectedItem("English");
        } else if (this.main.getLocale().getLanguage().equals("de")) {
            this.cbLanguage.setSelectedItem("Deutsch");
        } else if (this.main.getLocale().getLanguage().equals("es")) {
            this.cbLanguage.setSelectedItem("Español");
        } else if (this.main.getLocale().getLanguage().equals("no")) {
            this.cbLanguage.setSelectedItem("Norsk");
        }

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.cbLanguage, constraints);


        /* ********** line 2 ********** */
        this.labLaF = new JLabel();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(this.labLaF, constraints);


        String[] laF = {"Metal", "Nimbus", "System"};
        this.cbLaF = new JComboBox(laF);
        if (this.main.getAppearence().equals("metal")) {
            this.cbLaF.setSelectedItem("Metal");
        } else if (this.main.getAppearence().equals("nimbus")) {
            this.cbLaF.setSelectedItem("Nimbus");
        } else if (this.main.getAppearence().equals("system")) {
            this.cbLaF.setSelectedItem("System");
        }
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.cbLaF, constraints);

        /* ********** line 3 ********** */
        this.labIPAddress = new JLabel();
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(this.labIPAddress, constraints);

        ArrayList<IPAddress> ipAddresses = Tools.availableIPAddresses(); // récupération des adresses disponibles sur la machine
        // remove 127.0.0.1
        ipAddresses.remove(new IPAddress("127.0.0.1"));
        this.cbIPAddress = new JComboBox(ipAddresses.toArray());
        // sélection de l'adresse IP
        this.cbIPAddress.setSelectedItem(main.getIPAddress());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.cbIPAddress, constraints);

        /* ********** line 4 ********** */
        this.labUserName = new JLabel();
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(this.labUserName, constraints);

        this.txtUserName = new JTextField(main.getUserName());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.txtUserName, constraints);

        /* ********** line 5 ********** */
        this.labDestinationDirectoryPath = new JLabel();
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(this.labDestinationDirectoryPath, constraints);

        this.txtDestinationDirectoryPath = new JTextField(main.getDestinationDirectoryPath());
        this.txtDestinationDirectoryPath.setEditable(false);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.weightx = 1;
        add(this.txtDestinationDirectoryPath, constraints);

        this.btnDirectory = new JButton();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image im = tk.getImage(getClass().getResource("/images/folder_open.png"));
        ImageIcon folderIcon = new ImageIcon(im);
        this.btnDirectory.setIcon(folderIcon);
        this.btnDirectory.addActionListener(new ActionDirectory(this));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.btnDirectory, constraints);

        /* ********** line 6 ********** */
        this.chbAvatarOn = new JCheckBox();
        this.chbAvatarOn.setSelected(this.main.isAvatarOn());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 5;
        add(this.chbAvatarOn, constraints);

        this.txtAvatarFilePath = new JTextField(this.main.AvatarFilePath());
        this.txtAvatarFilePath.setEditable(false);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 5;
        add(this.txtAvatarFilePath, constraints);

        this.btnAvatar = new JButton();
        this.btnAvatar.setIcon(folderIcon);
        this.btnAvatar.addActionListener(new ActionAvatar(this));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 2;
        constraints.gridy = 5;
        add(this.btnAvatar, constraints);      
        
        /* ********** line 7 ********** */
        this.chbSoundOn = new JCheckBox();
        this.chbSoundOn.setSelected(this.main.isSoundOn());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 6;
        add(this.chbSoundOn, constraints);

        this.txtSoundFilePath = new JTextField();
        this.txtSoundFilePath.setEditable(false);
        this.txtSoundFilePath.setText(this.main.getSoundFilePath());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 6;
        add(this.txtSoundFilePath, constraints);

        this.btnSound = new JButton();
        this.btnSound.setIcon(folderIcon);
        this.btnSound.addActionListener(new ActionSound(this));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 2;
        constraints.gridy = 6;
        add(this.btnSound, constraints);

        /* ********** line 8 ********** */
        this.labPort = new JLabel();
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(this.labPort, constraints);

        //D.p("TCP"+this.main.getServerPortTCP());
        /*this.txtServerPort = new JTextField(this.main.getServerPortTCP() + "");
        this.txtServerPort.setEditable(true);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 7;
        add(this.txtServerPort, constraints);*/
        
        this.spiServerPort = new JSpinner(new SpinnerNumberModel(this.main.getServerPortTCP(), 0, 65535, 1));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 7;
        add(this.spiServerPort, constraints);
        
        /* ********** line 9 ********** */
        this.chbPresence = new JCheckBox();
        if (this.main.isPresenceInUserList()) {
            this.chbPresence.setSelected(true);
        }
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 8;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.chbPresence, constraints);

        /* ********** line 10 ********** */
        this.chbAcceptanceAuto = new JCheckBox();
        this.chbAcceptanceAuto.setSelected(this.main.isAcceptanceAutoOn());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 9;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.chbAcceptanceAuto, constraints);

        /* ********** line 11 ********** */
        this.chbShowHiddenFiles = new JCheckBox();
        this.chbShowHiddenFiles.setSelected(this.main.isShowHiddenFiles());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 10;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.chbShowHiddenFiles, constraints);

        /* ********** line 12 ********** */
        this.chbInvisible = new JCheckBox();
        this.chbInvisible.setSelected(this.main.isInvisible());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 11;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.chbInvisible, constraints);

        /* ********** line 13 ********** */
        this.chbSystemTray = new JCheckBox();
        this.chbSystemTray.setSelected(this.main.isSystemTray());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 12;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.chbSystemTray, constraints);

        /* ********** line 14 ********** */
        this.chbShowTabSendings = new JCheckBox();
        this.chbShowTabSendings.setSelected(this.main.isShowTabSendings());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 13;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.chbShowTabSendings, constraints);

        /* ********** line 15 ********** */
        this.chbShowTabReceptions = new JCheckBox();
        this.chbShowTabReceptions.setSelected(this.main.isShowTabReceptions());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 14;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.chbShowTabReceptions, constraints);

        /* ********** line 16 ********** */
        this.chbShowTabHelp = new JCheckBox();
        this.chbShowTabHelp.setSelected(this.main.isShowTabHelp());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 15;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.chbShowTabHelp, constraints);

        /* ********** line 17 ********** */
        this.chbShowTabAbout = new JCheckBox();
        this.chbShowTabAbout.setSelected(this.main.isShowTabAbout());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 16;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.chbShowTabAbout, constraints);

        /* ********** line 18 ********** */
        this.chbShowFilter = new JCheckBox();
        this.chbShowFilter.setSelected(this.main.isShowFilter());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 17;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.chbShowFilter, constraints);
        
        /* ********** line 19 ********** */
        this.btnDefaultValues = new JButton();
        this.btnDefaultValues.addActionListener(new ActionDefaultValues(this));
        constraints = new GridBagConstraints();
        //contraintes.fill = GridBagConstraints.NONE;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        //contraintes.anchor = GridBagConstraints.LINE_START;
        //contraintes.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 18;
        add(this.btnDefaultValues, constraints);

        this.btnRestoreValues = new JButton();
        this.btnRestoreValues.addActionListener(new ActionRestoreValues(this));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 18;
        add(this.btnRestoreValues, constraints);

        this.btnApply = new JButton();
        this.btnApply.addActionListener(new ActionApply(this));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 18;
        add(this.btnApply, constraints);
        
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        setTexts();
    }

    public void setTexts() {
        this.strings = main.getStrings();

        this.labLanguage.setText(this.strings.getString("PanPreferences.language") + " :");
        this.labLaF.setText(this.strings.getString("PanPreferences.appearance") + " :");
        this.labIPAddress.setText(this.strings.getString("PanPreferences.ipAddress") + " :");
        this.labUserName.setText(this.strings.getString("PanPreferences.userName") + " :");
        this.labDestinationDirectoryPath.setText(this.strings.getString("PanPreferences.destinationDirectory") + " :");
        this.labPort.setText(this.strings.getString("PanPreferences.portNumber") + " :");
        this.chbAvatarOn.setText(this.strings.getString("PanPreferences.avatar"));
        this.chbSoundOn.setText(this.strings.getString("PanPreferences.sound"));
        this.chbPresence.setText(this.strings.getString("PanPreferences.presence"));
        this.chbAcceptanceAuto.setText(this.strings.getString("PanPreferences.autoAccept"));
        this.chbShowHiddenFiles.setText(this.strings.getString("PanPreferences.showHiddenFiles"));
        this.chbInvisible.setText(this.strings.getString("PanPreferences.invisible"));
        this.chbSystemTray.setText(this.strings.getString("PanPreferences.systemTray"));
        this.chbShowTabSendings.setText(this.strings.getString("PanPreferences.showTabSendings"));
        this.chbShowTabReceptions.setText(this.strings.getString("PanPreferences.showTabReceptions"));
        this.chbShowTabHelp.setText(this.strings.getString("PanPreferences.showTabHelp"));
        this.chbShowTabAbout.setText(this.strings.getString("PanPreferences.showTabAbout"));
        this.chbShowFilter.setText(this.strings.getString("PanPreferences.showFilter"));
        this.btnDefaultValues.setText(this.strings.getString("PanPreferences.defaultValues"));
        this.btnDefaultValues.setMnemonic(this.strings.getString("PanPreferences.defaultValues.mnemonic").charAt(0));
        this.btnRestoreValues.setText(this.strings.getString("PanPreferences.restoreValues"));
        this.btnRestoreValues.setMnemonic(this.strings.getString("PanPreferences.restoreValues.mnemonic").charAt(0));
        this.btnApply.setText(this.strings.getString("apply"));
        this.btnApply.setMnemonic(this.strings.getString("apply.mnemonic").charAt(0));
    }

    public Main getMain() {
        return this.main;
    }

    public void setTxtDestinationDirectoryPath(String string) {
        this.txtDestinationDirectoryPath.setText(string);
    }

    public void setTxtAvatar(String string) {
        this.txtAvatarFilePath.setText(string);
    }

    public void setTxtSound(String string) {
        this.txtSoundFilePath.setText(string);
    }

    public void setSoundOn(boolean value) {
        this.chbSoundOn.setSelected(value);
    }

    public void setAvatarOn(boolean value) {
        this.chbAvatarOn.setSelected(value);
    }

    public void setUserName(String string) {
        this.txtUserName.setText(string);
    }

    public void setShowHiddenFilesOn(boolean value) {
        this.chbShowHiddenFiles.setSelected(value);
    }

    public void setInvisibleOn(boolean value) {
        this.chbInvisible.setSelected(value);
    }

    public void setPresenceOn(boolean value) {
        this.chbPresence.setSelected(value);
    }

    public void setSystemTrayOn(boolean value) {
        this.chbSystemTray.setSelected(value);
    }

    public void setAcceptanceAutoOn(boolean value) {
        this.chbAcceptanceAuto.setSelected(value);
    }

    public void setDestinationDirectoryPath(String string) {
        this.txtDestinationDirectoryPath.setText(string);
    }

    public void setLanguage(String string) {
        this.cbLanguage.setSelectedItem(string);
    }

    public void setAppearance(String string) {
        this.cbLaF.setSelectedItem(string);
    }

    public void setShowTabSendingsOn(boolean value) {
        this.chbShowTabSendings.setSelected(value);
    }

    public void setShowTabReceptionsOn(boolean value) {
        this.chbShowTabReceptions.setSelected(value);
    }

    public void setShowTabHelpOn(boolean value) {
        this.chbShowTabHelp.setSelected(value);
    }

    public void setShowTabAboutOn(boolean value) {
        this.chbShowTabAbout.setSelected(value);
    }

    public void setShowFilterOn(boolean value) {
        this.chbShowFilter.setSelected(value);
    }
    
    public JComboBox getCbIPAddress() {
        return cbIPAddress;
    }

    public JCheckBox getChbPresence() {
        return chbPresence;
    }

    public JComboBox getCbLanguage() {
        return cbLanguage;
    }

    public JTextField getTxtUserName() {
        return txtUserName;
    }

    public JComboBox getCbLaF() {
        return cbLaF;
    }

    public JCheckBox getChbAvatarOn() {
        return chbAvatarOn;
    }

    public JCheckBox getChbInvisible() {
        return chbInvisible;
    }

    public JCheckBox getChbAcceptanceAuto() {
        return chbAcceptanceAuto;
    }

    public JCheckBox getChbSystemTray() {
        return chbSystemTray;
    }

    public JCheckBox getChbSoundOn() {
        return chbSoundOn;
    }

    public JCheckBox getChbShowHiddenFiles() {
        return chbShowHiddenFiles;
    }

    public JTextField getTxtAvatarFilePath() {
        return txtAvatarFilePath;
    }

    public JTextField getTxtDestinationDirectoryPath() {
        return txtDestinationDirectoryPath;
    }

    public JTextField getTxtSoundFilePath() {
        return txtSoundFilePath;
    }

    public ResourceBundle getStrings() {
        return strings;
    }

    public JCheckBox getChbShowTabAbout() {
        return chbShowTabAbout;
    }

    public JCheckBox getChbShowTabSendings() {
        return chbShowTabSendings;
    }

    public JCheckBox getChbShowTabReceptions() {
        return chbShowTabReceptions;
    }

    public JCheckBox getChbShowTabHelp() {
        return chbShowTabHelp;
    }

    public JCheckBox getChbShowFilter() {
        return chbShowFilter;
    }

    public int getServerPort() {
        return (Integer)this.spiServerPort.getValue();
    }
    
    public void setServerPort(int portNumber) {
        this.spiServerPort.setValue(portNumber);
    }
    
}
