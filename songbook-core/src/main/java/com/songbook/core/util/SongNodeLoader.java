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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.songbook.core.model.SongNode;
import com.songbook.core.parser.Parser;
import com.songbook.core.parser.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SongNodeLoader {
    public static String SONG_INDEX_FILE_NAME = "_song-index.txt";
    private static final Logger logger = LoggerFactory.getLogger(SongNodeLoader.class);
    private final Parser<SongNode> parser;


    public SongNodeLoader(Parser<SongNode> parser) {
        this.parser = parser;
    }

    public SongNode loadSongNodeFromFile(File file, Charset encoding) {
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

    
    public List<SongNode> loadSongNodesFromDirectory(File directory, Charset encoding) {
        Map<String, Integer> songIndexMap = Stream.of(directory.listFiles())
                .filter(File::isFile)
                .filter(file -> file.getName().equals(SONG_INDEX_FILE_NAME))
                .findFirst()
                .map(file -> loadSongIndexMap(file, encoding))
                .orElse(null);

        List<SongNode> newSongList = Stream.of(directory.listFiles())
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith(".txt"))
                .filter(file -> !file.getName().equals(SONG_INDEX_FILE_NAME))
                .sorted(Comparator.comparing(File::getName))
                .map(file -> loadSongNodeFromFile(file, encoding))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        enrichSongsWithIndex(newSongList, songIndexMap);

        return newSongList;
    }


    public List<SongNode> loadSongNodesFromZip(InputStream zipStream, Charset encoding) {
        List<SongNode> songList = new ArrayList<>();
        Map<String,Integer> songIndexMap = null;

        // Load data from zip stream into SongList
        ZipInputStreamIterable zipIterable = new ZipInputStreamIterable(zipStream, "Songbook ZIP");
        try {
            for (DataEntry entry : zipIterable) {
                Reader reader = new InputStreamReader(new ByteArrayInputStream(entry.getData()), encoding);
                if (SONG_INDEX_FILE_NAME.equals(entry.getName())) {
                    // Load song order file
                    songIndexMap = loadSongIndexMap(reader);
                } else {
                    // Load and parse song
                    try {
                        SongNode songNode = parser.parse(reader);
                        songNode.setSourceFile(new File(entry.getName()));
                        songList.add(songNode);
                    } catch (ParserException ex) {
                        throw new RuntimeException("Failed to parse " + entry.getName(), ex);
                    }
                }
            }
        } finally {
            zipIterable.close();
        }

        // Set song list
        enrichSongsWithIndex(songList, songIndexMap);
        return songList;
    }


    public Map<String,Integer> loadTransposeMap(InputStream stream) {
        ReaderIterable readerIterable = new ReaderIterable(stream, StandardCharsets.UTF_8, "transpose map");
        try {
            Map<String, Integer> transposeMap = new HashMap<>();
            for (String line : readerIterable) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    transposeMap.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
            return transposeMap;
        } finally {
            readerIterable.close();
        }
    }

    public void saveTransposeMap(OutputStream stream, Map<String,Integer> transposeMap) {
        Writer writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
        try {
            for (Map.Entry<String,Integer> entry : transposeMap.entrySet()) {
                writer.append(entry.getKey())
                        .append("|")
                        .append(Integer.toString(entry.getValue()))
                        .append("\n");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save transpose map !", ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                logger.error("Saving of transpose map failed !", ex);
            }
        }
    }


    private void enrichSongsWithIndex(List<SongNode> songList, Map<String,Integer> songIndexMap) {
        if (songIndexMap == null) {
            logger.warn("No " + SONG_INDEX_FILE_NAME + " file defined !");
            return;
        }

        for (SongNode songNode : songList) {
            File sourceFile = songNode.getSourceFile();
            if (sourceFile == null) {
                logger.warn("Source file not set for " + songNode.getTitle());
                continue;
            }

            Integer index = songIndexMap.get(sourceFile.getName());
            if (index == null) {
                logger.warn("No index for song " + sourceFile.getName());
                continue;
            }

            songNode.setIndex(index);
        }
    }

    private Map<String,Integer> loadSongIndexMap(File indexFile, Charset encoding) {
        try {
            Reader reader = new InputStreamReader(new FileInputStream(indexFile), encoding);
            return loadSongIndexMap(reader);
        } catch (FileNotFoundException ex) {
            logger.error("File {} was not found", indexFile.getAbsolutePath());
            return Collections.emptyMap();
        }
    }

    private Map<String,Integer> loadSongIndexMap(Reader reader) {
        ReaderIterable readerIterable = null;
        try {
            HashMap<String,Integer> result = new HashMap<>();
            int lineNumber = 1;
            readerIterable = new ReaderIterable(new BufferedReader(reader), "song order file !");
            for (String line : readerIterable) {
                result.put(line.trim(), lineNumber);
                lineNumber++;
            }
            return result;
        } finally {
            if (readerIterable != null) {
                readerIterable.close();
            }
        }
    }


    public void saveSongIndexFile(File outputFile, List<SongNode> songList) {
        StringBuilder sb = new StringBuilder();
        for (SongNode songNode : songList) {
            sb.append(songNode.getSourceFile().getName());
            sb.append("\n");
        }
        FileIO.writeStringToFile(outputFile.getAbsolutePath(), StandardCharsets.ISO_8859_1, sb.toString());
    }
}
