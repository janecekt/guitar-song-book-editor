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
package com.songbook.model;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class representing the Chord in the Song.
 * @author Tomas Janecek.
 */
public class ChordNode implements Node {
    private static final Logger logger = LoggerFactory.getLogger(ChordNode.class);

    /** Exception thrown when an exception occurs during transposition. */
    public static class TransposeException extends Exception {
        public TransposeException(String msg) {
            super(msg);
        }
    }

    /** Main chord */
    private final String chord1;

    /** Addition bass chord (optional) */
    private final String chord2;


    /**
     * Constructor - Creates a new instance of ChordNode.
     * @param chord1 Main chord
     * @param chord2 Additional bass chord (optional)
     */
    public ChordNode(String chord1, String chord2) {
        this.chord1 = chord1;
        this.chord2 = chord2;
    }


    /** @return Text representation of the chord. */
    public String getText() {
        return (chord2.isEmpty()) ? chord1 : chord2 + "/" + chord1;
    }


    /**
     * @param transposition Required transposition.
     * @return Transposed main chord.
     */
    public String getChord1(int transposition) {
        return transposeChordWithFailBack(chord1, transposition);
    }


    /**
     * @param transposition Required transposition
     * @return Transposed bass chord - or empty string if no bass chord specified.
     */
    public String getChord2(int transposition) {
        return chord2.isEmpty() ? "" : transposeChordWithFailBack(chord2, transposition);
    }


    /** {@inheritDoc} */
    @Override
    public String getAsText(int transposition) {
        if (chord2.isEmpty()) {
            return "[" + transposeChordWithFailBack(chord1, transposition) + "]";
        } else {
            return "[" + transposeChordWithFailBack(chord1, transposition) + "/" + transposeChordWithFailBack(chord2, transposition) + "]";
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getAsHTML(int transposition) {
        String out = "<SPAN class=\"chord\">";

        out += transposeChordWithFailBack(chord1, transposition);

        if (!chord2.isEmpty()) {
            out += "/" + transposeChordWithFailBack(chord2, transposition);
        }

        out += "</SPAN>";

        return out;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ChordNode[ chord1=" + chord1 + " , chord2=" + chord2 + "]";
    }


    // === STATIC CONTEXT === //
    /** Chord to Idx conversion map. */
    private static HashMap<String, Integer> chordToIdx;

    /** Idx to Chord conversation map. */
    private static HashMap<Integer, String> idxToChord;


    private static String transposeChord(String chord, int trans) throws TransposeException {
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

        return idxToChord.get((chordIdx + 12 + (trans % 12)) % 12) + suffix;
    }

    private static String transposeChordWithFailBack(String chord, int trans) {
        try {
            return transposeChord(chord, trans);
        } catch (TransposeException ex) {
            logger.error(ex.getMessage());
            return chord;
        }
    }
}
