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
package com.songbook.exporter;

import java.io.File;

import com.songbook.parser.nodes.ChordNode;
import com.songbook.parser.nodes.LineNode;
import com.songbook.parser.nodes.Node;
import com.songbook.parser.nodes.SongBook;
import com.songbook.parser.nodes.SongNode;
import com.songbook.parser.nodes.TextNode;
import com.songbook.parser.nodes.VerseNode;
import com.songbook.util.FileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlExporter implements Exporter {
    private static final Logger logger = LoggerFactory.getLogger(HtmlExporter.class);

    @Override
    public void export(File baseDir, SongBook songBook) {
        logger.info("Starting export to HTML.");

        for (SongNode songNode : songBook.getSongNodeList()) {
            try {
                String htmlFileName = songNode.getSourceFile().getName().replace(".txt", "") + ".html";
                File outputFileName = new File(baseDir + "/html/" + htmlFileName);
                FileIO.createDirectoryIfRequired(outputFileName);

                FileIO.writeStringToFile(outputFileName.getAbsolutePath(), "utf8", buildSongNode(songNode));
            } catch (Exception ex) {
                logger.error("... FAILED to export song - " + songNode.getTitle(), ex);
            }
        }

        logger.info("COMPLETED export to HTML");
    }

    private String buildSongNode(SongNode songNode) {
        StringBuilder builder = new StringBuilder();

        builder.append("<HTML>\n");
        builder.append("<HEAD>\n");
        builder.append("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf8\">\n");
        builder.append("  <link href=\"song.css\" rel=\"stylesheet\" type=\"text/css\">\n");
        builder.append("  <SCRIPT src=\"song.js\" type=\"text/javascript\"></SCRIPT>\n");
        builder.append("  <TITLE>").append(songNode.getTitle()).append("</TITLE>\n");
        builder.append("</HEAD>\n");
        builder.append("<BODY>\n\n");

        builder.append("<DIV class=\"title\">").append(songNode.getTitle()).append("</DIV>\n\n");

        builder.append("<DIV class=\"transpose\">\n");
        builder.append("Transposition: <SPAN id=\"totaltranspose\">0</SPAN>\n");
        builder.append("[<a href=\"javascript:transpose(1)\">+1</a>]\n");
        builder.append("[<a href=\"javascript:transpose(-1)\">-1</a>]\n");
        builder.append("</DIV>");

        for (VerseNode verseNode : songNode.getVerseList()) {
            builder.append("\n\n");
            appendVerse(verseNode, builder);
        }

        builder.append("</BODY>\n");
        builder.append("</HTML>\n");

        return builder.toString();
    }


    private void appendVerse(VerseNode verseNode, StringBuilder builder) {
        builder.append("<DIV class=\"verse\">");
        for (LineNode lineNode : verseNode.getLineNodes()) {
            appendLine(lineNode, builder);
            builder.append("<BR/>\n");
        }
        builder.append("</DIV>");
    }


    private void appendLine(LineNode lineNode, StringBuilder builder) {
        for (Node node : lineNode.getContentList()) {
            if (node instanceof TextNode) {
                builder.append( ((TextNode) node).getText() );
            } else if (node instanceof ChordNode) {
                ChordNode chordNode = (ChordNode) node;

                builder.append("<SPAN class=\"chord\">");
                String chord2 = chordNode.getChord2(0);
                if (!chord2.isEmpty()) {
                    builder.append("<SPAN title=\"chord\">");
                    builder.append(chord2);
                    builder.append("</SPAN>");
                }
                builder.append("<SPAN title=\"chord\">");
                builder.append(chordNode.getChord1(0));
                builder.append("</SPAN></SPAN>");
            }
        }
    }
}
