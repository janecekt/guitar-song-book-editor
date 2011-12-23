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
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Class implementing the two-way iteration over the files in a given directory.
 * @author Tomas Janecek
 */
public class FileList implements Iterable<File> {
	/** Class representing the Base-Directory */
	protected File baseDir;
	
	/** Array of files in a baseDirectory */
	protected File[] fileArray;
	
	/** Current index in the array of files */
	protected int curIndex;

	
	
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
		rebuild(fileFilter, fileComparator);
	}

	
	
	/** Rebuilds and updates the FileList, current file is chaned to the first file in list.
	 *  @param fileFilter     Specifies which files form the baseDir will be added to the fileList.
	 *  @param fileComparator Comparator (for File classes) based on which the enteries will be sorted.
	 */
	public void rebuild(FileFilter fileFilter, Comparator<File> fileComparator) {
		fileArray = baseDir.listFiles(fileFilter);
		Arrays.sort(fileArray, fileComparator);
		curIndex = 0;
	}

	
	
	/** Returns the base-directory for which this FileList was costructed. */
	public File getBaseDir() {
		return baseDir;
	}

	
	
	/** Sets the file whose absolute path matches curAbsoltePath as current file, 
	 *  if not found the curent file is not changed. */
	public void setCurrent(String curAbsolutePath) {
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
