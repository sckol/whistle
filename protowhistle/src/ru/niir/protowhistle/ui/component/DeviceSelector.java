package ru.niir.protowhistle.ui.component;

import javax.bluetooth.BluetoothStateException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import ru.niir.protowhistle.io.BeaconDiscoverer;
import ru.niir.protowhistle.io.StorageManager;
import ru.niir.protowhistle.ui.UIController;
import ru.niir.protowhistle.util.Console;

public class DeviceSelector implements Component {
	private final BeaconDiscoverer discoverer;
	private final Console console = Console.getInstance();
	private final StorageManager storageManager;

	public DeviceSelector(final BeaconDiscoverer discoverer,
			final StorageManager storageManager) {
		super();
		this.discoverer = discoverer;
		this.storageManager = storageManager;
	}

	public void show(final Display display, final UIController controller) {
		display.setCurrent(new WaitingAlert("Поиск устроств Bluetooth..."));
		new Thread(new Runnable() {
			public void run() {
				try {
					discoverer.refreshDeviceList(15000);
				} catch (BluetoothStateException e) {
					controller.printStack(e,
							"Error while searching Bluetooth devices");
					return;
				}
				final List list = new List("Devices", List.IMPLICIT,
						discoverer.getDeviceNames(), null);
				list.setSelectCommand(new Command("Выбор", Command.OK, 0));
				list.addCommand(new Command("Назад", Command.BACK, 1));
				list.setCommandListener(new CommandListener() {
					public void commandAction(final Command command,
							final Displayable displayable) {
						switch (command.getCommandType()) {
						case Command.OK:
							try {
								final String url = discoverer.getUrl(list
										.getSelectedIndex());
								if (url == null) {
									console.println("Service not found");
									controller.showConsole();
									return;
								}
								storageManager.saveURL(url);
								controller.showMainMenu();
							} catch (BluetoothStateException e) {
								controller
										.printStack(e,
												"Cannot fetch information about selected device");
							} catch (RecordStoreNotOpenException e) {
								controller.printStack(e,
										"Cannot open record store");
							} catch (RecordStoreException e) {
								controller.printStack(e,
										"Cannot store fetched URL");
							}
							break;
						case Command.BACK:
							controller.showMainMenu();
							break;
						}
					}
				});
				display.setCurrent(list);
			}
		}).start();
	}
}
