package ru.niir.protowhistle.lisp;

import ru.niir.protowhistle.ui.UIController;
import ru.niir.protowhistle.util.Console;

public class LispEvaluator {
	private UIController controller;
	private final Console console = Console.getInstance();

	public void eval(final String cmd) {
		final StringBuffer buffer = new StringBuffer(cmd);
		if (cmd.startsWith("(emergency 0 ")) {
			buffer.delete(0, "(emergency 0 ".length());
			buffer.deleteCharAt(buffer.length() - 1);
			try {
				controller.showAlarm(Integer.parseInt(buffer.toString()));
			} catch (NumberFormatException e) {
				console.println("Cannot read emergency state");
			}
		} else {
			console.println("Unknown command");
		}
	}

	public void setController(final UIController controller) {
		this.controller = controller;
	}
	
}
