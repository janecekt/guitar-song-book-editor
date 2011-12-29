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
package com.songbook.parser;

import java.util.LinkedList;
import java.util.List;

import com.songbook.parser.nodes.ChordNode;
import com.songbook.parser.nodes.LineNode;
import com.songbook.parser.nodes.Node;
import com.songbook.parser.nodes.SongNode;
import com.songbook.parser.nodes.TextNode;
import com.songbook.parser.nodes.VerseNode;

/**
 * LL1 Syntax Analyzer of the plain text song representation - parses the text into a tree-like internal representation.
 * <PRE>
 * Implements the following LL1 grammar:
 *
 * (eol,string)   S -> N string N B
 *
 * (eol)          N -> eol N
 * (jinak)        N -> eps
 *
 * ( [,string )   B -> SL SLSEP B
 * ( eoi )        B -> eps
 *
 * (eol)          SLSEP -> eol N
 * (eoi)          SLSEP -> eps
 *
 * ( [,string )   SL   -> LN LNSEP ZbSL
 *
 * ( [,string )   ZbSL -> LN LNSEP ZbSL
 * ( eol,eoi )    ZbSL -> eps
 * ( [ )          LN-> [ AK ] ZbLN
 * (string)       LN-> string ZbLN
 *
 * (eol)          LNSEP -> eol
 * ([,string,eoi) LNSEP -> eps
 *
 * ( [ )          ZbLN -> [ AK ] ZbLN
 * (string)       ZbLN -> string ZbLN
 * ( eol )        ZbLN -> eol
 * ( eoi )        ZbLN -> eps
 *
 *
 * (string)       AK   -> string ZbAk
 *
 * ( / )          ZbAk -> slash string
 * ( ] )          ZbAk -> eps
 *
 *
 * S     - represents a song consisting of TITLE and BODY
 * N     - represents 0-N empty lines
 * B     - represents the body (0-N verses separated by 2 or more empty lines).
 * SLSEP - represents verse separator (1-N new lines).
 * SL    - represents verse (1-N verse lines separated my 0 or 1 new lines)
 * ZbSL  - represents the rest of the verse (0-N verse lines)
 * LN    - represents on line
 * LNSEP - represents line separator ( nothing or one empty line )
 * Ak    - represents chord ("D" or "D/F#")
 * ZbAK  - represents the rest of chod ( "" or "/F#" )
 * </PRE>
 *
 * @author Tomas Janecek
 */
public class SyntaxAn {

    /**
     * Class representing an Exception thrown when syntax error is encountered.
     * @author Tomas Janecek
     */
    public class SyntaxErrorException extends Exception {
        /** Position where the syntax error occurred. */
        private final InputSys.Position position;

        /** Constructor - specify the description and the position of syntax error. */
        public SyntaxErrorException(String message, InputSys.Position position) {
            super(message);
            this.position = position;
        }

        /** @see Throwable#getMessage() */
        @Override
        public String getMessage() {
            return "SyntaxError: " + super.getMessage() + " ... at " + position.toInfoString();
        }
    }


    /** Next lexical-element in the input. */
    private LexAn.LexElement nextLexElement;

    /** Lexial Analyzer */
    private final LexAn lexAn;


    /**
     * Constructor - creates the syntax analyzer.
     * @param lexAn Lexical-Analyzer used to obtain lexical symbols.
     */
    public SyntaxAn(LexAn lexAn) {
        this.lexAn = lexAn;
        this.nextLexElement = lexAn.getNextElement();
    }


    /**
     * Parses the input returning internal tree-representation whose root is SongNode.
     * @return SongNode representing this song.
     * @throws SyntaxErrorException if a syntax-error is encountered during syntax analysis.
     */
    public SongNode parse() throws SyntaxErrorException {
        return S();
    }


    /**
     * INTERNAL: Implements the "match operation" -
     * If the next lexical element is of the type lexType then
     * next lexical element is read
     * otherwise SyntaxErrorException is thrown.
     */
    protected LexAn.LexElement srovnani(LexAn.LexElement.Type lexType) throws SyntaxErrorException {
        if (nextLexElement.getType() != lexType) {
            throw new SyntaxErrorException("Expected lexical type " + lexType, nextLexElement.getPosition());
        }

        LexAn.LexElement tmp = nextLexElement;
        nextLexElement = lexAn.getNextElement();
        return tmp;
    }


