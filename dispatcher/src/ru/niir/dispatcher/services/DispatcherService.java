package ru.niir.dispatcher.services;

import ru.niir.dispatcher.events.DispatcherEvent;

public interface DispatcherService {
	public void onEvent(final DispatcherEvent _event);
}
