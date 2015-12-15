package spcoursework.lexanalyser.tokens;

/**
 * This class contains all terminals, which are the object of class Word
 * {@link spcoursework.lexanalyser.tokens.Word}
 * 
 * @author Roman Zakolenko
 *
 */
public class Terminals {
	public static final Word MUL = new Word("*", Tag.MUL);
	public static final Word DIV = new Word("/", Tag.DIV);
	public static final Word SHL = new Word("shl", Tag.SHL);
	public static final Word SHR = new Word("shr", Tag.SHR);
	public static final Word PLUS = new Word("+", Tag.PLUS);
	public static final Word MINUS = new Word("-", Tag.MINUS);
	public static final Word EQUAL = new Word("=", Tag.EQUAL);
	public static final Word LESS = new Word("<", Tag.LESS);
	public static final Word GREAT = new Word(">", Tag.GREAT);
	public static final Word NOT_EQUAL = new Word("<>", Tag.NOT_EQUAL);
	public static final Word LESS_EQUAL = new Word("<=", Tag.LESS_EQUAL);
	public static final Word GREAT_EQUAL = new Word(">=", Tag.GREAT_EQUAL);
	public static final Word ASSIGN = new Word(":=", Tag.ASSIGN);
	public static final Word IF = new Word("if", Tag.IF);
	public static final Word ELSE = new Word("else", Tag.ELSE);
	public static final Word FOR = new Word("for", Tag.FOR);
	public static final Word BEGIN = new Word("begin", Tag.BEGIN);
	public static final Word END = new Word("end", Tag.END);
	public static final Word VAR = new Word("var", Tag.VAR);
	public static final Word READLN = new Word("readln", Tag.READLN);
	public static final Word WRITELN = new Word("writeln", Tag.WRITELN);
	public static final Word TRUE = new Word("true", Tag.TRUE);
	public static final Word FALSE = new Word("false", Tag.FALSE);
	public static final Word LEFT_PARENTHESIS = new Word("(", Tag.LEFT_PARENTHESIS);
	public static final Word RIGHT_PARENTHESIS = new Word(")", Tag.RIGHT_PARENTHESIS);
	public static final Word SEMICOLON = new Word(";", Tag.SEMICOLON);
	public static final Word INTEGER = new Word("integer", Tag.INTEGER);
	public static final Word REAL = new Word("real", Tag.REAL);
	public static final Word THEN = new Word("then", Tag.THEN);
	public static final Word COLON = new Word(":", Tag.COLON);
	public static final Word TO = new Word("to", Tag.TO);
	public static final Word DO = new Word("do", Tag.DO);
	public static final Word DOT = new Word(".", Tag.DOT);
	public static final Word COMMA = new Word(",", Tag.COMMA);
	public static final Word BOOLEAN = new Word("boolean", Tag.BOOLEAN);
	public static final Word SQRT = new Word("sqrt", Tag.SQRT);
}
