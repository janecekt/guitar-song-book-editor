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
package songer.parser.nodes;

import java.util.Iterator;
import java.util.List;

/** 
 * Class representing one line of the verse of the song.
 * @author Tomas Janecek
 */
public class LineNode implements Node {
	/** List of fragments contained on the line (ChorNode or TextNode classes). */
	private List<Node> contentList;
	
	/** True if the line has chords. */
	private boolean hasChords;

	
	
	/** 
	 * Constructor - Creates the new instance of LineNode.
	 * @param contentList List of fragments contained on the line (ChorNode or TextNode classes).
	 */
	public LineNode(List<Node> contentList) {
		this.contentList = contentList;

		hasChords = false;
		for (Node node : contentList) {
			if (node instanceof ChordNode) {
				hasChords = true;
				break;
			}
		}
	}

	
	
	/** Returns true it the line hasChord; false otherwise. */
	public boolean hasChords() {
		return hasChords;
	}

	
	
	/** See - Node.getAsText. */
	public String getAsText(int trans) {
		String out = "";

		for (Node node : contentList) {
			out += node.getAsText(trans);
		}

		return out;
	}

	
	
	/** See - Node.getAsHTML. */
	public String getAsHTML(int trans) {
		String out = "";
		for (Iterator<Node> it = contentList.iterator(); it.hasNext();) {
			Node node = it.next();
			out += node.getAsHTML(trans);
		}
		return out;
	}

	
	
	/** See - Node.getAsExportHTML. */
	public String getAsExportHTML(int trans) {
		String out = "";
		for (Node node : contentList) {
			out += node.getAsExportHTML(trans);
		}
		return out;

	}

	
	
	/** See - Node.getAsLaText. */
	public String getAsLaTex(int trans) {
		String out = "\t\t";
		for (Node node : contentList) {
			out += node.getAsLaTex(trans);
		}
		return out;
	}

	
	
	/** See - Object.toString. */
	@Override
	public String toString() {
		String out = "\t\t" + "LineNode[\n";
		for (Node node : contentList) {
			out += "\t\t\t" + node.toString() + ",\n";
		}
		out += "\t\t]";
		return out;
	}
}
