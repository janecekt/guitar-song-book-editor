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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.songbook.model.ChordNode;
import com.songbook.model.LineNode;
import com.songbook.model.Node;
import com.songbook.model.SongBook;
import com.songbook.model.SongNode;
import com.songbook.model.TextNode;
import com.songbook.model.VerseNode;
import com.songbook.util.FileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LaTexExporter implements Exporter {
    private static final Logger logger = LoggerFactory.getLogger(LaTexExporter.class);


    @Override
    public void export(File baseDir, SongBook songBook) {
        // Output
        File outputDir = new File(baseDir, "tex");
        File outputFile = new File(outputDir, "allsongs.tex");
        logger.info("Starting export to latex file {}.", outputFile.getAbsolutePath());

        // Create directory
        FileIO.createDirectory(outputDir);

        // Sort songs
        List<SongNode> sortedArrayList = new ArrayList<SongNode>(songBook.getSongNodeList());
        Collections.sort(sortedArrayList, new SongNode.TitleComparator());

        // Initialize exporter
        StringBuilder builder = new StringBuilder();

        // Build document
        for (SongNode songNode : songBook.getSongNodeList()) {
            // Build chapter
            appendSongNode(builder, songNode);
        }

        // Write to file
        FileIO.writeStringToFile(outputFile.getAbsolutePath(), "utf8", builder.toString());

        logger.info("COMPLETED export to latex file {}.", outputFile.getAbsolutePath());
    }


    private void appendSongNode(StringBuilder builder, SongNode songNode) {
        builder.append("\n\n\n\\begin{song}{").append(songNode.getTitle()).append("}\n");

        for (VerseNode verseNode : songNode.getVerseList()) {
            appendVerseNode(builder, verseNode);
            builder.append("\n\n");
        }
        builder.append("\\end{song}\n\n\n");

    }


    private void appendVerseNode(StringBuilder builder, VerseNode verseNode) {
        builder.append("\t\\begin{songverse}\n");

        for (Iterator<LineNode> it = verseNode.getLineNodes().iterator(); it.hasNext(); ) {
            LineNode lineNode = it.next();
            appendLineNode(builder, lineNode);
            builder.append((it.hasNext()) ? "\\\\ \n" : "\n");
        }

        builder.append("\t\\end{songverse}\n");
    }


    private void appendLineNode(StringBuilder builder, LineNode lineNode) {
        builder.append("\t\t");
        for (Node node : lineNode.getContentList()) {
            if (node instanceof TextNode) {
                builder.append(((TextNode) node).getText());
            } else if (node instanceof ChordNode) {
                ChordNode chordNode = (ChordNode) node;
                String chord2 = chordNode.getChord2(0).replaceAll("#", "\\\\#");
                String chord1 = chordNode.getChord1(0).replaceAll("#", "\\\\#");
                if (chord2.isEmpty()) {
                    builder.append("\\chord{").append(chord1).append("}");
                } else {
                    builder.append("\\chord{").append(chord2).append("/").append(chord1).append("}");
                }
            }
        }
    }
}