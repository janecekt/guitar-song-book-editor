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
package com.songbook.pc.exporter;

import java.io.File;

import com.songbook.core.model.SongBook;
import com.songbook.core.model.SongNode;
import com.songbook.core.util.FileIO;
import com.songbook.pc.util.EPubBuilder;
import com.songbook.pc.util.FreeMakerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EPubExporter implements Exporter {
    private static final Logger logger = LoggerFactory.getLogger(EPubExporter.class);
    private static final String SONGBOOK_EPUB_SONG_TEMPLATE = "/export/epub/song.xhtml.ftl";
    private static final String SONGBOOK_EPUB_STYLESHEET = "/export/epub/epub-stylesheet.css";

    @Override
    public void export(File baseDir, SongBook songBook) {
        // Output file
        File outputDir = new File(baseDir, "epub");
        FileIO.createDirectory(outputDir);
        File outputFile = new File(outputDir, "songbook.epub");

        logger.info("Starting export to EPub {}.", outputFile.getAbsolutePath());
        try {
            EPubBuilder ePubBuilder = new EPubBuilder()
                    .withBookTitle("Song Book")
                    .withBookCreator("Tomas Janecek");

            for (SongNode songNode : songBook.getSongNodeList()) {
                String songXHTML = FreeMakerUtil.processTemplate(songNode, SONGBOOK_EPUB_SONG_TEMPLATE);
                ePubBuilder = ePubBuilder.withNewEntry(
                        songNode.getTitle(),
                        songNode.getSourceFile().getName().replace(".txt",".xhtml"),
                        "application/xhtml+xml",
                        true,
                        songXHTML.getBytes("UTF8"));
            }

            // Add stylesheet
            ePubBuilder = ePubBuilder.withNewEntry(
                    "epub-stylesheet",
                    "epub-stylesheet.css",
                    "text/css",
                    false,
                    FileIO.readResourceToString(SONGBOOK_EPUB_STYLESHEET).getBytes("UTF8"));

            // Build EPUB into file
            ePubBuilder.build(outputFile);

            logger.info("COMPLETED export to EPub {}.", outputFile.getAbsolutePath());
        } catch (Exception ex) {
            throw new RuntimeException("Export to EPUB failed !", ex);
        }
    }
}
