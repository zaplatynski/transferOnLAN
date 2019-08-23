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

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static transfer_on_lan.Constants.NAME;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class IPAddress {

    private Inet4Address inet4Address;
    private int byte1, byte2, byte3, byte4;

    public IPAddress(String string) {
        //StringTokenizer st = new StringTokenizer(string, "\\.");
        String[] strings = string.split("\\.");

        /*byte1 = Integer.decode(st.nextToken());
        byte2 = Integer.decode(st.nextToken());
        byte3 = Integer.decode(st.nextToken());
        byte4 = Integer.decode(st.nextToken());
        */
        byte1 = Integer.decode(strings[0]);
        byte2 = Integer.decode(strings[1]);
        byte3 = Integer.decode(strings[2]);
        byte4 = Integer.decode(strings[3]);
        
        try {
            inet4Address = (Inet4Address) Inet4Address.getByName(byte1 + "." + byte2 + "." + byte3 + "." + byte4);
        } catch (UnknownHostException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "IPAddress", "IPAddress", "problem with the IP address", ex);
        }
    }

    public IPAddress(Inet4Address inet4Address) {
        this.inet4Address = inet4Address;

        String[] strings = inet4Address.getHostAddress().split("\\.");
        byte1 = Integer.decode(strings[0]);
        byte2 = Integer.decode(strings[1]);
        byte3 = Integer.decode(strings[2]);
        byte4 = Integer.decode(strings[3]);
    }

    public Inet4Address getInet4Address() {
        return inet4Address;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj != null) {
            if (obj instanceof IPAddress) {
                IPAddress address = (IPAddress) obj;
                result = (byte1 == address.byte1)
                        && (byte2 == address.byte2)
                        && (byte3 == address.byte3)
                        && (byte4 == address.byte4);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        
        hash = 31 * hash + byte1;
        hash = 31 * hash + byte2;
        hash = 31 * hash + byte3;
        hash = 31 * hash + byte4;
                
        return hash;
    }
   
    @Override
    public String toString() {
        return byte1 + "." + byte2 + "." + byte3 + "." + byte4;
    }
}
