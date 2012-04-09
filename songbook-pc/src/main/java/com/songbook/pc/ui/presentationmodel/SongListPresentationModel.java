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
package com.songbook.pc.ui.presentationmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.jgoodies.binding.list.SelectionInList;
import com.songbook.core.model.SongBook;
import com.songbook.core.model.SongNode;
import com.songbook.core.parser.Parser;
import com.songbook.core.parser.ParserException;
import com.songbook.pc.util.FileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SongListPresentationModel {
    private static final Logger logger = LoggerFactory.getLogger(SongListPresentationModel.class);

    private final File baseDir;
    private final Parser<SongNode> parser;
    private final SelectionInList<SongNode> songListModel = new SelectionInList<SongNode>();


    public SongListPresentationModel(File baseDir, Parser<SongNode> parser) {
        this.baseDir = baseDir;
        this.parser = parser;
    }


    public void reloadFromDisk(String encoding) {
        // Reload songs form disk
        List<SongNode> newSongList = new ArrayList<SongNode>();
        for (File file : baseDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                SongNode songNode = loadSongFromFile(file, encoding);
                if (songNode != null) {
                    newSongList.add(songNode);
                }
            }
        }

        // Refresh song list
        songListModel.getList().clear();
        songListModel.getList().addAll(newSongList);
    }


    private SongNode loadSongFromFile(File file, String encoding) {
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



    public void saveCurrent(String encoding, String content) {
        SongNode currentSong = songListModel.getValue();
        File sourceFile = currentSong.getSourceFile();
        FileIO.writeStringToFile(sourceFile.getAbsolutePath(), encoding, content);

        // Load file from disk
        SongNode newSongNode = loadSongFromFile(sourceFile, encoding);

        // Update songListModel
        songListModel.getList().remove(currentSong);
        songListModel.getList().add(newSongNode);
        songListModel.setSelection(newSongNode);
    }


    public void addNew(String songName, String encoding) {
        // Create file
        File fileName = new File(baseDir.getAbsolutePath(), songName.replaceAll(" ", "_") + ".txt");
        FileIO.writeStringToFile(fileName.getAbsolutePath(), encoding, songName + "\n\nVerse1");

        // Load file from disk
        SongNode songNode = loadSongFromFile(fileName, encoding);

        // Update songListModel
        songListModel.getList().add(songNode);
        songListModel.setSelection(songNode);
    }


    public SelectionInList<SongNode> getSongListModel() {
        return songListModel;
    }

    public SongBook buildSongBook() {
        return new SongBook(new ArrayList<SongNode>(songListModel.getList()));
    }

    public File getBaseDir() {
        return baseDir;
    }
}
