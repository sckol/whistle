package ru.niir.protowhistle.ui.component;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import ru.niir.protowhistle.ui.UIController;
import ru.niir.protowhistle.util.Console;

public class ConsoleComponent extends Form implements Component {
	private final StringItem stringItem = new StringItem(null, "");
	private final Console console = Console.getInstance();

	public ConsoleComponent() {
		super("Console");
		append(stringItem);
		addCommand(new Command("Exit", Command.EXIT, 0));
	}

	public void show(final Display display, final UIController controller) {
		refresh();
		setCommandListener(new CommandListener() {
			public void commandAction(Command c, Displayable d) {
				controller.showMainMenu();
			}
		});
		display.setCurrent(this);
	}

	public void refresh() {
		stringItem.setText(console.getText());
	}
}
