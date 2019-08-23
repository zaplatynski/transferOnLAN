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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import static transfer_on_lan.Constants.CHUNK_SIZE;
import static transfer_on_lan.Constants.NAME;
//import static transfer_on_lan.Constants.SERVER_PORT_UDP;

/**
 *
 * @author Arnaud FRANOIS
 */
public class Message {

    public static void sendToAll(MulticastSocket socket, InetAddress group, int portUDP, String message) {
        try {
            byte[] sentData = new byte[100];
            sentData = message.getBytes();
            DatagramPacket packet = new DatagramPacket(sentData, sentData.length, group, portUDP);
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Message", "envoyerATous", "erreur au niveau du socket", ex);
        }
    }

    /**
     * à tester et à revoir
     * @param recipient
     * @param message
     */
    public static void sendToAll(InetAddress recipient, int portUDP, String message) {
        try {
            DatagramSocket socket = new DatagramSocket(portUDP, recipient);
            byte[] sentData = new byte[100];
            sentData = message.getBytes();
            DatagramPacket packet = new DatagramPacket(sentData, sentData.length, recipient, portUDP);
            socket.send(packet);
        } catch (SocketException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Message", "envoyerATous", "erreur au niveau du socket", ex);
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Message", "envoyerATous", "erreur au niveau du socket", ex);
        }
    }

    public static void send(BufferedOutputStream out, String contents) {

        byte[] bytesToBeTransmitted = Arrays.copyOf(contents.getBytes(), CHUNK_SIZE);
        try {
            out.write(bytesToBeTransmitted, 0, bytesToBeTransmitted.length); // message envoyé
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Message", "envoyer", "erreur au moment de l'écriture sur un socket (" + contents + ")", ex);
        }
    }

    public static void send2(BufferedOutputStream out, String... parts) {
        String contents = Charset.defaultCharset().name() + ";"; // on spécifie l'encodage

        for (String part : parts) {
            contents += part + ";";
        }

        byte[] bytesToBeTransmitted = Arrays.copyOf(contents.getBytes(), CHUNK_SIZE);
        try {
            out.write(bytesToBeTransmitted, 0, bytesToBeTransmitted.length); // message envoyé
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Message", "envoyer2", "erreur au niveau du socket", ex);
        }
    }

    public static void send(BufferedOutputStream out, byte[] bytes) {
        // si il n'y a pas Main.TAILLE_MORCEAU octets, on complète pas des 0
        byte[] bytesToBeTransmitted = Arrays.copyOf(bytes, CHUNK_SIZE);
        try {
            out.write(bytesToBeTransmitted, 0, bytesToBeTransmitted.length); // message envoyé
            out.flush();
        } catch (IOException ex) {
            Object[] params = {out, bytes};
            // niveau, classe, méthode, message, exception
            Logger.getLogger(NAME).logp(Level.WARNING, "Message", "envoyer", "erreur au niveau du socket", ex);
        }
    }
}
