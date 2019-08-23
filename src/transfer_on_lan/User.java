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
package transfer_on_lan;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import static transfer_on_lan.Constants.AVATAR_HEIGHT;
import static transfer_on_lan.Constants.AVATAR_WIDTH;
import static transfer_on_lan.Constants.NAME;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class User implements Comparable<User> {

    private String id;
    private String name;
    private IPAddress ipAddress;
    private long lastContact;
    private File avatarFile;
    private ImageIcon icon;
    private int portTCP;
    private String machineName;

    public User(String id, String name, IPAddress ipAddress, int portTCP, String machineName) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.portTCP = portTCP;
        this.machineName = machineName;
        this.lastContact = System.currentTimeMillis();        
    }

    public User(String id, String name, String ipAddressString) {
        this.id = id;
        this.name = name;
        this.ipAddress = new IPAddress(ipAddressString);
        this.lastContact = System.currentTimeMillis();
    }

    @Override
    public int compareTo(User u) {
        return this.name.compareToIgnoreCase(u.name);
    }

    @Override
    public String toString() {
        return this.name + " on " + this.machineName + " (" + this.ipAddress + ":" + this.portTCP + ")";
    }

    public IPAddress getIpAddress() {
        return this.ipAddress;
    }

    public void setAvatarFile(File avatarFile) {
        this.avatarFile = avatarFile;

        try {
            FileInputStream ImageFile = new FileInputStream(avatarFile);

            BufferedImage photo = ImageIO.read(ImageFile);
            ImageFile.close();
            this.icon = new ImageIcon(photo.getScaledInstance(AVATAR_WIDTH, AVATAR_HEIGHT, Image.SCALE_DEFAULT));

        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Utilisateur", "setFichierAvatar", "problème fichier avatar", ex);
        }
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public ImageIcon getIcon() {
        return this.icon;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getMachineName() {
        return this.machineName;
    }

    public User(String id) {
        this.id = id;
    }

    public void setLastContact(long lastContact) {
        this.lastContact = lastContact;
    }

    public long getLastContact() {
        return this.lastContact;
    }

    /**
     * compare two users : two users are "equal" if their id are equal
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;

        if (o != null) {
            if (o instanceof User) {
                User user = (User) o;
                if (this.id.compareTo(user.id) == 0) {
                    result = true;
                }
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + (this.id == null ? 0 : this.id.hashCode());

        return hash;
    }
}
