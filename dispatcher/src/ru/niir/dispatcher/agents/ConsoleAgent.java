package ru.niir.dispatcher.agents;

import java.util.HashMap;
import java.util.Scanner;

import ru.niir.dispatcher.EventBus;
import ru.niir.dispatcher.NodeType;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.ScannerResultsEvent;
import ru.niir.dispatcher.events.SensorChangedEvent;

public class ConsoleAgent implements Runnable {
	private final EventBus eventBus;

	public ConsoleAgent(final EventBus eventBus) {
		super();
		this.eventBus = eventBus;
	}

	@Override
	public void run() {
		final Scanner scanner = new Scanner(System.in);
		while (true) {
			final String[] s = scanner.nextLine().split(" ");
			if (s[0].equals("reset")) {
				eventBus.fireEvent(new ResetEvent());
			} else if (s[0].equals("scanner")) {
				try {
					final HashMap<String, NodeType> scannerResults = new HashMap<String, NodeType>();
					for (int i = 1; i + 1 < s.length; i += 2) {
						scannerResults.put(s[i + 1], NodeType.valueOf(s[i]));
					}
					eventBus.fireEvent(new ScannerResultsEvent(scannerResults));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (s[0].equals("change")) {
				try {
					eventBus.fireEvent(new SensorChangedEvent(s[1], Integer
							.parseInt(s[2])));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Cannot parse");
			}
		}
	}
}
