package Order;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import error.GeneralErrorException;
import error.LexicalErrorException;
import token.Token;
import token.TokenException;
import token.Tokenizer;

public class Session {

	private static final String Path = "log\\";
	private String current_file_name;
	private boolean islog;
	private PrintStream output;

	public Session() {
		islog = false;
	}

	/*
	 * this function handle the tokenizer about log
	 */
	public String set(Tokenizer tokenizer) throws TokenException, LexicalErrorException, GeneralErrorException {
		String r = "";
		try {
			Token T = tokenizer.peekNextToken();
			// if T equals log or log end or log "file"
			if (T.getIdentifier().equals("log")) {
				tokenizer.readNextToken();
				if (tokenizer.hasNextToken()) {
					Token s = tokenizer.peekNextToken();
					if (s.isIdentifier()) {// if T equals log end
						islog = false;
						r = "session was logged to " + current_file_name;
					} else {// input is log "file"
						current_file_name = s.getString();
						File output_file = new File(Path + current_file_name + ".txt");
						output = new PrintStream(output_file);
						islog = true;
						r = "logging session to " + current_file_name;
					}
				} else if (islog) // if T equals log
					r = current_file_name;
			} else
				r = this.loged();// if T equals loged
		} catch (FileNotFoundException e) {
			throw new GeneralErrorException("file not found");
		}
		return r;
	}

	public void write(String out) {
		output.println(out);
	}

	public void write(double out) {
		output.println(out);
	}

	public boolean get_islog() {
		return islog;
	}

	private String loged() {
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
}
