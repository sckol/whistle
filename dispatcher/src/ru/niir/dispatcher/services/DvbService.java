package ru.niir.dispatcher.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.EviRequestedEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.ShopComplaintEvent;
import ru.niir.dispatcher.events.ShopSubmittedEvent;
import ru.niir.dispatcher.events.StateChangedEvent;

public class DvbService implements DispatcherService {
	private final String octavepath, octaveWorkingDir;
	private int commandNumber = (int) (Math.random() * 1000);

	public DvbService(final String octavepath, final String octaveWorkingDir) {
		this.octavepath = octavepath;
		this.octaveWorkingDir = octaveWorkingDir;
	}

	@Override
	public void onEvent(DispatcherEvent _event) {
		try {
			if (_event instanceof ShopSubmittedEvent) {
				sendCommand("# cp /dvb/14.05.2013.txt /config/messages; cp /config.defaults/StbMainApp/menu-shop.xml /config.defaults/StbMainApp/menu.xml");
			} else if (_event instanceof ShopComplaintEvent) {
				sendCommand("cp /config.defaults/StbMainApp/menu-disabled.xml /config.defaults/StbMainApp/menu.xml");
			} else if (_event instanceof ResetEvent) {
				if (!((ResetEvent) _event).isEmergencyOnly()) {
					sendCommand("cp /config.defaults/StbMainApp/menu-wo-shop.xml /config.defaults/StbMainApp/menu.xml");
				}
			} else if (_event instanceof StateChangedEvent) {
				final StateChangedEvent event = (StateChangedEvent) _event;
				if (event.getNewState() > 0 && event.getOldState() == 0) {
					sendMessage("#");
				}
			} else if (_event instanceof EviRequestedEvent) {
				final EviRequestedEvent event = (EviRequestedEvent) _event;
				sendCommand("*" + event.getPacketNumber());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(final String msg) throws IOException {
		executeOctave("message_adding", msg);
	}

	private void sendCommand(final String cmd) throws IOException {
		executeOctave("command_adding", cmd);
	}

	private void executeOctave(final String function, final String arg)
			throws IOException {
		Process p = Runtime
				.getRuntime()
				.exec(new String[] { octavepath, "--eval",
						function + "(\"" + arg + "\"," + commandNumber + ",10)" },
						null, new File(octaveWorkingDir));
		System.out.println(function + "(\"" + arg + "\"," + commandNumber
				+ ",10)");
		commandNumber++;
		String s = null;
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));

		// read the output from the command
		System.out.println("Here is the standard output of the command:\n");
		while ((s = stdInput.readLine()) != null) {
			System.out.println(s);
		}

		// read any errors from the attempted command
		System.out
				.println("Here is the standard error of the command (if any):\n");
		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}
	}
}
