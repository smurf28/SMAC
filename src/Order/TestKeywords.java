package Order;

import java.io.PrintStream;
import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.Set;

import algorithm.Evaluator;
import token.Token;
import token.TokenException;
import token.Tokenizer;
import error.*;

public class TestKeywords {

	public static void main(String[] args) {
		// get the keyword in this system
		Keywords words = new Keywords();
		Set<String> key = words.getKeywords();
		// the object use to save variable and process keyword handling
		Variables variables = new Variables();
		// process the mathematical expression
		Evaluator eva = new Evaluator();
		// the session control the program whether record the input and output
		Session session = new Session();
		// format of the result
		Precision precision=new Precision();
		System.out.println("Welcome to SMAC");
		System.out.print("> ");
		// user input a string
		Scanner console = new Scanner(System.in);
		String input = console.nextLine();

		while (!input.equals("exit")) {
			try {
				// builds a new tokenizer with the string input from the user
				Tokenizer tokenizer = new Tokenizer(input);
				if (tokenizer.hasNextToken()) {
					Token t = tokenizer.peekNextToken();
					// System.out.println(t);
					// write into file before log end
					if (session.get_islog() && !input.equals("log end"))
						session.write(">> " + input);
					// Delimiter
					if (t.isDelimiter() && t.getDelimiter().equals("("))// À¨ºÅ£¬¶ººÅ
					{
						Double result = eva.algorithm(tokenizer, variables);
						if(precision.getPrecision()>=0){
							result=precision.format(result);	
							System.out.println(result);
							if (session.get_islog())
								session.write(result);
						}else{
							String s=precision.subZeroAndDot(result);
							System.out.println(s);
							if (session.get_islog())
								session.write(s);					
						}
					}
					// Operator for example :-5 + 3
					else if (t.isOperator()) {
						Double result = eva.algorithm(tokenizer, variables);
						if(precision.getPrecision()>=0){
							result=precision.format(result);	
							System.out.println(result);
							if (session.get_islog())
								session.write(result);
						}else{
							String s=precision.subZeroAndDot(result);
							System.out.println(s);
							if (session.get_islog())
								session.write(s);					
						}
					}
					// Identifier
					else if (t.isIdentifier()) {
						// for example input is:x + y or x,the part will use
						// algorithm
						if (variables.contains(t.getIdentifier()) || t.getIdentifier().equals("last")) {
							Double result = eva.algorithm(tokenizer, variables);
							if(precision.getPrecision()>=0){
								result=precision.format(result);	
								System.out.println(result);
								if (session.get_islog())
									session.write(result);
							}else{
								String s=precision.subZeroAndDot(result);
								System.out.println(s);
								if (session.get_islog())
									session.write(s);					
							}
						}
						// the part contains "let" "reset" etc. Identifier
						else if (key.contains(t.getIdentifier())) {
							if (t.getIdentifier().equals("log") || t.getIdentifier().equals("loged"))
								session.set(tokenizer);
							else if(t.getIdentifier().equals("precision") ){
								precision.setPrecision(tokenizer);
							}
							else// handle about "let reset save load"
								variables.command(tokenizer, t, session, eva,precision);
						} else
							throw new GeneralErrorException(t.getIdentifier() + " is not a variable");
					}
					// the part use algorithm
					else if (t.isNumber()) {
						Double result = eva.algorithm(tokenizer, variables);
						if(precision.getPrecision()>=0){
							result=precision.format(result);	
							System.out.println(result);
							if (session.get_islog())
								session.write(result);
						}else{
							String s=precision.subZeroAndDot(result);
							System.out.println(s);
							if (session.get_islog())
								session.write(s);					
						}
					}
				}
			} catch (GeneralErrorException e) {
				System.out.println(e.toString());
				if (session.get_islog())
					session.write(e.toString());
			} catch (SyntaxErrorException e) {
				System.out.println(e.toString());
				if (session.get_islog())
					session.write(e.toString());
			} catch (LexicalErrorException e) {
				System.out.print(e.toString());
			} catch (TokenException e) {
				System.out.print(e);
			}
			if (session.get_islog())
				System.out.print(">> ");
			else
				System.out.print("> ");
			input = console.nextLine();
		}
		System.out.println("Thank you for using SMAC");
	}
	
	private void formatResult(){
		
	}
}
