/*
   Copyright (C) 2011-2013 Arnaud FRANÇOIS
  
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
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import transfer_on_lan.Main;
import transfer_on_lan.Reception;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ActionCancelReceptions extends AbstractAction {

    private Main main;

    public ActionCancelReceptions(Main main) {
        super();
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<Reception> receptions = main.getWindow().getLstReceptions().getSelectedValuesList();
        for(Reception reception : receptions) {
            reception.cancel();            
        }
    }
}
