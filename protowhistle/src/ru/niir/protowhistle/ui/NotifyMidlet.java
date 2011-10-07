package ru.niir.protowhistle.ui;

import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStoreException;

import ru.niir.protowhistle.io.MediaManager;
import ru.niir.protowhistle.io.BeaconDiscoverer;
import ru.niir.protowhistle.io.ConnectionManager;
import ru.niir.protowhistle.io.ImageManager;
import ru.niir.protowhistle.io.StorageManager;
import ru.niir.protowhistle.lisp.LispEvaluator;
import ru.niir.protowhistle.ui.component.ConsoleComponent;
import ru.niir.protowhistle.util.Console;

public class NotifyMidlet extends MIDlet {
	private Display display;
	private UIController controller;
	private Console console;
	private SmsProcessor smsProcessor;
	private LispEvaluator lispEvaluator = new LispEvaluator();

	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		console = Console.getInstance();
		final String rootDirectory = System.getProperty("fileconn.dir.photos")
				+ "whistle/";
		try {
			final StorageManager storageManager = StorageManager
					.getStorageManager();
			controller = new UIController(display, lispEvaluator,
					BeaconDiscoverer.createBeaconDiscoverer(),
					new ConnectionManager(), storageManager, new ImageManager(
							rootDirectory), new MediaManager(System
									.getProperty("fileconn.dir.memorycard") + "Images/whistle/", storageManager));
			smsProcessor = SmsProcessor.getSmsProcesser(controller,
					storageManager);
			switch (smsProcessor.getProcessState()) {
			case SmsProcessor.PROCESSED:
				return;
			case SmsProcessor.NOT_PROCESSED:
				controller.showMainMenu();
				break;
			case SmsProcessor.CANCEL:
				destroyApp(false);
				notifyDestroyed();
			}
		} catch (BluetoothStateException e) {
			showConsole(e, "Cannot initialize Bluetooth");
		} catch (RecordStoreException e) {
			showConsole(e, "Cannot initialize a record store");
		} catch (IOException e) {
			showConsole(e);
		}
	}

	private void showConsole(final Throwable t, final String comment) {
		console.printThrowable(t, comment);
		final ConsoleComponent consoleComponent = new ConsoleComponent();
		consoleComponent.refresh();
		display.setCurrent(consoleComponent);
	}

	private void showConsole(final Throwable t) {
		showConsole(t, null);
	}

	protected void destroyApp(final boolean b)
			throws MIDletStateChangeException {
		smsProcessor.close();
	}

	protected void pauseApp() {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}
}
