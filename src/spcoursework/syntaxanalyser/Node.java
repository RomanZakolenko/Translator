package spcoursework.syntaxanalyser;

import java.util.ArrayList;

/**
 * This class represents node of parse tree
 * 
 * @author Roman Zakolenko
 *
 */
public class Node {
	private int tag;
	private int type;
	private String strValue;
	private float numValue;
	private int numLine;
	private ArrayList<Node> children = new ArrayList<Node>();

	/**
	 * Node constructor
	 * 
	 * @param tag
	 *            node type
	 * @param numLine
	 *            number of line in source file
	 */
	public Node(int tag, int numLine) {
		this.tag = tag;
		this.numLine = numLine;
	}

	/**
	 * Node constructor
	 * 
	 * @param tag
	 *            node type
	 * @param strValue
	 *            node value
	 * @param numLine
	 *            number of line in source file
	 */
	public Node(int tag, String strValue, int numLine) {
		this.tag = tag;
		this.strValue = strValue;
		this.numLine = numLine;
	}

	/**
	 * Node constructor
	 * 
	 * @param tag
	 *            node type
	 * @param numValue
	 *            node value
	 * @param numLine
	 *            number of line in source file
	 */
	public Node(int tag, float numValue, int numLine) {
		this.tag = tag;
		this.numValue = numValue;
		this.numLine = numLine;
	}

	public int getTag() {
		return tag;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}

	public int getNumLine() {
		return numLine;
	}

	public ArrayList<Node> getChildren() {
		return children;
	}

	public String getStrValue() {
		return strValue;
	}

	public float getNumValue() {
		return numValue;
	}

	public void addChild(Node child) {
		children.add(child);
	}

	@Override
	public String toString() {
		return "Type: " + type + "; Tag: " + tag + "; Value: "
				+ ((strValue == null) ? (numValue == 0 ? ("") : numValue) : strValue);
	}
}
