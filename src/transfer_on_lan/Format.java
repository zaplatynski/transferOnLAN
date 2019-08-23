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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class Format {

    private ResourceBundle strings;
    private Locale locale;

    public Format(Locale locale, ResourceBundle strings) {
        this.locale = locale;
        this.strings = strings;
    }

    /**
     * @param duration en milliseconde
     * @return la durée formatée au format "heures minutes secondes"
     */
    public String formatDurationHMS(long duration) {
        String result = "";

        long milliseconds = duration % 1000;
        long remainder = (duration - milliseconds) / 1000; // reste en secondes
        long seconds = remainder % 60;
        remainder = (remainder - seconds) / 60; // reste en minutes
        long minutes = remainder % 60;
        long hours = (remainder - minutes) / 60; // reste en heures

        if (hours != 0) {
            result = hours + "h " + minutes + "min " + seconds + "s ";
        } else if (minutes != 0) {
            result = minutes + "min " + seconds + "s ";
        } else if (seconds != 0) {
            result = seconds + "s " + milliseconds + "ms";
        } else {
            result = milliseconds + "ms";
        }

        return result;
    }

    /**
     *
     * @param volume
     * @return
     */
    public String formatVolume(long volume) {
        String result = "";

        double gio = volume / 1073741824.0; // 1024 * 1024 * 1024
        double mio = volume / 1048576.0; // 1024 * 1024
        double kio = volume / 1024.0;

        if (gio >= 1) {
            result = String.format(locale, "%,.1f", gio) + " " + strings.getString("GiB");
        } else if (mio >= 1) {
            result = String.format(locale, "%,.1f", mio) + " " + strings.getString("MiB");
        } else if (kio >= 1) {
            result = String.format(locale, "%,.1f", kio) + " " + strings.getString("KiB");
        } else {
            if (volume > 1) {
                result = volume + " " + strings.getString("bytes");
            } else {
                result = volume + " " + strings.getString("byte");
            }
        }

        return result;
    }

    public String formatFlowRate(long volume, long duration) {
        String result = "";

        duration++; // +1 => évite les divisions par 0
        double flowRate = (double) volume / (double) duration;

        if (flowRate < 1048.576) {
            result = String.format(locale, "%,.1f", flowRate * 0.9765625) + " " + strings.getString("KiB") + "/s";
        } else {
            result = String.format(locale, "%,.1f", flowRate * 0.000953674) + " " + strings.getString("MiB") + "/s";
        }

        return result;
    }
}
