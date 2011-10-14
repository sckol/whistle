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
import ru.niir.protowhistle.io.SmsProcessor;
import ru.niir.protowhistle.io.StorageManager;
import ru.niir.protowhistle.lisp.LispEvaluator;
import ru.niir.protowhistle.ui.component.ConsoleComponent;
import ru.niir.protowhistle.util.Console;

public class NotifyMidlet extends MIDlet implements Exitable {
	private Display display;
	private UIController controller;
	private Console console;
	private SmsProcessor smsProcessor;
	private LispEvaluator lispEvaluator;

	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		console = Console.getInstance();
		try {
			final StorageManager storageManager = StorageManager
					.getStorageManager();
			lispEvaluator = new LispEvaluator();
			controller = new UIController(display, lispEvaluator,
					BeaconDiscoverer.createBeaconDiscoverer(),
					new ConnectionManager(), storageManager,
					new MediaManager(), this,
					System.getProperty("fileconn.dir.memorycard") + "whistle/");
			smsProcessor = new SmsProcessor(lispEvaluator);
			switch (smsProcessor.getProcessState()) {
			case SmsProcessor.PROCESSED:
				return;
			case SmsProcessor.ERROR:
				controller.showConsole();
				break;
			case SmsProcessor.NOT_PROCESSED:
				controller.showMainMenu();
				break;
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

	public void exit() {
		try {
			destroyApp(false);
		} catch (MIDletStateChangeException e) {
			e.printStackTrace();
		}
		notifyDestroyed();
	}
}
