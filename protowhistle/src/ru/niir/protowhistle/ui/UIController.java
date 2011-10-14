package ru.niir.protowhistle.ui;

import javax.microedition.lcdui.Display;

import ru.niir.protowhistle.io.BeaconDiscoverer;
import ru.niir.protowhistle.io.ConnectionManager;
import ru.niir.protowhistle.io.MediaManager;
import ru.niir.protowhistle.io.StorageManager;
import ru.niir.protowhistle.lisp.LispEvaluator;
import ru.niir.protowhistle.lisp.LispReader;
import ru.niir.protowhistle.ui.component.Alarm;
import ru.niir.protowhistle.ui.component.Calibrator;
import ru.niir.protowhistle.ui.component.CategorySelector;
import ru.niir.protowhistle.ui.component.ConsoleComponent;
import ru.niir.protowhistle.ui.component.DeviceSelector;
import ru.niir.protowhistle.ui.component.MainMenu;
import ru.niir.protowhistle.ui.component.Terminal;
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
	private final LispReader lispReader;
	private final StorageManager storageManager;
	private final Exitable midlet;
	private final Calibrator calibrator;
	private final MediaManager mediaManager;
	private final String rootDirectory;

	public UIController(final Display display,
			final LispEvaluator lispEvaluator,
			final BeaconDiscoverer discoverer,
			final ConnectionManager connectionManager,
			final StorageManager storageManager,
			final MediaManager mediaManager, final Exitable midlet,
			final String rootDirectory) {
		this.display = display;
		this.rootDirectory = rootDirectory;
		this.mainMenu = new MainMenu();
		this.storageManager = storageManager;
		this.mediaManager = mediaManager;
		this.consoleComponent = new ConsoleComponent();
		this.terminal = new Terminal(connectionManager);
		this.deviceSelector = new DeviceSelector(discoverer, storageManager);
		this.connectionManager = connectionManager;
		this.alarm = new Alarm(mediaManager, storageManager, rootDirectory);
		this.categorySelector = new CategorySelector(storageManager, connectionManager);
		lispEvaluator.setController(this);
		this.lispReader = new LispReader(lispEvaluator);
		this.midlet = midlet;
		this.calibrator = new Calibrator(connectionManager);
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
		if (tryConnect())
			terminal.show(display, this);
	}

	public void showAlarm(final int type, final int state) {
		if (storageManager.loadCategory() == 'b' && state == 0 && type == 0) {
			midlet.exit();
			return;
		}
		alarm.updateState(state, type);
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
		final CanvasWithListeners canvas = mediaManager.getCanvas();
		canvas.setFullScreenMode(true);
		canvas.setKeyPressedListener(new CanvasListener() {
			public void proceedKeyEvent(int keyCode) {
				mediaManager.stop();
				showMainMenu();
			}
		});
		mediaManager.playMedia(rootDirectory + "10ae");
		display.setCurrent(canvas);
	}

	public void showAlarm() {
		showAlarm(0, 0);
	}

	public void showCalibrator() {
		if (tryConnect()) {
			calibrator.show(display, this);
			connectionManager.setGatewayReader(calibrator);
		}
	}

	public void fakeFall() {
		if (tryConnect()) {
			connectionManager.switchToMode('f');
		}
	}

	private boolean tryConnect() {
		if (!connectionManager.isConnected()) {
			if (!connectionManager.connect()) {
				showConsole();
				return false;
			}
			return true;
		}
		return true;
	}
}
