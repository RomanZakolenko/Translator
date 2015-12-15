package spcoursework.translator;

import spcoursework.lexanalyser.LexException;
import spcoursework.semanticanalyser.SemanticException;
import spcoursework.syntaxanalyser.SyntaxException;

import java.io.IOException;

public class TestTranslator {
    public static void main(String[] args) throws LexException, SemanticException, SyntaxException, IOException {
        Translator.translate("C:\\Users\\Администратор\\Desktop\\examples\\quadratic_equation.pas", "C:\\Users\\Администратор\\Desktop\\examples\\quadratic_equation.asm");
    }
}
