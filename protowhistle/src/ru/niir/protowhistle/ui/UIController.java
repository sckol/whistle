package ru.niir.protowhistle.ui;

import javax.microedition.lcdui.Display;

import ru.niir.protowhistle.io.MediaManager;
import ru.niir.protowhistle.io.BeaconDiscoverer;
import ru.niir.protowhistle.io.ConnectionManager;
import ru.niir.protowhistle.io.ImageManager;
import ru.niir.protowhistle.io.StorageManager;
import ru.niir.protowhistle.lisp.LispEvaluator;
import ru.niir.protowhistle.lisp.LispReader;
import ru.niir.protowhistle.ui.component.Alarm;
import ru.niir.protowhistle.ui.component.CategorySelector;
import ru.niir.protowhistle.ui.component.ConsoleComponent;
import ru.niir.protowhistle.ui.component.DeviceSelector;
import ru.niir.protowhistle.ui.component.MainMenu;
import ru.niir.protowhistle.ui.component.Terminal;
import ru.niir.protowhistle.ui.component.VideoPlayer;
import ru.niir.protowhistle.util.Console;

public class UIController {
	private ConsoleComponent consoleComponent;
	private final Display display;
	private final Console console = Console.getInstance();
	private final DeviceSelector deviceSelector;
	private final ConnectionManager connectionManager;
	private final MainMenu mainMenu;
	private final Terminal terminal;
	private final Alarm alarm;
	private final CategorySelector categorySelector;
	private final VideoPlayer videoPlayer;
	// private final Calibrator calibrator;
	private final LispReader lispReader;

	public UIController(final Display display,
			final LispEvaluator lispEvaluator,
			final BeaconDiscoverer discoverer,
			final ConnectionManager connectionManager,
			final StorageManager storageManager,
			final ImageManager imageManager, final MediaManager mediaManager) {
		this.display = display;
		this.mainMenu = new MainMenu();
		this.consoleComponent = new ConsoleComponent();
		this.terminal = new Terminal(connectionManager);
		this.deviceSelector = new DeviceSelector(discoverer, storageManager);
		this.connectionManager = connectionManager;
		this.alarm = new Alarm(mediaManager);
		this.categorySelector = new CategorySelector(storageManager);
		this.videoPlayer = new VideoPlayer();
		lispEvaluator.setController(this);
		this.lispReader = new LispReader(lispEvaluator);
	}

	public void showConsole() {
		consoleComponent.show(display, this);
	}

	public void printStack(final Throwable t) {
		console.printThrowable(t);
		showConsole();
	}

	public void printStack(final Throwable t, final String comment) {
		console.printThrowable(t, comment);
		showConsole();
	}

	public void showMainMenu() {
		mainMenu.show(display, this);
	}

	public void showDeviceSelector() {
		deviceSelector.show(display, this);
	}

	public void showCategorySelector() {
		categorySelector.show(display, this);
	}

	public void showTerminal() {
		connectionManager.setGatewayReader(terminal);
		if (!connectionManager.isConnected()) {
			if (!connectionManager.connect()) {
				showConsole();
				return;
			}
		}
		terminal.show(display, this);
	}

	public void showAlarm(final int state) {
		alarm.updateState(state);
		alarm.show(display, this);
		connectionManager.setGatewayReader(lispReader);
		if (!connectionManager.isConnected()) {
			new Thread(new Runnable() {
				public void run() {
					if (!connectionManager.connect()) {
						console.println("Cannot connect");
					}
				}
			}).start();
		}
	}

	public void showVideo() {
		videoPlayer.show(display, this);
	}

	public void showAlarm() {
		showAlarm(0);
	}

	public void showCalibrationMenu() {}
}
