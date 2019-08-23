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

import java.util.ArrayList;
import javax.swing.AbstractListModel;
import transfer_on_lan.Reception;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ModelReception extends AbstractListModel {

    private ArrayList<Reception> receptions;

    public ModelReception() {
        receptions = new ArrayList<Reception>();
    }

    @Override
    public int getSize() {
        return receptions.size();
    }

    @Override
    public Object getElementAt(int index) {
        return receptions.get(index);
    }

    public void add(Reception reception) {       
        int index = receptions.indexOf(reception);
        if (index != -1) {
            // la réception est déjà présente
            fireContentsChanged(this, index, index);
        } else {
            // la réception n'est pas encore présente
            receptions.add(receptions.size(), reception);
            fireIntervalAdded(this, receptions.size() - 1, receptions.size() - 1);
        }
    }

    public void remove(String id) {
        Reception reception = new Reception("");
        reception.setId(id);

        int index = receptions.indexOf(reception);

        if (index != -1) {
            // la réception est bien présente
            receptions.remove(reception);
            fireIntervalRemoved(this, index, index);
        }
    }
}
