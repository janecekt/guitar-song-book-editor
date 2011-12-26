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
package songer.util;

import songer.*;
import songer.parser.InputSys;
import songer.parser.LexAn;
import songer.parser.SyntaxAn;
import songer.parser.nodes.SongNode;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class implementing the two-way iteration over the files in a given directory.
 * @author Tomas Janecek
 */
public class FileList implements Iterable<File> {
    private static final Logger logger = Logger.getLogger("songer");
    
	/** Class representing the Base-Directory */
	protected File baseDir;
	
	/** Array of files in a baseDirectory */
	protected File[] fileArray;
	
	/** Current index in the array of files */
	protected int curIndex;
    
    private FileFilter fileFilter;
    private Comparator<File> fileComparator;

	
	
	/** FileFilter matching all files with ".txt" extension. */
	public static class TxtFileFilter implements FileFilter {
		public boolean accept(File file) {
			return (file.isFile() && file.getName().endsWith(".txt"));
		}
	}

	
	
	/** Comparator comparing two files lexicographicaly based on their file-name (comparison is case insensitive). */
	public static class FileNameComparator implements Comparator<File> {
		public int compare(File f1, File f2) {
			return f1.getName().compareToIgnoreCase(f2.getName());
		}
	}

	
	
	/** Construcor - Creates a new FileList instance.
	 *  @param baseDir        Base-directory for which the FileList will be constructed.
	 *  @param fileFilter     Specifies which files form the baseDir will be added to the fileList.
	 *  @param fileComparator Comparator (for File classes) based on which the enteries will be sorted.
	 */
	public FileList(String baseDir, FileFilter fileFilter, Comparator<File> fileComparator) {
		this.baseDir = new File(baseDir);
        this.fileFilter = fileFilter;
        this.fileComparator = fileComparator;
		rebuild();
	}


    public File getBaseDir() {
        return baseDir;
    }

    public void addNewFile(String songName, String encoding) {
        String fileName = baseDir.getAbsolutePath() + "/" + songName.replaceAll(" ", "_") + ".txt";
        try {
            FileIO.writeStringToFile(fileName, encoding, songName + "\n\nVerse1");
            logger.info("New file written " + fileName);
            rebuild();
            setCurrent(fileName);
            logger.info("FileList reloaded.");
        } catch (UnsupportedEncodingException ex) {
            logger.severe("Unsupported encoding.");
        } catch (FileNotFoundException e) {
            logger.severe("Failed to write file " + fileName);
        } catch (IOException e) {
            logger.severe("Failed to write file " + fileName);
        }
    }

	
	
	/**
     * Rebuilds and updates the FileList, current file is chaned to the first file in list.
	 */
	private void rebuild() {
		fileArray = baseDir.listFiles(fileFilter);
		Arrays.sort(fileArray, fileComparator);
		curIndex = 0;
	}

	/** Sets the file whose absolute path matches curAbsoltePath as current file, 
	 *  if not found the curent file is not changed. */
	private void setCurrent(String curAbsolutePath) {
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].getAbsolutePath().equals(curAbsolutePath)) {
				curIndex = i;
				return;
			}
		}
	}

	
	
	/** Returns the current file. */
	public File getCurrent() {
		return fileArray[curIndex];
	}


    public String getCurrentFileContent(String encoding) {
        try {
            return FileIO.readFileToString(getCurrent().getAbsolutePath(), encoding);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load current file - " + getCurrent().getAbsolutePath(), ex);
        }
    }

	
	/** Returns true if there exits a next file in the list. */
	public boolean hasNext() {
		return ((curIndex + 1) < fileArray.length);
	}

	
	
	/** Moves to the next file in the list (if it exists). */
	public void gotoNext() {
		if (hasNext()) {
			curIndex++;
		}
	}

	
	
	/** Retruns true if there exists a previous file in the list. */
	public boolean hasPrevious() {
		return (curIndex > 0);
	}

	
	
	/** Moves to the previos file in the list (if it exists). */
	public void gotoPrevious() {
		if (hasPrevious()) {
			curIndex--;
		}
	}

	
	
	/** Returns the itrerator iterating over all files in this list. */
	public Iterator<File> iterator() {
		return new ObjectArrayIterator<File>(fileArray);
	}
}
