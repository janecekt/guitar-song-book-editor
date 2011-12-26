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
 * Class representing one Verse of the song.
 * @author Tomas Janecek
 */
public class VerseNode implements Node {
	/** List of lines in a verse */
	protected List<LineNode> lines;
	
	/** Bollean flag indicating whether the song contains any chords. */
	private boolean containsChords;

	
	
	/** 
	 * Constructor - Creates a new instance of verseNode representing a Verse.
	 * @param lines  List of lines in the verse (represented by LineNode classes).
	 */
	public VerseNode(List<LineNode> lines) {
		this.lines = lines;

		this.containsChords = false;
		for (LineNode lineNode : lines) {
			if (lineNode.hasChords()) {
				this.containsChords = true;
				break;
			}

		}
	}

	
	
	/** See Node.getAsText */
	public String getAsText(int trans) {
		String out = "";
		for (LineNode lineNode : lines) {
			out += lineNode.getAsText(trans) + "\n";
			if (containsChords) {
				out += "\n";
			}
		}

		return out;
	}

	
	
	/** See Node.getAsHTML */
	public String getAsHTML(int trans) {
		String out = "<DIV class=\"verse\">\n";

		for (LineNode lineNode : lines) {
			out += lineNode.getAsHTML(trans) + "<BR />\n";
		}

		out += "</DIV>";
		return out;
	}

	
	
	/** See Node.getAsExportHTML */
	public String getAsExportHTML(int trans) {
		String out = "<DIV class=\"verse\">\n";

		for (LineNode lineNode : lines) {
			out += lineNode.getAsExportHTML(trans) + "<BR />\n";
		}

		out += "</DIV>";
		return out;
	}

	
	
	/** See Node.getLaTex() */
	public String getAsLaTex(int trans) {
		String out = "\t\\begin{songverse}\n";

		for (Iterator<LineNode> it = lines.iterator(); it.hasNext();) {
			LineNode lineNode = it.next();
			out += lineNode.getAsLaTex(trans);
			out += (it.hasNext()) ? "\\\\ \n" : "\n";
		}

		out += "\t\\end{songverse}\n";
		return out;
	}

	
	
	/** Override - see Object.toString(). */
	@Override
	public String toString() {
		String out = "\t" + "VerseNode[\n";
		for (LineNode lineNode : lines) {
			out += lineNode.toString() + ",\n";
		}
		out += "\t]";
		return out;
	}
}
