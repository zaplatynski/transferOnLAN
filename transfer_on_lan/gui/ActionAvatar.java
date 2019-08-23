/*
 Copyright (C) 2013 Arnaud FRANÇOIS

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
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ActionAvatar implements ActionListener {

    private PanPreferences pp;

    public ActionAvatar(PanPreferences pp) {
        this.pp = pp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser.setDefaultLocale(this.pp.getMain().getLocale());
        JFileChooser selectedFile = new JFileChooser();

        selectedFile.setFileSelectionMode(JFileChooser.FILES_ONLY); // permet de choisir uniquement des fichiers
        selectedFile.setMultiSelectionEnabled(false);

        FileFilter filtre = new FileNameExtensionFilter("Fichiers aux formats jpeg ou png", "jpg", "jpeg", "png");
        selectedFile.setFileFilter(filtre);

        int returnVal = selectedFile.showOpenDialog(this.pp.getMain().getWindow());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.pp.setTxtAvatar(selectedFile.getSelectedFile().getAbsolutePath());
        }
    }
}
