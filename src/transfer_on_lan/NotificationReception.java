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

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static transfer_on_lan.Constants.ICON_48_48;
import static transfer_on_lan.Constants.NAME;
import static transfer_on_lan.Constants.NOTIFICATION_CHANGE_AVATAR;
import static transfer_on_lan.Constants.NOTIFICATION_CHANGE_NAME;
import static transfer_on_lan.Constants.NOTIFICATION_PRESENCE;
import static transfer_on_lan.Constants.NOTIFICATION_QUIT;
//import static transfer_on_lan.Constants.SERVER_PORT_UDP;

/**
 * Permet d'attendre l'arrivée de messages régulièrement émis par les clients
 * pour signaler leur présence ou d'autres situations.
 *
 * @author Arnaud FRANCOIS
 */
public class NotificationReception extends TimerTask {

    private Main main;
    private MulticastSocket socketMulticast;

    public NotificationReception(Main main, MulticastSocket socketMulticast) {
        this.main = main;
        this.socketMulticast = socketMulticast;
    }

    @Override
    public void run() {
        while (true) { // infinite loop

            try {
                byte[] receivedData = new byte[100];
                DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
                socketMulticast.receive(receivedPacket); // attend jusqu'au time out

                // prise en compte de l'encodage du message
                String receivedMessageBedoreDecoding = new String(receivedPacket.getData());
                StringTokenizer st = new StringTokenizer(receivedMessageBedoreDecoding, ";");
                String encoding = st.nextToken();

                String receivedMessage = new String(receivedPacket.getData(), Charset.forName(encoding));
                // format message : encodage;identifiant;commande;nomUtilisateur;adresseIP;numeroPortTCP;nomMachine;
                // interprétation du message reçu
                st = new StringTokenizer(receivedMessage, ";");
                st.nextToken(); // on saute l'encodage
                String id = st.nextToken();
                String order = st.nextToken();
                String userName = st.nextToken();
                IPAddress ipAddress = new IPAddress(st.nextToken());
                int portTCP = Integer.decode(st.nextToken());
                String machineName = st.nextToken();

                if (order.compareTo(NOTIFICATION_PRESENCE) == 0) {
                    /////////////////////////////////////                    
                    //////// présence utilisateur //////                    
                    ///////////////////////////////////                     
                    gererPresenceUtilisateur(id, order, userName, ipAddress, portTCP, machineName);
                } else if (order.compareTo(NOTIFICATION_QUIT) == 0) {
                    //////////////////////////////////////
                    //////// utilisateur à enlever //////
                    ////////////////////////////////////
                    main.getModelUser().remove(id);                    
                    main.updateModelUserFiltered();
                    main.getWindow().manageFilter();
                } else if (order.compareTo(NOTIFICATION_CHANGE_NAME) == 0) {
                    ///////////////////////////////////////////////////
                    //////// changement de nom d'un utilisateur //////
                    /////////////////////////////////////////////////
                    if (main.getModelUser().getUser(id) != null) {
                        main.getModelUser().getUser(id).setName(userName);
                        main.getModelUser().sort();
                    }
                } else if (order.compareTo(NOTIFICATION_CHANGE_AVATAR) == 0) {
                    ////////////////////////////////////////////////////////
                    //////// changement de l'avatar d'un utilisateur //////
                    //////////////////////////////////////////////////////
                    // envoi d'un message pour demander le transfert d'avatar
                    try {
                        Socket socket = new Socket(ipAddress.getInet4Address(), main.getPortTCP());
                        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

                        //format message : encodage;identifiant;adresseIP;numeroPortTCP;nomUtilisateur;commande;nombreFichiers;tailleTransmission;
                        String message = Charset.defaultCharset().name() + ";" + main.getUserId() + ";" + main.getIPAddress() + ";" + main.getPortTCP() + ";" + main.getUserName() + ";" + "demande_avatar;" + "0;0;";

                        Message.send(out, message);
                        socket.close(); // ???
                    } catch (IOException ex) {
                        Logger.getLogger(NAME).logp(Level.WARNING, "NotificationReception", "run", "erreur avec les sockets", ex);
                    }
                }
            } catch (SocketTimeoutException ex) {
                // le temps d'attente est dépassé
                Logger.getLogger(NAME).logp(Level.WARNING, "NotificationReception", "run", "erreur avec les sockets", ex);
            } catch (IOException ex) {
                Logger.getLogger(NAME).logp(Level.WARNING, "NotificationReception", "run", "erreur avec les sockets", ex);
            }
        }
    }

