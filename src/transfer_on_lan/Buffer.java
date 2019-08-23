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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static transfer_on_lan.Constants.CHUNK_SIZE;
import static transfer_on_lan.Constants.NAME;

/**
 * permet de lire les données en réception
 * @author Arnaud FRANCOIS
 */
public class Buffer {

    private BufferedInputStream in;
    private byte[] data;
    private int position;

    /**
     * 
     * @param socket 
     */
    public Buffer(Socket socket) {
        try {
            in = new BufferedInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Buffer", "Buffer", "erreur avec les sockets", ex);
        }
        data = new byte[CHUNK_SIZE * 2];
        position = 0;
    }

    /**
     *
     * @return
     */
    public byte[] read() {
        byte[] result = null;
        try {
            int nbBytesRead = in.read(data, position, CHUNK_SIZE);
            position += nbBytesRead;
            // on lit tant qu'on a pas lu au moins TAILLE_MORCEAU octets
            while (position < CHUNK_SIZE) {
                nbBytesRead = in.read(data, position, CHUNK_SIZE);
                position += nbBytesRead;
            }
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Buffer", "Buffer", "erreur lecture socket", ex);
        }
        result = Arrays.copyOf(data, CHUNK_SIZE);

        if (position > CHUNK_SIZE) {
            byte[] bytes = Arrays.copyOfRange(data, CHUNK_SIZE, position);
            data = Arrays.copyOf(bytes, CHUNK_SIZE * 2);
        } else {
            data = new byte[CHUNK_SIZE * 2];
        }

        position -= CHUNK_SIZE;

        return result;
    }

    /**
     *
     * @param data
     * @return
     */
    public String[] decode(byte[] data) {
        String[] result = new String[30];
        String receivedMessageBeforeDecoding = new String(data);

        // récupération de l'encodage
        StringTokenizer st = new StringTokenizer(receivedMessageBeforeDecoding, ";");
        String encoding = st.nextToken();

        String receivedMessage = new String(data, Charset.forName(encoding));
        st = new StringTokenizer(receivedMessage, ";");
        int i = 0;
        while (st.hasMoreTokens()) {
            result[i++] = st.nextToken();
        }

        return result;
    }
}
