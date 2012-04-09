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
 * Class representing one text fragment in the line.
 * @author Tomas Janecek
 */
public class TextNode implements Node {
    /** Text represented by this class. */
    private final String text;

    /**
     * Constructor - Creates an instance of TextNode.
     * @param text Text to be represented by this node.
     */
    public TextNode(String text) {
        this.text = text;
    }


    /** @return Type of the node (required for FreeMaker). */
    @SuppressWarnings("unused")
    public String getType() {
        return "TextNode";
    }


    /** @return Text of this node. */
    public String getText() {
        return text;
    }


    /** {@inheritDoc} */
    @Override
    public String getAsText(int transposition) {
        return text;
    }


    /** {@inheritDoc} */
    @Override
    public String getAsHTML(int transposition) {
        return text;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "TextNode[" + text + "]";
    }
}
