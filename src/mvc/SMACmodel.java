package mvc;

import java.util.*;

import Order.Keywords;
import Order.Precision;
import Order.Session;
import Order.Variables;
import algorithm.Evaluator;
import error.GeneralErrorException;
import error.LexicalErrorException;
import error.SyntaxErrorException;
import token.Token;
import token.TokenException;
import token.Tokenizer;

/**
 * This class is the model in the MVC architecture. The model performs
 * computations and notifies the view with the result. The model should use your
 * code to evaluate SMAC commands and compute the result
 */
public class SMACmodel extends Observable {
	private Session session;
	private Variables variables;
	private Precision precision;

	public SMACmodel() {
		session = new Session();
		variables = new Variables();
		precision = new Precision();
	}

	/**
	 * evaluates the input and notify the view with the result (a String)
	 */
	public void eval(String input) {
		////// CHANGE THIS PART //////
		String r = "";
		// the session control the program whether record the input and output
		Evaluator eva = new Evaluator();
		// get the keyword in this system
		Keywords words = new Keywords();
		Set<String> key = words.getKeywords();
		if (session.get_islog() && !input.equals("log end"))
			r += ">> " + input + "\n";
		else
			r += "> " + input + "\n";
		try {
			// builds a new tokenizer with the string input from the user
			Tokenizer tokenizer = new Tokenizer(input);
			if (tokenizer.hasNextToken()) {
				Token t = tokenizer.peekNextToken();
				// System.out.println(t);
				// Delimiter
				if (t.isDelimiter() && t.getDelimiter().equals("(")) 
					r += eva.algorithm(tokenizer, variables);
				// Operator for example :-5 + 3
				else if (t.isOperator() || t.isNumber()) 
					r += eva.algorithm(tokenizer, variables);
				// Identifier
				else if (t.isIdentifier()) {
					// for example input is:x + y or x,the part will use
					// algorithm
					if (variables.contains(t.getIdentifier()) || t.getIdentifier().equals("last")) 
						r += eva.algorithm(tokenizer, variables);
					// the part contains "let" "reset" etc. Identifier
					else if (key.contains(t.getIdentifier())) {
						if (t.getIdentifier().equals("log") || t.getIdentifier().equals("loged"))
							r += session.set(tokenizer);
						else if (t.getIdentifier().equals("precision"))
							r += precision.setPrecision(tokenizer);
						else// handle about "let reset save load"
							r += variables.command(tokenizer, t, session, eva, precision);
					} else
						throw new GeneralErrorException(t.getIdentifier() + " is not a variable");
				}
			}
		} catch (GeneralErrorException e) {
			r += e.toString();
		} catch (SyntaxErrorException e) {
			r += e.toString();
		} catch (LexicalErrorException e) {
			r += e.toString();
		} catch (TokenException e) {
			r += e.getMessage();
		}
		if (session.get_islog())
			session.write(r);
		// r+=evaluate.analyse(input, session);
		//////////////////////////////
		// this notify the view and send the result
		setChanged();
		notifyObservers(r);
	}
}
