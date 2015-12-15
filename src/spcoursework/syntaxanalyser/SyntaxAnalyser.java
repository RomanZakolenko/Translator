package spcoursework.syntaxanalyser;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import spcoursework.lexanalyser.LexException;
import spcoursework.lexanalyser.Lexer;
import spcoursework.lexanalyser.tokens.Tag;
import spcoursework.lexanalyser.tokens.Token;
import spcoursework.lexanalyser.tokens.Word;
import spcoursework.semanticanalyser.SemanticException;

/**
 * This class represents a parser that creates an intermediate representation
 * (syntax tree) from the original text.
 * 
 * @author Roman Zakolenko
 *
 */
public class SyntaxAnalyser {
	private ArrayList<Node> tree = new ArrayList<Node>();
	private HashMap<Word, Integer> variables = new HashMap();
	private HashSet<Token> constants = new HashSet<>();
	private BufferedReader reader;
	private Lexer lexer;
	private Token currentToken;
	private boolean peek;

	/**
	 * SyntaxAnalyser constructor
	 * 
	 * @param reader
	 *            stream for analysis of the source text
	 */
	public SyntaxAnalyser(BufferedReader reader) {
		this.reader = reader;
		this.lexer = new Lexer(reader);
	}

	public ArrayList<Node> getTree() {
		return tree;
	}

	public HashMap<Word, Integer> getVariables() {
		return variables;
	}

	public HashSet<Token> getConstants() {
		return constants;
	}

	/**
	 * The method that sets the type of a variable
	 * 
	 * @param word
	 *            a variable whose type is defined
	 * @param type
	 *            type of a variable
	 */
	public void setVariableType(Word word, Integer type) {
		variables.put(word, type);
	}

