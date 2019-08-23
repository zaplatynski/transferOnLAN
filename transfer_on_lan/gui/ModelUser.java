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
import java.util.Collections;
import javax.swing.AbstractListModel;
import transfer_on_lan.User;
import static transfer_on_lan.Constants.WAITING;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ModelUser extends AbstractListModel {

    private ArrayList<User> users;
    //private ArrayList<User> filteredUsers;
    //private boolean isFiltered;
    //private String filter;

    public ModelUser() {
        users = new ArrayList<User>();
        //filteredUsers = new ArrayList<User>();
        //isFiltered = true;
        //filter = "";
    }

    @Override
    public int getSize() {
        //if (isFiltered) {
        //    return filteredUsers.size();
        //} else {
            return users.size();
        //}
    }

    @Override
    public Object getElementAt(int index) {
        //if (isFiltered) {
        //    return filteredUsers.get(index);
        //} else {
            return users.get(index);
        //}
    }

    public void add(User user) {
        int index = users.indexOf(user);
        if (index != -1) {
            // l'utilisateur est déjà présent
            fireContentsChanged(this, index, index);
            //fireContentsChanged(this, 0, users.size() - 1);
        } else {
            // l'utilisateur n'est pas encore présent
            users.add(users.size(), user);
            fireIntervalAdded(this, users.size() - 1, users.size() - 1);
            Collections.sort(users);
            fireContentsChanged(this, 0, users.size() - 1);
        }
        //applyFilter();
    }

    public void remove(String id) {
        User user = new User(id);
        int index = users.indexOf(user);

        if (index != -1) {
            // l'utilisateur est bien présent
            users.remove(user);
            fireIntervalRemoved(this, index, index);            
        }
        //applyFilter();
    }

    public boolean isPresent(String id) {
        boolean result = false;
        int index = users.indexOf(new User(id));

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
        int index = users.indexOf(user);

        if (index != -1) {
            result = users.get(index);
        }

        return result;
    }

    /**
     * enlève les utilisateurs qui ne signalent plus leur présence
     */
    public void checkPresence() {
        for (int i = users.size() - 1; i >= 0; i--) {
            User user = users.get(i);
            if ((System.currentTimeMillis() - user.getLastContact()) > 2 * WAITING * 1000) {
                int index = users.indexOf(user);
                fireIntervalRemoved(this, index, index);
                users.remove(user);
            }
        }
    }

    public void sort() {
        Collections.sort(users);
        fireContentsChanged(this, 0, users.size() - 1);
    }
/*
    public void setFilter(String filter) {
        this.filter = filter;
        applyFilter();
        fireContentsChanged(this, 0, users.size() - 1);
    }*/
/*
    public void setFilterOn(boolean value) {
        isFiltered = value;
        if (isFiltered) {
            applyFilter();
        }
        fireContentsChanged(this, 0, users.size() - 1);
    }*/
/*
    public void applyFilter() {
        filteredUsers.clear();
        for (User user : users) {
            if (user.getIpAddress().toString().matches(".*" + filter + ".*")
                    || user.getName().matches(".*" + filter + ".*")) {
                filteredUsers.add(user);
            }
        }
    }*/
    
    public ArrayList<User> getUsers() {
        return users;
    }
}
