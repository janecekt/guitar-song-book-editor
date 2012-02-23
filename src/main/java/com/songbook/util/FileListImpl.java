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
package com.songbook.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.songbook.parser.InputSys;
import com.songbook.parser.LexAn;
import com.songbook.parser.SyntaxAn;
import com.songbook.parser.nodes.SongBook;
import com.songbook.parser.nodes.SongNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class implementing the two-way iteration over the files in a given directory.
 * @author Tomas Janecek
 */
public class FileListImpl implements FileList {
    private static final Logger logger = LoggerFactory.getLogger("com/songbook");

    /** Class representing the Base-Directory */
    private final File baseDir;

    /** Array of files in a baseDirectory */
    private File[] fileArray;

    /** Current index in the array of files */
    private int curIndex;

    private final FileFilter fileFilter;
    private final Comparator<File> fileComparator;


    /** FileFilter matching all files with ".txt" extension. */
    public static class TxtFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            return (file.isFile() && file.getName().endsWith(".txt"));
        }
    }


    /** Comparator comparing two files lexicographically based on their file-name (comparison is case insensitive). */
    public static class FileNameComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }


    /**
     * Constructor - Creates a new FileListImpl instance.
     * @param baseDir        Base-directory for which the FileList will be constructed.
     * @param fileFilter     Specifies which files form the baseDir will be added to the fileList.
     * @param fileComparator Comparator (for File classes) based on which the entries will be sorted.
     */
    public FileListImpl(String baseDir, FileFilter fileFilter, Comparator<File> fileComparator) {
        this.baseDir = new File(baseDir);
        this.fileFilter = fileFilter;
        this.fileComparator = fileComparator;
        rebuild();
    }


    @Override
    public SongBook buildSongBook(String encoding) {
        List<SongNode> songNodeList = new ArrayList<SongNode>();
        for (File file : fileArray) {
            try {
                InputSys inputSys = new InputSys(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), encoding));
                SyntaxAn syntaxAn = new SyntaxAn(new LexAn(inputSys));
                SongNode tmpSongNode = syntaxAn.parse();
                tmpSongNode.setSourceFile(file);
                songNodeList.add(tmpSongNode);
            } catch (SyntaxAn.SyntaxErrorException ex) {
                logger.error("LOADING FAILED - SyntaxError : " + file.getName() + " : " + ex.getMessage(), ex);
            } catch (Exception ex) {
                logger.error("LOADING FAILED - IO Error : " + file.getName() + " : " + ex.getMessage(), ex);
            }
        }
        return new SongBook(songNodeList);
    }


    @Override
    public File getBaseDir() {
        return baseDir;
    }


    @Override
    public void addNewFile(String songName, String encoding) {
        File fileName = new File(baseDir.getAbsolutePath(), songName.replaceAll(" ", "_") + ".txt");
        FileIO.writeStringToFile(fileName.getAbsolutePath(), encoding, songName + "\n\nVerse1");
        logger.info("New file written " + fileName.getAbsolutePath());
        rebuild();
        setCurrent(fileName);
        logger.info("FileList reloaded.");
    }


    /** Rebuilds and updates the FileList, current file is changed to the first file in list. */
    private void rebuild() {
        fileArray = baseDir.listFiles(fileFilter);
        Arrays.sort(fileArray, fileComparator);
        curIndex = 0;
    }


    /**
     * Sets the file whose absolute path matches curAbsolutePath as current file,
     * if not found the current file is not changed.
     */
    private void setCurrent(File curFile) {
        for (int i = 0; i < fileArray.length; i++) {
            if (fileArray[i].getAbsolutePath().equals(curFile.getAbsolutePath())) {
                curIndex = i;
                return;
            }
        }
    }


    /** @return the current file. */
    @Override
    public File getCurrent() {
        return fileArray[curIndex];
    }


    /** @return Content of the current file. */
    @Override
    public String getCurrentFileContent(String encoding) {
        return FileIO.readFileToString(getCurrent().getAbsolutePath(), encoding);
    }


    /** Moves to the next file in the list (if it exists). */
    @Override
    public void gotoNext() {
        if ((curIndex + 1) < fileArray.length) {
            curIndex++;
        }
    }


    /** Moves to the previous file in the list (if it exists). */
    @Override
    public void gotoPrevious() {
        if (curIndex > 0) {
            curIndex--;
        }
    }
}
