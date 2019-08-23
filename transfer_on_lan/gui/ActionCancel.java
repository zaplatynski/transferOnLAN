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
import javax.swing.AbstractAction;
import transfer_on_lan.Main;
import transfer_on_lan.Sending;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ActionCancel extends AbstractAction {

    private Main main;

    public ActionCancel(Main main) {
        super();
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object[] objects = main.getWindow().getLstSendings().getSelectedValues();

        for (int i = 0; i < objects.length; i++) {
            ((Sending) objects[i]).cancel();
        }
    }
}
