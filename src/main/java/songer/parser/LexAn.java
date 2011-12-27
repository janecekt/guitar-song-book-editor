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


/** 
 * Lexical-Analyzer of Songer providing Lexical-Elements for the SyntaxAnalyzer.
 * @see SyntaxAn
 * @author Tomas Janecek
 */
public class LexAn {

	/** 
	 * Class representing the Lexical-Element of this Lexical-Analyzer. 
	 * @author Tomas Janecek
	 */
	public static class LexElement {
		/** Declaration of possible types of lexical elements. */
		public enum Type { L_UNDEF, L_STRING, L_LPAR, L_RPAR, L_SLASH, L_EOL, L_EOI }

        /** Type of lexical-element. */
		protected  LexElement.Type type;

		/** String representation of the lexical-element. */
		protected  String text ; 
		
		/** Position of this lexical element in the input. */
		protected  InputSys.Position position;

		
		
		/** 
		 * Constructor - Creates the new instance of lex element.
		 * @param type Type of lexical element.
		 * @param text String representation of the lexical element.
		 * @param position Position of the beginning of the lexical element in the input.
		 */
		public  LexElement(LexElement.Type type, String text , InputSys.Position  position){
			this.type  = type;
			this.text = text;
			this.position = position;
		}

		/** @return the type of the lexical-element. */
		public LexElement.Type getType() { 
			return type; 
		}
	
		/** @return the string representation of the lexical element. */
		public String getText() { 
			return text; 
		}
	
		/** @return the position of the beginning of the lexical element. */
		public InputSys.Position getPosition(){ 
			return position; 
		}
    }
    
    
    
    
    /** Reference to the input system used to retrieve input symbols. */
    private InputSys inputSys;
    
    /** Reference to the next input token. */
    private InputSys.Token nextToken;
    
    
    
    /** 
	 * Constructor - Creates the new lexical analyzer instance.
     * @param inputSys InputSystem to be used to retrieve input symbols. 
	 */
    public LexAn( InputSys inputSys ){
		this.inputSys = inputSys;
		this.nextToken = inputSys.getNextToken();
    }
     
    
    
    /** @return the next lexical element. */
    public LexElement getNextElement(){
		LexElement.Type lexType;
		String lexText = "";
		InputSys.Position lexPosition = nextToken.getPosition();

		switch (nextToken.getType()) {
			case I_LPAR:
				// L_LPAR: "[" //
				lexType = LexElement.Type.L_LPAR;
				lexText += nextToken.getCharacter();
				nextToken = inputSys.getNextToken();
				break;

			case I_RPAR:
				// L_RPAR: "]" //
				lexType = LexElement.Type.L_RPAR;
				lexText += nextToken.getCharacter();
				nextToken = inputSys.getNextToken();
				break;

			case I_SLASH:
				// L_SLASH: "/"  //
				lexType = LexElement.Type.L_SLASH;
				lexText += nextToken.getCharacter();
				nextToken = inputSys.getNextToken();
				break;

			case I_EOL:
				// L_EOL: "\n" //
				lexType = LexElement.Type.L_EOL;
				lexText += nextToken.getCharacter();
				nextToken = inputSys.getNextToken();
				break;

			case I_EOI:
				// L_EOI: end of input //
				lexType = LexElement.Type.L_EOI;
				lexText += nextToken.getCharacter();
				nextToken = inputSys.getNextToken();
				break;

			case I_CHAR:
				// L_STRING: "<char><char>*"
				lexType = LexElement.Type.L_STRING;
				while (nextToken.getType() == InputSys.Token.Type.I_CHAR) {
					lexText += nextToken.getCharacter();
					nextToken = inputSys.getNextToken();
				}
				break;

			default:
				throw new RuntimeException("Invalid input token type !");
		}

		return new LexElement(lexType, lexText, lexPosition);
	}
}
