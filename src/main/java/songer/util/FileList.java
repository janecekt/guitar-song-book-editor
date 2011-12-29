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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import songer.parser.InputSys;
import songer.parser.LexAn;
import songer.parser.SyntaxAn;
import songer.parser.nodes.SongBook;
import songer.parser.nodes.SongNode;

/**
 * Class implementing the two-way iteration over the files in a given directory.
 * @author Tomas Janecek
 */
public class FileList implements Iterable<File> {
    private static final Logger logger = LoggerFactory.getLogger("songer");
    
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

	
	
	/** Comparator comparing two files lexicographically based on their file-name (comparison is case insensitive). */
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
            logger.error("Unsupported encoding.", ex);
        } catch (FileNotFoundException ex) {
            logger.error("Failed to write file " + fileName, ex);
        } catch (IOException ex) {
            logger.error("Failed to write file " + fileName, ex);
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

	/**
     * Sets the file whose absolute path matches curAbsolutePath as current file,
	 * if not found the current file is not changed.
     */
	private void setCurrent(String curAbsolutePath) {
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].getAbsolutePath().equals(curAbsolutePath)) {
				curIndex = i;
				return;
			}
		}
	}

	
	
	/** @return the current file. */
	public File getCurrent() {
		return fileArray[curIndex];
	}


    /** @return Content of the current file. */
    public String getCurrentFileContent(String encoding) {
        try {
            return FileIO.readFileToString(getCurrent().getAbsolutePath(), encoding);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load current file - " + getCurrent().getAbsolutePath(), ex);
        }
    }

	
	/** @return true if there exits a next file in the list. */
	public boolean hasNext() {
		return ((curIndex + 1) < fileArray.length);
	}

	
	/** Moves to the next file in the list (if it exists). */
	public void gotoNext() {
		if (hasNext()) {
			curIndex++;
		}
	}


	/** @retrun true if there exists a previous file in the list. */
	public boolean hasPrevious() {
		return (curIndex > 0);
	}


	/** Moves to the previous file in the list (if it exists). */
	public void gotoPrevious() {
		if (hasPrevious()) {
			curIndex--;
		}
	}


	/** Returns the iterator iterating over all files in this list. */
	public Iterator<File> iterator() {
		return new ObjectArrayIterator<File>(fileArray);
	}
}
