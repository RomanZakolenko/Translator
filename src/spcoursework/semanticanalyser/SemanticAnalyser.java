package spcoursework.semanticanalyser;

import spcoursework.lexanalyser.LexException;
import spcoursework.lexanalyser.tokens.Tag;
import spcoursework.lexanalyser.tokens.Word;
import spcoursework.syntaxanalyser.Node;
import spcoursework.syntaxanalyser.SyntaxAnalyser;
import spcoursework.syntaxanalyser.SyntaxException;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class represents a semantic analyzer that is used for semantic analysis
 * of the source file.
 * 
 * @author Roman Zakolenko
 */
public class SemanticAnalyser {
	private SyntaxAnalyser syntaxAnalyser;

	/**
	 * SemanticAnalyser constructor
	 * 
	 * @param reader
	 *            stream for analysis of the source text
	 */
	public SemanticAnalyser(BufferedReader reader) {
		this.syntaxAnalyser = new SyntaxAnalyser(reader);
	}

	public SyntaxAnalyser getSyntaxAnalyser() {
		return syntaxAnalyser;
	}

	/**
	 * The method that analyzes the source file for semantic errors
	 * 
	 * @return object of class Node, root of the parse tree
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws SemanticException
	 *             if during the semantic analysis is found semantic errors
	 */
	public Node scan() throws SyntaxException, LexException, SemanticException {
		Node res = syntaxAnalyser.parse();
		ArrayList<Node> tree = syntaxAnalyser.getTree();
		ArrayList<Node> variables = new ArrayList<Node>();
		tree.forEach((node) -> {
			if (node.getTag() == Tag.VARIABLES)
				variables.add(node);
		});
		setVariablesType(variables);
		ArrayList<Node> sequence = new ArrayList<Node>();
		tree.forEach((node) -> {
			if (node.getTag() == Tag.SEQUENCE)
				sequence.add(node);
		});
		Collections.reverse(sequence);
		sequence.forEach((node) -> {
			node.getChildren().forEach((node1) -> {
				try {
					check(node1.getChildren().get(0));
				} catch (SemanticException e) {
					e.printStackTrace();
					System.exit(1);
				}
			});
		});
		return res;
	}

