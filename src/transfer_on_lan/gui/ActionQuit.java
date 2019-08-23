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
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import transfer_on_lan.Main;
import transfer_on_lan.Tools;
import static transfer_on_lan.Constants.NOTIFICATION_QUIT;
//import static transfer_on_lan.Constants.SERVER_PORT_UDP;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ActionQuit extends AbstractAction {

    private Main main;

    public ActionQuit(Main main) {
        super();
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // on prévient tout le monde que l'on quitte l'application

        byte[] sentData = new byte[100];
        // format du message : encodage;identifiant;commande;nomUtilisateur;adresseIP;numeroPortTCP;
        String message = Charset.defaultCharset().name() + ";" + this.main.getUserId() + ";" + NOTIFICATION_QUIT + ";" + this.main.getUserName() + ";" + this.main.getIPAddress() + ";" + this.main.getPortTCP() + ";";

        sentData = message.getBytes();
        try {
            DatagramPacket packet = new DatagramPacket(sentData, sentData.length, this.main.getGroup(), this.main.getServerPortUDP());

            this.main.getSocket().send(packet);
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "ActionQuitter", "actionPerformed", "erreur au niveau du socket", ex);
        }
        //this.main.saveProperties();
        this.main.savePrefs();
        
        // effacement du répertoire temporaire
        Tools.deleteDirectory(this.main.getTmpDirectory());
        Logger.getLogger(NAME).log(Level.INFO, "Arrêt");

        System.exit(0); // end of the application
    }
}
