package ru.niir.protowhistle.ui.component;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.rms.RecordStoreException;

import ru.niir.protowhistle.io.StorageManager;
import ru.niir.protowhistle.ui.UIController;

public class CategorySelector extends List implements Component {
	private final StorageManager storageManager;

	public CategorySelector(final StorageManager storageManager) {
		super("Select the category", List.IMPLICIT, new String[] {
				"Служащие", "Полностью дееспособные", "Частично недееспособные" }, null);
		this.storageManager = storageManager;
		addCommand(new Command("Выход", Command.EXIT, 0));
		setSelectCommand(new Command("Выбор", Command.OK, 0));
	}

	public void show(final Display display, final UIController controller) {
		setCommandListener(new CommandListener() {
			public void commandAction(Command c, Displayable d) {
				switch (c.getCommandType()) {
				case Command.OK:
					try {
						storageManager.saveCategory(StorageManager
								.indexToCategory(getSelectedIndex()));
					} catch (RecordStoreException e) {
						controller.printStack(e, "Cannot save category");
					}
					controller.showMainMenu();
				case Command.EXIT:
					controller.showMainMenu();
				}
			}
		});
		display.setCurrent(this);
	}
}
