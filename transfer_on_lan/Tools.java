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

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import static transfer_on_lan.Constants.NAME;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class Tools {

    /**
     * Recherche les adresses IP disponibles et les renvoie sous forme d'un
     * vecteur.
     *
     * @return
     */
    public static ArrayList<IPAddress> availableIPAddresses() {
        ArrayList<IPAddress> result = new ArrayList<IPAddress>();

        try {
            Enumeration<NetworkInterface> enumer = NetworkInterface.getNetworkInterfaces();
            // pour toutes les interfaces
            while (enumer.hasMoreElements()) {
                Enumeration<InetAddress> enumer2 = enumer.nextElement().getInetAddresses();
                // pour toutes les adresses IP de l'interface
                while (enumer2.hasMoreElements()) {
                    InetAddress ia = (InetAddress) enumer2.nextElement();
                    if (ia instanceof Inet4Address) {
                        result.add(new IPAddress((Inet4Address) ia));
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Outils", "adressesIPDisponibles", "erreur avec les sockets", ex);
        }
        return result;
    }

    /**
     * Efface le répertoire spécifié en paramètre.
     *
     * @param directory
     */
    public static void deleteDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        directory.delete();
    }

    /**
     *
     */
    public static String change(String string, String letterToReplace, String replacingLetter) {
        StringBuilder result = new StringBuilder(string);

        for (int i = 0; i < string.length(); i++) {
            if (string.substring(i, i + 1).compareTo(letterToReplace) == 0) {
                result.replace(i, i + 1, replacingLetter);
            }
        }

        return result.toString();
    }

    /**
     * Génère aléatoirement un identifiant en utilisant les caractères 0 à 9, A
     * à Z, a à z. Le nombre de caractères utilisés est "taille"
     *
     * @param size : la taille de l'indentifiant
     * @return : l'identifiant
     */
    public static String generateId(int size) {
        String result = "";

        String[] characters = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        for (int i = 0; i < size; i++) {
            int index = (int) (Math.random() * 62); // 0 <= index <= 61
            result += characters[index];
        }

        return result;
    }

    /**
     * Teste si une chaîne est contenue dans un tableau de chaînes.
     *
     * @param string : la chaîne recherchée
     * @param array : le tableau à explorer
     * @return :
     */
    public static boolean isContained(String string, String[] array) {
        boolean result = false;
        int i = 0;

        while (i < array.length && !result) {
            if (string.compareToIgnoreCase(array[i]) == 0) {
                result = true;
            }
            i++;
        }

        return result;
    }

    /**
     * Tente de créer un serveur de sockets avec le numéro de port fourni en
     * paramètre. Si la tentative échoue, on tente avec le numéro de port
     * suivant. Le nombre de tentatives est fourni en paramètre.
     *
     * @param port numéro de port.
     * @param nbEssays nombre de tentatives.
     * @return le serveur de sockets ou <code>null</code> si la tentative a
     * échoué.
     */
    public static ServerSocket createServerSocket(IPAddress ipAddress, int port, int nbEssays) {
        ServerSocket serverSocket = null;
        boolean found = false;

        while (!found && nbEssays >= 0) {
            try {
                found = true;
                serverSocket = new ServerSocket(port, 0, ipAddress.getInet4Address()); // 0 => backlog par défaut
            } catch (IOException ex) {
                found = false;
                port++;
                nbEssays--;
            }
        }
        return serverSocket;
    }
    
}
