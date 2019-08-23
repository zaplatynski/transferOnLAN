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
import transfer_on_lan.Sending;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ModelSending extends AbstractListModel {

    private ArrayList<Sending> sendings;

    public ModelSending() {
        sendings = new ArrayList<Sending>();
    }

    @Override
    public int getSize() {
        return sendings.size();
    }

    @Override
    public Object getElementAt(int index) {
        return sendings.get(index);
    }

    public void add(Sending sending) {
        int index = sendings.indexOf(sending);
        if (index != -1) {
            // l'expedition est déjà présente
            fireContentsChanged(this, index, index);
        } else {
            // l'expedition n'est pas encore présente
            sendings.add(sendings.size(), sending);
            fireIntervalAdded(this, sendings.size() - 1, sendings.size() - 1);
        }
    }

    public void remove(String id) {
        //Sending sending = new Sending("", null, null); // à vérifier
        Sending sending = new Sending("", null);
        sending.setId(id);

        int index = sendings.indexOf(sending);

        if (index != -1) {
            // l'expedition est bien présente
            sendings.remove(sending);
            fireIntervalRemoved(this, index, index);
        }
    }
}
