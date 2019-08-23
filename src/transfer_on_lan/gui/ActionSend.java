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
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import transfer_on_lan.Main;
import transfer_on_lan.ThreadTransferSending;
import transfer_on_lan.User;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ActionSend extends AbstractAction {

    private Main main;

    public ActionSend(Main main) {
        super();
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // récupération des utilisateurs sélectionnés
        List<User> users = main.getWindow().getLstUsers().getSelectedValuesList();

        // choix des fichiers à envoyer
        JFileChooser.setDefaultLocale(main.getLocale());
        JFileChooser choice = new JFileChooser(main.getSourceDirectoryPath());

        choice.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // permet de choisir des fichiers et des répertoires
        choice.setMultiSelectionEnabled(true);
        choice.setFileHidingEnabled(!main.isShowHiddenFiles());

        int response = choice.showOpenDialog(main.getWindow());
        if (response == JFileChooser.APPROVE_OPTION) {
            for (User user : users) {
                ThreadTransferSending threadTransferSending = new ThreadTransferSending(main, user, choice.getSelectedFiles());
                main.getExecutor().execute(threadTransferSending);
            }

            try {
                main.setSourceDirectoryPath(choice.getCurrentDirectory().getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(NAME).logp(Level.WARNING, "ActionExpedier", "actionPerformed", "erreur entrée/sortie", ex);
            }
        }
    }
}
