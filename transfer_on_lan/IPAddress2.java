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

/**
 *
 * @author Arnaud FRANCOIS
 */
public class IPAddress2 {

    private Inet4Address inet4Address;
    private short byte1, byte2, byte3, byte4;
    private short maskLength, maskByte1, maskByte2, maskByte3, maskByte4;

    public IPAddress2(String string) {
        String[] strings = string.split(".");
        byte1 = Short.decode(strings[0]);
        byte2 = Short.decode(strings[1]);
        byte3 = Short.decode(strings[2]);
        byte4 = Short.decode(strings[3]);
        
        try {
            inet4Address = (Inet4Address) Inet4Address.getByName(byte1 + "." + byte2 + "." + byte3 + "." + byte4);
        } catch (UnknownHostException ex) {
            //Logger.getLogger(NAME).logp(Level.WARNING, "IPAddress", "IPAddress", "problem with the IP address", ex);
            System.err.println("problem with the IP address");
        }
    }

    public IPAddress2(Inet4Address inet4Address) {
        this.inet4Address = inet4Address;

        String[] strings = inet4Address.getHostAddress().split(".");
        byte1 = Short.decode(strings[0]);
        byte2 = Short.decode(strings[1]);
        byte3 = Short.decode(strings[2]);
        byte4 = Short.decode(strings[3]);
    }

    public Inet4Address getInet4Address() {
        return inet4Address;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj != null) {
            if (obj instanceof IPAddress2) {
                IPAddress2 address = (IPAddress2) obj;
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
    
    public boolean sameIPNetwork(IPAddress2 address) {
        return true;
    }
    
    private void calculateMask() {
        switch(maskLength) {
            case 0 : maskByte1 = 0; maskByte2 = 0; maskByte3 = 0; maskByte4 = 0; break;
            case 1 : maskByte1 = 128; maskByte2 = 0; maskByte3 = 0; maskByte4 = 0; break;
            case 2 : maskByte1 = 192; maskByte2 = 0; maskByte3 = 0; maskByte4 = 0; break;
            case 3 : maskByte1 = 224; maskByte2 = 0; maskByte3 = 0; maskByte4 = 0; break;
            case 4 : maskByte1 = 240; maskByte2 = 0; maskByte3 = 0; maskByte4 = 0; break;
            case 5 : maskByte1 = 248; maskByte2 = 0; maskByte3 = 0; maskByte4 = 0; break;
            case 6 : maskByte1 = 252; maskByte2 = 0; maskByte3 = 0; maskByte4 = 0; break;
            case 7 : maskByte1 = 254; maskByte2 = 0; maskByte3 = 0; maskByte4 = 0; break;
            case 8 : maskByte1 = 255; maskByte2 = 0; maskByte3 = 0; maskByte4 = 0; break;
            case 9 : maskByte1 = 255; maskByte2 = 128; maskByte3 = 0; maskByte4 = 0; break;
            case 10 : maskByte1 = 255; maskByte2 = 192; maskByte3 = 0; maskByte4 = 0; break;
            case 11 : maskByte1 = 255; maskByte2 = 224; maskByte3 = 0; maskByte4 = 0; break;
            case 12 : maskByte1 = 255; maskByte2 = 240; maskByte3 = 0; maskByte4 = 0; break;
            case 13 : maskByte1 = 255; maskByte2 = 248; maskByte3 = 0; maskByte4 = 0; break;
            case 14 : maskByte1 = 255; maskByte2 = 252; maskByte3 = 0; maskByte4 = 0; break;
            case 15 : maskByte1 = 255; maskByte2 = 254; maskByte3 = 0; maskByte4 = 0; break;
            case 16 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 0; maskByte4 = 0; break;
            case 17 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 128; maskByte4 = 0; break;
            case 18 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 192; maskByte4 = 0; break;
            case 19 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 224; maskByte4 = 0; break;
            case 20 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 240; maskByte4 = 0; break;
            case 21 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 248; maskByte4 = 0; break;
            case 22 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 252; maskByte4 = 0; break;
            case 23 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 254; maskByte4 = 0; break;
            case 24 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 255; maskByte4 = 0; break;
            case 25 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 128; maskByte4 = 128; break;
            case 26 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 192; maskByte4 = 192; break;
            case 27 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 224; maskByte4 = 224; break;
            case 28 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 240; maskByte4 = 240; break;
            case 29 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 248; maskByte4 = 248; break;
            case 30 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 252; maskByte4 = 252; break;
            case 31 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 254; maskByte4 = 254; break;
            case 32 : maskByte1 = 255; maskByte2 = 255; maskByte3 = 255; maskByte4 = 255; break;                            
        }
    }
}
