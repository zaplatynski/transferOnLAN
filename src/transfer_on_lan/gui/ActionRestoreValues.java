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

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ActionRestoreValues extends AbstractAction {

    private PanPreferences panPreferences;

    public ActionRestoreValues(PanPreferences panPreferences) {
        super();
        this.panPreferences = panPreferences;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.panPreferences.setUserName(this.panPreferences.getMain().getUserName());
        this.panPreferences.setDestinationDirectoryPath(this.panPreferences.getMain().getDestinationDirectoryPath());
        this.panPreferences.setAvatarOn(this.panPreferences.getMain().isAvatarOn());
        this.panPreferences.setSoundOn(this.panPreferences.getMain().isSoundOn());
        this.panPreferences.setPresenceOn(this.panPreferences.getMain().isPresenceInUserList());
        this.panPreferences.setAcceptanceAutoOn(this.panPreferences.getMain().isAcceptanceAutoOn());
        this.panPreferences.setShowHiddenFilesOn(this.panPreferences.getMain().isShowHiddenFiles());
        this.panPreferences.setInvisibleOn(this.panPreferences.getMain().isInvisible());
        this.panPreferences.setSystemTrayOn(this.panPreferences.getMain().isSystemTray());
        //this.panPreferences.setLanguage(this.panPreferences.getMain().getL);
        this.panPreferences.setAppearance(this.panPreferences.getMain().getAppearence());
        this.panPreferences.setShowTabSendingsOn(this.panPreferences.getMain().isShowTabSendings());
        this.panPreferences.setShowTabReceptionsOn(this.panPreferences.getMain().isShowTabReceptions());
        this.panPreferences.setShowTabHelpOn(this.panPreferences.getMain().isShowTabHelp());
        this.panPreferences.setShowTabAboutOn(this.panPreferences.getMain().isShowTabAbout());
        this.panPreferences.setShowFilterOn(this.panPreferences.getMain().isShowFilter());
    }
}
