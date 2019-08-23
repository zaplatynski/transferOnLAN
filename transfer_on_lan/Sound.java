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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import static transfer_on_lan.Constants.NAME;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class Sound extends Thread {

    private String soundFilePath;

    public Sound(String soundFilePath) {
        this.soundFilePath = soundFilePath;
    }

    @Override
    public void run() {
        SourceDataLine line;
        AudioInputStream audioInputStream;
        AudioFormat audioFormat;

        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(soundFilePath));
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Son", "run", "problème avec le fichier son", ex);
            return;
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Son", "run", "erreur entrée/sortie", ex);
            return;
        }

        audioFormat = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        // On récupère le DataLine adéquat et on l'ouvre
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Son", "run", "erreur", ex);
            return;
        }

        // Avant toute chose il est nécessaire d'ouvrir la ligne
        try {
            line.open(audioFormat);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Son", "run", "erreur", ex);
            return;
        }
        // pour que le flux soit effectivement redirigé sur la carte son il
        // faut démarrer la ligne
        line.start();

        // il faut maintenant écrire sur la ligne. Travail comme sur un
        // inputStream quelconque
        try {
            byte bytes[] = new byte[1024];
            int bytesRead = 0;
            while (((bytesRead = audioInputStream.read(bytes, 0, bytes.length)) != -1)) {
                line.write(bytes, 0, bytesRead);
            }
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Son", "run", "problème au moment de l'écriture sur la ligne", ex);
            return;
        }
        // on ferme la ligne à la fin
        line.close();
    }
}
