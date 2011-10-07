package ru.niir.protowhistle.ui.component;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import ru.niir.protowhistle.io.ConnectionManager;
import ru.niir.protowhistle.io.ConnectionManager.GatewayReader;
import ru.niir.protowhistle.ui.UIController;
import ru.niir.protowhistle.util.Console;

public class Terminal extends ConsoleComponent implements GatewayReader {
	private final Console console = Console.getInstance();

	public Terminal(final ConnectionManager connectionManager) {
		super();
		addCommand(new Command("Clear", Command.CANCEL, 0));
	}

	public void show(final Display display, final UIController controller) {
		super.show(display, controller);
		setCommandListener(new CommandListener() {
			public void commandAction(final Command c, final Displayable d) {
				switch (c.getCommandType()) {
				case Command.EXIT:
					controller.showMainMenu();
					break;
				case Command.CANCEL:
					console.clear();
					refresh();
					break;
				}
			}
		});
	}

	public void onSymbolRead(final char c) {
		console.append(c);
		refresh();
	}
}
