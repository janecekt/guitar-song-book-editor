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
package com.songbook.logback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;

public class EventAppender<E> extends AppenderBase<E> {
    private static final Map<String, EventAppender<?>> instances = new ConcurrentHashMap<String, EventAppender<?>>();


    public static EventAppender<?> getByName(String name) {
        return instances.get(name);
    }


    public interface LogEventListener {
        enum Severity {ERROR, WARNING, INFO}

        void onLogEvent(Severity severity, String logMessage);
    }

    private Layout<E> layout;
    private final Collection<WeakReference<LogEventListener>> listeners = new ArrayList<WeakReference<LogEventListener>>();


    public void addListener(LogEventListener listener) {
        this.listeners.add(new WeakReference<LogEventListener>(listener));
    }


    @Override
    protected void append(E eventObject) {
        String msg = layout.doLayout(eventObject);
        for (Iterator<WeakReference<LogEventListener>> it = listeners.iterator(); it.hasNext(); ) {
            LogEventListener listener = it.next().get();
            if (listener != null) {
                listener.onLogEvent(mapSeverity(eventObject), msg);
            } else {
                it.remove();
            }
        }
    }


    @Override
    public void start() {
        // Register instance
        String name = getName();
        if (name != null && !name.isEmpty()) {
            instances.put(name, this);
        }
        super.start();
    }


    @SuppressWarnings("unused")
    public Layout<E> getLayout() {
        return layout;
    }


    @SuppressWarnings("unused")
    public void setLayout(Layout<E> layout) {
        this.layout = layout;
    }


    private LogEventListener.Severity mapSeverity(E eventObject) {
        if (eventObject instanceof ILoggingEvent) {
            ILoggingEvent event = (ILoggingEvent) eventObject;
            Level level = event.getLevel();
            if (level != null) {
                if (Level.ERROR.equals(level)) {
                    return LogEventListener.Severity.ERROR;
                } else if (Level.WARN.equals(level)) {
                    return LogEventListener.Severity.WARNING;
                }
            }
        }
        return LogEventListener.Severity.INFO;
    }
}
