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

import debug.D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * représente une réception de fichiers/dossiers
 *
 * @author Arnaud FRANCOIS
 */
public class Reception {

    public static final int IN_PROGRESS = 1;
    public static final int DONE = 2;
    public static final int REFUSED = 3;
    public static final int CANCELED = 4;
    public static final int ERROR = 5;

    static private int counter = 1;
    
    private int number;
    private String id;
    private int state;
    private String message;
    private int progression;
    private long totalDataVolume;
    private long dataVolume;
    private String senderName;
    private long elapsedTime;
    private StringBuffer fileNames;
    private String directory;
    private Date date;
    private DateFormat shortDateFormat;
    //private boolean canceled;

    public Reception(String directory) {
        this.number = counter++;
        this.id = Tools.generateId(20);
        this.directory = directory;
        this.state = IN_PROGRESS;
        this.fileNames = new StringBuffer("<html>");
        this.shortDateFormat = new SimpleDateFormat("H:mm");
    }

    public void addFileName(String fileName) {
        this.fileNames.append(fileName);
        this.fileNames.append("<br/>");
    }

    public void cancel() {
        this.state = CANCELED;
        //this.canceled = true;
    }

    public StringBuffer getFileNames() {
        return this.fileNames;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public int getProgression() {
        return this.progression;
    }

    public String getMessage() {
        return message;
    }

    public long getTotalDataVolume() {
        return totalDataVolume;
    }

    public void setTotalDataVolume(long totalDataVolume) {
        this.totalDataVolume = totalDataVolume;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setProgression(int progression) {
        this.progression = progression;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNumber() {
        return number;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getDataVolume() {
        return dataVolume;
    }

    public void setDataVolume(long dataVolume) {
        this.dataVolume = dataVolume;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFormattedDate() {
        if (this.state == DONE) {
            return shortDateFormat.format(date);
        } else {
            return "";
        }
    }

    public boolean isCanceled() {
        return this.state == CANCELED;
    }

    /**
     * compare two receptions : two receptions are "equal" if their id are equal
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;

        if (o != null) {
            if (o instanceof Reception) {
                Reception reception = (Reception) o;
                if (this.id.compareTo(reception.id) == 0) {
                    result = true;
                }
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + (this.id == null ? 0 : this.id.hashCode());

        return hash;
    }
}
