package ru.niir.dispatcher.agents;

import java.util.HashMap;
import java.util.Scanner;

import ru.niir.dispatcher.EventBus;
import ru.niir.dispatcher.NodeType;
import ru.niir.dispatcher.events.EviRequestedEvent;
import ru.niir.dispatcher.events.ExitEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.ScannerResultsEvent;
import ru.niir.dispatcher.events.SensorChangedEvent;
import ru.niir.dispatcher.events.ShopComplaintEvent;
import ru.niir.dispatcher.events.ShopOrderEvent;
import ru.niir.dispatcher.events.ShopSubmittedEvent;
import ru.niir.dispatcher.events.UserLocationChangedEvent;

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
			} else if (s[0].equals("shop-order")) {
				try {
					eventBus.fireEvent(new ShopOrderEvent(s[1], Integer
							.parseInt(s[2]), Double.parseDouble(s[3])));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (s[0].equals("shop-submit")) {
				try {
					eventBus.fireEvent(new ShopSubmittedEvent());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (s[0].equals("shop-complaint")) {
				try {
					eventBus.fireEvent(new ShopComplaintEvent());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (s[0].equals("location")) {
				try {
					eventBus.fireEvent(new UserLocationChangedEvent(s[1]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (s[0].equals("evi")) {
				try {
					eventBus.fireEvent(new EviRequestedEvent(Integer.parseInt(s[1])));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (s[0].equals("exit")) {
				try {
					eventBus.fireEvent(new ExitEvent());
					scanner.close();
					System.out.println("Exiting");
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Cannot parse");
			}
		}
	}
}
