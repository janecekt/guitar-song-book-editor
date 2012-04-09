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
package com.songbook.core.model;

/**
 * Interface for classes of internal song representation.
 * @author Tomas Janecek
 */
public interface Node {
    /**
     * Plain-text representation of the node.
     * @param transposition Transposition to be applied.
     * @return the plain-text format representation.
     */
    String getAsText(int transposition);


    /**
     * HTML representation of the node.
     * @param transposition Transposition to be applied.
     * @return the simple-html format representation.
     */
    String getAsHTML(int transposition);
}
