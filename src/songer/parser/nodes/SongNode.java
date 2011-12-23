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


import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;



/** 
 * Class representing the Song.
 * @author Tomas Janecek
 */
public class SongNode implements Node {

	/** Comparator of SongNode classes based on title - uses current locale for comparisons. */
	public static class TitleComparator implements Comparator<SongNode> {

		/** Collator class used for Locale-aware string comparisons. */
		protected Collator collator;

		/** Constructor - Creates the instance of TitleComparator. */
		public TitleComparator() {
			collator = Collator.getInstance(Locale.getDefault());
		}

		/** Compare method - see Comparator.compare. */
		public int compare(SongNode o1, SongNode o2) {
			return collator.compare(o1.getTitle(), o2.getTitle());
		}
	}
	
	
	
	/** Title of the Song. */
	private String title;
	
	/** List of Verses of the song (represented by VerseNode classes). */
	private List<VerseNode> verseList;

	
	
	/** 
	 * Constructor - creates an instance of SongNode.
	 * @param title      Title of the song.
	 * @param verseList  List of verses of the song (represented by VerseNode classes).
	 */
	public SongNode(String title, List<VerseNode> verseList) {
		this.title = title;
		this.verseList = verseList;
	}

	
	
	/** Returns the title of the song. */
	public String getTitle() {
		return title;
	}

	
	
	/** See - Node.getAsText. */
	public String getAsText(int trans) {
		String out = title;
		out += "\n\n\n";

		for (VerseNode verseNode : verseList) {
			out += verseNode.getAsText(trans);
			out += "\n\n";
		}

		return out;
	}

	
	
	/** See - Node.getAsHTML. */
	public String getAsHTML(int trans) {
		String out = "<DIV class=\"title\">" + title + "</DIV>\n";

		for (VerseNode verseNode : verseList) {
			out += verseNode.getAsHTML(trans) + "\n\n";
		}

		return out;
	}

	
	
	/** See - Node.getAsExportHTML. */
	public String getAsExportHTML(int trans) {
		String out = "<HTML>\n";
		out += "<HEAD>\n";
		out += "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf8\">\n";
		out += "  <link href=\"song.css\" rel=\"stylesheet\" type=\"text/css\">\n";
		out += "  <SCRIPT src=\"song.js\" type=\"text/javascript\"></SCRIPT>\n";
		out += "  <TITLE>" + title + "</TITLE>\n";
		out += "</HEAD>\n";
		out += "<BODY>\n\n";

		out += "<DIV class=\"title\">" + title + "</DIV>\n\n";

		out += "<DIV class=\"transpose\">\n";
		out += "Transpozice: <SPAN id=\"totaltranspose\">0</SPAN>\n";
		out += "[<a href=\"javascript:transpose(1)\">+1</a>]\n";
		out += "[<a href=\"javascript:transpose(-1)\">-1</a>]\n";
		out += "</DIV>\n\n";

		for (VerseNode verseNode : verseList) {
			out += verseNode.getAsExportHTML(trans) + "\n\n";
		}

		out += "</BODY>\n";
		out += "</HTML>\n";

		return out;
	}

	
	
	/** See - Node.getAsLaTex. */
	public String getAsLaTex(int trans) {
		String out = "\n\n\n\\begin{song}{" + title + "}\n";

		for (VerseNode verseNode : verseList) {
			out += verseNode.getAsLaTex(trans) + "\n\n";
		}
		out += "\\end{song}\n\n\n";
		return out;
	}

	
	
	/** See - Object.toString. */
	@Override
	public String toString() {
		String out = "SongNode[" + "title=" + title + ",\n";

		for (VerseNode verseNode : verseList) {
			out += verseNode.toString() + ",\n";
		}

		out += "]";
		return out;
	}
}
