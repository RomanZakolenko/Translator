package spcoursework.lexanalyser.tokens;

/**
 * A class that represents the token - the object to represent a token, creating
 * during lexical analysis.
 * 
 * @author Roman Zakolenko
 *
 */
public class Token {
	private final int tag;

	/**
	 * Token constructor
	 * 
	 * @param tag
	 *            tag value
	 */
	public Token(int tag) {
		this.tag = tag;
	}

	public int getTag() {
		return tag;
	}

	@Override
	public String toString() {
		return "" + tag;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Token token = (Token) o;
		return tag == token.tag;
	}

	@Override
	public int hashCode() {
		return tag;
	}
}
