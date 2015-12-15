package spcoursework.translator;

import spcoursework.generateasm.TranslateASM;
import spcoursework.lexanalyser.LexException;
import spcoursework.semanticanalyser.SemanticAnalyser;
import spcoursework.semanticanalyser.SemanticException;
import spcoursework.syntaxanalyser.Node;
import spcoursework.syntaxanalyser.SyntaxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class builds a lexical, syntactic, semantic analyzer, and performs the
 * role of an interpreter.
 * 
 * @author Roman Zakolenko
 *
 */
public class Translator {
	/**
	 * This method generates an assembly of a program written in Pascal. In the
	 * course of its work is carried out lexical, syntactic and semantic
	 * analysis.
	 * 
	 * @param src
	 *            the path to the source file (extension .pas)
	 * @param dest
	 *            the path to the output file (extension .asm)
	 * @throws IOException
	 * @throws SemanticException
	 * @throws SyntaxException
	 * @throws LexException
	 */
	public static void translate(String src, String dest)
			throws IOException, SemanticException, SyntaxException, LexException {
		if (src.replaceAll(".*\\.pas$", "").isEmpty()) {
			BufferedReader reader = new BufferedReader(new FileReader(new File(src)));
			SemanticAnalyser semanticAnalyser = new SemanticAnalyser(reader);
			Node root = semanticAnalyser.scan();
			TranslateASM asm = new TranslateASM(dest);
			asm.generate(semanticAnalyser.getSyntaxAnalyser().getVariables(),
					semanticAnalyser.getSyntaxAnalyser().getConstants(), root.getChildren().get(1));
		} else {
			throw new IllegalArgumentException("An incorrect extension of the input file.");
		}
	}

	public static void main(String[] args) throws LexException, SemanticException, SyntaxException, IOException {
		translate(args[0], args[1]);
	}
}
