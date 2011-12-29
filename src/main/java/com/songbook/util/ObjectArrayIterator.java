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
package com.songbook.util;

import java.util.Iterator;

/**
 * Implements the generic Iterator over the array of elements of type T.
 * @param <T> Type of array elements.
 * @author Tomas Janecek
 */
public class ObjectArrayIterator<T> implements Iterator<T> {

    /** Array over which we iterate. */
    private final T[] array;

    /** Current position index. */
    private int idx;


    /**
     * Constructor - creates of the iterator.
     * @param array Array over which we will iterate.
     */
    ObjectArrayIterator(T[] array) {
        this.array = array;
        this.idx = 0;
    }


    /**
     * Returns true if the iteration has more elements.
     * @see Iterator#hasNext()
     */
    public boolean hasNext() {
        return (idx < array.length);
    }


    /**
     * Returns the next element in the iteration.
     * @see Iterator#next()
     */
    public T next() {
        return array[idx++];
    }


    /**
     * Not implemented - throws UnsupportedOperationException if called.
     * @see Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

