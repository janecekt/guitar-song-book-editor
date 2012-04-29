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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.songbook.core.comparator.SongNodeTitleComparator;
import com.songbook.core.model.SongBook;
import com.songbook.core.model.SongNode;
import com.songbook.core.util.FileIO;
import com.songbook.pc.util.FreeMakerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LaTexExporter implements Exporter {
    private static final Logger logger = LoggerFactory.getLogger(LaTexExporter.class);
    private static final String SONGBOOK_LATEX_ALLSONGS_TEMPLATE = "/export/latex/allsongs.tex.ftl";


    @Override
    public void export(File baseDir, SongBook songBook) {
        // Output
        File outputDir = new File(baseDir, "latex");
        File outputFile = new File(outputDir, "allsongs.tex");
        logger.info("Starting export to latex file {}.", outputFile.getAbsolutePath());

        // Create directory
        FileIO.createDirectory(outputDir);

        // Sort songs
        List<SongNode> sortedArrayList = new ArrayList<SongNode>(songBook.getSongNodeList());
        Collections.sort(sortedArrayList, new SongNodeTitleComparator(Locale.getDefault()));

        // Build document
        String output = FreeMakerUtil.processTemplate(new SongBook(sortedArrayList), SONGBOOK_LATEX_ALLSONGS_TEMPLATE);

        // Write to file
        FileIO.writeStringToFile(outputFile.getAbsolutePath(), "utf8", output);

        logger.info("COMPLETED export to latex file {}.", outputFile.getAbsolutePath());
    }
}