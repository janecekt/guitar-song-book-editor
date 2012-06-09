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

import java.util.Arrays;
import java.util.List;

import com.songbook.core.model.ChordNode;
import com.songbook.core.model.LineNode;
import com.songbook.core.model.SongNode;
import com.songbook.core.model.TextNode;
import com.songbook.core.model.TitleNode;
import com.songbook.core.model.VerseNode;
import com.songbook.core.model.Visitor;
import com.songbook.core.util.StringUtil;

/**
 * SongNode visitor which traverses the SongNode tree and builds a HTML representation.
 */
public class HtmlBuilderVisitor implements Visitor {
    public static enum Mode { CHORDS_ON, TWO_LINE_TITLE, DISPLAY_TRANSPOSITION, HTML_ESCAPING }
    private StringBuffer sb;
    private int transposition;
    private List<Mode> mode;


    public HtmlBuilderVisitor(StringBuffer sb, int transposition, Mode... mode) {
        this.sb = sb;
        this.transposition = transposition;
        this.mode = Arrays.asList(mode);
    }

    @Override
    public void enterSongNode(SongNode songNode) { }


    @Override
    public void exitSongNode(SongNode songNode) { }


    @Override
    public void enterVerseNode(VerseNode verseNode, boolean isFirst, boolean isLast) {
        sb.append("\n\n");
        sb.append("<div class=\"verse\">\n");
    }


    @Override
    public void exitVerseNode(VerseNode verseNode, boolean isFirst, boolean isLast) {
        sb.append("</div>\n");
    }


    @Override
    public void enterLineNode(LineNode lineNode, boolean isFirst, boolean isLast) {
        sb.append("\t<div class=\"line\">\n");
    }


    @Override
    public void exitLineNode(LineNode lineNode, boolean isFirst, boolean isLast) {
        sb.append("\n\t</div>\n");
    }


    @Override
    public void visitTitleNode(TitleNode titleNode) {
        // Add transposition info
        if (mode.contains(Mode.DISPLAY_TRANSPOSITION) && transposition != 0) {
            sb.append("<div class=\"transposition\">");
            sb.append(transposition > 0 ? "+"+transposition : transposition );
            sb.append("</div>");
        }

        // Add title
        sb.append("<div class=\"titleNode\">");

        sb.append("<span class=\"title\">");
        if (mode.contains(Mode.HTML_ESCAPING)) {
            StringUtil.appendAndHtmlEscape(sb, titleNode.getTitle());
        } else {
            sb.append(titleNode.getTitle());
        }
        sb.append("</span>");

        if (titleNode.getSubTitle() != null) {
            if (mode.contains(Mode.TWO_LINE_TITLE)) {
                sb.append("<br/>\n");
            } else {
                sb.append(" - ");
            }

            sb.append("<span class=\"subtitle\">");
            if (mode.contains(Mode.HTML_ESCAPING)) {
                StringUtil.appendAndHtmlEscape(sb, titleNode.getSubTitle());
            } else {
                sb.append(titleNode.getSubTitle());
            }
            sb.append("</span>");
        }
        sb.append("</div>\n");
    }


    @Override
    public void visitTextNode(TextNode textNode, boolean isFirst, boolean isLast) {
        sb.append("<span class=\"text\">");
        if (mode.contains(Mode.HTML_ESCAPING)) {
            StringUtil.appendAndHtmlEscape(sb, textNode.getText());
        } else {
            sb.append(textNode.getText());
        }
        sb.append("</span>");
    }


    @Override
    public void visitChordNode(ChordNode chordNode, boolean isFirst, boolean isLast) {
        if (mode.contains(Mode.CHORDS_ON)) {
            sb.append("<span class=\"chord\">");
            if (mode.contains(Mode.HTML_ESCAPING)) {
                StringUtil.appendAndHtmlEscape(sb, chordNode.getText(transposition));
            } else {
                sb.append(chordNode.getText(transposition));
            }
            sb.append("</span>");
        }
    }
}
