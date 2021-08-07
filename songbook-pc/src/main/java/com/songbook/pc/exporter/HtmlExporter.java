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
import java.nio.charset.StandardCharsets;

import com.songbook.core.model.SongBook;
import com.songbook.core.model.SongNode;
import com.songbook.core.util.FileIO;
import com.songbook.pc.util.FreeMakerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlExporter implements Exporter {
    private static final Logger logger = LoggerFactory.getLogger(HtmlExporter.class);
    private static final String SONGBOOK_HTML_SONG_TEMPLATE = "/export/html/song.html.ftl";


    @Override
    public File export(File baseDir, SongBook songBook) {
        logger.info("Starting export to HTML.");

        // Create directory
        File outputDir = new File(baseDir, "html");
        FileIO.createDirectory(outputDir);

        for (SongNode songNode : songBook.getSongNodeList()) {
            exportSong(songNode, outputDir);
        }

        logger.info("COMPLETED export to HTML");

        return outputDir;
    }


    private void exportSong(SongNode songNode, File outputDir) {
        try {
            // Determine target filename
            String htmlFileName = songNode.getSourceFile().getName().replace(".txt", "") + ".html";
            File outputFileName = new File(outputDir, htmlFileName);

            // Write content to HTML
            String htmlContent = FreeMakerUtil.processTemplate(songNode, SONGBOOK_HTML_SONG_TEMPLATE);
            FileIO.writeStringToFile(outputFileName.getAbsolutePath(), StandardCharsets.UTF_8, htmlContent);
        } catch (Exception ex) {
            logger.error("... FAILED to export song - " + songNode.getTitle(), ex);
        }
    }
}
