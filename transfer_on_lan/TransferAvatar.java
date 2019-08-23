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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static transfer_on_lan.Constants.CHUNK_SIZE;
import static transfer_on_lan.Constants.ICON_48_48;
import static transfer_on_lan.Constants.NAME;

/**
 * permet de transmettre le fichier représentant l'avatar à un utilisateur
 *
 * fonctionnement : - création d'un socket avec l'adresse du destinataire -
 * récupération du fichier contenant l'avatar (éventuellement, le fichier par
 * défaut contenu dans le jar est utilisé) - un message est envoyé au
 * destinataire pour lui préciser les modalités du transfert - le fichier est
 * envoyé par morceaux au destinataire
 *
 * @author Arnaud FRANCOIS
 */
public class TransferAvatar extends Thread {

    private Main main;
    private IPAddress recipientAddress;
    private int recipientPort;
    private Socket socket;
    private BufferedOutputStream out;

    public TransferAvatar(Main main, IPAddress recepientAddress, int recipientPort) {
        this.main = main;
        this.recipientAddress = recepientAddress;
        this.recipientPort = recipientPort;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(recipientAddress.getInet4Address(), recipientPort);
            out = new BufferedOutputStream(socket.getOutputStream());

            // on envoie au destinataire le fichier représentant l'avatar et du nombre d'octets à transférer
            // format message : encodage;identifiant;adresseIP;numeroPortTCP;nomUtilisateur;commande;nombreFichiers;tailleTransmission;<extension>;
            // calcul du nombre de fichiers et du nombre total d'octets à transemettre
            long totalSize = 0;
            int nbFiles = 1;

            // gérer extension du fichier
            File avatarFile = new File(main.AvatarFilePath());
            if (!main.isAvatarOn() || !avatarFile.canRead()) {
                // on utilise l'avatar par défaut
                BufferedImage image = ImageIO.read(getClass().getResource("/images/" + ICON_48_48));
                avatarFile = new File(main.getTmpDirectory() + File.separator + ICON_48_48);
                ImageIO.write(image, "png", avatarFile);
            }

            totalSize = avatarFile.length();
            String extension = "png";
            if (avatarFile.getAbsolutePath().endsWith(".jpeg") || avatarFile.getAbsolutePath().endsWith(".jpg")) {
                extension = "jpg";
            }

            // format message : encodage;identifiant;adresseIP;numeroPortTCP;nomUtilisateur;avatar;nombreFichiers;tailleTransmission;extension;
            String firstMessage = Charset.defaultCharset().name() + ";" + main.getUserId() + ";" + main.getIPAddress() + ";" + main.getPortTCP() + ";" + main.getUserName() + ";" + "avatar;" + nbFiles + ";" + totalSize + ";" + extension + ";";
            Message.send(out, firstMessage);

            long fileSize = avatarFile.length();
            long nbChunks = fileSize / CHUNK_SIZE;
            long remainder = fileSize % CHUNK_SIZE;

            // envoi du fichier par morceaux
            byte[] bytes = new byte[CHUNK_SIZE];
            BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(avatarFile));
            for (int i = 0; i < nbChunks; i++) {
                fileIn.read(bytes, 0, CHUNK_SIZE);
                Message.send(out, bytes);
            }

            // éventuellement un dernier morceau incomplet
            if (remainder != 0) {
                fileIn.read(bytes, 0, CHUNK_SIZE);
                Message.send(out, bytes);
            }
            out.flush();
            fileIn.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "TransfertAvatar", "run", "erreur avec les sockets", ex);
        }
    }
}