    /**
     *
     * @param identifiant
     * @param order
     * @param userName
     * @param ipAddress
     * @param portTCP
     * @param machineName
     */
    private void gererPresenceUtilisateur(String id, String order, String userName, IPAddress ipAddress, int portTCP, String machineName) {
        // utilisateur à ajouter (s'il n'est pas déjà présent dans le dictionnaire)
        // l'utilisateur est-il déjà présent dans la liste ? (s'il est déjà présent, on met à jour le moment de présence)
        if (!main.getModelUser().isPresent(id)) {
            // non, il n'est pas présent
            User user = null;

            if (id.compareTo(main.getUserId()) == 0) {
                // cas de sa propre adresse 
                if (main.isPresenceInUserList()) { // si nous avons choisi d'apparaître dans la liste des utilisateurs alors
                    user = new User(id, userName, ipAddress, portTCP, machineName);

                    // affectation du fichier avatar à l'utilisateur
                    File avatarFile = new File(main.AvatarFilePath());
                    if (!main.isAvatarOn() || !avatarFile.canRead()) { // si l'avatar doit être utilisé ou si le fichier n'est pas lisible
                        // on utilise l'avatar par défaut
                        try {
                            BufferedImage image = ImageIO.read(getClass().getResource("/images/" + ICON_48_48));
                            avatarFile = new File(main.getTmpDirectory() + File.separator + ICON_48_48);
                            ImageIO.write(image, "png", avatarFile);
                        } catch (IOException ex) {
                            Logger.getLogger(NAME).logp(Level.SEVERE, "NotificationReception", "gererPresenceUtilisateur", "erreur entrée/sortie", ex);
                        }
                    }

                    user.setAvatarFile(avatarFile);
                    main.getModelUser().add(user);
                    main.updateModelUserFiltered();
                    main.getWindow().manageFilter();
                }
            } else {
                // cas d'un utilisateur autre que nous-même
                user = new User(id, userName, ipAddress, portTCP, machineName);


                // émission d'un message pour signaler sa présence
                try {
                    byte[] sentData = new byte[100];
                    //  format message : encodage;identifiant;commande;nomUtilisateur;adresseIP;numeroPortTCP;nomMachine;
                    String message = Charset.defaultCharset().name() + ";" + main.getUserId() + ";" + NOTIFICATION_PRESENCE + ";" + main.getUserName() + ";" + main.getIPAddress()+ ";" + main.getPortTCP() + ";" + main.getMachineName() + ";";

                    sentData = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(sentData, sentData.length, main.getGroup(), main.getServerPortUDP());
                    socketMulticast.send(packet);
                } catch (IOException ex) {
                    Logger.getLogger(NAME).logp(Level.WARNING, "ActionNotificationPresenceEmission", "actionPerformed", "erreur au niveau du socket", ex);
                }

                // envoi d'un message pour demander le transfert d'avatar
                try {
                    Socket socket = new Socket(ipAddress.getInet4Address(), portTCP);

                    BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
                    //format message : encodage;identifiant;adresseIP;numeroPortTCP;nomUtilisateur;commande;nombreFichiers;tailleTransmission;
                    String message = Charset.defaultCharset().name() + ";" + main.getUserId() + ";" + main.getIPAddress() + ";" + main.getPortTCP() + ";" + main.getUserName() + ";" + "demande_avatar;" + "0;0;";

                    Message.send(out, message);
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(NAME).logp(Level.WARNING, "NotificationReception", "gererPresenceUtilisateur", "erreur entrée/sortie", ex);
                }

                main.getModelUser().add(user); // le nouvel utilisateur est ajouté à la liste des utilisateurs
                main.updateModelUserFiltered();
                main.getWindow().manageFilter();
            }
        } else {
            // mise à jour du moment
            User user = main.getModelUser().getUser(id);
            user.setLastContact(System.currentTimeMillis());
        }
    }
}
