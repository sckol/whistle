package ru.niir.protowhistle.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;

import ru.niir.protowhistle.io.ConnectionManager;
import ru.niir.protowhistle.io.ConnectionManager.GatewayReader;
import ru.niir.protowhistle.io.StorageManager;
import ru.niir.protowhistle.ui.component.Component;
import ru.niir.protowhistle.util.Console;
import ru.niir.protowhistle.util.StringUtils;

public class Calibrator extends List implements Component, GatewayReader {
	private final ConnectionManager connectionManager;
	private final StorageManager storageManager;
	private final StringBuffer buffer = new StringBuffer();
	private Console console = Console.getInstance();
	
	public Calibrator(final ConnectionManager connectionManager, final StorageManager storageManager) {
		super("Калибровка", List.IMPLICIT, new String[]{
				"Сканировать", "Записать"}, null);
		this.connectionManager = connectionManager;
		this.storageManager = storageManager;
	}


	public void show(final Display display, final UIController controller) {
		setCommandListener(new CommandListener() {
			public void commandAction(final Command c, final Displayable d) {
				switch (c.getCommandType()) {
				case Command.OK:
					
				}
			}
		});
		display.setCurrent(this);
	}

	public void onSymbolRead(char c) {
		if(c != ')') buffer.append(c);
		else if (buffer.toString().startsWith("(neigbors ")) {
			buffer.delete(0, "(neighbors ".length());
			String[] neighbors = StringUtils.split(buffer.toString(), ' ');
			
		} else {
			console.println("Unknown command");
		}
	}
	
//	private class CalibratorRequest extends Form implements Component {
//
//		public void show(Display display, UIController controller) {
//			// TODO Auto-generated method stub
//			
//		}
		
//	}
}
