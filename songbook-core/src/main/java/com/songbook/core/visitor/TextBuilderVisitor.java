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
package com.songbook.core.visitor;

import com.songbook.core.model.ChordNode;
import com.songbook.core.model.LineNode;
import com.songbook.core.model.SongNode;
import com.songbook.core.model.TextNode;
import com.songbook.core.model.TitleNode;
import com.songbook.core.model.VerseNode;
import com.songbook.core.model.Visitor;

/**
 * SongNode visitor which creates a text representation of the song.
 */
public class TextBuilderVisitor implements Visitor {
    private StringBuilder sb;
    private int transposition;
    private VerseNode currentVerseNode = null;


    public TextBuilderVisitor(StringBuilder sb, int transposition) {
        this.sb = sb;
        this.transposition = transposition;
    }

    @Override
    public void enterSongNode(SongNode songNode) { }


    @Override
    public void exitSongNode(SongNode songNode) { }


    @Override
    public void enterVerseNode(VerseNode verseNode, boolean isFirst, boolean isLast) {
        currentVerseNode = verseNode;
        sb.append("\n\n");
    }


    @Override
    public void exitVerseNode(VerseNode verseNode, boolean isFirst, boolean isLast) {
        currentVerseNode = null;        
    }


    @Override
    public void enterLineNode(LineNode lineNode, boolean isFirst, boolean isLast) { }


    @Override
    public void exitLineNode(LineNode lineNode, boolean isFirst, boolean isLast) {
        sb.append("\n");
        if (currentVerseNode.hasChords() && (!isLast)) {
            sb.append("\n");
        }
    }


    @Override
    public void visitTitleNode(TitleNode titleNode) {
        sb.append(titleNode.getTitle());
        if (titleNode.getSubTitle() != null) {
            sb.append(" - ");
            sb.append(titleNode.getSubTitle());
        }
        sb.append("\n");
    }


    @Override
    public void visitTextNode(TextNode textNode, boolean isFirst, boolean isLast) {
        sb.append(textNode.getText());
    }


    @Override
    public void visitChordNode(ChordNode chordNode, boolean isFirst, boolean isLast) {
        sb.append("[");                
        sb.append(chordNode.getText(transposition));
        sb.append("]");
    }
}
