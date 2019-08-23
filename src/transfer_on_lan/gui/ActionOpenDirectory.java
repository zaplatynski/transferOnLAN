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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import transfer_on_lan.Main;
import transfer_on_lan.Reception;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ActionOpenDirectory extends AbstractAction {

    private Main main;
    private ResourceBundle strings;

    public ActionOpenDirectory(Main main) {
        super();
        this.main = main;
        this.strings = ResourceBundle.getBundle("lang/strings", main.getLocale());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Desktop.isDesktopSupported()) {
            if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                // récupération des utilisateurs sélectionnés
                Object[] objects = main.getWindow().getLstReceptions().getSelectedValues();
                // pour tous les réceptions sélectionnées
                for (int i = 0; i < objects.length; i++) {
                    Reception r = (Reception) objects[i];
                    try {
                        Desktop.getDesktop().browse((new File(r.getDirectory()).toURI()));
                    } catch (IOException ex) {
                        try {
                            // 2ème tentative
                            Runtime.getRuntime().exec("xdg-open " + r.getDirectory());
                        } catch (IOException ex1) {
                            Logger.getLogger(NAME).logp(Level.WARNING, "ActionOpenDirectory", "actionPerformed", "problème avec le répertoire", ex);
                            JOptionPane.showMessageDialog(null, strings.getString("messageFileManager"));
                        }
                    }
                }
            }
        }
    }
}
