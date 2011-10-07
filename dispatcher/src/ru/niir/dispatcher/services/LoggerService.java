package ru.niir.dispatcher.services;

import ru.niir.dispatcher.events.DispatcherEvent;

public class LoggerService implements DispatcherService {
	@Override
	public void onEvent(final DispatcherEvent event) {
		System.out.println(event);
	}
}
