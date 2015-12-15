package spcoursework.generateasm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import spcoursework.lexanalyser.tokens.Tag;
import spcoursework.lexanalyser.tokens.Token;
import spcoursework.lexanalyser.tokens.Word;
import spcoursework.syntaxanalyser.Node;

/**
 * This class is the interpreter of the intermediate representation (syntax
 * tree) in assembler.
 * 
 * @author Roman Zakolenko
 */
public class TranslateASM {

	private BufferedWriter writer;
	private int numLabel;

	/**
	 * TranslateASM constructor
	 * 
	 * @param dest
	 *            the path of the output file (.asm extension)
	 * @throws IOException
	 */
	public TranslateASM(String dest) throws IOException {
		this.writer = new BufferedWriter(new FileWriter(new File(dest)));
	}

	/**
	 * The method that generates the assembler of the intermediate
	 * representation (syntax tree)
	 * 
	 * @param variables
	 *            map of variables and their types
	 * @param constants
	 *            set of constants
	 * @param statementPart
	 *            node that points to the structure "Statement part"
	 * @throws IOException
	 */
	public void generate(HashMap<Word, Integer> variables, HashSet<Token> constants, Node statementPart)
			throws IOException {
		writer.write(".586\r\n");
		writer.write(".model flat, stdcall\r\n\r\n");
		writer.write("option casemap :none\r\n\r\n" + "include C:\\masm32\\include\\kernel32.inc\r\n"
				+ "include C:\\masm32\\include\\user32.inc\r\n" + "include C:\\masm32\\include\\windows.inc\r\n"
				+ "include C:\\masm32\\macros\\macros.asm\r\n" + "include C:\\masm32\\include\\masm32.inc\r\n"
				+ "include C:\\masm32\\include\\gdi32.inc\r\n" + "include C:\\masm32\\include\\msvcrt.inc\r\n"
				+ "includelib C:\\masm32\\lib\\masm32.lib\r\n" + "includelib C:\\masm32\\lib\\gdi32.lib\r\n"
				+ "includelib C:\\masm32\\lib\\msvcrt.lib\r\n" + "includelib C:\\masm32\\lib\\kernel32.lib\r\n"
				+ "includelib C:\\masm32\\lib\\user32.lib\r\n\r\n");
		writer.write(".data\r\n");
		variables.forEach((key, value) -> {
			try {
				writer.write(key.getLexem() + " dq 0.0\r\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		constants.forEach((con) -> {
			try {
				if (con.getTag() == Tag.BOOL_NUM) {
					writer.write(("num" + con).replace(".", "$") + " dd " + (con.toString().equals("true") ? "1" : "0")
							+ "\r\n");
				} else {
					if (con.getTag() == Tag.INT_NUM) {
						writer.write(("num" + con).replace(".", "$") + " dq " + con + "\r\n");
					} else {
						writer.write(("num" + con).replace(".", "$") + " dq " + con + "\r\n");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		writer.write("temp dq 0\r\n");
		writer.write("temp1 dq 0\r\n");
		writer.write("tempWrite dq 0.0\r\n");
		writer.write("tempShift dq 0\r\n");
		writer.write("tempShiftCl dq 0\r\n");
		writer.write("\r\n.code\r\n\r\nstart:\r\n\r\n");
		write(statementPart.getChildren().get(0));
		writer.write("INVOKE ExitProcess, 0\r\n\r\nEND start");
		writer.close();
	}

	/**
	 * The method which is called recursively to all nodes of the syntax tree to
	 * generate the assembler
	 * 
	 * @param root
	 *            root of parse tree
	 * @throws IOException
	 */
	private void write(Node root) throws IOException {
		int multOper = 0;
		int arithmOper = 0;
		switch (root.getTag()) {
		case Tag.SEQUENCE:
			root.getChildren().forEach((root1) -> {
				try {
					write(root1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			break;
		case Tag.STATEMENT:
			write(root.getChildren().get(0));
			break;
		case Tag.STRUCTERED_STATEMENT:
			if (root.getChildren().get(0).getTag() == Tag.BEGIN) {
				write(root.getChildren().get(1));
			} else {
				int num = numLabel;
				numLabel++;
				if (root.getChildren().get(0).getTag() == Tag.IF) {
					write(root.getChildren().get(1));
					writer.write("FISTP temp\r\n");
					writer.write("CMP dword ptr[temp], 1\r\n");
					writer.write("JNE @label" + num + "\r\n");
					write(root.getChildren().get(3));
					if (root.getChildren().size() > 4) {
						writer.write("JMP @end" + num + "\r\n");
						writer.write("@label" + num + ":\r\n");
						write(root.getChildren().get(5));
						writer.write("@end" + num + ":\r\n");
					} else {
						writer.write("@label" + num + ":\r\n");
					}
					writer.write("MOV dword ptr[temp], 0\r\n");
				} else {
					if (root.getChildren().get(0).getTag() == Tag.WRITELN) {
						write(root.getChildren().get(1));
						if (root.getChildren().get(1).getType() == Tag.INT_NUM) {
							writer.write("FISTP tempWrite\r\n");
							writer.write("printf(\"%d\\n\", tempWrite)\r\n");
						} else {
							if (root.getChildren().get(1).getType() == Tag.REAL_NUM) {
								writer.write("FSTP tempWrite\r\n");
								writer.write("printf(\"%f\\n\", tempWrite)\r\n");
							} else {
								writer.write("FISTP tempWrite\r\n");
								writer.write("CMP dword ptr[tempWrite], 1\r\n");
								writer.write("JE @printTrue" + num + "\r\n");
								writer.write("printf(\"%s\\n\", \"false\")\r\n");
								writer.write("JMP @endPrint" + num + "\r\n");
								writer.write("@printTrue" + num + ":\r\n printf(\"%s\\n\", \"true\")\r\n");
								writer.write("@endPrint" + num + ":\r\n");
							}
						}
					} else {
						write(root.getChildren().get(1));
						writer.write("PUSH ebx\r\n");
						write(root.getChildren().get(3));
						writer.write("FISTP temp1\r\n");
						writer.write("MOV ebx, dword ptr[temp1]\r\n");
						writer.write("CMP ebx, dword ptr["
								+ root.getChildren().get(1).getChildren().get(0).getStrValue() + "]\r\n");
						writer.write("JL @end" + num + "\r\n");
						writer.write("@label" + num + ":\r\n");
						write(root.getChildren().get(5));
						writer.write("INC dword ptr[" + root.getChildren().get(1).getChildren().get(0).getStrValue()
								+ "]\r\n");
						writer.write("CMP ebx, dword ptr["
								+ root.getChildren().get(1).getChildren().get(0).getStrValue() + "]\r\n");
						writer.write("JGE @label" + num + "\r\n");
						writer.write("@end" + num + ":\r\n");
						writer.write("POP ebx\r\n");
					}
				}
			}
			break;
		case Tag.SIMPLE_STATEMENT:
			write(root.getChildren().get(2));
			if (root.getChildren().get(0).getType() == Tag.BOOL_NUM
					|| root.getChildren().get(0).getType() == Tag.INT_NUM) {
				writer.write("FISTP " + root.getChildren().get(0).getStrValue() + "\r\n");
			} else {
				writer.write("FSTP " + root.getChildren().get(0).getStrValue() + "\r\n");
			}
			break;
		case Tag.EXPRESSION:
			if (root.getChildren().size() == 1) {
				write(root.getChildren().get(0));
			} else {
				write(root.getChildren().get(2));
				write(root.getChildren().get(0));
				writer.write("FCOMPP\r\n" + "FSTSW ax\r\n" + "SAHF\r\n");
				switch (root.getChildren().get(1).getTag()) {
				case Tag.EQUAL:
					writer.write("JNZ @label" + numLabel + "\r\n");
					writer.write("FLD1\r\nJMP @end" + numLabel + "\r\n");
					writer.write("@label" + numLabel + ":\r\n");
					writer.write("FLDZ\r\n@end" + numLabel++ + ":\r\n");
					break;
				case Tag.NOT_EQUAL:
					writer.write("JNZ @label" + numLabel + "\r\n");
					writer.write("FLDZ\r\nJMP @end" + numLabel + "\r\n");
					writer.write("@label" + numLabel + ":\r\n");
					writer.write("FLD1\r\n@end" + numLabel++ + ":\r\n");
					break;
				case Tag.LESS:
					writer.write("JNC @label" + numLabel + "\r\n");
					writer.write("FLD1\r\nJMP @end" + numLabel + "\r\n");
					writer.write("@label" + numLabel + ":\r\n");
					writer.write("FLDZ\r\n@end" + numLabel++ + ":\r\n");
					break;
				case Tag.GREAT:
					writer.write("JNC @label" + numLabel + "\r\n");
					writer.write("FLDZ\r\nJMP @end" + numLabel + "\r\n");
					writer.write("@label" + numLabel + ":\r\n");
					writer.write("FLD1\r\n@end" + numLabel++ + ":\r\n");
					break;
				case Tag.LESS_EQUAL:
					writer.write("JZ @label" + numLabel + "\r\n");
					writer.write("JC @label" + numLabel + "\r\n");
					writer.write("FLDZ\r\nJMP @end" + numLabel + "\r\n");
					writer.write("@label" + numLabel + ":\r\n");
					writer.write("FLD1\r\n@end" + numLabel++ + ":\r\n");
					break;
				case Tag.GREAT_EQUAL:
					writer.write("JZ @label" + numLabel + "\r\n");
					writer.write("JNC @label" + numLabel + "\r\n");
					writer.write("FLDZ\r\nJMP @end" + numLabel + "\r\n");
					writer.write("@label" + numLabel + ":\r\n");
					writer.write("FLD1\r\n@end" + numLabel++ + ":\r\n");
					break;
				}
			}
			break;
		case Tag.SIMPLE_EXPRESSION:
			Iterator<Node> iter1 = root.getChildren().iterator();
			Node node1;
			if (root.getChildren().get(0).getTag() == Tag.MINUS) {
				writer.write("FLDZ\r\n");
			}
			while (iter1.hasNext()) {
				node1 = iter1.next();
				if (node1.getTag() == Tag.TERM) {
					write(node1);
					if (arithmOper != 0) {
						if (arithmOper == Tag.MINUS) {
							writer.write("FSUBP\r\n");
						} else {
							writer.write("FADDP\r\n");
						}
						arithmOper = 0;
					}
				} else {
					arithmOper = node1.getTag();
				}
			}
			break;
		case Tag.TERM:
			Iterator<Node> iter2 = root.getChildren().iterator();
			Node node;
			while (iter2.hasNext()) {
				node = iter2.next();
				if (node.getTag() == Tag.FACTOR) {
					write(node);
					if (multOper != 0) {
						if (multOper == Tag.MUL) {
							writer.write("FMULP\r\n");
						} else {
							if (multOper == Tag.DIV) {
								writer.write("FDIVP\r\n");
							} else {
								writer.write("PUSH ebx\r\n");
								writer.write("PUSH ecx\r\n");
								writer.write("FISTP tempShiftCl\r\n");
								writer.write("FISTP tempShift\r\n");
								writer.write("MOV ecx, dword ptr[tempShiftCl]\r\n");
								writer.write("MOV ebx, dword ptr[tempShift]\r\n");
								if (multOper == Tag.SHR) {
									writer.write("SHR ebx, cl\r\n");
								} else {
									writer.write("SHL ebx, cl\r\n");
								}
								writer.write("MOV dword ptr[temp1], ebx\r\n");
								writer.write("FILD temp1\r\n");
								writer.write("POP ecx\r\n");
								writer.write("POP ebx\r\n");
							}
						}
						multOper = 0;
					}
				} else {
					multOper = node.getTag();
				}
			}
			break;
		case Tag.FACTOR:
			if (root.getChildren().get(0).getTag() == Tag.LEFT_PARENTHESIS) {
				write(root.getChildren().get(1));
			} else {
				if (root.getChildren().get(0).getTag() == Tag.SQRT) {
					write(root.getChildren().get(1));
					writer.write("FSQRT\r\n");
				} else {
					if (root.getChildren().get(0).getType() == Tag.BOOL_NUM
							|| root.getChildren().get(0).getType() == Tag.INT_NUM) {
						writer.write("FILD " + ((root.getChildren().get(0).getTag() == Tag.ID) ? "" : "num")
								+ root.getChildren().get(0).getStrValue().replace(".", "$") + "\r\n");
					} else {
						writer.write("FLD " + ((root.getChildren().get(0).getTag() == Tag.ID) ? "" : "num")
								+ root.getChildren().get(0).getStrValue().replace(".", "$") + "\r\n");
					}
				}
			}
			break;
		}
	}
}
