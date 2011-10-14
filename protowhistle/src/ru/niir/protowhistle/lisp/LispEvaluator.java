package ru.niir.protowhistle.lisp;

import ru.niir.protowhistle.ui.UIController;
import ru.niir.protowhistle.util.Console;

public class LispEvaluator {
	private UIController controller;
	private final Console console = Console.getInstance();
	private final String COMMAND_PATTERN = "(emergency A B)";

	public LispEvaluator() {
		super();
	}

	public boolean eval(final String cmd) {
		if (cmd.length() == COMMAND_PATTERN.length()) {
			final int argument1Index = COMMAND_PATTERN.indexOf('A');
			final int argument2Index = COMMAND_PATTERN.indexOf('B');
			if (cmd.startsWith(COMMAND_PATTERN.substring(0, argument1Index))) {
				try {
					final int type = Integer.parseInt(cmd.substring(
							argument1Index, argument1Index + 1));
					final int state = Integer.parseInt(cmd.substring(
							argument2Index, argument2Index + 1));
					controller.showAlarm(type, state);
					return true;
				} catch (NumberFormatException e) {
					console.println("Cannot read emergency state");
					return false;
				}
			} else {
				console.println("Unknown command:" + cmd);
				return false;
			}
		} else {
			console.println("Unknown command" + cmd);
			return false;
		}
	}

	public void setController(final UIController controller) {
		this.controller = controller;
	}
}
