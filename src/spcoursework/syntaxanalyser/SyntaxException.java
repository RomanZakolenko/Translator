package spcoursework.syntaxanalyser;

/**
 * This class is a syntax error, which may be parsing
 * 
 * @author Roman Zakolenko
 *
 */
public class SyntaxException extends Exception {
	public SyntaxException() {
	}

	public SyntaxException(String message) {
		super(message);
	}
}
