package ru.niir.protowhistle.lisp;

import ru.niir.protowhistle.io.ConnectionManager.GatewayReader;

public class LispReader implements GatewayReader {
	private StringBuffer buffer = new StringBuffer();
	private final LispEvaluator evaluator;

	public LispReader(final LispEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	public void onSymbolRead(char c) {
		if (c != ')')
			buffer.append(c);
		else {
			evaluator.eval(buffer.toString() + ')');
			buffer = new StringBuffer();
		}
	}
}
