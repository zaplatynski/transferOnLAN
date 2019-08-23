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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import static transfer_on_lan.Constants.CHUNK_SIZE;
import static transfer_on_lan.Constants.NAME;
import static transfer_on_lan.Constants.PROGRESS_BAR_LENGTH;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class ThreadService extends Thread {

    private Main main;
    private Socket clientSocket;
    private Locale locale;
    private ResourceBundle strings;
    private Format format;
    private Reception reception;
    private boolean canceled;

    public ThreadService(Main main, Socket clientSocket) {
        this.main = main;
        this.clientSocket = clientSocket;
        this.locale = main.getLocale();
        this.strings = ResourceBundle.getBundle("lang/strings", locale);
        this.format = new Format(this.locale, this.strings);
        this.canceled = false;
    }

    @Override
    public void run() {
        Buffer buffer = new Buffer(clientSocket);
        try {
            BufferedOutputStream outputSocket = new BufferedOutputStream(clientSocket.getOutputStream());

            // réception du premier message
            // format message : encodage;identifiantUtilisateur;adresseIP;numeroPortTCP;nomUtilisateur;commande;nombreFichiers;tailleTransmission;
            byte[] data;
            data = buffer.read();
            String[] tab = buffer.decode(data);
            String userId = tab[1];
            String ipAddressSender = tab[2];
            int portTCP = Integer.decode(tab[3]);
            String senderName = tab[4];
            String order = tab[5];
            int nbFiles = Integer.decode(tab[6]);
            long transmissionLength = Long.decode(tab[7]);
            String extension = tab[8];

            if (order.compareTo("avatar") == 0) {
                //////////////////////////////////////////                 
                /////// réception du fichier avatar /////                
                ////////////////////////////////////////
                long nbChunks = transmissionLength / CHUNK_SIZE;
                long remainder = transmissionLength % CHUNK_SIZE;
                File avatarFile = new File(main.getTmpDirectory() + File.separator + ipAddressSender + "." + extension);
                // écriture du fichier
                BufferedOutputStream outputFile = new BufferedOutputStream(new FileOutputStream(avatarFile));

                for (int j = 0; j < nbChunks; j++) {
                    data = buffer.read();
                    outputFile.write(data, 0, CHUNK_SIZE);
                }

                // éventuellement un dernier morceau incomplet
                if (remainder != 0) {
                    data = buffer.read();
                    byte[] usefulData = Arrays.copyOfRange(data, 0, (int) remainder);

                    outputFile.write(usefulData, 0, (int) remainder);
                }

                outputFile.close();

                // il faut mettre à jour la liste des utilisateurs
                User user = main.getModelUser().getUser(userId);
                if (user == null) {
                    // l'utilisateur n'a pas été trouvé : un nouvel utilisateur est créé
                    user = new User(userId, senderName, new IPAddress(ipAddressSender), portTCP, null);
                }

                user.setAvatarFile(avatarFile);
                main.getModelUser().add(user);
            } else if (order.compareTo("demande_avatar") == 0) {
                ////////////////////////////////////
                //////// avatar file request //////
                //////////////////////////////////
                TransferAvatar transferAvatar = new TransferAvatar(main, new IPAddress(ipAddressSender), portTCP);
                main.getExecutor().execute(transferAvatar);
            } else {
                //////////////////////////////////
                //////// normal reception  //////
                ////////////////////////////////
                if (main.isSoundOn()) {
                    // l'utilisateur est prévenu en jouant un son
                    Sound sound = new Sound(main.getSoundFilePath());
                    sound.start();
                }

                reception = new Reception(main.getDestinationDirectoryPath());
                reception.setSenderName(senderName);
                main.getModelReception().add(reception);

                boolean transferAccepted = true;
                File destinationDirectory = new File(main.getDestinationDirectoryPath());
                String transmissionLengthString = this.format.formatVolume(transmissionLength);
                if (transmissionLength < destinationDirectory.getUsableSpace()) {
                    // there is enough space on the disk
                    if (!main.isAcceptanceAutoOn()) {
                        // a question is asked to the user
                        //String transmissionLengthString = this.format.formatVolume(transmissionLength);
                        int reponse = JOptionPane.showConfirmDialog(main.getWindow(), strings.getString("ThreadService.acceptTransfer.1") + " (" + transmissionLengthString + ") " + strings.getString("ThreadService.acceptTransfer.2") + " " + senderName + " (" + ipAddressSender + ") ?", strings.getString("ThreadService.transfer"), JOptionPane.YES_NO_OPTION);
                        if (reponse != JOptionPane.YES_OPTION) {
                            transferAccepted = false;
                        }
                    }
                } else {
                    // there is not enough space on the disk
                    transferAccepted = false; // the transfer is impossible
                    if (!main.isAcceptanceAutoOn()) {
                        // the user is alerted
                        //Format format = new Format(main.getLocale(), main.getStrings());
                        String usableSpace = this.format.formatVolume(destinationDirectory.getUsableSpace());
                        String message = senderName + " (" + ipAddressSender + ") " + this.strings.getString("impossibleTransfer1")
                                + " " + transmissionLengthString + ".\n" + this.strings.getString("impossibleTransfer2") + " " + usableSpace + " "
                                + this.strings.getString("impossibleTransfer3") + ".";
                        JOptionPane.showMessageDialog(null, message, this.strings.getString("Main.warning") + " !", JOptionPane.WARNING_MESSAGE);
                    }
                }

                if (transferAccepted) {
                    Chrono chrono = new Chrono();
                    chrono.start();

                    // on transmet la réponse positive au demandeur
                    String yes = Charset.defaultCharset().name() + ";oui;";
                    Message.send(outputSocket, yes);

                    long amountReceived = 0;
                    reception.setTotalDataVolume(transmissionLength);
                    // pour tous les fichiers/répertoires transmis
                    for (int i = 0; i < nbFiles; i++) {
                        // réception du message concernant le fichier/répertoire
                        // format message pour un fichier : <encodage>;fic;<nomFichier>;<tailleFichier>;
                        // format message pour un repertoire : <encodage>;rep;<nomRépertoire>;
                        data = buffer.read();
                        tab = buffer.decode(data);

                        order = tab[1];
                        if (order.compareTo("fic") == 0) {
                            // for a file
                            String fileName = tab[2];
                            long fileLength = Long.decode(tab[3]);
                            long nbChunks = fileLength / CHUNK_SIZE;
                            long remainder = fileLength % CHUNK_SIZE;

                            reception.addFileName(fileName);
                            // écriture du fichier
                            BufferedOutputStream outputFile = new BufferedOutputStream(new FileOutputStream(main.getDestinationDirectoryPath() + File.separator + fileName));

                            for (int j = 0; j < nbChunks; j++) {
                                if(reception.isCanceled()) {
                                    outputFile.close();
                                    
                                    // on transmet la réponse négtive au demandeur
                    String  annulation = Charset.defaultCharset().name() + ";annulation;\n";
                    reception.setState(Reception.CANCELED);
                    Message.send(outputSocket, annulation);
                    
                                    try {
                                        this.sleep(2000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(ThreadService.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                    outputSocket.close();                
                                    clientSocket.close();
                                    
                                    return; //arrêt du transfert
                                            //break;
                                }
                                data = buffer.read();
                                outputFile.write(data, 0, CHUNK_SIZE);

                                amountReceived += CHUNK_SIZE;
                                long percentage = Math.round(amountReceived / (double) transmissionLength * PROGRESS_BAR_LENGTH);

                                if (percentage > reception.getProgression()) {
                                    reception.setProgression((int) percentage);
                                    reception.setMessage(strings.getString("inProgress"));
                                    reception.setDataVolume(amountReceived);
                                    reception.setElapsedTime(chrono.getElapsedTime());

                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            main.getModelReception().add(reception);
                                        }
                                    });
                                }
                            }

                            // éventuellement un dernier morceau incomplet
                            if (remainder != 0) {
                                data = buffer.read();
                                byte[] usefulData = Arrays.copyOfRange(data, 0, (int) (remainder));
                                amountReceived += usefulData.length;
                                outputFile.write(usefulData, 0, (int) remainder);
                            }

                            outputFile.close();
                        } else {
                            // for a directory
                            String directoryName = tab[2];
                            File dir = new File(main.getDestinationDirectoryPath() + File.separator + directoryName);
                            dir.mkdir();
                        }
                    } // end for
                    
                    // transfer terminé
                    reception.setProgression(PROGRESS_BAR_LENGTH);
                    reception.setMessage(reception.getNumber() + " - " + strings.getString("transfer") + " : " + strings.getString("completed"));
                    reception.setState(Reception.DONE);
                    reception.setElapsedTime(chrono.getElapsedTime());
                    reception.setDataVolume(amountReceived);
                    reception.setDate(new Date());
                    this.main.getModelReception().add(this.reception);
                } else {
                    // on transmet la réponse négtive au demandeur
                    String no = Charset.defaultCharset().name() + ";non;";
                    reception.setState(Reception.REFUSED);
                    Message.send(outputSocket, no);
                }

                outputSocket.close();
            } // end normal reception
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Service", "run", "erreur lors d'un transfert", ex);
        }

    }
    
    public void cancel() {
        this.canceled = true;
    }
}
