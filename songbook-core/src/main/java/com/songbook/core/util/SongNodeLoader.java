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
package com.songbook.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.songbook.core.model.SongNode;
import com.songbook.core.parser.Parser;
import com.songbook.core.parser.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SongNodeLoader {
    private static Logger logger = LoggerFactory.getLogger(SongNodeLoader.class);
    private final Parser<SongNode> parser;


    public SongNodeLoader(Parser<SongNode> parser) {
        this.parser = parser;
    }

    public SongNode loadSongNodeFromFile(File file, String encoding) {
        try {
            Reader fileReader = new InputStreamReader(new FileInputStream(file.getAbsolutePath()), encoding);
            SongNode songNode = parser.parse(fileReader);
            songNode.setSourceFile(file);
            return songNode;
        } catch (ParserException ex) {
            logger.error("LOADING FAILED - SyntaxError : " + file.getName() + " : " + ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.error("LOADING FAILED - IO Error : " + file.getName() + " : " + ex.getMessage(), ex);
        }
        return null;
    }

    
    public List<SongNode> loadSongNodesFromDirectory(File directory, String encoding) {
        List<SongNode> newSongList = new ArrayList<SongNode>();
        for (File file : directory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                SongNode songNode = loadSongNodeFromFile(file, encoding);
                if (songNode != null) {
                    newSongList.add(songNode);
                }
            }
        }
        return newSongList;
    }


    public List<SongNode> loadSongNodesFromZip(InputStream zipStream, String encoding) throws ParserException {
        List<SongNode> songList = new ArrayList<SongNode>();

        // Load data from zip stream into SongList
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream = new ZipInputStream(zipStream);
            ZipEntry zipEntry;
            byte[] buffer = new byte[1024];
            while ( (zipEntry = zipInputStream.getNextEntry()) != null) {
                // Read into stream
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int count;
                while ((count = zipInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, count);
                }

                // Create a Reader
                Reader reader = new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray()), encoding);
                try {
                    songList.add(parser.parse(reader));
                } catch (ParserException ex) {
                    throw new RuntimeException("Failed to parse " + zipEntry.getName(), ex);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to open SongBook from ZIP", ex);
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException ex) {
                    //throw new RuntimeException("Failed to close ZIP stream", ex);
                }
            }
        }
        // Set song list
        return songList;
    }
}
