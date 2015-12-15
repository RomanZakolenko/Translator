package spcoursework.lexanalyser.tokens;

/**
 * This class represents tokens that store integer values
 * 
 * @author Roman Zakolenko
 *
 */
public class Integer extends Token {
	private final int value;

	/**
	 * Integer constructor
	 * 
	 * @param value
	 *            token value
	 */
	public Integer(int value) {
		super(Tag.INT_NUM);
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
		Integer integer = (Integer) o;
		return value == integer.value;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + value;
		return result;
	}
}
