package spcoursework.lexanalyser.tokens;

/**
 * This class represents tokens that store real values
 * 
 * @author Roman Zakolenko
 *
 */
public class Real extends Token {
	private final float value;

	/**
	 * Real constructor
	 * 
	 * @param value
	 *            token value
	 */
	public Real(float value) {
		super(Tag.REAL_NUM);
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
		Real real = (Real) o;
		return Float.compare(real.value, value) == 0;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (value != +0.0f ? Float.floatToIntBits(value) : 0);
		return result;
	}
}
