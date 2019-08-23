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
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import transfer_on_lan.Main;
import static transfer_on_lan.Constants.NOTIFICATION_PRESENCE;
//import static transfer_on_lan.Constants.SERVER_PORT_UDP;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ActionNotificationPresenceEmission extends AbstractAction {

    private Main main;
    private MulticastSocket socket;
    private InetAddress group;

    public ActionNotificationPresenceEmission(Main main, MulticastSocket socket, InetAddress group) {
        super();

        this.main = main;
        this.socket = socket;
        this.group = group;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!this.main.isInvisible()) {
            try {
                byte[] sentData = new byte[100];
                // format message : encodage;identifiant;commande;nomUtilisateur;adresseIP;numeroPortTCP;nomMachine;
                String message = Charset.defaultCharset().name() + ";" + this.main.getUserId() + ";" + NOTIFICATION_PRESENCE + ";" + this.main.getUserName() + ";" + this.main.getIPAddress() + ";" + this.main.getPortTCP() + ";" + this.main.getMachineName() + ";";

                sentData = message.getBytes();
                DatagramPacket packet = new DatagramPacket(sentData, sentData.length, this.group, this.main.getServerPortUDP());
                this.socket.send(packet);

                this.main.getModelUser().checkPresence(); // on en profite pour éliminer les utilisateurs qui ne signalent plus leur présence
            } catch (IOException ex) {
                Logger.getLogger(NAME).logp(Level.WARNING, "ActionNotificationPresenceEmission", "actionPerformed", "erreur au niveau du socket", ex);
            }
        }
    }
}
