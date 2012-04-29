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
package com.songbook.android.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class EventBroker {
    public interface EventListener<T> {
        public void onEvent(T event);
    }


    private List<EventRecord<?>> listenerList = new ArrayList<EventRecord<?>>();

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        for (Iterator<EventRecord<?>> recordIterator = listenerList.iterator(); recordIterator.hasNext(); ) {
            EventRecord<?> eventRecord = recordIterator.next();
            EventListener<?> listener = eventRecord.getListener();
            if (listener == null) {
                recordIterator.remove();
            } else {
                if (eventRecord.isApplicableFor(event.getClass())) {
                    ((EventListener<T>) listener).onEvent(event);
                }
            }
        }
    }

    public <T> void subscribe(Class<T> event, EventListener<T> listener) {
        // Cleanup
        for (Iterator<EventRecord<?>> recordIterator = listenerList.iterator(); recordIterator.hasNext(); ) {
            EventRecord<?> eventRecord = recordIterator.next();
            if (eventRecord.getListener() == null) {
                recordIterator.remove();
            }
        }

        // Subscribe
        listenerList.add( new EventRecord<T>(event, listener) );
    }


    public <T> void unsubscribe(EventListener<T> listener) {
        // Cleanup
        for (Iterator<EventRecord<?>> recordIterator = listenerList.iterator(); recordIterator.hasNext(); ) {
            EventRecord<?> eventRecord = recordIterator.next();
            EventListener<?> currentListener = eventRecord.getListener();
            if ((listener == null) || (currentListener == listener)) {
                recordIterator.remove();
            }
        }
    }


    private static class EventRecord<T> {
        private Class<T> eventClass;
        private WeakReference<EventListener<T>> listenerWeakReference;

        public EventRecord(Class<T> eventClass, EventListener<T> listenerWeakReference) {
            this.eventClass = eventClass;
            this.listenerWeakReference = new WeakReference<EventListener<T>>(listenerWeakReference);
        }

        public boolean isApplicableFor(Class<?> eventType) {
            return eventClass.isAssignableFrom(eventType);
        }

        public EventListener<T> getListener() {
            return listenerWeakReference.get();
        }
    }
}