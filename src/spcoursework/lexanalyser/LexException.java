package spcoursework.lexanalyser;

/**
 * This class is a syntax error, which may be parsing if was detected syntax
 * violation
 * 
 * @author Roman Zakolenko
 *
 */
public class LexException extends Exception {

	public LexException() {
	}

	public LexException(String message) {
		super(message);
	}
}
