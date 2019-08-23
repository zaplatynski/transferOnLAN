/*
 Copyright (C) 2013 Arnaud FRANÇOIS
  
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

import chronometer.Chrono;
import debug.D;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import static transfer_on_lan.Constants.CHUNK_SIZE;
import static transfer_on_lan.Constants.NAME;
import static transfer_on_lan.Constants.PROGRESS_BAR_LENGTH;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ThreadTransferSending extends Thread {

    static private int counter = 0;
    private int id;
    private Main main;
    private File[] files;
    private User user;
    private Socket socket;
    private BufferedOutputStream socketOut;
    private BufferedReader socketIn;
    private long totalSize;
    private long amountTransmitted;
    private Sending sending;
    private ResourceBundle strings;
    private Chrono chrono;

    public ThreadTransferSending(Main main, User user, File[] files) {
        counter++;
        this.id = counter;
        this.main = main;
        this.user = user;
        this.files = files;
        this.sending = new Sending(id + " - transfert : en attente d'expédition", user/*, this*/);
        this.main.getModelSending().add(this.sending);
        this.strings = main.getStrings();
    }

    @Override
    public void run() {
        try {
            this.socket = new Socket(this.user.getIpAddress().getInet4Address(), this.main.getPortTCP());
            this.socketOut = new BufferedOutputStream(this.socket.getOutputStream());
            Buffer buffer = new Buffer(this.socket);
            this.socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            // on prévient le destinataire du nombre de fichiers et du nombre d'octets à transférer
            // format message : encodage;identifiantUtilisateur;adresseIP;numeroPortTCP;nomUtilisateur;nombreFichiers;tailleTransmission;
            // calcul du nombre de fichiers et du nombre total d'octets à transemettre
            this.totalSize = 0;
            int nbFiles = 0;
            //for (int j = 0; j < this.files.length; j++) { // pour tous les fichiers/répertoires
            for (File file : files) {
                if (file.isDirectory()) {
                    // case of a directory
                    nbFiles += calculateNbFiles(file) + 1; // +1 : il faut prendre en compte le répertoire lui-même
                    this.totalSize += calculateSize(file);
                } else {
                    // case of a file
                    this.totalSize += file.length();
                    nbFiles++;
                }
            } // fin pour tous les fichiers/répertoires

            this.sending.setTotalDataVolume(this.totalSize);
            Message.send2(this.socketOut, this.main.getUserId(), "" + this.main.getIPAddress(), "" + this.main.getPortTCP(), this.main.getUserName(), "transfert", "" + nbFiles, "" + this.totalSize);
            // il faut attendre l'accord du destinataire
            byte[] data;
            data = buffer.read();
            String[] tab = buffer.decode(data);
            String response = tab[1];

            if (response.compareTo("oui") == 0) {
                // le transfert est accepté par le destinataire
                this.sending.setMessage(id + " - " + this.strings.getString("transfer") + " : " + this.strings.getString("pending"));
                this.main.getModelSending().add(this.sending);

                chrono = new Chrono();
                chrono.start();

                this.amountTransmitted = 0; // transformé en attribut
                // pour tous les fichiers
                for (File file : files) {
                    if (file.isDirectory()) {
                        // cas d'un répertoire
                        // format message : encodage;commande;nomRepertoire;
                        Message.send2(this.socketOut, "rep", file.getName());

                        explore(file.getAbsolutePath(), file.getName());
                    } else {
                        // cas d'un fichier

                        // écriture du message
                        long fileLength = file.length();
                        long nbChunks = fileLength / CHUNK_SIZE;
                        long remainder = fileLength % CHUNK_SIZE;

                        // format message : encodage;commande;nomFichier;tailleFichier;
                        Message.send2(this.socketOut, "fic", file.getName(), "" + fileLength);

                        this.sending.addFileNames(file.getName());

                        // envoi du fichier par morceaux
                        byte[] bytes = new byte[CHUNK_SIZE];
                        BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file));
                        for (int i = 0; i < nbChunks; i++) {
                            /*if (false) { // à revoir : cas l'annulation
                             // transfert terminé
                             this.socketOut.close();
                             this.socket.close();

                             this.sending.setMessage(this.id + " - " + this.strings.getString("transfer") + " : " + this.strings.getString("canceled"));
                             //this.sending.setEtat(Sending.TERMINE);
                             this.main.getModelSending().add(this.sending);

                             // il faut prévenir le destinataire 
                             // une erreur est générée côté destinataire
                             //return 0; // le traitement est arrêté 
                             }*/
                            if (this.socketIn.ready()) {
                                // le récpeteur a envoyé un message (pour l'instant ce ne peut être que pour l'annulation
                                // mais plus tard il faudra gérer le cas d'erreur
                                String reponse = this.socketIn.readLine();

                                // le récpeteur annule le transfert
                                this.sending.setState(Sending.CANCELED);
                                this.sending.setElapsedTime(chrono.getElapsedTime());
                                this.sending.setDataVolume(this.amountTransmitted);
                                this.main.getModelSending().add(this.sending);

                                this.socketOut.close();
                                this.socketIn.close();
                                this.socket.close();

                                fileIn.close();
                                return;

                            }

                            fileIn.read(bytes, 0, CHUNK_SIZE); // lecture dans le fichier source
                            Message.send(this.socketOut, bytes);
                            this.amountTransmitted += CHUNK_SIZE;
                            long percentage = Math.round(this.amountTransmitted / (double) this.totalSize * PROGRESS_BAR_LENGTH);
                            this.sending.setDataVolume(this.amountTransmitted);

                            if (percentage > this.sending.getProgression()) {
                                this.sending.setProgression((int) percentage);
                                this.sending.setMessage(this.id + " - " + this.strings.getString("transfer") + " : " + this.strings.getString("inProgress"));
                                this.sending.setElapsedTime(chrono.getElapsedTime());

                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        main.getModelSending().add(sending);
                                    }
                                });
                            }
                        }

                        // éventuellement un dernier morceau incomplet
                        if (remainder != 0) {
                            fileIn.read(bytes, 0, CHUNK_SIZE);
                            Message.send(this.socketOut, bytes);
                            this.amountTransmitted += remainder;
                        }
                        this.socketOut.flush();
                        fileIn.close();
                    }
                }

                // transfer done
                this.socketOut.close();
                this.socket.close();

                this.sending.setProgression(PROGRESS_BAR_LENGTH);
                this.sending.setDate(new Date());
                this.sending.setMessage(this.id + " - " + this.strings.getString("transfer") + " : " + this.strings.getString("completed"));
                this.sending.setState(Sending.DONE);
                this.sending.setElapsedTime(chrono.getElapsedTime());
                this.sending.setDataVolume(this.amountTransmitted);
                this.main.getModelSending().add(this.sending);
            } else {
                this.sending.setMessage(this.id + " - " + this.strings.getString("transfer") + " : " + this.strings.getString("refused"));
                this.sending.setState(Sending.REFUSED);
                this.main.getModelSending().add(this.sending);
            }
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "TransfertEmissionSwingWorker", "doInBackground", "erreur avec les sockets", ex);
            this.sending.setMessage(this.id + " - " + this.strings.getString("transfer") + " : " + this.strings.getString("error"));
            this.main.getModelSending().add(sending);
        }
    }

    /**
     *
     * @param currentDirectoryPath
     * @param relativeName
     */
    private void explore(String currentDirectoryPath, String relativeName) {
        // récupération de tous les fichiers et répertoires du répertoire courant
        File currentDirectory = new File(currentDirectoryPath);
        File[] filesInCurrentDirectory = currentDirectory.listFiles();

        for (File file : filesInCurrentDirectory) {
            if (file.isDirectory()) {
                // cas d'un répertoire
                String fileName = relativeName + File.separator + file.getName();
                // attention au séparateur de fichiers sous Windows
                Message.send2(this.socketOut, "rep", Tools.change(fileName, "\\", "/")); // les "\" propres à Windows sont changés en "/"

                explore(currentDirectoryPath + File.separator + file.getName(), relativeName + File.separator + file.getName());

            } else {
                // cas d'un fichier
                // écriture du message
                long fileSize = file.length();
                long nbChunks = fileSize / CHUNK_SIZE;
                long remainder = fileSize % CHUNK_SIZE;

                // format message : encodage;commande;nomFichier;tailleFichier;
                String fileName = relativeName + File.separator + file.getName();
                this.sending.addFileNames(file.getName());

                // attention au séparateur de fichiers sous Windows
                Message.send2(socketOut, "fic", Tools.change(fileName, "\\", "/"), "" + fileSize);

                // envoi du fichier par morceaux
                byte[] bytes = new byte[CHUNK_SIZE];
                try {
                    BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file));

                    for (int j = 0; j < nbChunks; j++) {
                        fileIn.read(bytes, 0, CHUNK_SIZE);
                        Message.send(socketOut, bytes);

                        amountTransmitted += CHUNK_SIZE;
                        long percentage = Math.round(amountTransmitted / (double) totalSize * PROGRESS_BAR_LENGTH);
                        this.sending.setDataVolume(this.amountTransmitted);
                        if (percentage > sending.getProgression()) {
                            sending.setProgression((int) percentage);
                            sending.setMessage(id + " - " + strings.getString("transfer") + " : " + strings.getString("inProgress"));
                            main.getModelSending().add(sending);

                            this.sending.setElapsedTime(chrono.getElapsedTime());

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    main.getModelSending().add(sending);
                                }
                            });
                        }
                    }
                    // éventuellement un dernier morceau incomplet
                    if (remainder != 0) {
                        fileIn.read(bytes, 0, CHUNK_SIZE);
                        Message.send(socketOut, bytes);
                    }
                    fileIn.close();

                    this.main.getModelSending().add(this.sending);
                } catch (IOException ex) {
                    Logger.getLogger(NAME).logp(Level.WARNING, "TransfertEmissionSwingWorker", "explorer", "erreur", ex);
                }
            }
        }
    }

    /**
     *
     * @param dir : le répertoire à examiner
     * @return : le nombre de fichiers contenus dans le répertoire
     */
    private int calculateNbFiles(File dir) {
        int result = 0;
        File[] files = dir.listFiles();

        //for (int i = 0; i < files.length; i++) {
        for (File file : files) {
            if (file.isDirectory()) {
                result += calculateNbFiles(file);
            }
            result++;
        }

        return result;
    }

    /**
     * Calcule la taille en octets d'un répertoire.
     *
     * @param dir : le répertoire à examiner
     * @return : la taille en octet du répertoire
     */
    private long calculateSize(File dir) {
        long result = 0;
        File[] files = dir.listFiles(); // renvoie la liste des fichiers du répertoire

        // pour tous les fichiers du répertoire
        //for (int i = 0; i < files.length; i++) {
        for (File file : files) {
            if (file.isDirectory()) {
                result += calculateSize(file);
            } else {
                result += file.length();
            }
        }

        return result;
    }
}
