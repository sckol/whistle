package ru.niir.protowhistle.ui.component;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import ru.niir.protowhistle.io.ConnectionManager;
import ru.niir.protowhistle.io.ConnectionManager.GatewayReader;
import ru.niir.protowhistle.ui.UIController;
import ru.niir.protowhistle.util.Console;

public class Calibrator extends List implements Component, GatewayReader {
	private static final int TIMEOUT = 20000;
	private final ConnectionManager connectionManager;
	private final Timer timeoutTimer = new Timer();
	private Display display;
	private final Console console = Console.getInstance();

	public Calibrator(final ConnectionManager connectionManager) {
		super("Калибровка", List.IMPLICIT, new String[] { "Сканировать" }, null);
		this.connectionManager = connectionManager;
		setSelectCommand(new Command("Выбор", Command.OK, 0));
		addCommand(new Command("Exit", Command.EXIT, 1));
	}

	public void show(final Display display, final UIController controller) {
		this.display = display;
		setCommandListener(new CommandListener() {
			public void commandAction(final Command c, final Displayable d) {
				switch (c.getCommandType()) {
				case Command.EXIT:
					timeoutTimer.cancel();
					connectionManager.switchToNormalMode();
					controller.showMainMenu();
					break;
				case Command.OK:
					switch (getSelectedIndex()) {
					case 0:
						connectionManager.switchToMode('C');
						display.setCurrent(new WaitingAlert(
								"Ожидание ответа от устройства"));
						timeoutTimer.schedule(new TimerTask() {
							public void run() {
								connectionManager.switchToNormalMode();
								console.println("Timeout exceeded");
								controller.showConsole();
							}
						}, TIMEOUT);
						break;
					}
					break;
				}
			}
		});
		display.setCurrent(this);
	}

	public void onSymbolRead(char c) {
		timeoutTimer.cancel();
		display.setCurrent(new Alert(c + "neighbor(s)"), this);
		connectionManager.switchToNormalMode();
	}
}
