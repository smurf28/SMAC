package Order;

import java.math.BigDecimal;

import error.LexicalErrorException;
import token.Token;
import token.TokenException;
import token.Tokenizer;

public class Precision {
	private int precision;
	public Precision(){
		precision=-1;
	}
	public int getPrecision() {
		return precision;
	}

	public String setPrecision(Tokenizer tokenizer) throws TokenException, LexicalErrorException {
		tokenizer.readNextToken();
		if(tokenizer.hasNextToken()){
			Token t=tokenizer.readNextToken();
			this.precision = (int)t.getNumber();
			return "precision set to "+this.precision;
		}else
			return "current precision is "+this.precision;
	}
	
	public double format(double f){
		BigDecimal b = new BigDecimal(f);
		return b.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public String subZeroAndDot(double f) {
		String s = Double.toString(f);
		if (s.indexOf(".") > 0) {
			s = s.replaceAll("0+?$", "");// remove  the extra 0
			s = s.replaceAll("[.]$", "");// if dot at the last,remove it
		}
		return s;
	}
	
}
