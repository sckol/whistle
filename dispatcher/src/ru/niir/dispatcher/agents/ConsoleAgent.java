package ru.niir.dispatcher.agents;

import java.util.Scanner;

import ru.niir.dispatcher.EventBus;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.SensorChangedEvent;

public class ConsoleAgent implements Runnable {
	private final EventBus eventBus;

	public ConsoleAgent(final EventBus eventBus) {
		super();
		this.eventBus = eventBus;
	}

	@Override
	public void run() {
		final Scanner s = new Scanner(System.in);
		while (true) {
			final String q = s.next();
			if (q.startsWith("#")) {
				eventBus.fireEvent(new SensorChangedEvent(q.substring(2),
						Integer.parseInt(q.substring(1, 2))));
			} else {
				eventBus.fireEvent(new ResetEvent());
			}
		}
	}
}
