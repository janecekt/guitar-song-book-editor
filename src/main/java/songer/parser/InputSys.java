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
package songer.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * InputSystem of Songer reading the input and providing the input-tokens for the LexAn.
 * @see LexAn
 * @author Tomas Janecek
 */
public class InputSys {

	/**
	 * Class representing an input token of this Input-System.
	 * @author Tomas Janecek
	 */
	public static class Token {
		/** Declaration of possible types of input tokens. */
		public enum Type { I_UNDEF, I_CHAR, I_LPAR, I_RPAR, I_SLASH, I_EOL, I_EOI };
		
		/** Type of the input-token. */
		protected Token.Type type; 
		
		/** Character of the input-token. */		     
		protected  char character;

		/** Position of the input-token. */
		protected  Position position;

		
		
		/** Constructor - Creates a new input token with the specified parameters.
		 * @param type      Type of the input-token.
		 * @param character Character if the input token. 
		 * @param position  Position of the input-token. 
		 */
		public Token( Token.Type  type, char character, Position position ) {
			this.type = type;
			this.character = character;
			this.position = position;
		}

		/** Returns the type of the input token. */
		public Token.Type getType() { 
			return type; 
		}
	
		/** Returns the character of the input token. */
		public char getCharacter() { 
			return character; 
		}
	
		/** Returns the position of the input token. */
		public Position getPosition() { 
			return position; 
		}
    }
    
    
    
	/**
	 * Class representing a position in the input stream.
	 * @author Tomas Janecek
	 */
	public static class Position {
		/** Position in the input stream (number of characters from the beginning). */
		protected int position;
	
		/** Line number in the input stream. */
		protected int line;
	
		/** Collumn number in the current line of the stream. */
		protected int collumn;

		
		
		/** Costructor - Creates a new instance of position.
		 * @param position  Position in the input stream (number of characters from the beginning).  
		 * @param line      Line number in the input stream.
		 * @param collumn   Collumn number in the current line of the stream. 
		 */
		public Position(int position, int line, int collumn) {
			this.position = position;
			this.line = line;
			this.collumn = collumn;
		}

		/** Returns the position in the input stream ((number of characters from the beginning). */
		public int getPosition() {
			return position;
		}

		/** Retruns the line number in the input stream. */
		public int getLine() {
			return line;
		}

		/** Returns the collumn number in the current line. */
		public int getCollumn() {
			return collumn;
		}
	
		/** Returns the String representation of Possition suitable for insertion into an information message. */
		public String toInfoString(){
			return "line:" + line + " col:" + collumn + " (position=" + position + ")";
		}

		/** @see Object#toString() */
		@Override
		public String toString() {
			return "Position[line=" + line + ", col=" + collumn + ", position=" + position + "]";
		}
	}
	
	
	
	/** Current line. */
	private int curLine;
	
	/** Current collumn. */
	private int curCol;
	
	/** Current position */
	private int curPosition;
	
	/** Reference of the reader. */
	private Reader reader;
	
	/** Current-character - the one which has just been read. */
	private char curChar;

	
	
	/** Constructor - Creates the new instance of InputSys reading the input form the reader specified be the parameter. */
	public InputSys(Reader reader) {
		curLine = 1;
		curCol = 1;
		curPosition = 0;
		this.reader = reader;
		curChar = '\0';
	}

	
	
	/** Returns the next InputSystem token. */
	public Token getNextToken() {
		// set position
		Position tokenPosition = new Position(curPosition, curLine, curCol);

		// read-next char
		getNextChar();

		// analyze nextChar
		Token.Type tokenType = Token.Type.I_UNDEF;
		switch (curChar) {
			case '[':
				tokenType = Token.Type.I_LPAR;
				break;
			case ']':
				tokenType = Token.Type.I_RPAR;
				break;
			case '/':
				tokenType = Token.Type.I_SLASH;
				break;
			case '\n':
				tokenType = Token.Type.I_EOL;
				break;
			case '\0':
				tokenType = Token.Type.I_EOI;
				break;
			default:
				tokenType = Token.Type.I_CHAR;
		}

		// retrun token
		return new Token(tokenType, curChar, tokenPosition);
	}

	
	
	/** INTERNAL: reads the character from the input. */
	private char readChar() throws IOException {
		int out = reader.read();

		if (out != -1) {
			// Update curCol and curRow
			if (out == '\n') {
				curCol = 0;
				curLine++;
			} else {
				curCol++;
			}

			// Update position
			curPosition++;
			
			// Return character
			return ((char) out);
		} else {
			reader.close();
			return '\0';
		}
	}

	
	
	/** INTERNAL: reads the acceptable character from the input (unacceptable characters are automatically skipped). */
	private void getNextChar() {
		try {
			// read next char
			char previous = curChar;
			curChar = readChar();

			// skip '\r' characters
			if (curChar == '\r') {
				curChar = readChar();
			}

			// ignore white spaces following the newline
			if (previous == '\n') {
				while ((curChar == ' ') || (curChar == '\t')) {
					curChar = readChar();
				}
			}
		} catch (Exception ex) {
			curChar = '\0';
		}
	}
}
    