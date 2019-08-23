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
import static transfer_on_lan.Constants.*;

/**
 *
 * @author arno
 */
public class ActionDefaultValues extends AbstractAction {

    private PanPreferences panPreferences;

    public ActionDefaultValues(PanPreferences panPreferences) {
        super();
        this.panPreferences = panPreferences;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*this.panPreferences.setUserName(System.getProperty("user.name", DV_USER_NAME));
        this.panPreferences.setDestinationDirectoryPath(System.getProperty("user.home"));
        this.panPreferences.setAvatarOn(Boolean.parseBoolean(DV_AVATAR_ON));
        this.panPreferences.setSoundOn(Boolean.parseBoolean(DV_SOUND_ON));
        this.panPreferences.setPresenceOn(Boolean.parseBoolean(DV_PRESENCE_IN_USER_LIST));
        this.panPreferences.setAcceptanceAutoOn(Boolean.parseBoolean(DV_ACCEPTANCE_AUTO_ON));
        this.panPreferences.setShowHiddenFilesOn(Boolean.parseBoolean(DV_SHOW_HIDDEN_FILES_ON));
        this.panPreferences.setInvisibleOn(Boolean.parseBoolean(DV_INVISIBLE_ON));
        this.panPreferences.setSystemTrayOn(Boolean.parseBoolean(DV_SYSTEM_TRAY_ON));
        this.panPreferences.setLanguage(DV_LANGUAGE);
        this.panPreferences.setAppearance(DV_APPEARANCE);
        */
        this.panPreferences.setUserName(System.getProperty("user.name", DV_USER_NAME));
        this.panPreferences.setDestinationDirectoryPath(System.getProperty("user.home"));
        this.panPreferences.setAvatarOn(DV_AVATAR_ON);
        this.panPreferences.setSoundOn(DV_SOUND_ON);
        this.panPreferences.setPresenceOn(DV_PRESENCE_IN_USER_LIST);
        this.panPreferences.setAcceptanceAutoOn(DV_ACCEPTANCE_AUTO_ON);
        this.panPreferences.setShowHiddenFilesOn(DV_SHOW_HIDDEN_FILES_ON);
        this.panPreferences.setInvisibleOn(DV_INVISIBLE_ON);
        this.panPreferences.setSystemTrayOn(DV_SYSTEM_TRAY_ON);
        this.panPreferences.setLanguage(DV_LANGUAGE);
        this.panPreferences.setAppearance(DV_APPEARANCE);
        this.panPreferences.setShowTabSendingsOn(DV_SHOW_TAB_SENDINGS_ON);
        this.panPreferences.setShowTabReceptionsOn(DV_SHOW_TAB_RECEPTIONS_ON);
        this.panPreferences.setShowTabHelpOn(DV_SHOW_TAB_HELP_ON);
        this.panPreferences.setShowTabAboutOn(DV_SHOW_TAB_ABOUT_ON);
        this.panPreferences.setShowFilterOn(DV_SHOW_FILTER_ON);
        this.panPreferences.setServerPort(DV_SERVER_PORT_TCP);
    }
}
