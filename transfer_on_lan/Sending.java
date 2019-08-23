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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * représente une expédition de fichiers/dossiers
 *
 * @author Arnaud FRANCOIS
 */
public class Sending {

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
    private User recipient;
    private long elapsedTime;
    private StringBuffer fileNames;
    private Date date;
    private DateFormat shortDateFormat;

    public Sending(String message, User recipient) {
        this.number = counter++;
        this.message = message;
        this.progression = 0;
        this.id = Tools.generateId(20);
        this.state = IN_PROGRESS;
        this.totalDataVolume = 0;
        this.dataVolume = 0;
        this.recipient = recipient;
        this.elapsedTime = 0;
        this.fileNames = new StringBuffer("<html>");
        this.shortDateFormat = new SimpleDateFormat("H:mm");
    }

    public void cancel() {
        this.state = CANCELED;
    }

    public void addFileNames(String fileName) {
        this.fileNames.append(fileName);
        this.fileNames.append("<br/>");
    }

    public int getNumber() {
        return number;
    }
    
    public StringBuffer getFileNames() {
        return this.fileNames;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setProgression(int avancement) {
        this.progression = avancement;
    }

    public int getProgression() {
        return this.progression;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTotalDataVolume() {
        return this.totalDataVolume;
    }

    public void setTotalDataVolume(long totalDataVolume) {
        this.totalDataVolume = totalDataVolume;
    }

    public User getRecipient() {
        return this.recipient;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    public void setElapsedTime(long tempsEcoule) {
        this.elapsedTime = tempsEcoule;
    }

    public long getDataVolume() {
        return this.dataVolume;
    }

    public void setDataVolume(long dataVolume) {
        this.dataVolume = dataVolume;
    }

    /**
     * compare two sendings : two sendings are "equal" if their id are equal
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;

        if (o != null) {
            if (o instanceof Sending) {
                Sending sending = (Sending) o;
                if (this.id.compareTo(sending.id) == 0) {
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
}
