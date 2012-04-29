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
 * Class representing one Verse of the song.
 * @author Tomas Janecek
 */
public class VerseNode implements Node {
    /** List of lines in a verse */
    private final List<LineNode> lines;

    /** Boolean flag indicating whether the song contains any chords. */
    private final boolean hasChords;


    /**
     * Constructor - Creates a new instance of verseNode representing a Verse.
     * @param lines List of lines in the verse (represented by LineNode classes).
     */
    public VerseNode(List<LineNode> lines) {
        this.lines = Collections.unmodifiableList(lines);

        boolean hasChords = false;
        for (LineNode lineNode : lines) {
            if (lineNode.hasChords()) {
                hasChords = true;
                break;
            }
        }
        this.hasChords = hasChords;
    }


    /** @return True if any line in this verse contain chords (false otherwise). */
    public boolean hasChords() {
        return hasChords;
    }


    /** @return List of LineNodes. */
    public List<LineNode> getLineNodes() {
        return lines;
    }


    /**
     * Accepts the visitor (as per the Visitor design pattern).
     * @param visitor Visitor to be accepted.
     * @param isFirst Indicates whether verse is the first verse.
     * @param isLast  Indicates whether verse is a last verse.
     */
    public void accept(Visitor visitor, boolean isFirst, boolean isLast) {
        visitor.enterVerseNode(this, isFirst, isLast);

        for (int i = 0; i < lines.size(); i++) {
            LineNode lineNode = lines.get(i);
            lineNode.accept(visitor, i == 0, (i + 1) == lines.size());
        }


        visitor.exitVerseNode(this, isFirst, isLast);
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        String out = "\t" + "VerseNode[\n";
        for (LineNode lineNode : lines) {
            out += lineNode.toString() + ",\n";
        }
        out += "\t]";
        return out;
    }
}
