package Order;

import token.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

import algorithm.Evaluator;
import error.GeneralErrorException;
import error.LexicalErrorException;
import error.SyntaxErrorException;

public class Variables {
	private static final String Path = "save\\";
	private Map<String, Double> var;
	private Precision precision;

	public Variables() {
		var = new HashMap<>();
	}

	public boolean contains(String key) {
		return var.containsKey(key);
	}

	public double getValue(String key) {
		return var.get(key);
	}

	public String command(Tokenizer tokenizer, Token t, Session session, Evaluator eva, Precision precision)
			throws TokenException, LexicalErrorException, SyntaxErrorException, GeneralErrorException {
		String r = "";
		try {
			this.precision = precision;
			String order = t.getIdentifier();
			if (order.equals("let"))
				r = setLet(tokenizer, eva);
			else if (order.equals("reset"))
				r = setReset(tokenizer);
			else if (order.equals("save"))
				r = setSave(tokenizer);
			else if (order.equals("saved"))
				r = saved();
			else if (order.equals("load"))
				r = load(tokenizer);
			else
				r = isVariables(tokenizer);
		} catch (FileNotFoundException e) {
			throw new GeneralErrorException("file is not found");
		}
		return r;
	}

	private String load(Tokenizer tokenizer) throws TokenException, LexicalErrorException, FileNotFoundException {
		tokenizer.readNextToken();
		String file_name = tokenizer.peekNextToken().getString();
		File output_file = new File(Path + file_name + ".txt");
		Scanner input = new Scanner(output_file);
		while (input.hasNextLine()) {
			String key = input.next();
			input.next();
			double value = input.nextDouble();
			this.var.put(key, value);
			input.nextLine();
		}
		return file_name + " loaded";
	}

	/*
	 * read all the file's name have saved
	 */
	private String saved() {
		String r = "";
		File file = new File(Path);
		File[] array = file.listFiles();
		for (int i = 0; i < array.length; i++) {
			// only take file name
			String fullname = array[i].getName();
			String name = fullname.substring(0, fullname.indexOf("."));
			r += name + "\n";
		}
		return r;
	}

	private String setSave(Tokenizer tokenizer) throws TokenException, LexicalErrorException, FileNotFoundException {
		tokenizer.readNextToken();
		String file_name = tokenizer.peekNextToken().getString();
		File output_file = new File(Path + file_name + ".txt");
		PrintStream output = new PrintStream(output_file);
		tokenizer.readNextToken();
		if (tokenizer.hasNextToken()) {
			Token t = tokenizer.peekNextToken();
			if (var.containsKey(t.getIdentifier()))
				output.println(t.getIdentifier() + " = " + var.get(t.getIdentifier()));
			else {
				return t.getIdentifier() + " is not a variable";
			}
		} else {
			save(output);
		}
		return "variables saved in " + file_name;
	}

	private void save(PrintStream output) {
		for (Map.Entry<String, Double> entry : this.var.entrySet()) {
			String key = entry.getKey();
			Double value = entry.getValue();
			output.println(key + " = " + value);
		}
	}

	private String setLet(Tokenizer tokenizer, Evaluator eva) throws TokenException, LexicalErrorException,
			EmptyStackException, GeneralErrorException, SyntaxErrorException {
		// boolean flag = false;
		String r = "";
		tokenizer.readNextToken();
		if (tokenizer.hasNextToken()) {// let is following with others
			Token t = tokenizer.peekNextToken();
			String var = t.getIdentifier();// the variables
			tokenizer.readNextToken();
			tokenizer.readNextToken();// read equal
			// after "equal" make a calculation
			double num = eva.algorithm(tokenizer, this);
			this.var.put(var, num);
			if (precision.getPrecision() >= 0) {
				num = precision.format(num);
				return Double.toString(num);
			} else {
				String s = precision.subZeroAndDot(num);
				return s;
			}
		} // let is following with others

		else {// only "let"
			if (!this.var.isEmpty()) {
				for (Map.Entry<String, Double> entry : this.var.entrySet()) {
					String key = entry.getKey();
					Double value = entry.getValue();
					if (precision.getPrecision() >= 0) {
						value = precision.format(value);
						r += key + " = " + value + "\n";
					} else {
						String s = precision.subZeroAndDot(value);
						r += key + " = " + s + "\n";
					}
				}
				return r;
			} else {
				return "no variable defined";
			}
		}
	}

	private String setReset(Tokenizer tokenizer) throws LexicalErrorException, SyntaxErrorException, TokenException {
		String r = "";
		boolean flag = false;
		tokenizer.readNextToken();
		while (tokenizer.hasNextToken()) {
			flag = true;
			Token t = tokenizer.peekNextToken();// t在variables中是否存在
			if (this.var.containsKey(t.getIdentifier())) {
				this.var.remove(t.getIdentifier());
				r += t.getIdentifier() + " has been removed" + "\n";
			} else
				throw new SyntaxErrorException(t.getIdentifier() + " is not a variable");
			tokenizer.readNextToken();
		}
		if (!flag) {
			this.var.clear();
		}
		return r;
	}

	/*
	 * input a variable if the variable exist output the value of the variable
	 * else output error message
	 */
	private String isVariables(Tokenizer tokenizer) throws TokenException, LexicalErrorException {
		Token t = tokenizer.peekNextToken();
		double value = this.var.get(t.getIdentifier());
		if (this.var.containsKey(t.getIdentifier())) {
			if (precision.getPrecision() >= 0) {
				value = precision.format(value);
				return Double.toString(value);
			} else {
				String s = precision.subZeroAndDot(value);
				return s;
			}
		} else
			return t.getIdentifier() + " is not a variable";
	}
}