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
package com.songbook.parser.nodes;

import java.io.File;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Class representing the Song.
 * @author Tomas Janecek
 */
public class SongNode implements Node {

    /** Comparator of SongNode classes based on title - uses current locale for comparisons. */
    public static class TitleComparator implements Comparator<SongNode> {

        /** Collator class used for Locale-aware string comparisons. */
        private final Collator collator;

        /** Constructor - Creates the instance of TitleComparator. */
        public TitleComparator() {
            collator = Collator.getInstance(Locale.getDefault());
        }

        /** Compare method - see Comparator.compare. */
        public int compare(SongNode o1, SongNode o2) {
            return collator.compare(o1.getTitle(), o2.getTitle());
        }
    }

    /** Title of the Song. */
    private final String title;

    /** List of Verses of the song (represented by VerseNode classes). */
    private final List<VerseNode> verseList;

    /** Source file (may be null). */
    private File sourceFile;


    /**
     * Constructor - creates an instance of SongNode.
     * @param title     Title of the song.
     * @param verseList List of verses of the song (represented by VerseNode classes).
     */
    public SongNode(String title, List<VerseNode> verseList) {
        this.title = title;
        this.verseList = Collections.unmodifiableList(verseList);
    }


    /** @return Source file. */
    public File getSourceFile() {
        return sourceFile;
    }


    /**
     * Sets the source file.
     * @param sourceFile Source file from which this node was created.
     */
    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }


    /** @return the title of the song. */
    public String getTitle() {
        return title;
    }


    /** @return List of VerseNodes of the song. */
    public List<VerseNode> getVerseList() {
        return verseList;
    }


    /** {@inheritDoc} */
    @Override
    public String getAsText(int transposition) {
        String out = title;
        out += "\n\n\n";

        for (VerseNode verseNode : verseList) {
            out += verseNode.getAsText(transposition);
            out += "\n\n";
        }

        return out;
    }


    /** {@inheritDoc} */
    @Override
    public String getAsHTML(int transposition) {
        String out = "<DIV class=\"title\">" + title + "</DIV>\n";

        for (VerseNode verseNode : verseList) {
            out += verseNode.getAsHTML(transposition) + "\n\n";
        }

        return out;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        String out = "SongNode[" + "title=" + title + ",\n";

        for (VerseNode verseNode : verseList) {
            out += verseNode.toString() + ",\n";
        }

        out += "]";
        return out;
    }
}
