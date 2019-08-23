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
import java.util.List;
import javax.swing.AbstractAction;
import transfer_on_lan.Main;
import transfer_on_lan.Sending;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ActionRemoveSendings extends AbstractAction {

    private Main main;

    public ActionRemoveSendings(Main main) {
        super();
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // récupération des expéditions sélectionnés
        //Object[] objects = this.main.getWindow().getLstSendings().getSelectedValues();
        List<Sending> sendings = this.main.getWindow().getLstSendings().getSelectedValuesList();
        ModelSending ms = (ModelSending) this.main.getWindow().getLstSendings().getModel();
        // pour tous les expéditions sélectionnées
        //for (int i = objects.length - 1; i >= 0; i--) {
        for (Sending sending : sendings) {
            //Sending sending = (Sending) objects[i];
            if (sending.getState() != Sending.IN_PROGRESS) {
                ms.remove(sending.getId());
            }
        }
    }
}
