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

import java.util.HashMap;
import java.util.logging.*;

/** 
 * Class representing the Chord in the Song.
 * @author Tomas Janecek.
 */
public class ChordNode implements Node {
	
	/** Exception thrown when an exception occurs during Trnasposition. */
	public static class TransposeException extends Exception {
		public TransposeException(String msg) {
			super(msg);
		}
	}
	
	
	/** Main chord */
	public String chord1;
	
	/** Addition bass chord (optional) */
	public String chord2;
	
	
	/** 
	 * Constructor - Creates a new instance of ChordNode.
	 * @param chord1	Main chord
	 * @param chord2    Additiona bass chord (optional)
	 */
	public ChordNode(String chord1, String chord2) {
		this.chord1 = chord1;
		this.chord2 = chord2;
	}


	/** See - Node.getAsText. */
	public String getAsText(int trans) {
		if (chord2.isEmpty()) {
			try {
				return "[" + transposeChord(chord1, trans) + "]";
			} catch (TransposeException ex) {
				Logger.getLogger("songer").log(Level.SEVERE, ex.getMessage(), ex);
				return "[" + chord1 + "]";
			}
		} else {
			try {
				return "[" + transposeChord(chord1, trans) + "/" + transposeChord(chord2, trans) + "]";
			} catch (TransposeException ex) {
				Logger.getLogger("songer").log(Level.SEVERE, ex.getMessage(), ex);
				return "[" + chord1 + "/" + chord2 + "]";
			}
		}
	}

	
	
	/** See - Node.getAsHTML. */
	public String getAsHTML(int trans) {
		try {
			String out = "<SPAN class=\"chord\">";

			out += transposeChord(chord1, trans);

			if (!chord2.isEmpty()) {
				out += "/" + transposeChord(chord2, trans);
			}

			out += "</SPAN>";

			return out;
		} catch (TransposeException ex) {
			Logger.getLogger("songer").log(Level.SEVERE, ex.getMessage(), ex);
			String out = "<SPAN class=\"invchord\">" + chord1;
			if (!chord2.isEmpty()) {
				out += "/" + chord2;
			}
			out += "</SPAN>";
			return out;
		}
	}

	
	/** See - Node.getAsExportHTML. */
	public String getAsExportHTML(int trans) {
		try {
			String out = "<SPAN class=\"chord\">";
			out += "<SPAN title=\"chord\">" + transposeChord(chord1, trans) + "</SPAN>";

			if (!chord2.isEmpty()) {
				out += "/" + "<SPAN title=\"chord\">" + transposeChord(chord2, trans) + "</SPAN>";
			}

			out += "</SPAN>";

			return out;
		} catch (TransposeException ex) {
			Logger.getLogger("songer").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	
	
	/** See - Node.getAsLaTex. */
	public String getAsLaTex(int trans) {
		String out = "";

		if (chord2.isEmpty()) {
			try {
				out = "\\chord{" + transposeChord(chord1, trans) + "}";
			} catch (TransposeException ex) {
				Logger.getLogger("songer").log(Level.SEVERE, ex.getMessage(), ex);
				return null;
			}
		} else {
			try {
				out = "\\chord{" + transposeChord(chord1, trans) + "/" + transposeChord(chord2, trans) + "}";
			} catch (TransposeException ex) {
				Logger.getLogger("songer").log(Level.SEVERE, ex.getMessage(), ex);
				return null;
			}
		}

		return out.replaceAll("#", "\\\\#");
	}

	
	
	/** See - Object.toString. */
	@Override
	public String toString() {
		return "ChordNode[ chord1=" + chord1 + " , chord2=" + chord2 + "]";
	}
	
	
	// === STATIC CONTEXT === //
	/** Chord to Idx conversion map. */
	public static HashMap<String, Integer> chordToIdx;
	
	/** Idx to Chord converstion map. */
	public static HashMap<Integer, String> idxToChord;
	
	
	public static String transposeChord(String chord, int trans) throws TransposeException {
		// Initialize hashMaps if necessary
		if (chordToIdx == null) {
			chordToIdx = new HashMap<String, Integer>();
			idxToChord = new HashMap<Integer, String>();
			chordToIdx.put("C" , 0 );   idxToChord.put(0 , "C" );
			chordToIdx.put("C#", 1 );   idxToChord.put(1 , "C#");
			chordToIdx.put("D" , 2 );   idxToChord.put(2 , "D" );
			chordToIdx.put("D#", 3 );
			chordToIdx.put("Es", 3 );   idxToChord.put(3 , "Es");
			chordToIdx.put("E" , 4 );   idxToChord.put(4 , "E" );
			chordToIdx.put("F" , 5 );   idxToChord.put(5 , "F" );
			chordToIdx.put("F#", 6 );   idxToChord.put(6 , "F#");
			chordToIdx.put("G" , 7 );   idxToChord.put(7 , "G" );
			chordToIdx.put("G#", 8 );   idxToChord.put(8 , "G#");
			chordToIdx.put("As", 8 );   
			chordToIdx.put("A" , 9 );   idxToChord.put(9 , "A" );
			chordToIdx.put("A#", 10);  
			chordToIdx.put("B" , 10);   idxToChord.put(10, "B" );
			chordToIdx.put("H" , 11);   idxToChord.put(11, "H" );
		}


		// Set base and Suffix
		String base, suffix;

		if (chord.length() > 1) {
			base = ((chord.charAt(1) == 's') || (chord.charAt(1) == '#')) ? chord.substring(0, 2) : chord.substring(0, 1);
			suffix = chord.replaceFirst(base, "");
		} else {
			base = chord;
			suffix = "";
		}

		// Transpose base
		Integer chordIdx = chordToIdx.get(base);
		if (chordIdx == null) {
			throw new TransposeException("Chord " + chord + " is not known !");
		}

		return idxToChord.get((chordIdx.intValue() + 12 + (trans % 12)) % 12) + suffix;
	}
}
