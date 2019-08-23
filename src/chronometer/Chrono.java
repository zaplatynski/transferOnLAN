/*
Copyright (C) 2011 Arnaud FRANÇOIS

This file is part of Transfer on LAN.

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
package chronometer;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class Chrono {

    private long beginning;
    private long elapsedTime;
    private boolean isStarted;

    /**
     *
     */
    public Chrono() {
        this.elapsedTime = 0;
        this.isStarted = false;
    }

    /**
     * start the chronometer
     */
    public void start() {
        this.isStarted = true;
        this.beginning = System.currentTimeMillis();
    }

    /**
     * stop the chronometer
     */
    public void stop() {        
        if (this.isStarted) {
            this.elapsedTime = this.elapsedTime + System.currentTimeMillis() - this.beginning;
            this.isStarted = false;
        }
    }
    
    /**
     *
     */
    public void reset() {
        this.elapsedTime = 0;
        this.isStarted = false;
    }

    /**
     * @return  elapsed time in millisecond
     */
    public long getElapsedTime() {
        long result;

        if (this.isStarted) {
            result = this.elapsedTime + System.currentTimeMillis() - this.beginning;
        } else {
            result = this.elapsedTime;
        }

        return result;
    }
}
