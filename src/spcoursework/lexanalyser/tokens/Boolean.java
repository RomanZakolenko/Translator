package spcoursework.lexanalyser.tokens;

/**
 * This class represents tokens that store boolean values
 * 
 * @author Roman Zakolenko
 *
 */
public class Boolean extends Token {
	private final boolean value;

	/**
	 * Boolean constructor
	 * 
	 * @param value
	 *            token value, true or false
	 */
	public Boolean(boolean value) {
		super(Tag.BOOL_NUM);
		this.value = value;
	}

	@Override
	public String toString() {
		return "" + value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		Boolean aBoolean = (Boolean) o;

		return value == aBoolean.value;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (value ? 1 : 0);
		return result;
	}
}
