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

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import transfer_on_lan.Constants;
import transfer_on_lan.IPAddress;
import transfer_on_lan.User;
import static transfer_on_lan.Constants.*;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ActionApply extends AbstractAction {

    private PanPreferences pp;

    public ActionApply(PanPreferences pp) {
        this.pp = pp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // set language
        if (this.pp.getCbLanguage().getSelectedItem().equals("Français")) {
            this.pp.getMain().setLocale(new Locale("fr"));
        } else if (this.pp.getCbLanguage().getSelectedItem().equals("English")) {
            this.pp.getMain().setLocale(new Locale("en"));
        } else if (this.pp.getCbLanguage().getSelectedItem().equals("Deutsch")) {
            this.pp.getMain().setLocale(new Locale("de"));
        } else if (this.pp.getCbLanguage().getSelectedItem().equals("Español")) {
            this.pp.getMain().setLocale(new Locale("es"));
        } else if (this.pp.getCbLanguage().getSelectedItem().equals("Norsk")) {
            this.pp.getMain().setLocale(new Locale("no"));
        }
        this.pp.getMain().setStrings(ResourceBundle.getBundle("lang/strings", this.pp.getMain().getLocale()));
        this.pp.getMain().getWindow().setTexts();
        this.pp.setTexts();

        // changement de la langue de toutes les récpetions
        
        // le nom a-t-il changé ?
        if (this.pp.getMain().getUserName().compareTo(this.pp.getTxtUserName().getText()) != 0) {
            // oui : on change le nom de l'utilisateur ...
            this.pp.getMain().setUserName(this.pp.getTxtUserName().getText());
            // ... et on prévient tout le monde du changement
            byte[] sentData = new byte[100];
            // format du message : encodage;identifiant;commande;nomUtilisateur;adresseIP;numeroPortTCP;
            String message = Charset.defaultCharset().name() + ";" + this.pp.getMain().getUserId() + ";" + NOTIFICATION_CHANGE_NAME + ";" + this.pp.getMain().getUserName() + ";" + this.pp.getMain().getIPAddress() + ";" + this.pp.getMain().getPortTCP() + ";";
            sentData = message.getBytes();
            try {
                DatagramPacket packet = new DatagramPacket(sentData, sentData.length, this.pp.getMain().getGroup(), this.pp.getMain().getServerPortUDP());
                this.pp.getMain().getSocket().send(packet);
            } catch (IOException ex) {
                Logger.getLogger(NAME).logp(Level.WARNING, "BtnAppliquerActionListener", "actionPerformed", "erreur entrée/sortie", ex);
            }
        }

        // gestion du changement d'avatar /////////////////////////////////

        // l'avatar par défaut n'est pas utilisé et le chemin du fichier avatar a changé
        this.pp.getMain().setAvatarOn(this.pp.getChbAvatarOn().isSelected());
        if (this.pp.getChbAvatarOn().isSelected() && (this.pp.getMain().AvatarFilePath().compareTo(this.pp.getTxtAvatarFilePath().getText()) != 0)) {
            // oui : on change l'avatar de l'utilisateur ...
            File file = new File(this.pp.getTxtAvatarFilePath().getText());
            if (file.canRead()) {
                if (file.length() <= Constants.AVATAR_FILE_LENGTH_MAX) {
                    this.pp.getMain().setAvatarFilePath(this.pp.getTxtAvatarFilePath().getText());
                } else {
                    JOptionPane.showMessageDialog(null, this.pp.getStrings().getString("fileTooLarge") + ".", this.pp.getStrings().getString("Main.warning") + " !", JOptionPane.WARNING_MESSAGE);
                }
            }

            // ... et on prévient tout le monde du changement
            byte[] sentData = new byte[100];
            // format du message : encodage;identifiant;commande;nomUtilisateur;adresseIP;numeroPortTCP;
            String message = Charset.defaultCharset().name() + ";" + this.pp.getMain().getUserId() + ";" + NOTIFICATION_CHANGE_AVATAR + ";" + this.pp.getMain().getUserName() + ";" + this.pp.getMain().getIPAddress() + ";" + this.pp.getMain().getPortTCP() + ";";
            sentData = message.getBytes();
            try {
                DatagramPacket packet = new DatagramPacket(sentData, sentData.length, this.pp.getMain().getGroup(), this.pp.getMain().getServerPortUDP());
                this.pp.getMain().getSocket().send(packet);
            } catch (IOException ex) {
                Logger.getLogger(NAME).logp(Level.WARNING, "BtnAppliquerActionListener", "actionPerformed", "erreur entrée/sortie", ex);
            }
        }
        // fin gestion du changement d'avatar /////////////////////////////

        this.pp.getMain().setDestinationDirectoryPath(this.pp.getTxtDestinationDirectoryPath().getText());
        this.pp.getMain().setSoundOn(this.pp.getChbSoundOn().isSelected());
        this.pp.getMain().setSoundFilePath(this.pp.getTxtSoundFilePath().getText());
        this.pp.getMain().setAcceptationAuto(this.pp.getChbAcceptanceAuto().isSelected());
        this.pp.getMain().setShowHiddenFiles(this.pp.getChbShowHiddenFiles().isSelected());
        this.pp.getMain().setInvisible(this.pp.getChbInvisible().isSelected());

        this.pp.getMain().setSystemTray(this.pp.getChbSystemTray().isSelected());
        if (this.pp.getMain().isSystemTray()) {
            this.pp.getMain().putInSystemTray();
        } else {
            this.pp.getMain().removeFromSystemTray();
        }

        // gestion de la présence de l'utilisateur dans la liste des utilisateurs
        if (this.pp.getMain().isPresenceInUserList()) {
            if (!this.pp.getChbPresence().isSelected()) {
                // il faut enlever l'utilisateur de la liste des utilisateurs
                this.pp.getMain().getModelUser().remove(this.pp.getMain().getUserId());
            }
        } else {
            // l'utilisateur n'est pas présent dans la liste
            if (this.pp.getChbPresence().isSelected()) {
                // maintenant il doit l'être
                try {
                    // il faut ajouter l'utilisateur de la liste des utilisateurs
                    User user = new User(this.pp.getMain().getUserId(), this.pp.getTxtUserName().getText(), this.pp.getMain().getIPAddress(), this.pp.getMain().getPortTCP(), null);
                    File avatarFile = new File(this.pp.getMain().AvatarFilePath());
                    if (!this.pp.getMain().isAvatarOn() || !avatarFile.canRead()) {
                        // on utilise l'avatar par défaut                        
                        BufferedImage image = ImageIO.read(getClass().getResource("/images/" + ICON_48_48));
                        avatarFile = new File(this.pp.getMain().getTmpDirectory() + File.separator + ICON_48_48);
                        ImageIO.write(image, "png", avatarFile);
                    }
                    user.setAvatarFile(avatarFile);
                    this.pp.getMain().getModelUser().add(user);
                } catch (IOException ex) {
                    Logger.getLogger(NAME).logp(Level.WARNING, "BtnAppliquerActionListener", "actionPerformed", "erreur entrée/sortie", ex);
                }
            }
        }
        this.pp.getMain().setPresenceInUserList(this.pp.getChbPresence().isSelected());

        try {
            SwingUtilities.updateComponentTreeUI(this.pp.getMain().getWindow());
            SwingUtilities.updateComponentTreeUI(this.pp);
        } catch (Exception ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "BtnAppliquerActionListener", "actionPerformed", "Problème avec le look & feel", ex);
        }


        IPAddress ipAddress = (IPAddress) this.pp.getCbIPAddress().getSelectedItem();
        if (!ipAddress.equals(this.pp.getMain().getIPAddress())) {
            this.pp.getMain().setIPAddress((IPAddress) this.pp.getCbIPAddress().getSelectedItem());
            JOptionPane.showMessageDialog(this.pp.getMain().getWindow(), this.pp.getMain().getStrings().getString("restart") + ".", this.pp.getMain().getStrings().getString("ipAddressChange"), JOptionPane.WARNING_MESSAGE);
        }

        if (this.pp.getCbLaF().getSelectedItem().equals("Metal")) {
            for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(laf.getName())) {
                    try {
                        UIManager.setLookAndFeel(laf.getClassName());
                        SwingUtilities.updateComponentTreeUI(this.pp.getMain().getWindow());
                        SwingUtilities.updateComponentTreeUI(this.pp);
                    } catch (Exception ex) {
                        Logger.getLogger(NAME).logp(Level.WARNING, "BtnAppliquerActionListener", "actionPerformed", "Problème avec le look & feel", ex);
                    }
                    this.pp.getMain().setAppearence("metal");
                }
            }
        } else if (this.pp.getCbLaF().getSelectedItem().equals("Nimbus")) {
            for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(laf.getName())) {
                    try {
                        UIManager.setLookAndFeel(laf.getClassName());
                        SwingUtilities.updateComponentTreeUI(this.pp.getMain().getWindow());
                        SwingUtilities.updateComponentTreeUI(this.pp);
                    } catch (Exception ex) {
                        Logger.getLogger(NAME).logp(Level.WARNING, "BtnAppliquerActionListener", "actionPerformed", "Problème avec le look & feel", ex);
                    }
                    this.pp.getMain().setAppearence("nimbus");
                }
            }
        } else if (this.pp.getCbLaF().getSelectedItem().equals("System")) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                SwingUtilities.updateComponentTreeUI(this.pp.getMain().getWindow());
                SwingUtilities.updateComponentTreeUI(this.pp);
            } catch (Exception ex) {
                Logger.getLogger(NAME).logp(Level.WARNING, "BtnAppliquerActionListener", "actionPerformed", "Problème avec le look & feel", ex);
            }

            this.pp.getMain().setAppearence("system");
        }
        
        
        if (this.pp.getServerPort() != this.pp.getMain().getServerPortTCP()) {
            //this.pp.getMain().setServerPortTCP(Integer.parseInt(this.pp.getTxtPort().getText()));
            //this.pp.getMain().setServerPortUDP(Integer.parseInt(this.pp.getTxtPort().getText()));
            this.pp.getMain().setServerPortTCP(this.pp.getServerPort());
            this.pp.getMain().setServerPortUDP(this.pp.getServerPort());
            //JOptionPane.showMessageDialog(this.pp.getMain().getWindow(), this.pp.getMain().getStrings().getString("restart") + ".", this.pp.getMain().getStrings().getString("ipAddressChange"), JOptionPane.WARNING_MESSAGE);
            JOptionPane.showMessageDialog(this.pp.getMain().getWindow(),
                    this.pp.getMain().getStrings().getString("portNumberChange2") + ".\n" +
                    this.pp.getMain().getStrings().getString("portNumberChange3") + " " + this.pp.getServerPort() + ".\n" +
                    this.pp.getMain().getStrings().getString("portNumberChange4") + ".\n" + 
                    this.pp.getMain().getStrings().getString("restart") + ".",
                    this.pp.getMain().getStrings().getString("portNumberChange1"),
                    JOptionPane.WARNING_MESSAGE);

            // if you use a firewall, perhaps you must modify his rules
            // Vous avez modifié le numéro de port. Vous ne pourrez communiquer qu'avec les utilsateurs qui utilisent le port x.
            //  Si vous utilisez un pare-feu, peut-être devrez-vous modifier ses règles.
        }

        this.pp.getMain().setShowFilter(this.pp.getChbShowFilter().isSelected());
        //this.pp.getMain().getModelUser().setFilterOn(this.pp.getChbShowFilter().isSelected());
        this.pp.getMain().updateModelUserFiltered();
        this.pp.getMain().getWindow().manageFilter();

        this.pp.getMain().setShowTabSendings(this.pp.getChbShowTabSendings().isSelected());
        this.pp.getMain().setShowTabReceptions(this.pp.getChbShowTabReceptions().isSelected());
        this.pp.getMain().setShowTabHelp(this.pp.getChbShowTabHelp().isSelected());
        this.pp.getMain().setShowTabAbout(this.pp.getChbShowTabAbout().isSelected());
        this.pp.getMain().getWindow().manageTabs();
    }
}