	/**
	 * The method which creates a parse tree from the source code. At the same
	 * time parsing lexical analysis is performed.
	 * 
	 * @return root of the syntax tree
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node parse() throws LexException, SyntaxException, SemanticException {
		Node program = new Node(Tag.PROGRAM, lexer.getCurrentLine());
		program.addChild(declaration());
		program.addChild(statementPart());
		nextToken();
		if (this.currentToken.getTag() != Tag.DOT) {
			throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
					+ ", expected symbol: '.', but found: " + currentToken);
		}
		tree.add(program);
		return program;
	}

	/**
	 * The method that is used to parse the design "Declaration", where the
	 * variables are declared.
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node declaration() throws SyntaxException, LexException, SemanticException {
		nextToken();
		if (this.currentToken.getTag() != Tag.VAR) {
			throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
					+ ", expected symbol: 'var', but found: " + currentToken);
		}
		Node declaration = new Node(Tag.VAR, lexer.getCurrentLine());
		declaration.addChild(variables());
		nextToken();
		if (this.currentToken.getTag() != Tag.SEMICOLON) {
			throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
					+ ", expected symbol: ';' , but found: " + currentToken);
		}
		nextToken();
		peek = true;
		while (currentToken.getTag() != Tag.BEGIN) {
			declaration.addChild(variables());
			nextToken();
			if (this.currentToken.getTag() != Tag.SEMICOLON) {
				throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
						+ ", expected symbol: ';' , but found: " + currentToken);
			}
			nextToken();
			peek = true;
		}
		tree.add(declaration);
		return declaration;
	}

	/**
	 * The method that is used to parse the design "Variables"
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node variables() throws LexException, SyntaxException, SemanticException {
		Node variables = new Node(Tag.VARIABLES, lexer.getCurrentLine());
		variables.addChild(identifierList());
		nextToken();
		if (this.currentToken.getTag() != Tag.COLON) {
			throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
					+ ", expected symbol: ':' , but found: " + currentToken);
		}
		variables.addChild(type());
		tree.add(variables);
		return variables;
	}

	/**
	 * The method that is used to parse the design "Identifier list"
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node identifierList() throws LexException, SyntaxException, SemanticException {
		Node identifierList = new Node(Tag.IDENTIFIER_LIST, lexer.getCurrentLine());
		Node id = identifier();
		Word variable = new Word(id.getStrValue(), id.getTag());
		if (variables.get(variable) == null) {
			variables.put(variable, 0);
		} else {
			throw new SemanticException("Semantic error in line " + lexer.getCurrentLine()
					+ ", duplicate declaration variable " + variable.getLexem());
		}
		identifierList.addChild(id);
		nextToken();
		while (this.currentToken.getTag() == Tag.COMMA) {
			Node id1 = identifier();
			Word variable1 = new Word(id1.getStrValue(), id1.getTag());
			if (variables.get(variable1) == null) {
				variables.put(variable1, 0);
			} else {
				throw new SemanticException("Semantic error in line " + lexer.getCurrentLine()
						+ ", duplicate declaration variable " + variable1.getLexem());
			}
			identifierList.addChild(id1);
			nextToken();
		}
		peek = true;
		tree.add(identifierList);
		return identifierList;
	}

	/**
	 * The method that is used to parse the design "identifier"
	 * 
	 * @return node of parse tree, than represents identifier
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 */
	public Node identifier() throws LexException, SyntaxException {
		nextToken();
		if (this.currentToken.getTag() != Tag.ID) {
			throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
					+ ", expected 'identificator', but found: " + currentToken);
		}
		Node id = new Node(Tag.ID, currentToken.toString(), lexer.getCurrentLine());
		tree.add(id);
		return id;
	}

	/**
	 * The method that is used to parse the design "type"
	 * 
	 * @return node of parse tree, than represents type of identifier
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 */
	public Node type() throws SyntaxException, LexException {
		nextToken();
		Node type;
		if (currentToken.getTag() != Tag.INTEGER && currentToken.getTag() != Tag.BOOLEAN
				&& currentToken.getTag() != Tag.REAL) {
			throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
					+ ", expected 'integer'|'real'|'boolean', but found: " + currentToken);
		} else {
			type = new Node(currentToken.getTag(), currentToken.toString(), lexer.getCurrentLine());
		}
		tree.add(type);
		return type;
	}

	/**
	 * The method that is used to parse the design "Statement part"
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node statementPart() throws LexException, SyntaxException, SemanticException {
		nextToken();
		if (this.currentToken.getTag() != Tag.BEGIN) {
			throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
					+ ", expected 'begin', but found: " + currentToken);
		}
		Node statementPart = new Node(Tag.STATEMENT_PART, lexer.getCurrentLine());
		statementPart.addChild(sequence());
		nextToken();
		if (this.currentToken.getTag() != Tag.END) {
			throw new SyntaxException(
					"Syntax error in line " + lexer.getCurrentLine() + ", expected 'end', but found: " + currentToken);
		}
		tree.add(statementPart);
		return statementPart;
	}

	/**
	 * The method that is used to parse the design "Sequence"
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node sequence() throws LexException, SyntaxException, SemanticException {
		Node sequence = new Node(Tag.SEQUENCE, lexer.getCurrentLine());
		sequence.addChild(statement());
		nextToken();
		while (this.currentToken.getTag() == Tag.SEMICOLON) {
			nextToken();
			if (currentToken.getTag() != Tag.END) {
				peek = true;
				sequence.addChild(statement());
				nextToken();
			}
		}
		peek = true;
		tree.add(sequence);
		return sequence;
	}

	/**
	 * The method that is used to parse the design "Statement"
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node statement() throws LexException, SyntaxException, SemanticException {
		Node statement = new Node(Tag.STATEMENT, lexer.getCurrentLine());
		nextToken();
		peek = true;
		if (currentToken.getTag() == Tag.ID) {
			statement.addChild(simpleStatement());
		} else {
			statement.addChild(structeredStatement());
		}
		tree.add(statement);
		return statement;
	}

	/**
	 * The method that is used to parse the design "Simple statement"
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node simpleStatement() throws LexException, SyntaxException, SemanticException {
		Node simpleStatement = new Node(Tag.SIMPLE_STATEMENT, lexer.getCurrentLine());
		Node id = identifier();
		Word word = new Word(id.getStrValue(), id.getTag());
		if (variables.get(word) == null) {
			throw new SemanticException("Semantic error in line " + lexer.getCurrentLine() + ", variable '"
					+ word.getLexem() + "' is not declared.");
		}
		simpleStatement.addChild(id);
		nextToken();
		if (this.currentToken.getTag() != Tag.ASSIGN) {
			throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
					+ ", expected symbol ':=', but found: " + currentToken);
		}
		simpleStatement.addChild(new Node(Tag.ASSIGN, currentToken.toString(), lexer.getCurrentLine()));
		simpleStatement.addChild(expression());
		tree.add(simpleStatement);
		return simpleStatement;
	}

	/**
	 * The method that is used to parse the design "Expression"
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node expression() throws LexException, SyntaxException, SemanticException {
		Node expression = new Node(Tag.EXPRESSION, lexer.getCurrentLine());
		expression.addChild(simpleExpression());
		nextToken();
		if (this.currentToken.getTag() >= 262 && this.currentToken.getTag() <= 267) {
			expression.addChild(new Node(currentToken.getTag(), currentToken.toString(), lexer.getCurrentLine()));
			expression.addChild(simpleExpression());
		} else {
			peek = true;
		}
		tree.add(expression);
		return expression;
	}

	/**
	 * The method that is used to parse the design "Simple expression"
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node simpleExpression() throws LexException, SyntaxException, SemanticException {
		Node simpleExpression = new Node(Tag.SIMPLE_EXPRESSION, lexer.getCurrentLine());
		nextToken();
		if (currentToken.getTag() == Tag.MINUS || currentToken.getTag() == Tag.PLUS) {
			simpleExpression.addChild(new Node(currentToken.getTag(), currentToken.toString(), lexer.getCurrentLine()));
		} else {
			peek = true;
		}
		simpleExpression.addChild(term());
		nextToken();
		while (currentToken.getTag() == Tag.MINUS || currentToken.getTag() == Tag.PLUS) {
			simpleExpression.addChild(new Node(currentToken.getTag(), currentToken.toString(), lexer.getCurrentLine()));
			simpleExpression.addChild(term());
			nextToken();
		}
		peek = true;
		tree.add(simpleExpression);
		return simpleExpression;
	}

	/**
	 * The method that is used to parse the design "Term"
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node term() throws LexException, SyntaxException, SemanticException {
		Node term = new Node(Tag.TERM, lexer.getCurrentLine());
		term.addChild(factor());
		nextToken();
		while (currentToken.getTag() == Tag.MUL || currentToken.getTag() == Tag.DIV || currentToken.getTag() == Tag.SHR
				|| currentToken.getTag() == Tag.SHL) {
			term.addChild(new Node(currentToken.getTag(), currentToken.toString(), lexer.getCurrentLine()));
			term.addChild(factor());
			nextToken();
		}
		peek = true;
		tree.add(term);
		return term;
	}

	/**
	 * The method that is used to parse the design "Factor"
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node factor() throws LexException, SyntaxException, SemanticException {
		Node factor = new Node(Tag.FACTOR, lexer.getCurrentLine());
		nextToken();
		if (currentToken.getTag() == Tag.ID) {
			peek = true;
			Node id = identifier();
			Word word = new Word(id.getStrValue(), id.getTag());
			if (variables.get(word) == null) {
				throw new SemanticException("Semantic error in line " + lexer.getCurrentLine() + ", variable '"
						+ word.getLexem() + "' is not declared.");
			}
			factor.addChild(id);
		} else {
			if (currentToken.getTag() == Tag.REAL_NUM || currentToken.getTag() == Tag.INT_NUM
					|| currentToken.getTag() == Tag.BOOL_NUM) {
				factor.addChild(number());
			} else {
				if (currentToken.getTag() == Tag.LEFT_PARENTHESIS) {
					factor.addChild(new Node(Tag.LEFT_PARENTHESIS, currentToken.toString(), lexer.getCurrentLine()));
					factor.addChild(expression());
					nextToken();
					if (currentToken.getTag() != Tag.RIGHT_PARENTHESIS) {
						throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
								+ ", expected symbol ')', but found: " + currentToken);
					}
					factor.addChild(new Node(Tag.RIGHT_PARENTHESIS, currentToken.toString(), lexer.getCurrentLine()));
				} else {
					if (currentToken.getTag() == Tag.SQRT) {
						factor.addChild(new Node(Tag.SQRT, currentToken.toString(), lexer.getCurrentLine()));
						nextToken();
						if (currentToken.getTag() != Tag.LEFT_PARENTHESIS) {
							throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
									+ ", expected '(', but found: " + currentToken);
						}
						factor.addChild(expression());
						nextToken();
						if (currentToken.getTag() != Tag.RIGHT_PARENTHESIS) {
							throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
									+ ", expected ')', but found: " + currentToken);
						}
					} else {
						throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
								+ ", expected 'identificator'|'number'|'(expression)|'boolean'', but found: "
								+ currentToken);
					}
				}
			}
		}
		tree.add(factor);
		return factor;
	}

	/**
	 * The method that is used to parse the design "number"
	 * 
	 * @return node of parse tree
	 */
	public Node number() {
		Node number = new Node(currentToken.getTag(), currentToken.toString(), lexer.getCurrentLine());
		number.setType(currentToken.getTag());
		constants.add(currentToken);
		tree.add(number);
		return number;
	}

	/**
	 * The method that is used to parse the design "Structured statement"
	 * 
	 * @return node of parse tree
	 * @throws SyntaxException
	 *             if during the syntax analysis is found unexpected symbol or
	 *             constructions
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 * @throws SemanticException
	 *             it was found repeated declaration of variables.
	 */
	public Node structeredStatement() throws LexException, SyntaxException, SemanticException {
		Node structeredStatement = new Node(Tag.STRUCTERED_STATEMENT, lexer.getCurrentLine());
		nextToken();
		switch (currentToken.getTag()) {
		case Tag.BEGIN:
			structeredStatement.addChild(new Node(Tag.BEGIN, currentToken.toString(), lexer.getCurrentLine()));
			structeredStatement.addChild(sequence());
			nextToken();
			if (currentToken.getTag() != Tag.END) {
				throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
						+ ", expected 'end', but found: " + currentToken);
			}
			structeredStatement.addChild(new Node(Tag.END, currentToken.toString(), lexer.getCurrentLine()));
			break;
		case Tag.FOR:
			structeredStatement.addChild(new Node(Tag.FOR, currentToken.toString(), lexer.getCurrentLine()));
			structeredStatement.addChild(simpleStatement());
			nextToken();
			if (currentToken.getTag() != Tag.TO) {
				throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
						+ ", expected 'to', but found: " + currentToken);
			}
			structeredStatement.addChild(new Node(Tag.TO, currentToken.toString(), lexer.getCurrentLine()));
			structeredStatement.addChild(expression());
			nextToken();
			if (currentToken.getTag() != Tag.DO) {
				throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
						+ ", expected 'do', but found: " + currentToken);
			}
			structeredStatement.addChild(new Node(Tag.DO, currentToken.toString(), lexer.getCurrentLine()));
			structeredStatement.addChild(statement());
			break;
		case Tag.IF:
			structeredStatement.addChild(new Node(Tag.IF, currentToken.toString(), lexer.getCurrentLine()));
			structeredStatement.addChild(expression());
			nextToken();
			if (currentToken.getTag() != Tag.THEN) {
				throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
						+ ", expected 'then', but found: " + currentToken);
			}
			structeredStatement.addChild(new Node(Tag.THEN, currentToken.toString(), lexer.getCurrentLine()));
			structeredStatement.addChild(statement());
			nextToken();
			if (currentToken.getTag() == Tag.ELSE) {
				structeredStatement.addChild(new Node(Tag.ELSE, currentToken.toString(), lexer.getCurrentLine()));
				structeredStatement.addChild(statement());
			} else {
				peek = true;
			}
			break;
		case Tag.WRITELN:
			structeredStatement.addChild(new Node(Tag.WRITELN, currentToken.toString(), lexer.getCurrentLine()));
			nextToken();
			if (currentToken.getTag() != Tag.LEFT_PARENTHESIS) {
				throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
						+ ", expected '(', but found: " + currentToken);
			}
			structeredStatement.addChild(expression());
			nextToken();
			if (currentToken.getTag() != Tag.RIGHT_PARENTHESIS) {
				throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
						+ ", expected ')', but found: " + currentToken);
			}
			break;
		default:
			throw new SyntaxException("Syntax error in line " + lexer.getCurrentLine()
					+ ", expected 'begin'|'for'|'if', but found: " + currentToken);
		}
		tree.add(structeredStatement);
		return structeredStatement;
	}

	/**
	 * This method creates another token
	 * 
	 * @throws LexException
	 *             if during the lexical analysis is found unexpected character
	 */
	private void nextToken() throws LexException {
		if (!peek) {
			currentToken = lexer.scan();
		} else {
			peek = false;
		}
	}
}