	/**
	 * This method will test types of identifier and expression for
	 * compatibility
	 * 
	 * @param tag1
	 *            type of identifier
	 * @param tag2
	 *            type of expression
	 * @return true, if two types compatible, false, otherwise
	 */
	public boolean checkTags(int tag1, int tag2) {
		if (tag1 == Tag.BOOL_NUM) {
			if (tag2 == Tag.BOOL_NUM) {
				return true;
			} else {
				return false;
			}
		}
		if (tag1 == Tag.INT_NUM) {
			if (tag2 == Tag.INT_NUM) {
				return true;
			} else {
				return false;
			}
		}
		if (tag1 == Tag.REAL_NUM) {
			if (tag2 == Tag.INT_NUM || tag2 == Tag.REAL_NUM) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * This method will test types of two subexpression in expression for
	 * compatibility
	 * 
	 * @param tag1
	 *            type of first subexpression
	 * @param tag2
	 *            type of second subexpression
	 * @return true, if two types compatible, false, otherwise
	 */
	public boolean checkTagsExpr(int tag1, int tag2) {
		if (tag1 == Tag.BOOL_NUM) {
			if (tag2 == Tag.BOOL_NUM) {
				return true;
			} else {
				return false;
			}
		}
		if (tag1 == Tag.INT_NUM) {
			if (tag2 == Tag.INT_NUM || tag2 == Tag.REAL_NUM) {
				return true;
			} else {
				return false;
			}
		}
		if (tag1 == Tag.REAL_NUM) {
			if (tag2 == Tag.INT_NUM || tag2 == Tag.REAL_NUM) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * The method which is called recursively for the nodes of the parse tree
	 * and analyze it for semantic errors (type mismatch)
	 * 
	 * @param root
	 *            root of the parse tree
	 * @return value of type of node(root)
	 * @throws SemanticException
	 *             if during the semantic analysis is found semantic errors
	 */
	public int check(Node root) throws SemanticException {
		if (root.getType() == 0) {
			switch (root.getTag()) {
			case Tag.STRUCTERED_STATEMENT:
				int tag = root.getChildren().get(0).getTag();
				if (tag == Tag.BEGIN) {
					root.getChildren().get(1).getChildren().forEach((node) -> {
						try {
							check(node.getChildren().get(0));
						} catch (SemanticException e) {
							e.printStackTrace();
							System.exit(1);
						}
					});
				} else {
					if (tag == Tag.FOR) {
						if (check(root.getChildren().get(1)) != Tag.INT_NUM) {
							throw new SemanticException("Semantic error in line " + root.getNumLine()
									+ ", unexpected type, expected type 'integer'.");
						}
						if (check(root.getChildren().get(3)) != Tag.INT_NUM) {
							throw new SemanticException("Semantic error in line " + root.getNumLine()
									+ ", unexpected type, expected type 'integer'.");
						}
						check(root.getChildren().get(5).getChildren().get(0));
					} else {
						if (tag == Tag.WRITELN) {
							check(root.getChildren().get(1));
						} else {
							if (check(root.getChildren().get(1)) != Tag.BOOL_NUM) {
								throw new SemanticException("Semantic error in line " + root.getNumLine()
										+ ", unexpected type, expected type 'boolean'.");
							}
							check(root.getChildren().get(3).getChildren().get(0));
							if (root.getChildren().size() > 4) {
								check(root.getChildren().get(5).getChildren().get(0));
							}
						}
					}
				}
				break;
			case Tag.SIMPLE_STATEMENT:
				int tagId = check(root.getChildren().get(0));
				int tagExpr = check(root.getChildren().get(2));
				if (!checkTags(tagId, tagExpr)) {
					throw new SemanticException("Semantic error in line " + root.getNumLine() + ", unexpected type.");
				}
				root.setType(tagId);
				break;
			case Tag.ID:
				root.setType(syntaxAnalyser.getVariables().get(new Word(root.getStrValue(), root.getTag())));
				break;
			case Tag.EXPRESSION:
				if (root.getChildren().size() > 1) {
					int tag1 = check(root.getChildren().get(0));
					int tag2 = check(root.getChildren().get(2));
					if (!checkTagsExpr(tag1, tag2)) {
						throw new SemanticException(
								"Semantic error in line " + root.getNumLine() + ", unexpected type.");
					}
					root.setType(Tag.BOOL_NUM);
				} else {
					root.setType(check(root.getChildren().get(0)));
				}
				break;
			case Tag.SIMPLE_EXPRESSION:
				if (root.getChildren().size() <= 2) {
					int index = 0;
					if (root.getChildren().get(0).getTag() != Tag.TERM) {
						index = 1;
					}
					root.setType(check(root.getChildren().get(index)));
				} else {
					for (Node node : root.getChildren()) {
						if (node.getTag() == Tag.TERM) {
							int type = check(node);
							if (type == Tag.BOOL_NUM) {
								throw new SemanticException(
										"Semantic error in line " + root.getNumLine() + ", unexpected type.");
							}
							if (type == Tag.REAL_NUM) {
								root.setType(type);
							}
						}
					}
					if (root.getType() == 0) {
						root.setType(Tag.INT_NUM);
					}
				}
				break;
			case Tag.TERM:
				if (root.getChildren().size() == 1) {
					root.setType(check(root.getChildren().get(0)));
				} else {
					int index = 0;
					for (Node node : root.getChildren()) {
						if (node.getTag() == Tag.FACTOR) {
							int type = check(node);
							if (type == Tag.BOOL_NUM) {
								throw new SemanticException(
										"Semantic error in line " + root.getNumLine() + ", unexpected type.");
							}
							if (type == Tag.REAL_NUM) {
								root.setType(type);
							}
						}
						if (node.getTag() == Tag.DIV) {
							root.setType(Tag.REAL_NUM);
						}
						if (node.getTag() == Tag.SHR || node.getTag() == Tag.SHL) {
							if (root.getChildren().get(index - 1).getType() != Tag.INT_NUM
									|| check(root.getChildren().get(index + 1)) != Tag.INT_NUM) {
								throw new SemanticException(
										"Semantic error in line " + root.getNumLine() + ", unexpected type.");
							}
						}
						index++;
					}
					if (root.getType() == 0) {
						root.setType(Tag.INT_NUM);
					}
				}
				break;
			case Tag.FACTOR:
				if (root.getChildren().get(0).getTag() == Tag.LEFT_PARENTHESIS) {
					root.setType(check(root.getChildren().get(1)));
				} else {
					if (root.getChildren().get(0).getTag() == Tag.ID) {
						root.setType(check(root.getChildren().get(0)));
					} else {
						if (root.getChildren().get(0).getTag() == Tag.SQRT) {
							root.setType(check(root.getChildren().get(1)));
						} else {
							root.setType(root.getChildren().get(0).getType());
						}
					}
				}
				break;
			}
		}
		return root.getType();
	}

	/**
	 * The method that analyzes and sets the variable types
	 * 
	 * @param variables
	 *            ArrayList that contains nodes of variable
	 */
	private void setVariablesType(ArrayList<Node> variables) {
		variables.forEach((v) -> {
			Node type = v.getChildren().get(1);
			Node listId = v.getChildren().get(0);
			listId.getChildren().forEach((id) -> {
				int tag = 0;
				if (type.getTag() == Tag.REAL) {
					tag = Tag.REAL_NUM;
				} else {
					if (type.getTag() == Tag.INTEGER) {
						tag = Tag.INT_NUM;
					} else {
						tag = Tag.BOOL_NUM;
					}
				}
				syntaxAnalyser.setVariableType(new Word(id.getStrValue(), id.getTag()), tag);
			});
		});
	}
}
