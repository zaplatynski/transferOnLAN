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

import java.util.ArrayList;
import java.util.Collections;
import javax.swing.AbstractListModel;
import static transfer_on_lan.Constants.WAITING;
import transfer_on_lan.User;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ModelUserFiltered extends AbstractListModel {

    private ArrayList<User> filteredUsers;
    private String filter;

    public ModelUserFiltered(String filter, ArrayList<User> users) {
        this.filter = filter;
        filteredUsers = new ArrayList<User>();
        for(User user : users) {
            if (user.getIpAddress().toString().matches(".*" + filter + ".*")
                    || user.getName().matches(".*" + filter + ".*")) {
                filteredUsers.add(user);
            }
        }
    }

    @Override
    public int getSize() {
        return filteredUsers.size();
    }

    @Override
    public Object getElementAt(int index) {
        return filteredUsers.get(index);
    }

    public void add(User user) {
        int index = filteredUsers.indexOf(user);
        if (index != -1) {
            // l'utilisateur est déjà présent
            fireContentsChanged(this, index, index);
        } else {
            // l'utilisateur n'est pas encore présent
            filteredUsers.add(filteredUsers.size(), user);
            fireIntervalAdded(this, filteredUsers.size() - 1, filteredUsers.size() - 1);
            Collections.sort(filteredUsers);
            fireContentsChanged(this, 0, filteredUsers.size() - 1);
        }
    }

    public void remove(String id) {
        User user = new User(id);
        int index = filteredUsers.indexOf(user);

        if (index != -1) {
            // l'utilisateur est bien présent
            filteredUsers.remove(user);
            fireIntervalRemoved(this, index, index);
        }
    }

    public boolean isPresent(String id) {
        boolean result = false;
        int index = filteredUsers.indexOf(new User(id));

        if (index != -1) {
            result = true;
        }

        return result;
    }

    /**
     * retourne un utilisateur à partir de son adresse IP
     */
    public User getUser(String id) {
        User result = null;

        User user = new User(id);
        int index = filteredUsers.indexOf(user);

        if (index != -1) {
            result = filteredUsers.get(index);
        }

        return result;
    }

    /**
     * enlève les utilisateurs qui ne signalent plus leur présence
     */
    public void checkPresence() {
        for (int i = filteredUsers.size() - 1; i >= 0; i--) {
            User user = filteredUsers.get(i);
            if ((System.currentTimeMillis() - user.getLastContact()) > 2 * WAITING * 1000) {
                int index = filteredUsers.indexOf(user);
                fireIntervalRemoved(this, index, index);
                filteredUsers.remove(user);
            }
        }
    }

    public void sort() {
        Collections.sort(filteredUsers);
        fireContentsChanged(this, 0, filteredUsers.size() - 1);
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

}