    /**
     * INTERNAL: Parses the non-terminal symbol S.
     * <PRE>
     * (eol,string)   S -> N string N B
     * </PRE>
     *
     * @return SongNode representing this song.
     */
    protected SongNode S() throws SyntaxErrorException {
        switch (nextLexElement.getType()) {
            // (eol,string)   S -> N string N B  //
            case L_EOL:
            case L_STRING:
                N();
                LexAn.LexElement titleLex = srovnani(LexAn.LexElement.Type.L_STRING);
                N();
                List<VerseNode> verseList = new LinkedList<VerseNode>();
                B(verseList);
                return new SongNode(titleLex.getText(), verseList);
        }

        // Syntax ERROR
        throw new SyntaxErrorException("S: Expected EOL or STRING", nextLexElement.getPosition());
    }


    /**
     * INTERNAL: Parses the non-terminal symbol N.;
     * <PRE>
     * (eol)        N -> eol N
     * (jinak)      N -> eps
     * </PRE>
     */
    protected void N() throws SyntaxAn.SyntaxErrorException {
        switch (nextLexElement.getType()) {
            // (eol)        N -> eol N  //
            case L_EOL:
                srovnani(LexAn.LexElement.Type.L_EOL);
                N();
                return;

            // (jinak)      N -> eps	//
            default:
        }
    }


    /**
     * INTERNAL: Parses the non-terminal symbol B.
     * <PRE>
     * ( [,string )  B -> SL SLSEP B
     * ( eoi )       B -> eps
     * </PRE>
     *
     * @param verseList List of Verses to be extended by VerseNodes found in this song.
     */
    protected void B(List<VerseNode> verseList) throws SyntaxErrorException {
        switch (nextLexElement.getType()) {
            // ( [,string )   B -> SL SLSEP B   //
            case L_LPAR:
            case L_STRING:
                verseList.add(SL());
                SLSEP();
                B(verseList);
                return;

            //  ( eoi )       B -> eps   //
            case L_EOI:
                return;
        }

        // Syntax error
        throw new SyntaxErrorException("B: Expected [, STRING or EOI", nextLexElement.getPosition());
    }


    /**
     * INTERNAL: Parses the non-terminal symbol SLSEP.
     * <PRE>
     * (eol)        SLSEP -> eol N
     * (eoi)        SLSEP -> eps
     * </PRE>
     */
    protected void SLSEP() throws SyntaxErrorException {
        switch (nextLexElement.getType()) {
            // (eol)        SLSEP -> eol N   //
            case L_EOL:
                srovnani(LexAn.LexElement.Type.L_EOL);
                N();
                return;

            // (eoi)        SLSEP -> eps    //
            case L_EOI:
                return;
        }

        // Syntax error
        throw new SyntaxErrorException("SLSEP: Expected EOL or EOI", nextLexElement.getPosition());
    }


    /**
     * INTERNAL: Parses the non-terminal symbol SL.
     * <PRE>
     * ( [,string )   SL   -> LN LNSEP ZbSL
     * </PRE>
     *
     * @return VerseNode representing the verse.
     */
    protected VerseNode SL() throws SyntaxErrorException {
        switch (nextLexElement.getType()) {
            // ( [,string )   SL   -> LN LNSEP ZbSL  //
            case L_LPAR:
            case L_STRING:
                List<LineNode> lineList = new LinkedList<LineNode>();
                lineList.add(LN());
                LNSEP();
                ZbSL(lineList);
                return new VerseNode(lineList);
        }

        // Syntax error
        throw new SyntaxErrorException("SL: Expected [ or STRING", nextLexElement.getPosition());
    }


    /**
     * INTERNAL: Parses the non-terminal symbol ZbSL.
     * <PRE>
     * ( [,string )   ZbSL -> LN LNSEP ZbSL
     * ( eol,eoi )    ZbSL -> eps
     * </PRE>
     *
     * @param lineList List of LineNodes to be extended by LineNodes found in this verse.
     */
    protected void ZbSL(List<LineNode> lineList) throws SyntaxErrorException {
        switch (nextLexElement.getType()) {
            // ( [,string )   ZbSL -> LN LNSEP ZbSL   //
            case L_LPAR:
            case L_STRING:
                lineList.add(LN());
                LNSEP();
                ZbSL(lineList);
                return;

            // ( eol,eoi )    ZbSL -> eps     //
            case L_EOL:
            case L_EOI:
                return;
        }

        // Syntax error
        throw new SyntaxErrorException("ZbSL: Expected [, STRING, EOL or EOI", nextLexElement.getPosition());
    }


