/*
 *  Copyright (c) 2008 - Tomas Janecek.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.songbook.android.paging;

public class FpsLimiter {
    private long lastEventTimeStamp;
    private long minDurationValue;

    public FpsLimiter(long minDuration) {
        this.minDurationValue = minDuration;
        this.lastEventTimeStamp = 0;
    }

    public void sleepForRemainingTime() {
        long currentTimeStamp = System.currentTimeMillis();
        long minDuration = getMinDuration();
        long remainingTime = minDuration - (currentTimeStamp - lastEventTimeStamp);
        if (remainingTime > 0) {
            try{
                Thread.sleep(remainingTime);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        lastEventTimeStamp = currentTimeStamp;
    }

    public synchronized long getMinDuration() {
        return minDurationValue;
    }

    public synchronized void setMinDuration(long minDuration) {
        this.minDurationValue = minDuration;
    }
}
