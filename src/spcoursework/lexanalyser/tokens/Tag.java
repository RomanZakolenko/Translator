package spcoursework.lexanalyser.tokens;

/**
 * This class contains all tags, which may have tokens
 * 
 * @author Roman Zakolenko
 *
 */
public class Tag {
	public static final int MUL = 256;
	public static final int DIV = 257;
	public static final int SHL = 258;
	public static final int SHR = 259;
	public static final int PLUS = 260;
	public static final int MINUS = 261;
	public static final int EQUAL = 262;
	public static final int LESS = 263;
	public static final int GREAT = 264;
	public static final int NOT_EQUAL = 265;
	public static final int LESS_EQUAL = 266;
	public static final int GREAT_EQUAL = 267;
	public static final int ASSIGN = 268;
	public static final int IF = 269;
	public static final int ELSE = 270;
	public static final int FOR = 271;
	public static final int BEGIN = 272;
	public static final int END = 273;
	public static final int VAR = 274;
	public static final int READLN = 275;
	public static final int WRITELN = 276;
	public static final int ID = 277;
	public static final int INT_NUM = 278;
	public static final int REAL_NUM = 279;
	public static final int TRUE = 280;
	public static final int FALSE = 281;
	public static final int LEFT_PARENTHESIS = 282;
	public static final int RIGHT_PARENTHESIS = 283;
	public static final int SEMICOLON = 284;
	public static final int THEN = 285;
	public static final int COLON = 286;
	public static final int TO = 287;
	public static final int DO = 288;
	public static final int INTEGER = 289;
	public static final int REAL = 290;
	public static final int PROGRAM = 291;
	public static final int VARIABLES = 292;
	public static final int IDENTIFIER_LIST = 293;
	public static final int COMMA = 294;
	public static final int IDENTIFIER = 295;
	public static final int STATEMENT_PART = 296;
	public static final int DOT = 297;
	public static final int SEQUENCE = 298;
	public static final int STATEMENT = 299;
	public static final int SIMPLE_STATEMENT = 300;
	public static final int EXPRESSION = 301;
	public static final int SIMPLE_EXPRESSION = 302;
	public static final int TERM = 303;
	public static final int FACTOR = 304;
	public static final int STRUCTERED_STATEMENT = 305;
	public static final int BOOLEAN = 306;
	public static final int BOOL_NUM = 307;
	public static final int SQRT = 308;

	private Tag() {
	}
}
