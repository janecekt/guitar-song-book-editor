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
package com.songbook.ui.presentationmodel;

import java.awt.Frame;

import com.songbook.ui.PresentationModel;
import org.slf4j.Logger;


public abstract class BasePresentationModel implements PresentationModel {
    private Frame ownerFrame;
    private boolean propagateErrors = false;


    protected abstract Logger getLogger();


    protected void handleError(String description, Exception exception) {
        getLogger().error(description.toUpperCase(), exception);
        if (!propagateErrors) {
            throw new RuntimeException(description, exception);
        }
    }


    protected Frame getOwnerFrame() {
        return ownerFrame;
    }


    @Override
    public void setFrame(Frame ownerFrame) {
        this.ownerFrame = ownerFrame;
    }


    @Override
    public void setPropagateErrors(boolean propagateErrors) {
        this.propagateErrors = propagateErrors;
    }
}
