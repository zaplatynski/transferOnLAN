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

import java.util.logging.Logger;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class Constants {

    public static final String NAME = "Transfer on LAN";
    public static final String AUTHOR = "Arnaud FRANCOIS";
    public static final String VERSION = "0.6.1";
    public static final String SITE = "http://code.google.com/p/transfer-on-lan";
    public static final String DESCRIPTION = "Cross-plateform file transferring on LAN without configuration";
    //public static final int SERVER_PORT_UDP = 2011;
    //public static final int SERVER_PORT_TCP = 2011;
    public static final int WAITING = 20; // attente en secondes
    public static final int CHUNK_SIZE = 250;
    public static final String GROUP = "230.0.0.1";
    public static final Logger LOGGER = Logger.getLogger(NAME);
    public static final String LOG_FILE_NAME = "log.txt";
    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 650;

    public static final int AVATAR_WIDTH = 48;
    public static final int AVATAR_HEIGHT = 48;
    public static final int AVATAR_FILE_LENGTH_MAX = 100000;
    public static final String NOTIFICATION_PRESENCE = "presence";
    public static final String NOTIFICATION_QUIT = "quitter";
    public static final String NOTIFICATION_CHANGE_NAME = "changement_nom";
    public static final String NOTIFICATION_CHANGE_AVATAR = "changement_avatar";
    public static final int PROGRESS_BAR_LENGTH = 500;
    public static final String ICON_48_48 = "icon-mv-48_48.png";
    public static final String ICON_256_256 = "icon-mv-256_256.png";
    public static final boolean DV_SOUND_ON = false; // default value
    //public static final String DV_AVATAR_ON = "false"; // default value
    public static final boolean DV_AVATAR_ON = false; // default value
    public static final String DV_USER_NAME = "Anonymous"; // default value
    public static final boolean DV_SHOW_HIDDEN_FILES_ON = false; // default value
    public static final boolean DV_INVISIBLE_ON = false; // default value
    public static final boolean DV_PRESENCE_IN_USER_LIST = true; // default value
    public static final boolean DV_SYSTEM_TRAY_ON = false; // default value
    public static final boolean DV_ACCEPTANCE_AUTO_ON = false; // default value
    public static final String DV_LANGUAGE = "English"; // default value
    public static final String DV_APPEARANCE = "Nimbus"; // default value
    public static final boolean DV_SHOW_TAB_SENDINGS_ON = true; // default value
    public static final boolean DV_SHOW_TAB_RECEPTIONS_ON = true; // default value
    public static final boolean DV_SHOW_TAB_HELP_ON = true; // default value
    public static final boolean DV_SHOW_TAB_ABOUT_ON = true; // default value
    public static final boolean DV_SHOW_FILTER_ON = false; // default value
    public static final int DV_SERVER_PORT_UDP = 2011; // default value
    public static final int DV_SERVER_PORT_TCP = 2011; // default value
}
