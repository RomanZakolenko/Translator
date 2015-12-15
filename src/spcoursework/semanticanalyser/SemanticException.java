package spcoursework.semanticanalyser;

/**
 * This class is a semantic error which may occur at the stage of semantic
 * analysis, if it was found violation of semantics
 * 
 * @author Roman Zakolenko
 */
public class SemanticException extends Exception {
	public SemanticException() {
	}

	public SemanticException(String message) {
		super(message);
	}
}
