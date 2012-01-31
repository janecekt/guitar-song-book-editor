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

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.songbook.logback.EventAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogObservingPresentationModel {
    private static final Logger logger = LoggerFactory.getLogger(LogObservingPresentationModel.class);
    private final ValueHolder textModel = new ValueHolder("");
    private final ValueHolder caretPositionModel = new ValueHolder(0);

    // CAREFUL: EvenAppender uses WeakReference to listeners - we need to keep a reference to the listener class
    //    so that it is not garbage-collected.
    private final EventAppender.LogEventListener listener = new EventAppender.LogEventListener() {
        @Override
        public void onLogEvent(Severity severity, String logMessage) {
            String htmlMessage = "<DIV class=\"entry\"><PRE class=\"" + severity.name() + "\">" + logMessage.trim() + "</PRE></DIV>";
            textModel.setValue(textModel.getString() + htmlMessage);
            caretPositionModel.setValue(caretPositionModel.intValue() + logMessage.length());
        }
    };


    public LogObservingPresentationModel() {
        EventAppender<?> eventAdapter = EventAppender.getByName("UI-APPENDER");
        if (eventAdapter != null) {
            eventAdapter.addListener(listener);
        }


        logger.info("Handler initialized !");
    }


    public ValueModel getTextModel() {
        return textModel;
    }


    public ValueModel getCaretPositionModel() {
        return caretPositionModel;
    }
}