    /**
     * INTERNAL: Parses the non-terminal symbol LN.
     * <PRE>
     * ( [ )         LN-> [ AK ] ZbLN
     * (string)      LN-> string ZbLN
     * </PRE>
     *
     * @return LineNode representing the line.
     */
    protected LineNode LN() throws SyntaxErrorException {
        List<Node> contentList;

        switch (nextLexElement.getType()) {
            // ( [ )         LN-> [ AK ] ZbLN  //
            case L_LPAR:
                contentList = new LinkedList<Node>();
                srovnani(LexAn.LexElement.Type.L_LPAR);
                contentList.add(AK());
                srovnani(LexAn.LexElement.Type.L_RPAR);
                ZbLN(contentList);
                return new LineNode(contentList);

            // (string)      LN-> string ZbLN  //
            case L_STRING:
                contentList = new LinkedList<Node>();
                LexAn.LexElement textLex = srovnani(LexAn.LexElement.Type.L_STRING);
                contentList.add(new TextNode(textLex.getText()));
                ZbLN(contentList);
                return new LineNode(contentList);
        }

        // Syntax error
        throw new SyntaxErrorException("LN: Expected [ or STRING", nextLexElement.getPosition());
    }


    /**
     * INTERNAL: Parses the non-terminal symbol LNSEP.
     * <PRE>
     * (eol)           LNSEP -> eol
     * ([,string,eoi)  LNSEP -> eps
     * </PRE>
     */
    protected void LNSEP() throws SyntaxErrorException {
        switch (nextLexElement.getType()) {
            // ( eol )         LNSEP->eol   //
            case L_EOL:
                srovnani(LexAn.LexElement.Type.L_EOL);
                return;

            // ([,string,eoi)  LNSEP -> eps    //
            case L_LPAR:
            case L_STRING:
            case L_EOI:
                return;
        }

        // Syntax error
        throw new SyntaxErrorException("LNSEP Expected EOL,[, STRING or EOI", nextLexElement.getPosition());
    }


    /**
     * INTERNAL: Parses the non-terminal symbol ZbLN.
     * <PRE>
     * ( [ )        ZbLN -> [ AK ] ZbLN
     * (string)     ZbLN -> string ZbLN
     * ( eol )      ZbLN -> eol
     * ( eoi )      ZbLN -> eps
     * </PRE>
	 *
     * @param contentList List of Nodes to be extended by ChordNode and TextNode found on this line.
     */
    protected void ZbLN(List<Node> contentList) throws SyntaxErrorException {
        switch (nextLexElement.getType()) {
            // ( [ )        ZbLN -> [ AK ] ZbLN   //
            case L_LPAR:
                srovnani(LexAn.LexElement.Type.L_LPAR);
                contentList.add(AK());
                srovnani(LexAn.LexElement.Type.L_RPAR);
                ZbLN(contentList);
                return;

            // (string)     ZbLN -> string ZbLN   //
            case L_STRING:
                LexAn.LexElement textLex = srovnani(LexAn.LexElement.Type.L_STRING);
                contentList.add(new TextNode(textLex.getText()));
                ZbLN(contentList);
                return;

            // ( eol )      ZbLN -> eol  //
            case L_EOL:
                srovnani(LexAn.LexElement.Type.L_EOL);
                return;

            // ( eoi )      ZbLN -> eps //
            case L_EOI:
                return;
        }

        // Syntax error
        throw new SyntaxErrorException("ZbLN: Expected [, STRING, EOL or EOI", nextLexElement.getPosition());
    }


    /**
     * INTERNAL: Parses the non-terminal symbol AK.
     * <PRE>
     * (string)      AK   -> string ZbAk
     * </PRE>
     *
     * @return ChordNode representing the Chord.
     */
    protected ChordNode AK() throws SyntaxErrorException {
        switch (nextLexElement.getType()) {
            //  (string)      AK   -> string ZbAk //
            case L_STRING:
                LexAn.LexElement chordLex = srovnani(LexAn.LexElement.Type.L_STRING);
                String chord2 = ZbAk();
                return new ChordNode(chordLex.getText(), chord2);
        }

        // Syntax error
        throw new SyntaxErrorException("AK: Expected STRING", nextLexElement.getPosition());
    }


    /**
     * INTERNAL: Parses the non-terminal symbol ZbAK.
     * <PRE>
     * ( / )        ZbAk -> slash string
     * ( ] )        ZbAk -> eps
     * </PRE>
     *
     * @return String representing ZbAk.
     */
    protected String ZbAk() throws SyntaxErrorException {
        switch (nextLexElement.getType()) {
            // ( / )        ZbAk -> slash string  //
            case L_SLASH:
                srovnani(LexAn.LexElement.Type.L_SLASH);
                LexAn.LexElement chordLex = srovnani(LexAn.LexElement.Type.L_STRING);
                return chordLex.getText();

            // ( ] )        ZbAk -> eps  //
            case L_RPAR:
                return "";
        }

        // Syntax Error
        throw new SyntaxErrorException("ZbAK: Expected / or ]", nextLexElement.getPosition());
    }
}
