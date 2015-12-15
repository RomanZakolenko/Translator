package spcoursework.lexanalyser.tokens;

/**
 * This class represents the words that are tokens created during lexical
 * analysis.
 * 
 * @author Roman Zakolenko
 * @see spcoursework.lexanalyser.tokens.Token;
 */
public class Word extends Token {
	private final String lexem;

	/**
	 * Word constructor
	 * 
	 * @param lexem
	 *            word value
	 * @param tag
	 *            word tag
	 */
	public Word(String lexem, int tag) {
		super(tag);
		this.lexem = lexem;
	}

	public String getLexem() {
		return lexem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lexem == null) ? 0 : lexem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (lexem == null) {
			if (other.lexem != null)
				return false;
		} else if (!lexem.equals(other.lexem))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return lexem;
	}
}
