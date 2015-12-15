package spcoursework.lexanalyser;

import spcoursework.lexanalyser.tokens.*;
import spcoursework.lexanalyser.tokens.Boolean;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class represents a model of the lexical analyzer. Its functionality is
 * to analyze the source code, parsing it into tokens and recognition unexpected
 * characters.
 * 
 * @author Roman Zakolenko
 */
/**
 * @author Roman Zakolenko
 *
 */
public class Lexer {
	private HashMap<String, Token> lexems = new HashMap<String, Token>();
	private int currentLine = 1;
	private char currentChar = ' ';
	private BufferedReader reader;
	private boolean peek;

	/**
	 * Lexer constructor which are reserved terminals.
	 */
	public Lexer() {
		reserveTerminal(Terminals.SHL);
		reserveTerminal(Terminals.SHR);
		reserveTerminal(Terminals.BEGIN);
		reserveTerminal(Terminals.END);
		reserveTerminal(Terminals.IF);
		reserveTerminal(Terminals.ELSE);
		reserveTerminal(Terminals.FOR);
		reserveTerminal(Terminals.VAR);
		reserveTerminal(Terminals.WRITELN);
		reserveTerminal(Terminals.READLN);
		reserveTerminal(Terminals.TRUE);
		reserveTerminal(Terminals.FALSE);
		reserveTerminal(Terminals.REAL);
		reserveTerminal(Terminals.INTEGER);
		reserveTerminal(Terminals.BOOLEAN);
		reserveTerminal(Terminals.TO);
		reserveTerminal(Terminals.DO);
		reserveTerminal(Terminals.THEN);
		reserveTerminal(Terminals.SQRT);
	}

	/**
	 * Lexer constructor
	 * 
	 * @param reader
	 *            stream for analysis of the source text
	 */
	public Lexer(BufferedReader reader) {
		this();
		this.reader = reader;
	}

	public int getCurrentLine() {
		return currentLine;
	}

	/**
	 * Method to read the next character.
	 */
	public void readChar() {
		try {
			currentChar = Character.toLowerCase((char) reader.read());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for the analysis of the next token from the source text.
	 * 
	 * @return an instance of the class Token
	 *         {@link spcoursework.lexanalyser.tokens.Token}
	 * @throws LexException
	 *             if it was met unexpected character
	 */
	public Token scan() throws LexException {
		if (!peek) {
			boolean flag = true;
			while (flag) {
				readChar();
				if (isNotSpace(currentChar)) {
					if (currentChar == '\r' || currentChar == '\n') {
						if (currentChar == '\n') {
							currentLine++;
						}
					} else {
						flag = false;
					}
				}
			}
		} else {
			peek = false;
		}
		switch (currentChar) {
		case '.':
			return Terminals.DOT;
		case ',':
			return Terminals.COMMA;
		case '-':
			return Terminals.MINUS;
		case '+':
			return Terminals.PLUS;
		case '*':
			return Terminals.MUL;
		case '/':
			return Terminals.DIV;
		case ';':
			return Terminals.SEMICOLON;
		case '(':
			return Terminals.LEFT_PARENTHESIS;
		case ')':
			return Terminals.RIGHT_PARENTHESIS;
		case ':':
			readChar();
			peek = true;
			if (currentChar == '=') {
				peek = false;
				return Terminals.ASSIGN;
			}
			if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
				if (currentChar == '\n') {
					currentLine++;
				}
				peek = false;
			}
			return Terminals.COLON;
		case '=':
			return Terminals.EQUAL;
		case '<':
			readChar();
			peek = true;
			if (currentChar == '>') {
				peek = false;
				return Terminals.NOT_EQUAL;
			}
			if (currentChar == '=') {
				peek = false;
				return Terminals.LESS_EQUAL;
			}
			if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
				if (currentChar == '\n') {
					currentLine++;
				}
				peek = false;
			}
			return Terminals.LESS;
		case '>':
			readChar();
			peek = true;
			if (currentChar == '=') {
				peek = false;
				return Terminals.GREAT_EQUAL;
			}
			if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
				if (currentChar == '\n') {
					currentLine++;
				}
				peek = false;
			}
			return Terminals.GREAT;
		}
		if (Character.isDigit(currentChar)) {
			int num = 0;
			do {
				num = 10 * num + Character.digit(currentChar, 10);
				readChar();
			} while (Character.isDigit(currentChar));
			if (currentChar != '.') {
				if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
					if (currentChar == '\n') {
						currentLine++;
					}
					peek = false;
				} else {
					peek = true;
				}
				return new spcoursework.lexanalyser.tokens.Integer(num);
			} else {
				boolean flag = true;
				float real = num;
				float d = 10;
				while (flag) {
					readChar();
					if (!Character.isDigit(currentChar)) {
						flag = false;
						if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
							if (currentChar == '\n') {
								currentLine++;
							}
							peek = false;
						} else {
							peek = true;
						}
					} else {
						real += Character.digit(currentChar, 10) / d;
						d *= 10;
					}
				}
				return new spcoursework.lexanalyser.tokens.Real(real);
			}
		}
		if (Character.isLetter(currentChar)) {
			StringBuilder id = new StringBuilder();
			do {
				id.append(currentChar);
				readChar();
			} while (Character.isLetter(currentChar) || Character.isDigit(currentChar));
			if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
				if (currentChar == '\n') {
					currentLine++;
				}
				peek = false;
			} else {
				peek = true;
			}
			Word word = (Word) lexems.get(id.toString());
			if (word == null) {
				word = new Word(id.toString(), Tag.ID);
				lexems.put(id.toString(), word);
			}
			if (word.getLexem().equalsIgnoreCase("true") || word.getLexem().equalsIgnoreCase("false")) {
				return new Boolean(new java.lang.Boolean(word.getLexem()));
			}
			return word;
		}
		throw new LexException("Lexical error in line " + currentLine + ", unexpected symbol: " + currentChar);
	}

	/**
	 * Method for adding a plurality of terminals of another terminal
	 * 
	 * @param word
	 *            terminal to be reserved
	 */
	private void reserveTerminal(Word word) {
		lexems.put(word.getLexem(), word);
	}

	/**
	 * The method that checks whether this is not a space character
	 * 
	 * @param ch
	 *            verifiable character
	 * @return true, if the character is not a space, and false otherwise
	 */
	private boolean isNotSpace(char ch) {
		return ch != ' ' && ch != '\t';
	}
}
