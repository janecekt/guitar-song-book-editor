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

import java.util.Collections;
import java.util.List;

/**
 * Class representing one line of the verse of the song.
 * @author Tomas Janecek
 */
public class LineNode implements Node {
    /** True if the line has chords. */
    private final boolean hasChords;

    /** List of fragments contained on the line (ChordNode or TextNode classes). */
    private final List<Node> contentList;

    /**
     * Constructor - Creates the new instance of LineNode.
     * @param contentList List of fragments contained on the line (CharNode or TextNode classes).
     */
    public LineNode(List<Node> contentList) {
        this.contentList = Collections.unmodifiableList(contentList);

        boolean hasChords = false;
        for (Node node : contentList) {
            if (node instanceof ChordNode) {
                hasChords = true;
                break;
            }
        }
        this.hasChords = hasChords;
    }


    /** @return true it the line hasChord; false otherwise. */
    public boolean hasChords() {
        return hasChords;
    }


    /** @return list of content nodes. */
    public List<Node> getContentList() {
        return contentList;
    }


    /**
     * Accepts the visitor (as per the Visitor design pattern).
     * @param visitor Visitor to be accepted.
     * @param isFirst Indicates whether line is the is the first in the verse.
     * @param isLast  Indicates whether line is the last in the verse.
     */
    public void accept(Visitor visitor, boolean isFirst, boolean isLast) {
        visitor.enterLineNode(this, isFirst, isLast);

        for (int i=0; i<contentList.size(); i++) {
            Node node = contentList.get(i);
            if (node instanceof TextNode) {
                ((TextNode) node).accept(visitor, i==0, (i+1)==contentList.size());
            } else if (node instanceof ChordNode) {
                ((ChordNode) node).accept(visitor, i==0, (i+1)==contentList.size());
            }
        }

        visitor.exitLineNode(this, isFirst, isLast);
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        String out = "\t\t" + "LineNode[\n";
        for (Node node : contentList) {
            out += "\t\t\t" + node.toString() + ",\n";
        }
        out += "\t\t]";
        return out;
    }
}
