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

/** Visitor of the song node. */
public interface Visitor {
    /**
     * Called when entering the SongNode.
     * @param songNode SongNode being entered.
     */
    void enterSongNode(SongNode songNode);

    /**
     * Called when leaving the SongNode.
     * @param songNode SongNode being exited.
     */
    void exitSongNode(SongNode songNode);

    /**
     * Called when entering a VerseNode.
     * @param verseNode Verse node being entered.
     * @param isFirst   True iff verse is a first verse in the song.
     * @param isLast    True iff verse is a last verse in the song.
     */
    void enterVerseNode(VerseNode verseNode, boolean isFirst, boolean isLast);

    /**
     * Called when leaving a VerseNode.
     * @param verseNode Verse node being exited.
     * @param isFirst   True iff verse is a first verse in the song.
     * @param isLast    True iff verse is a last verse in the song.
     */
    void exitVerseNode(VerseNode verseNode, boolean isFirst, boolean isLast);

    /**
     * Called when entering a LineNode.
     * @param lineNode LineNode being entered.
     * @param isFirst  True iff line is the first line in the verse.
     * @param isLast   True iff line is the last line in the verse.
     */
    void enterLineNode(LineNode lineNode, boolean isFirst, boolean isLast);

    /**
     * Called when leaving a LineNode.
     * @param lineNode LineNode being entered.
     * @param isFirst  True iff line is the first line in the verse.
     * @param isLast   True iff line is the last line in the verse.
     */
    void exitLineNode(LineNode lineNode, boolean isFirst, boolean isLast);

    /**
     * Called when visiting TitleNode
     * @param titleNode TitleNode being visited.
     */
    void visitTitleNode(TitleNode titleNode);


    /**
     * Called when visiting a TextNode.
     * @param textNode TextNode being visited.
     * @param isFirst  True iff TextNode is a first fragment in the line.
     * @param isLast   True iff TextNode is a last fragment in the line.
     */
    void visitTextNode(TextNode textNode, boolean isFirst, boolean isLast);

    /**
     * Called when visiting a ChordNode.
     * @param chordNode ChordNode being visited.
     * @param isFirst   True iff TextNode is a first fragment in the line.
     * @param isLast    True iff TextNode is a last fragment in the line.
     */
    void visitChordNode(ChordNode chordNode, boolean isFirst, boolean isLast);
}
