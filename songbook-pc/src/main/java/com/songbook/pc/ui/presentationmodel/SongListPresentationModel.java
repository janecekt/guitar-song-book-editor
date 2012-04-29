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
import java.util.ArrayList;
import java.util.List;

import com.jgoodies.binding.list.SelectionInList;
import com.songbook.core.model.SongBook;
import com.songbook.core.model.SongNode;
import com.songbook.core.util.FileIO;
import com.songbook.core.util.SongNodeLoader;

public class SongListPresentationModel {
    private final File baseDir;
    private final SongNodeLoader loader;
    private final SelectionInList<SongNode> songListModel = new SelectionInList<SongNode>();


    public SongListPresentationModel(File baseDir, SongNodeLoader loader) {
        this.baseDir = baseDir;
        this.loader = loader;
    }


    public void reloadFromDisk(String encoding) {
        // Reload songs form disk
        List<SongNode> newSongList = loader.loadSongNodesFromDirectory(baseDir, encoding);

        // Refresh song list
        songListModel.getList().clear();
        songListModel.getList().addAll(newSongList);
    }


    public void saveCurrent(String encoding, String content) {
        SongNode currentSong = songListModel.getValue();
        File sourceFile = currentSong.getSourceFile();
        FileIO.writeStringToFile(sourceFile.getAbsolutePath(), encoding, content);

        // Load file from disk
        SongNode newSongNode = loader.loadSongNodeFromFile(sourceFile, encoding);

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
        SongNode songNode = loader.loadSongNodeFromFile(fileName, encoding);

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
