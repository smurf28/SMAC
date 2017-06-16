package algorithm;

/*
 * 分析表达式
 * */
import java.util.*;

import Order.Session;
import Order.Variables;
import error.GeneralErrorException;
import error.LexicalErrorException;
import error.SyntaxErrorException;
import token.Token;
import token.TokenException;
import token.Tokenizer;

public class Evaluator {
	private Stack<Double> value;
	private Stack<String> operator;
	private double last;

	public Evaluator() {
		value = new Stack<Double>();
		operator = new Stack<String>();
	}

	public double algorithm(Tokenizer input, Variables variables) throws TokenException, GeneralErrorException,
			EmptyStackException, SyntaxErrorException, LexicalErrorException {
		// 1
		Token before = null;
		while (input.hasNextToken()) {
			Token t = input.peekNextToken();
			// System.out.println(t);
			// process value stack (push)
			if (t.isNumber())
				value.push(t.getNumber());
			// if the token is a variables,find the variables'value then push
			// into value stack
			if (t.isIdentifier()) {
				if (variables.contains(t.getIdentifier())) {
					Double val = variables.getValue(t.getIdentifier());
					value.push(val);
				} else if (t.getIdentifier().equals("last")) {
					value.push(last);
				} else
					throw new GeneralErrorException("Let is not a variable");
			}

			// process operator stack (push) when read '('
			if (t.isDelimiter() && t.getDelimiter().equals("("))
				operator.push(t.getDelimiter());

			// operator stack (pop) when read ')'
			if (t.isDelimiter() && t.getDelimiter().equals(")")) {
				while (!operator.peek().equals("(")) {
					Operator();
				}
				operator.pop();
			}
			// process operator stack (push) when read the operator
			if (t.isOperator()) {
				FunOp thisOp = new FunOp(t.getOperator());
				boolean flag = true;
				while (!operator.isEmpty() && flag) {
					FunOp Op = new FunOp(operator.peek());
					if (Op.getPrority() >= thisOp.getPrority()) {
						Operator();
					} else
						flag = false;
				}
				operator.push(t.getOperator());
			}
			before = t;
			input.readNextToken();
		}
		while (!operator.isEmpty())
			Operator();

		last = value.pop();
		return last;
	}

	/*
	 * this function is to evaluator the value which is not the final result at
	 * last the temporary value will push into the value stack
	 */
	private void Operator() throws EmptyStackException, GeneralErrorException, SyntaxErrorException {
		try {
			String op = operator.pop();
			FunOp funop = new FunOp(op);
			Mathematical math = new Mathematical();

			if (funop.getArity() == 2) {
				double val1 = value.pop();
				double val2 = value.pop();
				double result = math.getResult(val1, val2, op);
				value.push(result);
			} else if (funop.getArity() == 1) {
				double val = value.pop();
				double result = math.getResult(val, op);
				value.push(result);
			}
		} catch (EmptyStackException e) {
			throw new SyntaxErrorException("malformed expression");
		}

	}

}
